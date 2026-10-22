package br.com.dio.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Testes unitários para {@link Board} — validação das regras do Sudoku
 * (linhas, colunas, quadrantes 3x3) e manipulação de células.
 */
class BoardTest {

    // ===================== FIXTURES =====================

    /** Cria um tabuleiro 9x9 totalmente vazio (null em todas as células). */
    private static Board emptyBoard() {
        return new Board(IntStream.range(0, Board.BOARD_SIZE)
                .mapToObj(col -> IntStream.range(0, Board.BOARD_SIZE)
                        .mapToObj(row -> new Space(0, false))
                        .<Space>toList())
                .toList());
    }

    /** Cria um tabuleiro com valores fornecidos.
     *  {@code values} é uma matriz 9x9: {@code values[coluna][linha]}. */
    private static Board boardFromValues(final int[][] values) {
        final var spaces = new ArrayList<List<Space>>(Board.BOARD_SIZE);
        for (int col = 0; col < Board.BOARD_SIZE; col++) {
            final var column = new ArrayList<Space>(Board.BOARD_SIZE);
            for (int row = 0; row < Board.BOARD_SIZE; row++) {
                final var space = new Space(values[col][row], false);
                space.setActual(values[col][row]); // preenche o valor real (editável)
                column.add(space);
            }
            spaces.add(column);
        }
        return new Board(spaces);
    }

    /** Tabuleiro completo e válido (resolvido) para testes de gameIsFinished. */
    private static Board solvedBoard() {
        // Um Sudoku 9x9 completo e válido (exemplo real)
        return boardFromValues(new int[][]{
            //col0 col1 col2 col3 col4 col5 col6 col7 col8
            {5,3,4,6,7,8,9,1,2},  // row 0
            {6,7,2,1,9,5,3,4,8},  // row 1
            {1,9,8,3,4,2,5,6,7},  // row 2
            {8,5,9,7,6,1,4,2,3},  // row 3
            {4,2,6,8,5,3,7,9,1},  // row 4
            {7,1,3,9,2,4,8,5,6},  // row 5
            {9,6,1,5,3,7,2,8,4},  // row 6
            {2,8,7,4,1,9,6,3,5},  // row 7
            {3,4,5,2,8,6,1,7,9}   // row 8
        });
    }

    // ===================== TESTES =====================

    @Nested
    @DisplayName("Status do jogo (getStatus)")
    class StatusTests {

        @Test
        @DisplayName("Tabuleiro vazio → NON_STARTED")
        void emptyBoard_returnsNonStarted() {
            final var board = emptyBoard();
            assertEquals(GameStatusEnum.NON_STARTED, board.getStatus());
        }

        @Test
        @DisplayName("Uma célula preenchida → INCOMPLETE")
        void partiallyFilled_returnsIncomplete() {
            final var board = emptyBoard();
            board.changeValue(0, 0, 5);
            assertEquals(GameStatusEnum.INCOMPLETE, board.getStatus());
        }

        @Test
        @DisplayName("Todas as células preenchidas → COMPLETE")
        void fullyFilled_returnsComplete() {
            final var board = solvedBoard();
            assertEquals(GameStatusEnum.COMPLETE, board.getStatus());
        }
    }

    @Nested
    @DisplayName("Validação de linha (hasErrorInRow)")
    class RowValidationTests {

        @Test
        @DisplayName("Linha sem repetições → false")
        void validRow_returnsFalse() {
            final var board = solvedBoard();
            assertFalse(board.hasErrorInRow(0));
        }

        @Test
        @DisplayName("Linha com repetição → true")
        void duplicateInRow_returnsTrue() {
            final var board = solvedBoard();
            board.changeValue(1, 0, 5); // col1,row0 já tem 3, agora coloca 5 que já está em col0,row0
            assertTrue(board.hasErrorInRow(0));
        }

        @Test
        @DisplayName("Linha com célula vazia → false (vazio não é erro)")
        void rowWithNull_returnsFalse() {
            final var board = emptyBoard();
            board.changeValue(0, 0, 5);
            assertFalse(board.hasErrorInRow(0));
        }

        @Test
        @DisplayName("Todas as 9 linhas de um board resolvido → false")
        void allRowsOfSolvedBoard_areValid() {
            final var board = solvedBoard();
            IntStream.range(0, Board.BOARD_SIZE)
                    .forEach(row -> assertFalse(board.hasErrorInRow(row),
                            "Linha " + row + " deveria ser válida"));
        }
    }

    @Nested
    @DisplayName("Validação de coluna (hasErrorInColumn)")
    class ColumnValidationTests {

        @Test
        @DisplayName("Coluna sem repetições → false")
        void validColumn_returnsFalse() {
            final var board = solvedBoard();
            assertFalse(board.hasErrorInColumn(0));
        }

        @Test
        @DisplayName("Coluna com repetição → true")
        void duplicateInColumn_returnsTrue() {
            final var board = solvedBoard();
            board.changeValue(0, 1, 5); // col0,row1 — 5 já está em col0,row0
            assertTrue(board.hasErrorInColumn(0));
        }

        @Test
        @DisplayName("Todas as 9 colunas de um board resolvido → false")
        void allColumnsOfSolvedBoard_areValid() {
            final var board = solvedBoard();
            IntStream.range(0, Board.BOARD_SIZE)
                    .forEach(col -> assertFalse(board.hasErrorInColumn(col),
                            "Coluna " + col + " deveria ser válida"));
        }
    }

    @Nested
    @DisplayName("Validação de quadrante 3x3 (hasErrorInRegion)")
    class RegionValidationTests {

        @Test
        @DisplayName("Quadrante sem repetições → false")
        void validRegion_returnsFalse() {
            final var board = solvedBoard();
            // Quadrante superior esquerdo (região 0)
            assertFalse(board.hasErrorInRegion(0, 0));
        }

