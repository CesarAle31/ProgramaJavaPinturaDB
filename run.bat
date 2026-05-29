@echo off
setlocal

echo Compilando proyecto...

if not exist bin mkdir bin

if defined JAVA_HOME (
    set "JAVAC=%JAVA_HOME%\bin\javac.exe"
    set "JAVA=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVAC=javac"
    set "JAVA=java"
)

"%JAVAC%" -cp "lib\*" -d bin src\com\ipesapinturas\models\*.java src\com\ipesapinturas\utils\*.java src\com\ipesapinturas\dao\*.java src\com\ipesapinturas\services\*.java src\com\ipesapinturas\ui\*.java src\com\ipesapinturas\ui\panels\*.java

if errorlevel 1 (
    echo Error al compilar.
    pause
    exit /b 1
)

echo Ejecutando...

"%JAVA%" -cp "bin;lib\*" com.ipesapinturas.ui.LoginFrame

pause
