package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Pure 3x3 tic-tac-toe board logic (indices 0..8, row-major). No networking,
 * so it is the single source of truth on the server and easy to test.
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final char EMPTY = ' ';
    public static final char X = 'X';
    public static final char O = 'O';

    private static final int[][] LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    private final char[] cells = new char[9];
    private char turn = X;

    public Board() {
        reset(X);
    }

    public void reset(char startingTurn) {
        Arrays.fill(cells, EMPTY);
        this.turn = startingTurn;
    }

    public char getTurn() {
        return turn;
    }

    public void setTurn(char turn) {
        this.turn = turn;
    }

    public char cellAt(int index) {
        return cells[index];
    }

    public boolean isValidMove(int index) {
        return index >= 0 && index < 9 && cells[index] == EMPTY;
    }

    public boolean place(int index, char mark) {
        if (!isValidMove(index)) {
            return false;
        }
        cells[index] = mark;
        return true;
    }

    public boolean isFull() {
        for (char c : cells) {
            if (c == EMPTY) {
                return false;
            }
        }
        return true;
    }

    public char winner() {
        for (int[] line : LINES) {
            char a = cells[line[0]];
            if (a != EMPTY && a == cells[line[1]] && a == cells[line[2]]) {
                return a;
            }
        }
        return EMPTY;
    }

    public int[] winningLine() {
        for (int[] line : LINES) {
            char a = cells[line[0]];
            if (a != EMPTY && a == cells[line[1]] && a == cells[line[2]]) {
                return new int[]{line[0], line[1], line[2]};
            }
        }
        return null;
    }

    public boolean isGameOver() {
        return winner() != EMPTY || isFull();
    }
}
