package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/** Broadcast by the server to both clients when the game ends. */
public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final char winner;
    private final boolean draw;
    private final int[] line;

    public GameResult(char winner, boolean draw, int[] line) {
        this.winner = winner;
        this.draw = draw;
        this.line = line;
    }

    public char getWinner() {
        return winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public int[] getLine() {
        return line;
    }
}
