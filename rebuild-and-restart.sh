#!/bin/bash

# 一键重新编译并启动所有服务脚本
# 特点：
# 1. 强制杀死端口占用进程
# 2. 前端端口固定不递增
# 3. 完整的编译和启动流程
# 4. 详细的日志和状态检查

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 配置参数
BACKEND_PORT=8080
FRONTEND_PORT=8081
BACKEND_JAR="ruoyi-admin/target/ruoyi-admin.jar"
FRONTEND_DIR="ruoyi-ui"

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_success() {
    echo -e "${PURPLE}[SUCCESS]${NC} $1"
}

log_debug() {
    echo -e "${CYAN}[DEBUG]${NC} $1"
}

# 打印分隔线
print_separator() {
    echo "=================================================="
}

# 检查命令是否存在
check_command() {
    local cmd=$1
    local name=$2
    
    if ! command -v $cmd &> /dev/null; then
        log_error "$name 未安装，请先安装 $name"
        exit 1
    fi
    log_debug "$name 已安装: $(which $cmd)"
}

# 检查环境依赖
check_environment() {
    log_step "检查环境依赖..."
    
    check_command "java" "Java"
    check_command "mvn" "Maven"
    check_command "npm" "Node.js/NPM"
    check_command "lsof" "lsof"
    
    # 检查Java版本
    local java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_debug "Java版本: $java_version"
    
    # 检查Maven版本
    local maven_version=$(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)
    log_debug "Maven版本: $maven_version"
    
    # 检查Node版本
    local node_version=$(node -v 2>/dev/null || echo "未安装")
    log_debug "Node.js版本: $node_version"
    
    log_success "环境检查完成！"
}

# 检查MySQL服务
check_mysql() {
    log_step "检查MySQL服务..."
    
    if systemctl is-active --quiet mysql 2>/dev/null; then
        log_success "MySQL服务正在运行"
    elif systemctl is-active --quiet mysqld 2>/dev/null; then
        log_success "MySQL服务正在运行 (mysqld)"
    else
        log_warning "MySQL服务未运行，尝试启动..."
        if sudo systemctl start mysql 2>/dev/null || sudo systemctl start mysqld 2>/dev/null; then
            log_success "MySQL服务启动成功"
        else
            log_error "无法启动MySQL服务，请手动启动"
            exit 1
        fi
    fi
}

# 强制杀死占用指定端口的进程
kill_port_processes() {
    local port=$1
    local service_name=$2
    
    log_debug "检查端口 $port ($service_name) 是否被占用..."
    
    # 使用lsof查找占用端口的进程
    local pids=$(lsof -ti:$port 2>/dev/null)
    
    if [[ -n "$pids" ]]; then
        log_warning "端口 $port 被以下进程占用："
        for pid in $pids; do
            local process_info=$(ps -p $pid -o pid,ppid,cmd --no-headers 2>/dev/null || echo "$pid - 进程信息获取失败")
            log_warning "  PID: $process_info"
        done
        
        log_step "强制杀死占用端口 $port 的进程..."
        for pid in $pids; do
            if kill -9 $pid 2>/dev/null; then
                log_info "已杀死进程 $pid"
            else
                log_warning "无法杀死进程 $pid (可能已经结束)"
            fi
        done
        
        # 等待进程完全结束
        sleep 3
        
        # 再次检查端口是否释放
        local remaining_pids=$(lsof -ti:$port 2>/dev/null)
        if [[ -n "$remaining_pids" ]]; then
            log_error "端口 $port 仍被占用，无法释放！"
            log_error "请手动处理以下进程: $remaining_pids"
            exit 1
        else
            log_success "端口 $port 已成功释放"
        fi
    else
        log_debug "端口 $port 未被占用"
    fi
}

