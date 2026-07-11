#!/bin/bash
# 扶贫帮扶管理系统 - 一键启动开发环境 (Mac/Linux)

echo "============================================"
echo "  扶贫帮扶管理系统 - 一键启动开发环境"
echo "============================================"
echo ""

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 检查端口占用
if lsof -i:3000 >/dev/null 2>&1; then
    echo "[警告] 端口 3000 已被占用"
fi
if lsof -i:8080 >/dev/null 2>&1; then
    echo "[警告] 端口 8080 已被占用"
fi

# 启动 Vue 前端
echo "[1/2] 正在启动 Vue 前端开发服务器 (端口 3000)..."
cd "$SCRIPT_DIR/frontend"
if [ ! -d "node_modules" ]; then
    echo "  → 首次运行，正在安装依赖..."
    npm install
fi
npm run dev &
VUE_PID=$!
echo "  → Vue DevServer PID: $VUE_PID"

# 等待 3 秒
sleep 3

# 启动 Spring Boot 后端
echo "[2/2] 正在启动 Spring Boot 后端服务 (端口 8080)..."
cd "$SCRIPT_DIR/backend"
mvn spring-boot:run -Pdev &
SPRING_PID=$!
echo "  → Spring Boot PID: $SPRING_PID"

echo ""
echo "============================================"
echo "  启动完成！请使用以下地址访问："
echo ""
echo "  前端页面：   http://localhost:3000"
echo "  后端接口：   http://localhost:8080"
echo "  API 文档：   http://localhost:8080/doc.html"
echo ""
echo "  按 Ctrl+C 停止所有服务"
echo "============================================"
echo ""

# 捕获退出信号，停止所有后台进程
trap "echo '正在停止服务...'; kill $VUE_PID $SPRING_PID 2>/dev/null; exit 0" SIGINT SIGTERM

# 等待子进程
wait
