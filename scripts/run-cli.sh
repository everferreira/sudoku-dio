#!/usr/bin/env bash
# Compila e executa a versão TERMINAL do Sudoku.
# Uso: ./scripts/run-cli.sh        (tabuleiro padrão)
#      ./scripts/run-cli.sh 0,0;4,false 1,0;7,false ...   (tabuleiro customizado)
set -euo pipefail
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

# Localiza o JDK: JAVA_HOME -> ~/tools/jdk-* (instalação local) -> PATH
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
  JAVAC="$JAVA_HOME/bin/javac"; JAVA="$JAVA_HOME/bin/java"
elif compgen -G "$HOME/tools/jdk-*/bin/javac" > /dev/null 2>&1; then
  JDK_HOME="$(dirname "$(compgen -G "$HOME/tools/jdk-*/bin/javac" | head -1)")/.."
  JAVAC="$JDK_HOME/bin/javac"; JAVA="$JDK_HOME/bin/java"
else
  JAVAC=javac; JAVA=java
fi

echo "==> Compilando (JDK: $("$JAVA" -version 2>&1 | head -1))..."
find src -name "*.java" -print > /tmp/sudoku-sources.txt
"$JAVAC" --release 17 -encoding UTF-8 -d out @/tmp/sudoku-sources.txt

# Monta os argumentos do tabuleiro a partir do arquivo de dados (um por linha)
BOARD_ARGS=""
if [ -f scripts/board-data.txt ]; then
  BOARD_ARGS=$(tr '\n' ' ' < scripts/board-data.txt)
fi

echo "==> Iniciando Sudoku (terminal)..."
exec "$JAVA" -cp out br.com.dio.Main $BOARD_ARGS "$@"