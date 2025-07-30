#!/bin/bash

# 停止所有服务脚本

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

# 停止所有服务
stop_all() {
    log_info "停止所有服务..."
    echo ""
    
    # 停止前端
    log_step "1. 停止前端服务..."
    ./stop-frontend.sh
    
    echo ""
    
    # 停止后端
    log_step "2. 停止后端服务..."
    ./stop-backend.sh
    
    echo ""
    log_info "所有服务已停止！"
}

# 主函数
main() {
    stop_all
}

# 执行主函数
main "$@"