# 停止所有相关服务
stop_all_services() {
    log_step "停止所有相关服务..."
    
    # 停止通过PID文件记录的服务
    for pid_file in "backend.pid" "frontend.pid"; do
        if [[ -f "$pid_file" ]]; then
            local pid=$(cat $pid_file)
            local service_name=${pid_file%.pid}
            
            if kill -0 $pid 2>/dev/null; then
                log_info "停止 $service_name 服务 (PID: $pid)..."
                kill $pid 2>/dev/null
                sleep 2
                
                # 如果进程仍在运行，强制杀死
                if kill -0 $pid 2>/dev/null; then
                    log_warning "强制杀死 $service_name 服务 (PID: $pid)..."
                    kill -9 $pid 2>/dev/null
                fi
            fi
            rm -f $pid_file
        fi
    done
    
    # 杀死可能残留的相关进程
    log_debug "清理残留进程..."
    pkill -f "ruoyi-admin.jar" 2>/dev/null || true
    pkill -f "npm.*serve" 2>/dev/null || true
    pkill -f "vue-cli-service.*serve" 2>/dev/null || true
    pkill -f "webpack.*serve" 2>/dev/null || true
    
    # 强制释放端口
    kill_port_processes $BACKEND_PORT "后端服务"
    kill_port_processes $FRONTEND_PORT "前端服务"
    
    log_success "所有服务已停止！"
}

# 清理编译产物
clean_build_artifacts() {
    log_step "清理编译产物..."
    
    # 清理Maven编译产物
    log_info "清理后端编译产物..."
    if mvn clean -q; then
        log_success "后端编译产物清理完成"
    else
        log_error "后端编译产物清理失败"
        exit 1
    fi
    
    # 清理前端编译产物
    if [[ -d "$FRONTEND_DIR/dist" ]]; then
        log_info "清理前端编译产物..."
        rm -rf "$FRONTEND_DIR/dist"
        log_success "前端编译产物清理完成"
    fi
    
    # 可选：清理前端依赖缓存
    if [[ -d "$FRONTEND_DIR/.cache" ]]; then
        log_info "清理前端缓存..."
        rm -rf "$FRONTEND_DIR/.cache"
    fi
}

# 编译后端项目
compile_backend() {
    log_step "编译后端项目..."
    
    log_info "执行Maven编译 (跳过测试)..."
    if mvn clean package -Dmaven.test.skip=true -q; then
        log_success "后端编译成功！"
        
        # 检查JAR文件是否生成
        if [[ -f "$BACKEND_JAR" ]]; then
            local jar_size=$(du -h "$BACKEND_JAR" | cut -f1)
            log_info "生成的JAR文件: $BACKEND_JAR ($jar_size)"
        else
            log_error "JAR文件未生成: $BACKEND_JAR"
            exit 1
        fi
    else
        log_error "后端编译失败！请检查代码和依赖"
        exit 1
    fi
}

# 安装前端依赖
install_frontend_dependencies() {
    log_step "检查前端依赖..."
    
    cd "$FRONTEND_DIR"
    
    if [[ ! -d "node_modules" ]] || [[ ! -f "node_modules/.package-lock.json" ]]; then
        log_info "安装前端依赖..."
        if npm install --silent; then
            log_success "前端依赖安装成功！"
        else
            log_error "前端依赖安装失败！"
            cd ..
            exit 1
        fi
    else
        log_info "前端依赖已存在，跳过安装"
    fi
    
    cd ..
}

# 启动后端服务
start_backend_service() {
    log_step "启动后端服务..."

    # 确保日志目录存在
    mkdir -p logs

    # 再次确保端口未被占用
    kill_port_processes $BACKEND_PORT "后端服务"

    # 启动后端服务
    log_info "启动Spring Boot应用..."
    cd ruoyi-admin
    nohup java -jar target/ruoyi-admin.jar > ../logs/backend.log 2>&1 &
    local backend_pid=$!
    echo $backend_pid > ../backend.pid
    cd ..

    log_info "后端服务启动中，PID: $backend_pid"
    log_info "日志文件: logs/backend.log"

    # 等待服务启动
    log_step "等待后端服务启动 (最多等待30秒)..."
    local wait_count=0
    local max_wait=30

    while [[ $wait_count -lt $max_wait ]]; do
        if kill -0 $backend_pid 2>/dev/null; then
            # 检查服务是否真正启动（通过检查日志或端口）
            if lsof -ti:$BACKEND_PORT >/dev/null 2>&1; then
                log_success "后端服务启动成功！"
                log_info "后端访问地址: http://localhost:$BACKEND_PORT"
                return 0
            fi
        else
            log_error "后端服务进程已退出，启动失败！"
            log_error "请查看日志文件: logs/backend.log"
            tail -20 logs/backend.log
            exit 1
        fi

        sleep 2
        wait_count=$((wait_count + 2))
        echo -n "."
    done

    echo ""
    log_error "后端服务启动超时！"
    log_error "请查看日志文件: logs/backend.log"
    exit 1
}

