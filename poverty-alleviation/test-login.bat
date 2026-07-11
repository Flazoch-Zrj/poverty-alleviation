@echo off
chcp 65001 >nul
echo 测试登录接口...
curl -s -X POST http://localhost:8080/api/v1/system/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
echo.
pause
