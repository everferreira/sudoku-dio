package br.com.dio;

import br.com.dio.service.BoardService;
import br.com.dio.util.BoardConfig;
import br.com.dio.util.BoardTemplate;

import java.util.Map;
import java.util.Scanner;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Versão de TERMINAL do jogo de Sudoku — Desafio da Formação Java (DIO).
 *
 * <p>O tabuleiro inicial é montado a partir dos argumentos de linha de comando
 * no formato DIO {@code <coluna>,<linha>;<valor>,<fixo>}. Sem argumentos, o
 * jogo usa o tabuleiro clássico ({@link BoardConfig#DEFAULT_BOARD}).</p>
 *
 * <p><b>Exemplo de execução:</b>
 * {@code java br.com.dio.Main 0,0;4,false 1,0;7,false ...}</p>
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final int BOARD_LIMIT = 9;

    /** Serviço do tabuleiro — {@code null} enquanto nenhum jogo foi iniciado. */
    private static BoardService boardService;

    public static void main(final String[] args) {
        // 1. Converte "col,row;value,fixed" em um mapa (ou usa o tabuleiro padrão)
        final var gameConfig = BoardConfig.parse(args);

        var option = -1;
        while (true) {
            printMenu();
            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(gameConfig);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida. Selecione uma das opções do menu (1 a 8).");
            }
        }
    }

    private static void printMenu() {
        System.out.println("Selecione uma das opções a seguir:");
        System.out.println("1 - Iniciar um novo jogo");
        System.out.println("2 - Colocar um novo número");
        System.out.println("3 - Remover um número");
        System.out.println("4 - Visualizar jogo atual");
        System.out.println("5 - Verificar status do jogo");
        System.out.println("6 - Limpar jogo");
        System.out.println("7 - Finalizar jogo");
        System.out.println("8 - Sair");
    }

    /**
     * Inicia um novo jogo com a configuração recebida. Só é possível iniciar
     * um jogo por vez; para recomeçar, finalize (7) ou saia (8) primeiro.
     */
    private static void startGame(final Map<String, String> gameConfig) {
        if (nonNull(boardService)) {
            System.out.println("O jogo já foi iniciado.");
            return;
        }
        boardService = new BoardService(gameConfig);
        System.out.println("O jogo está pronto para começar!");
    }

    /**
     * Lê coluna, linha e valor e tenta inserir o número na célula.
     * Células fixas (pré-preenchidas) não podem ser alteradas.
     */
    private static void inputNumber() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        System.out.println("Informe a coluna em que o número será inserido (0 a 8):");
        final var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o número será inserido (0 a 8):");
        final var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [coluna %s, linha %s] (1 a 9):%n", col, row);
        final var value = runUntilGetValidNumber(1, 9);

        if (!boardService.changeValue(col, row, value)) {
            System.out.printf("A posição [coluna %s, linha %s] tem um valor fixo e não pode ser alterada.%n", col, row);
        }
    }

    /** Remove o número da célula escolhida (células fixas são preservadas). */
    private static void removeNumber() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        System.out.println("Informe a coluna do número a ser removido (0 a 8):");
        final var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha do número a ser removido (0 a 8):");
        final var row = runUntilGetValidNumber(0, 8);

        if (!boardService.clearValue(col, row)) {
            System.out.printf("A posição [coluna %s, linha %s] tem um valor fixo e não pode ser alterada.%n", col, row);
        }
    }

    /** Imprime o tabuleiro atual no console (template da DIO). */
    private static void showCurrentGame() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        // Preenche os 81 placeholders do template na ordem linha a linha
        final var args = new Object[81];
        var argPos = 0;
        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (var column : boardService.getSpaces()) {
                args[argPos++] = " " + (isNull(column.get(row).getActual()) ? " " : column.get(row).getActual());
            }
        }

        System.out.println("Seu jogo se encontra da seguinte forma:");
        System.out.printf(BoardTemplate.BOARD_TEMPLATE + "%n", args);
    }

    /**
     * Exibe o status do jogo e, se houver violações, aponta exatamente onde
     * estão os erros (linhas, colunas e quadrantes com números repetidos).
     */
    private static void showGameStatus() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s%n", boardService.getStatus().getLabel());

        if (boardService.hasErrors()) {
            System.out.println("O jogo contém erros — números repetidos em:");
            System.out.printf("  Linhas:     %s%n", boardService.getRowsWithErrors());
            System.out.printf("  Colunas:    %s%n", boardService.getColumnsWithErrors());
            System.out.printf("  Quadrantes: %s%n", boardService.getRegionsWithErrors());
        } else {
            System.out.println("O jogo não contém erros.");
        }
    }

    /** Pergunta a confirmação e limpa todas as células editáveis. */
    private static void clearGame() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso? (sim/não)");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            boardService.reset();
        }
    }

    /** Verifica se o jogo foi concluído com sucesso (tabuleiro completo e sem erros). */
    private static void finishGame() {
        if (isNull(boardService)) {
            System.out.println("O jogo ainda não foi iniciado. Selecione a opção 1.");
            return;
        }

        if (boardService.gameIsFinished()) {
            System.out.println("Parabéns, você concluiu o jogo!");
            showCurrentGame();
            boardService = null;
        } else if (boardService.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique seu tabuleiro e ajuste-o.");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço.");
        }
    }

    /**
     * Lê números do teclado até receber um valor dentro do intervalo
     * {@code [min, max]} (inclusive).
     */
    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s:%n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}