# 启动前端服务
start_frontend_service() {
    log_step "启动前端服务..."

    # 确保端口未被占用
    kill_port_processes $FRONTEND_PORT "前端服务"

    cd "$FRONTEND_DIR"

    # 设置环境变量确保端口固定
    export PORT=$FRONTEND_PORT
    export VUE_CLI_SERVICE_CONFIG_PATH=$(pwd)/vue.config.js

    # 启动前端开发服务器
    log_info "启动Vue开发服务器..."
    nohup npm run dev > ../logs/frontend.log 2>&1 &
    local frontend_pid=$!
    echo $frontend_pid > ../frontend.pid
    cd ..

    log_info "前端服务启动中，PID: $frontend_pid"
    log_info "日志文件: logs/frontend.log"

    # 等待服务启动
    log_step "等待前端服务启动 (最多等待20秒)..."
    local wait_count=0
    local max_wait=20

    while [[ $wait_count -lt $max_wait ]]; do
        if kill -0 $frontend_pid 2>/dev/null; then
            # 检查端口是否被监听
            if lsof -ti:$FRONTEND_PORT >/dev/null 2>&1; then
                log_success "前端服务启动成功！"
                log_info "前端访问地址: http://localhost:$FRONTEND_PORT"
                return 0
            fi
        else
            log_error "前端服务进程已退出，启动失败！"
            log_error "请查看日志文件: logs/frontend.log"
            tail -20 logs/frontend.log
            exit 1
        fi

        sleep 2
        wait_count=$((wait_count + 2))
        echo -n "."
    done

    echo ""
    log_error "前端服务启动超时！"
    log_error "请查看日志文件: logs/frontend.log"
    exit 1
}

# 显示最终信息
show_final_info() {
    print_separator
    log_success "🎉 所有服务重新编译启动完成！"
    print_separator
    echo ""

    log_info "📱 访问地址:"
    echo "  🌐 前端管理界面: http://localhost:$FRONTEND_PORT"
    echo "  🔧 后端API接口: http://localhost:$BACKEND_PORT"
    echo "  📊 Druid数据源监控: http://localhost:$BACKEND_PORT/druid"
    echo "  📚 Swagger API文档: http://localhost:$BACKEND_PORT/swagger-ui/"
    echo ""

    log_info "👤 默认管理员账号:"
    echo "  用户名: admin"
    echo "  密码: admin123"
    echo ""

    log_info "📋 常用管理命令:"
    echo "  查看后端日志: tail -f logs/backend.log"
    echo "  查看前端日志: tail -f logs/frontend.log"
    echo "  停止所有服务: ./stop-all.sh"
    echo "  重新编译启动: ./rebuild-and-restart.sh"
    echo "  查看服务状态: ./status.sh"
    echo ""

    log_info "🔧 技术信息:"
    echo "  前端端口: $FRONTEND_PORT (固定，不会递增)"
    echo "  后端端口: $BACKEND_PORT"
    echo "  前端技术栈: Vue.js 2.6 + Element UI"
    echo "  后端技术栈: Spring Boot 2.5 + MyBatis"
    echo ""

    log_info "📝 注意事项:"
    echo "  - 首次启动可能需要较长时间"
    echo "  - 如遇到问题请查看对应的日志文件"
    echo "  - 端口被占用时会自动杀死占用进程"
    echo ""

    print_separator
}

# 主函数
main() {
    clear
    print_separator
    log_info "🚀 若依管理系统 - 一键重新编译启动脚本"
    print_separator
    echo ""

    # 执行各个步骤
    check_environment
    echo ""

    check_mysql
    echo ""

    stop_all_services
    echo ""

    clean_build_artifacts
    echo ""

    compile_backend
    echo ""

    install_frontend_dependencies
    echo ""

    start_backend_service
    echo ""

    start_frontend_service
    echo ""

    show_final_info
}

# 错误处理
set -e
trap 'log_error "脚本执行过程中发生错误，请检查上面的错误信息"; exit 1' ERR

# 执行主函数
main "$@"
