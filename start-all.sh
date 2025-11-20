#!/bin/bash

# 全部服务启动脚本

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

# 启动所有服务
start_all() {
    log_info "启动所有服务..."
    echo ""
    
    # 启动后端
    log_step "1. 启动后端服务..."
    ./start-backend.sh
    
    echo ""
    
    # 启动前端
    log_step "2. 启动前端服务..."
    ./start-frontend.sh
    
    echo ""
    log_info "所有服务启动完成！"
    
    # 显示访问信息
    echo ""
    log_info "========== 访问信息 =========="
    echo ""
    log_info "前端访问地址: http://localhost:80"
    log_info "后端API地址: http://localhost:8080"
    log_info "Druid监控: http://localhost:8080/druid"
    echo ""
    log_info "默认管理员账号:"
    echo "  用户名: admin"
    echo "  密码: admin123"
    echo ""
    log_info "管理命令:"
    echo "  停止所有服务: ./stop-all.sh"
    echo "  重启所有服务: ./restart-all.sh"
    echo "  查看服务状态: ./status.sh"
    echo ""
}

# 主函数
main() {
    start_all
}

# 执行主函数
main "$@"
