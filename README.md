# 🧩 Sudoku — Formação Java (DIO)

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/everferreira/sudoku-dio)
[![CI — Build & Test](https://github.com/everferreira/sudoku-dio/actions/workflows/ci.yml/badge.svg)](https://github.com/everferreira/sudoku-dio/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17%2B-%23ED8B00?logo=openjdk&logoColor=white)

Jogo de Sudoku 9x9 com validação real de **linhas, colunas e quadrantes 3x3**.
Duas versões: terminal interativo e interface gráfica (Swing).

---

## 🎮 Como jogar

### Opção 1 — Terminal (CLI)

```bash
git clone https://github.com/everferreira/sudoku-dio.git
cd sudoku-dio
./scripts/run-cli.sh
```

Menu interativo com 8 opções:

```
1 - Iniciar novo jogo    5 - Verificar status do jogo
2 - Colocar novo número  6 - Limpar jogo
3 - Remover um número    7 - Finalizar jogo
4 - Visualizar jogo      8 - Sair
```

### Opção 2 — Interface gráfica (Swing)

Se tiver ambiente gráfico, use:

```bash
./scripts/run-ui.sh
```

### Opção 3 — GitHub Codespaces (navegador)

[Clique aqui](https://codespaces.new/everferreira/sudoku-dio) para abrir no seu navegador.
Depois execute no terminal integrado:

```bash
./scripts/run-cli.sh
```

### Opção 4 — Maven direto

```bash
mvn compile                    # compilar
mvn exec:java                  # rodar (CLI)
mvn exec:java -Dexec.mainClass="br.com.dio.UIMain"   # rodar (GUI)
mvn test                       # rodar testes
```

---

## 📦 Estrutura

```
sudoku/
├── pom.xml                        # Build Maven
├── src/
│   ├── main/java/br/com/dio/
│   │   ├── Main.java              # Terminal (CLI)
│   │   ├── UIMain.java            # Interface gráfica (Swing)
│   │   ├── model/                 # Board, Space, GameStatusEnum
│   │   ├── service/               # BoardService + eventos
│   │   ├── ui/                    # Componentes Swing
│   │   └── util/                  # BoardConfig, BoardTemplate
│   └── test/java/br/com/dio/
│       └── model/BoardTest.java   # 23 testes JUnit 5
├── .devcontainer/devcontainer.json
├── .github/workflows/ci.yml
└── scripts/                       # run-cli.sh, run-ui.sh, board-data.txt
```

## 🧪 Testes

```bash
mvn test
```

23 testes cobrindo:
- Status do jogo (NON_STARTED, INCOMPLETE, COMPLETE)
- Validação de linhas, colunas e quadrantes 3x3
- Proteção de células fixas
- Reset do tabuleiro

## 📋 Formato dos argumentos

```bash
java -cp target/classes br.com.dio.Main <coluna>,<linha>;<valor>,<fixo> ...
```

Exemplo: `0,0;4,false` → coluna 0, linha 0 = valor 4, editável.

O tabuleiro da DIO (33 células fixas) está em [`scripts/board-data.txt`](scripts/board-data.txt).

## 🛠 Tecnologias

- **Java 17+** (text blocks, switch expressions, streams)
- **Maven** (build + dependências)
- **JUnit 5** (testes)
- **Swing** (interface gráfica)
- **Observer** (eventos via `NotifierService`)
- **GitHub Codespaces / Actions**
- Zero dependências externas em runtime

---
Projeto baseado em [digitalinnovationone/sudoku](https://github.com/digitalinnovationone/sudoku).