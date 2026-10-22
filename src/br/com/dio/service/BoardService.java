package br.com.dio.service;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Camada de serviço do tabuleiro: isola a construção e as operações do jogo.
 * É compartilhada pela versão de terminal ({@code Main}) e pela interface
 * gráfica ({@code MainScreen}), evitando duplicação de lógica.
 */
public class BoardService {

    private static final int BOARD_LIMIT = 9;

    /** Configuração padrão de uma célula vazia: sem valor esperado, editável. */
    private static final String EMPTY_CELL_CONFIG = "0,false";

    private final Board board;

    /**
     * @param gameConfig mapa {@code "coluna,linha" -&gt; "valor,fixo"} com a
     *                   configuração inicial do tabuleiro. Posições ausentes
     *                   viram células vazias e editáveis.
     */
    public BoardService(final Map<String, String> gameConfig) {
        this.board = new Board(initBoard(gameConfig));
    }

    /**
     * Monta as 81 células ({@code spaces[coluna][linha]}) a partir do mapa de
     * configuração. Posições sem configuração recebem o padrão de célula vazia.
     */
    private List<List<Space>> initBoard(final Map<String, String> gameConfig) {
        final List<List<Space>> spaces = new ArrayList<>();
        for (int column = 0; column < BOARD_LIMIT; column++) {
            spaces.add(new ArrayList<>());
            for (int row = 0; row < BOARD_LIMIT; row++) {
                final var positionConfig = gameConfig.getOrDefault(
                        "%s,%s".formatted(column, row), EMPTY_CELL_CONFIG);
                final var expected = Integer.parseInt(positionConfig.split(",")[0]);
                final var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                spaces.get(column).add(new Space(expected, fixed));
            }
        }
        return spaces;
    }

    public List<List<Space>> getSpaces() {
        return board.getSpaces();
    }

    public List<Integer> getRowsWithErrors() {
        return board.getRowsWithErrors();
    }

    public List<Integer> getColumnsWithErrors() {
        return board.getColumnsWithErrors();
    }

    public List<Integer> getRegionsWithErrors() {
        return board.getRegionsWithErrors();
    }

    public boolean changeValue(final int column, final int row, final int value) {
        return board.changeValue(column, row, value);
    }

    public boolean clearValue(final int column, final int row) {
        return board.clearValue(column, row);
    }

    public void reset() {
        board.reset();
    }

    public boolean hasErrors() {
        return board.hasErrors();
    }

    public GameStatusEnum getStatus() {
        return board.getStatus();
    }

    public boolean gameIsFinished() {
        return board.gameIsFinished();
    }
}