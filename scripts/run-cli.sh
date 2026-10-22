#!/usr/bin/env bash
# Compila e executa a versão terminal (CLI) via Maven
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

echo "=== Compilando com Maven ==="
mvn compile -q

echo ""
echo "=== Executando Sudoku (Terminal) ==="
echo ""

# Usa o tabuleiro padrão (sem argumentos = DEFAULT_BOARD em BoardConfig)
mvn exec:java -q -Dexec.mainClass="br.com.dio.Main" 2>/dev/null || {
  # Se maven-exec-plugin não estiver configurado, usa java diretamente
  java -cp "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout 2>/dev/null):target/classes" br.com.dio.Main
}