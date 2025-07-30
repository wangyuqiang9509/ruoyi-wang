#!/bin/bash

# 服务状态检查脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 检查MySQL服务状态
check_mysql() {
    log_step "检查MySQL服务状态..."
    
    if systemctl is-active --quiet mysql; then
        log_info "MySQL服务: 运行中"
    else
        log_error "MySQL服务: 未运行"
    fi
}

# 检查后端服务状态
check_backend() {
    log_step "检查后端服务状态..."
    
    if [[ -f "backend.pid" ]]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            log_info "后端服务: 运行中 (PID: $BACKEND_PID)"
            
            # 检查端口是否监听
            if netstat -tuln | grep -q ":8080 "; then
                log_info "后端端口8080: 监听中"
            else
                log_warn "后端端口8080: 未监听"
            fi
        else
            log_error "后端服务: 未运行 (PID文件存在但进程不存在)"
            rm -f backend.pid
        fi
    else
        # 检查是否有相关进程
        PIDS=$(pgrep -f "ruoyi-admin.jar")
        if [[ -n "$PIDS" ]]; then
            log_warn "后端服务: 运行中但无PID文件 (PID: $PIDS)"
        else
            log_error "后端服务: 未运行"
        fi
    fi
}

# 检查前端服务状态
check_frontend() {
    log_step "检查前端服务状态..."
    
    if [[ -f "frontend.pid" ]]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            log_info "前端服务: 运行中 (PID: $FRONTEND_PID)"
            
            # 检查端口是否监听
            if netstat -tuln | grep -q ":80 "; then
                log_info "前端端口80: 监听中"
            else
                log_warn "前端端口80: 未监听"
            fi
        else
            log_error "前端服务: 未运行 (PID文件存在但进程不存在)"
            rm -f frontend.pid
        fi
    else
        # 检查是否有相关进程
        PIDS=$(pgrep -f "npm.*dev")
        if [[ -n "$PIDS" ]]; then
            log_warn "前端服务: 运行中但无PID文件 (PID: $PIDS)"
        else
            log_error "前端服务: 未运行"
        fi
    fi
}

# 检查端口占用
check_ports() {
    log_step "检查端口占用情况..."
    
    echo "端口占用情况:"
    echo "  MySQL (3306): $(netstat -tuln | grep ':3306 ' | wc -l) 个连接"
    echo "  后端 (8080): $(netstat -tuln | grep ':8080 ' | wc -l) 个连接"
    echo "  前端 (80): $(netstat -tuln | grep ':80 ' | wc -l) 个连接"
}

# 显示访问信息
show_access_info() {
    log_step "访问信息..."
    
    WSL_IP=$(hostname -I | awk '{print $1}')
    
    echo ""
    echo "访问地址:"
    echo "  前端: http://localhost:80"
    echo "  后端API: http://localhost:8080"
    echo "  Druid监控: http://localhost:8080/druid"
    echo ""
    echo "数据库连接 (Navicat):"
    echo "  主机: $WSL_IP"
    echo "  端口: 3306"
    echo "  数据库: ry-vue"
    echo "  用户名: root"
    echo "  密码: 123456"
    echo ""
}

# 主函数
main() {
    log_info "检查服务状态..."
    echo ""
    
    check_mysql
    echo ""
    check_backend
    echo ""
    check_frontend
    echo ""
    check_ports
    echo ""
    show_access_info
}

# 执行主函数
main "$@"
