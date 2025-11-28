@echo off
echo === EJECUTANDO EN MODO ESP32 (ENVIO DE COMANDOS) ===
java -cp target/classes runtime.RuntimePlayer target/out.lobj 192.168.137.193
pause
