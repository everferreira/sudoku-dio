package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;
import br.com.dio.util.BoardConfig;

/**
 * Versão com INTERFACE GRÁFICA (Swing) do jogo de Sudoku — Desafio da Formação
 * Java (DIO). Usa a mesma configuração de tabuleiro da versão de terminal.
 *
 * <p><b>Exemplo de execução:</b>
 * {@code java br.com.dio.UIMain 0,0;4,false 1,0;7,false ...}</p>
 */
public class UIMain {

    public static void main(final String[] args) {
        // 1. Converte "col,row;value,fixed" em um mapa (ou usa o tabuleiro padrão)
        final var gameConfig = BoardConfig.parse(args);

        // 2. Constrói e exibe a janela do jogo
        final var mainScreen = new MainScreen(gameConfig);
        mainScreen.buildMainScreen();
    }
}