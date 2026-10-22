# Sudoku — Formação Java (DIO)

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
├── src/br/com/dio/
│   ├── Main.java                  # Versão terminal (CLI)
│   ├── UIMain.java                # Versão com interface gráfica (Swing)
│   ├── model/                     # Board, Space, GameStatusEnum
│   ├── service/                   # BoardService (lógica compartilhada) + eventos
│   ├── ui/                        # Componentes Swing (janela, setores, botões)
│   └── util/                      # BoardConfig (args) e BoardTemplate (impressão)
├── scripts/                       # run-cli.sh, run-ui.sh, board-data.txt
└── out/                           # Classes compiladas (gerado, não versionado)
```

## Requisitos

- JDK 17 ou superior (recomendado: 21 LTS). O projeto compila com `--release 17`.

## Como rodar

```bash
# 1. Versão terminal (tabuleiro clássico da DIO, 33 células fixas)
./scripts/run-cli.sh

# 2. Versão com interface gráfica (Swing — requer ambiente gráfico)
./scripts/run-ui.sh
```

Manual:

```bash
# Compilar
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java" -print)

# Terminal (sem argumentos → tabuleiro padrão)
java -cp out br.com.dio.Main

# Terminal (com tabuleiro customizado — formato DIO)
java -cp out br.com.dio.Main 0,0;4,false 1,0;7,false 2,0;9,true ...

# Interface gráfica
java -cp out br.com.dio.UIMain 0,0;4,false 1,0;7,false 2,0;9,true ...
```

O tabuleiro completo está em [`scripts/board-data.txt`](scripts/board-data.txt)
(um argumento por linha — basta colar como argumentos).

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

- Java 17+ (text blocks, switch expressions, streams)
- Swing (interface gráfica)
- Padrão Observer (eventos da interface via `NotifierService`)
- Sem dependências externas — JDK puro

---
Projeto baseado no repositório [digitalinnovationone/sudoku](https://github.com/digitalinnovationone/sudoku).