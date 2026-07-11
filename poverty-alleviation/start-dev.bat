@echo off
chcp 65001 >nul
title 扶贫帮扶管理系统 - 开发模式

echo ============================================
echo   扶贫帮扶管理系统 - 一键启动开发环境
echo ============================================
echo.

:: 检查端口占用
netstat -ano | findstr ":3000" >nul 2>&1
if not errorlevel 1 (
    echo [警告] 端口 3000 已被占用，Vite 可能启动失败
)

netstat -ano | findstr ":8080" >nul 2>&1
if not errorlevel 1 (
    echo [警告] 端口 8080 已被占用，Spring Boot 可能启动失败
)

:: 启动 Vue 前端（Vite 开发服务器）
echo [1/2] 正在启动 Vue 前端开发服务器 (端口 3000)...
cd /d "%~dp0frontend"
if not exist "node_modules" (
    echo   → 首次运行，正在安装依赖...
    call npm install
)
start "Vue-DevServer" cmd /k npm run dev

:: 等待 3 秒让 Vite 先启动
timeout /t 3 /nobreak >nul

:: 启动 Spring Boot 后端
echo [2/2] 正在启动 Spring Boot 后端服务 (端口 8080)...
cd /d "%~dp0backend"
start "SpringBoot-Server" cmd /k mvn spring-boot:run -Pdev

echo.
echo ============================================
echo   启动完成！请使用以下地址访问：
echo.
echo   前端页面：   http://localhost:3000
echo   后端接口：   http://localhost:8080
echo   API 文档：   http://localhost:8080/doc.html
echo ============================================
echo.
pause
