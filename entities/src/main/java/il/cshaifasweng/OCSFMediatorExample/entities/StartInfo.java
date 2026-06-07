package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/** Sent by the server to each client when a game starts. */
public class StartInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final char yourMark;
    private final boolean yourTurn;

    public StartInfo(char yourMark, boolean yourTurn) {
        this.yourMark = yourMark;
        this.yourTurn = yourTurn;
    }

    public char getYourMark() {
        return yourMark;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }
}
