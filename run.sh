#!/usr/bin/env bash
set -euo pipefail

echo "Compilando proyecto..."
mkdir -p bin

javac -cp "lib/*" -d bin \
  src/com/ipesapinturas/models/*.java \
  src/com/ipesapinturas/utils/*.java \
  src/com/ipesapinturas/dao/*.java \
  src/com/ipesapinturas/services/*.java \
  src/com/ipesapinturas/ui/*.java \
  src/com/ipesapinturas/ui/panels/*.java

echo "Ejecutando..."
java -cp "bin:lib/*" com.ipesapinturas.ui.LoginFrame
