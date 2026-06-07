package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/** Broadcast by the server to both clients after a valid move is applied. */
public class MoveInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int index;
    private final char mark;
    private final char nextTurn;

    public MoveInfo(int index, char mark, char nextTurn) {
        this.index = index;
        this.mark = mark;
        this.nextTurn = nextTurn;
    }

    public int getIndex() {
        return index;
    }

    public char getMark() {
        return mark;
    }

    public char getNextTurn() {
        return nextTurn;
    }
}
