package server;

import java.util.Random;
import java.util.stream.IntStream;

public class TresEnRaya {
    private int activePlayer = 0; // 0 - X, 1 - O
    private int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2}; // 0 - X, 1 - O, 2 - Null
    private int[][] winPositions = {{0, 1, 2},
                                    {3, 4, 5},
                                    {6, 7, 8},
                                    {0, 3, 6},
                                    {1, 4, 7},
                                    {2, 5, 8},
                                    {0, 4, 8},
                                    {2, 4, 6}};
    private int counter = 0;

    public String makeMove(int position) {
        if (gameState[position] != 2) {
            return "Invalid move";
        }

        counter++;
        gameState[position] = activePlayer;

        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] && gameState[winPosition[1]] == gameState[winPosition[2]] && gameState[winPosition[0]] != 2) {
                String winner = (gameState[winPosition[0]] == 0) ? "X" : "O";
                return "Winner: " + winner;
            }
        }

        if (counter == 9) {
            return "Draw";
        }

        activePlayer = (activePlayer == 0) ? 1 : 0;
        return (activePlayer == 0) ? "X's turn" : "O's turn";
    }

    public String serverMove() {
        Random random = new Random();
        // Buscar posiciones disponibles
        int[] availablePositions = IntStream.range(0, gameState.length).filter(i -> gameState[i] == 2).toArray();

        // Si no hay posiciones disponibles, el juego ha terminado
        if (availablePositions.length == 0) {
            return "No moves left";
        }

        // Elegir una posición aleatoria disponible para el movimiento del servidor
        int serverMove = availablePositions[random.nextInt(availablePositions.length)];
        gameState[serverMove] = activePlayer;
        counter++;

        // Comprobar si el servidor ha ganado con este movimiento
        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] && gameState[winPosition[1]] == gameState[winPosition[2]] && gameState[winPosition[0]] != 2) {
                return "Winner: Server";
            }
        }

        // Si el servidor no ganó y no hay más movimientos, es un empate
        if (counter == 9) {
            return "Draw";
        }

        // Cambiar turno al cliente
        activePlayer = (activePlayer == 0) ? 1 : 0;
        return "Server moved " + serverMove + ". Your turn";
    }

    public boolean isGameOver() {
        // Revisar si algún jugador ha ganado
        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] && gameState[winPosition[1]] == gameState[winPosition[2]] && gameState[winPosition[0]] != 2) {
                return true;
            }
        }

        // Revisar si no hay más movimientos posibles
        if (counter == 9) {
            return true;
        }

        // En caso contrario, el juego no ha terminado
        return false;
    }
}
