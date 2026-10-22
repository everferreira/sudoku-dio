#!/usr/bin/env bash
# Compila e executa a versão com interface gráfica (Swing) via Maven
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

echo "=== Compilando com Maven ==="
mvn compile -q

echo ""
echo "=== Executando Sudoku (GUI) ==="
echo ""

# Tenta exec:java com mainClass da UI
mvn exec:java -q -Dexec.mainClass="br.com.dio.UIMain" 2>/dev/null || {
  java -cp "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout 2>/dev/null):target/classes" br.com.dio.UIMain
}