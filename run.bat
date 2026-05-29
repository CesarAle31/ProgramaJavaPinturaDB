@echo off
echo Compilando proyecto...

if not exist bin mkdir bin

"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp "lib\*" -d bin src\com\ipesapinturas\models\*.java src\com\ipesapinturas\utils\*.java src\com\ipesapinturas\dao\*.java src\com\ipesapinturas\services\*.java src\com\ipesapinturas\ui\*.java src\com\ipesapinturas\ui\panels\*.java

if errorlevel 1 (
    echo Error al compilar.
    pause
    exit /b
)

echo Ejecutando...

"C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp "bin;lib\*" com.ipesapinturas.ui.LoginFrame

pause
