#!/usr/bin/env bash
# Compila e executa a versão terminal (CLI)
# Funciona com Maven no PATH, ~/tools, ou baixa automaticamente
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR/.."

# Detecta Maven
find_maven() {
    if command -v mvn &>/dev/null; then
        echo "mvn"
        return
    fi
    for dir in "$HOME/tools/apache-maven-"*"/bin"; do
        if [ -x "$dir/mvn" ]; then
            echo "$dir/mvn"
            return
        fi
    done
    echo ""
}

MVN=$(find_maven)
if [ -z "$MVN" ]; then
    echo "❌ Maven não encontrado. Instale com:"
    echo "   sudo apt install maven"
    echo "   Ou extraia em ~/tools/apache-maven-*/"
    exit 1
fi

echo "=== Compilando ==="
$MVN compile -q

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║         SUDOKU — Terminal (CLI)         ║"
echo "╚══════════════════════════════════════════╝"
echo ""

$MVN exec:java -q -Dexec.mainClass="br.com.dio.Main" "$@"