        @Test
        @DisplayName("Quadrante com repetição → true")
        void duplicateInRegion_returnsTrue() {
            final var board = solvedBoard();
            // col1,row1 pertence ao quadrante sup-esq (0-2)
            // col0,row0 tem 5; col1,row1 tem 7 — vamos colocar 5 em col1,row1
            board.changeValue(1, 1, 5);
            assertTrue(board.hasErrorInRegion(1, 1));
        }

        @Test
        @DisplayName("Todas as 9 regiões de um board resolvido → false")
        void allRegionsOfSolvedBoard_areValid() {
            final var board = solvedBoard();
            for (int region = 0; region < Board.BOARD_SIZE; region++) {
                final var r = (region / Board.REGION_SIZE) * Board.REGION_SIZE;
                final var c = (region % Board.REGION_SIZE) * Board.REGION_SIZE;
                assertFalse(board.hasErrorInRegion(r, c),
                        "Região " + region + " (ancorada em " + r + "," + c + ") deveria ser válida");
            }
        }
    }

    @Nested
    @DisplayName("Validação geral (hasErrors / gameIsFinished)")
    class OverallValidationTests {

        @Test
        @DisplayName("Board vazio sem erros → hasErrors = false")
        void emptyBoard_hasNoErrors() {
            assertFalse(emptyBoard().hasErrors());
        }

        @Test
        @DisplayName("Board não iniciado → hasErrors = false")
        void nonStartedBoard_hasNoErrors() {
            final var board = emptyBoard();
            board.changeValue(0, 0, 5);
            board.changeValue(0, 0, 5); // mesmo valor não gera erro se só tem 1 célula preenchida
            assertFalse(board.hasErrors());
        }

        @Test
        @DisplayName("Board resolvido → gameIsFinished = true")
        void solvedBoard_gameIsFinished() {
            assertTrue(solvedBoard().gameIsFinished());
        }

        @Test
        @DisplayName("Board resolvido → hasErrors = false")
        void solvedBoard_hasNoErrors() {
            assertFalse(solvedBoard().hasErrors());
        }

        @Test
        @DisplayName("Board completo com erro → gameIsFinished = false")
        void completeButWithErrors_notFinished() {
            final var board = solvedBoard();
            board.changeValue(0, 0, 3); // col0,row0 tinha 5 — conflito com col0,row1?
            // Só queremos verificar que não está finished, mesmo estando completo
            // Como sobrescrevemos com 3, e 3 está em col2,row0 — row0 tem duplicata
            assertFalse(board.gameIsFinished());
        }
    }

    @Nested
    @DisplayName("Manipulação de células (changeValue / clearValue)")
    class CellManipulationTests {

        @Test
        @DisplayName("changeValue em célula editável → true e valor alterado")
        void changeValue_editableCell_succeeds() {
            final var board = emptyBoard();
            assertTrue(board.changeValue(0, 0, 5));
            assertEquals(5, board.getSpaces().get(0).get(0).getActual());
        }

        @Test
        @DisplayName("changeValue em célula fixa → false e valor inalterado")
        void changeValue_fixedCell_returnsFalse() {
            final var spaces = new ArrayList<List<Space>>();
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                final var column = new ArrayList<Space>(Board.BOARD_SIZE);
                for (int row = 0; row < Board.BOARD_SIZE; row++) {
                    // Célula (0,0) = fixa com valor 1; as demais vazias
                    column.add(new Space(col == 0 && row == 0 ? 1 : 0, col == 0 && row == 0));
                }
                spaces.add(column);
            }
            final var board = new Board(spaces);
            assertFalse(board.changeValue(0, 0, 9));
            assertEquals(1, board.getSpaces().get(0).get(0).getActual());
        }

        @Test
        @DisplayName("clearValue em célula editável → limpa o valor")
        void clearValue_editableCell_clears() {
            final var board = emptyBoard();
            board.changeValue(0, 0, 5);
            assertTrue(board.clearValue(0, 0));
            assertNull(board.getSpaces().get(0).get(0).getActual());
        }

        @Test
        @DisplayName("clearValue em célula fixa → false e mantém valor")
        void clearValue_fixedCell_returnsFalse() {
            final var spaces = new ArrayList<List<Space>>();
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                final var column = new ArrayList<Space>(Board.BOARD_SIZE);
                for (int row = 0; row < Board.BOARD_SIZE; row++) {
                    column.add(new Space(col == 0 && row == 0 ? 1 : 0, col == 0 && row == 0));
                }
                spaces.add(column);
            }
            final var board = new Board(spaces);
            assertFalse(board.clearValue(0, 0));
            assertEquals(1, board.getSpaces().get(0).get(0).getActual());
        }

        @Test
        @DisplayName("reset zera células editáveis, mantém fixas")
        void reset_clearsNonFixed_keepsFixed() {
            final var spaces = new ArrayList<List<Space>>();
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                final var column = new ArrayList<Space>(Board.BOARD_SIZE);
                for (int row = 0; row < Board.BOARD_SIZE; row++) {
                    // Célula (0,0) = fixa; todas as outras editáveis
                    column.add(new Space(col == 0 && row == 0 ? 1 : 0, col == 0 && row == 0));
                }
                spaces.add(column);
            }
            final var board = new Board(spaces);
            // Preenche algumas células editáveis
            board.changeValue(1, 0, 5);
            board.changeValue(0, 1, 7);
            board.reset();
            // Fixa deve permanecer
            assertEquals(1, board.getSpaces().get(0).get(0).getActual());
            // Editáveis devem estar null
            assertNull(board.getSpaces().get(1).get(0).getActual());
            assertNull(board.getSpaces().get(0).get(1).getActual());
        }
    }
}