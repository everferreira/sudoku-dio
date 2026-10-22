package br.com.dio.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Representa o tabuleiro 9x9 do Sudoku.
 *
 * <p>A estrutura interna é uma lista de <b>colunas</b>: {@code spaces[coluna][linha]},
 * ou seja, {@code spaces.get(coluna).get(linha)} acessa a célula da coluna
 * {@code coluna} na linha {@code linha} — exatamente o formato usado nos
 * argumentos de inicialização {@code <coluna>,<linha>;...}.</p>
 *
 * <p>Validação conforme as regras reais do Sudoku:
 * <ul>
 *   <li>cada linha deve conter os números 1..9 sem repetição;</li>
 *   <li>cada coluna idem;</li>
 *   <li>cada quadrante 3x3 idem.</li>
 * </ul></p>
 */
public class Board {

    /** Tamanho do tabuleiro (9x9). */
    public static final int BOARD_SIZE = 9;

    /** Tamanho do quadrante (3x3). */
    public static final int REGION_SIZE = 3;

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    /**
     * Status do jogo:
     * <ul>
     *   <li>{@link GameStatusEnum#NON_STARTED}: nenhuma célula editável preenchida;</li>
     *   <li>{@link GameStatusEnum#INCOMPLETE}: ainda existem células vazias;</li>
     *   <li>{@link GameStatusEnum#COMPLETE}: todas as células preenchidas.</li>
     * </ul>
     */
    public GameStatusEnum getStatus() {
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))) {
            return NON_STARTED;
        }
        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    // ====================== VALIDAÇÃO (REGRAS DO SUDOKU) ======================

    /**
     * Verifica se a <b>linha</b> {@code row} possui números repetidos.
     * Regra: cada linha deve conter 1..9 sem repetição.
     *
     * @param row índice da linha (0 a 8)
     * @return {@code true} se houver duplicata
     */
    public boolean hasErrorInRow(final int row) {
        return hasDuplicates(IntStream.range(0, BOARD_SIZE)
                .mapToObj(column -> spaces.get(column).get(row).getActual()));
    }

    /**
     * Verifica se a <b>coluna</b> {@code column} possui números repetidos.
     *
     * @param column índice da coluna (0 a 8)
     * @return {@code true} se houver duplicata
     */
    public boolean hasErrorInColumn(final int column) {
        return hasDuplicates(IntStream.range(0, BOARD_SIZE)
                .mapToObj(row -> spaces.get(column).get(row).getActual()));
    }

    /**
     * Verifica se o <b>quadrante 3x3</b> que contém a célula ({@code row}, {@code column})
     * possui números repetidos. A âncora do quadrante (canto superior esquerdo) é
     * calculada truncando a coordenada para o múltiplo de 3 mais próximo (0-2, 3-5, 6-8).
     *
     * @param row    linha de uma célula contida no quadrante (0 a 8)
     * @param column coluna de uma célula contida no quadrante (0 a 8)
     * @return {@code true} se houver duplicata
     */
    public boolean hasErrorInRegion(final int row, final int column) {
        final var regionInitColumn = (column / REGION_SIZE) * REGION_SIZE;
        final var regionInitRow = (row / REGION_SIZE) * REGION_SIZE;
        return hasDuplicates(IntStream.range(0, BOARD_SIZE)
                .mapToObj(offset -> spaces.get(regionInitColumn + offset % REGION_SIZE)
                        .get(regionInitRow + offset / REGION_SIZE)
                        .getActual()));
    }

    /**
     * Varre o tabuleiro inteiro aplicando as três regras do Sudoku
     * (linhas, colunas e quadrantes). Retorna {@code true} se QUALQUER
     * violação existir.
     *
     * @return {@code true} se o tabuleiro contiver algum erro
     */
    public boolean hasErrors() {
        if (getStatus() == NON_STARTED) {
            return false;
        }
        for (var index = 0; index < BOARD_SIZE; index++) {
            if (hasErrorInRow(index) || hasErrorInColumn(index)) {
                return true;
            }
        }
        for (var row = 0; row < BOARD_SIZE; row += REGION_SIZE) {
            for (var column = 0; column < BOARD_SIZE; column += REGION_SIZE) {
                if (hasErrorInRegion(row, column)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ================== DIAGNÓSTICO (APOIO AO TERMINAL/UI) ==================

    /** Índices das linhas que violam a regra de repetição. */
    public List<Integer> getRowsWithErrors() {
        return IntStream.range(0, BOARD_SIZE).filter(this::hasErrorInRow).boxed().toList();
    }

    /** Índices das colunas que violam a regra de repetição. */
    public List<Integer> getColumnsWithErrors() {
        return IntStream.range(0, BOARD_SIZE).filter(this::hasErrorInColumn).boxed().toList();
    }

    /**
     * Índices dos quadrantes que violam a regra de repetição.
     * Numeração no sentido linha a linha:
     * <pre>0 | 1 | 2
     * 3 | 4 | 5
     * 6 | 7 | 8</pre>
     */
    public List<Integer> getRegionsWithErrors() {
        return IntStream.range(0, BOARD_SIZE)
                .filter(region -> hasErrorInRegion(
                        (region / REGION_SIZE) * REGION_SIZE,
                        (region % REGION_SIZE) * REGION_SIZE))
                .boxed().toList();
    }

    // ====================== MANIPULAÇÃO DE CÉLULAS ======================

    /**
     * Tenta inserir {@code value} na célula ({@code column}, {@code row}).
     * Células fixas são imutáveis.
     *
     * @return {@code false} se a célula for fixa (operação não realizada)
     */
    public boolean changeValue(final int column, final int row, final int value) {
        final var space = spaces.get(column).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.setActual(value);
        return true;
    }

    /**
     * Tenta limpar a célula ({@code column}, {@code row}).
     *
     * @return {@code false} se a célula for fixa (operação não realizada)
     */
    public boolean clearValue(final int column, final int row) {
        final var space = spaces.get(column).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.clearSpace();
        return true;
    }

    /** Limpa todas as células editáveis (mantém os valores fixos). */
    public void reset() {
        spaces.forEach(column -> column.forEach(Space::clearSpace));
    }

    /**
     * O jogo está finalizado quando o tabuleiro está {@link GameStatusEnum#COMPLETE}
     * (todas as células preenchidas) e não viola nenhuma regra do Sudoku.
     */
    public boolean gameIsFinished() {
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

    /**
     * Detecta duplicatas em uma sequência de valores: se o número de valores
     * preenchidos difere do número de valores distintos, existe repetição.
     * Células vazias ({@code null}) são ignoradas.
     */
    private static boolean hasDuplicates(final java.util.stream.Stream<Integer> values) {
        final var filledValues = values.filter(Objects::nonNull).toList();
        return filledValues.size() != filledValues.stream().distinct().count();
    }
}