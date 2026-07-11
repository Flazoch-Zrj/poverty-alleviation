@echo off
chcp 65001 >nul
title 扶贫帮扶管理系统 - 命令行编译启动

echo ============================================
echo   扶贫帮扶管理系统 - Maven 编译 + 启动
echo ============================================
echo.

cd /d "%~dp0backend"

:: 使用系统 Maven 编译
echo [1/3] 编译项目...
call mvn clean compile -Pdev
if errorlevel 1 (
    echo [错误] 编译失败，请检查代码错误。
    pause
    exit /b 1
)
echo [完成] 编译成功
echo.

:: 检查 target/classes 下主类是否存在
if not exist "target\classes\com\poverty\PovertyApplication.class" (
    echo [错误] 编译后仍未找到主类 PovertyApplication.class
    pause
    exit /b 1
)
echo [确认] 主类文件存在: target\classes\com\poverty\PovertyApplication.class
echo.

:: 运行 Spring Boot
echo [2/3] 启动 Spring Boot...
echo.
call mvn spring-boot:run -Pdev
pause
