# Sudoku — Formação Java (DIO)

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/everferreira/sudoku-dio)
[![CI — Build & Test](https://github.com/everferreira/sudoku-dio/actions/workflows/ci.yml/badge.svg)](https://github.com/everferreira/sudoku-dio/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17%2B-%23ED8B00?logo=openjdk&logoColor=white)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven)](https://maven.apache.org/)

Jogo de Sudoku 9x9 desenvolvido no desafio da **Formação Java** da [DIO](https://www.dio.me),
com **duas versões** no mesmo repositório:

- **Terminal (CLI)** — menu interativo com 8 opções (`Main`)
- **Interface gráfica (Swing)** — tabuleiro visual com setores 3x3 (`UIMain`)

A lógica foi evoluída em relação à base original da DIO: a validação compara
**linhas, colunas e quadrantes 3x3** (regras reais do Sudoku), em vez de apenas
conferir o valor contra uma resposta esperada.

## Estrutura

```
sudoku/
├── pom.xml                        # Build Maven
├── src/
│   ├── main/java/br/com/dio/
│   │   ├── Main.java              # Versão terminal (CLI)
│   │   ├── UIMain.java            # Versão com interface gráfica (Swing)
│   │   ├── model/                 # Board, Space, GameStatusEnum
│   │   ├── service/               # BoardService (lógica compartilhada) + eventos
│   │   ├── ui/                    # Componentes Swing (janela, setores, botões)
│   │   └── util/                  # BoardConfig (args) e BoardTemplate (impressão)
│   └── test/java/br/com/dio/
│       └── model/BoardTest.java   # Testes unitários (JUnit 5)
├── .devcontainer/
│   └── devcontainer.json          # Configuração do GitHub Codespaces
├── .github/workflows/
│   └── ci.yml                     # Integração contínua (GitHub Actions)
├── scripts/                       # run-cli.sh, run-ui.sh, board-data.txt
└── .gitignore
```

## Requisitos

- **JDK 17 ou superior** (recomendado: 21 LTS)
- **Maven 3.9+** (ou use `mvnw` — Maven Wrapper)

## Como rodar

Com uma linha:

```bash
# Terminal — tabuleiro clássico da DIO (33 células fixas)
./scripts/run-cli.sh

# GUI (Swing — requer ambiente gráfico)
./scripts/run-ui.sh
```

Ou manualmente com Maven:

```bash
# Compilar
mvn compile

# Terminal (tabuleiro padrão)
mvn exec:java -Dexec.mainClass="br.com.dio.Main"

# Terminal (tabuleiro customizado — formato DIO)
mvn exec:java -Dexec.mainClass="br.com.dio.Main" \
  -Dexec.args="0,0;4,false 1,0;7,false 2,0;9,true ..."

# Interface gráfica
mvn exec:java -Dexec.mainClass="br.com.dio.UIMain"

# Rodar testes
mvn test
```

O tabuleiro completo está em [`scripts/board-data.txt`](scripts/board-data.txt)
(um argumento por linha — basta colar como argumentos).

## Testes

Os testes unitários cobrem:

| Categoria | O que testa |
|-----------|-------------|
| **Status** | `NON_STARTED`, `INCOMPLETE`, `COMPLETE` |
| **Linhas** | Detecção de repetições em cada linha |
| **Colunas** | Detecção de repetições em cada coluna |
| **Quadrantes 3x3** | Detecção de repetições em cada região |
| **Validação geral** | `hasErrors()` e `gameIsFinished()` |
| **Células fixas** | `changeValue` / `clearValue` bloqueados em células fixas |
| **Reset** | Limpa células editáveis sem afetar fixas |

```bash
mvn test
```

## Formato dos argumentos

`<coluna>,<linha>;<valor>,<fixo>`

| Parte | Descrição |
|-------|-----------|
| `coluna,linha` | Posição da célula (0 a 8). Ex.: `0,0` = canto superior esquerdo |
| `valor` | Número esperado na célula (1 a 9) |
| `fixo` | `true` = célula pré-preenchida e imutável; `false` = editável |

Exemplo: `0,0;4,false` → célula da coluna 0/linha 0 vale `4` e pode ser alterada.

## Menu do terminal

```
1 - Iniciar um novo jogo    5 - Verificar status do jogo
2 - Colocar um novo número  6 - Limpar jogo
3 - Remover um número       7 - Finalizar jogo
4 - Visualizar jogo atual   8 - Sair
```

A opção **5** informa o status e aponta exatamente onde estão os erros:
linhas, colunas e quadrantes com números repetidos.

## Regras implementadas (validação real do Sudoku)

- Cada **linha** deve conter os números 1..9 sem repetição
- Cada **coluna** deve conter os números 1..9 sem repetição
- Cada **quadrante 3x3** deve conter os números 1..9 sem repetição

O jogo é finalizado quando todas as 81 células estão preenchidas e nenhuma
regra é violada.

## Tecnologias

- **Java 17+** (text blocks, switch expressions, streams)
- **Maven** (build e gerenciamento de dependências)
- **JUnit 5** (testes unitários)
- **Swing** (interface gráfica)
- **Padrão Observer** (eventos da interface via `NotifierService`)
- **GitHub Codespaces** (desenvolvimento na nuvem)
- **GitHub Actions** (integração contínua)
- Sem dependências externas no runtime — JDK puro

---
Projeto baseado no repositório [digitalinnovationone/sudoku](https://github.com/digitalinnovationone/sudoku).