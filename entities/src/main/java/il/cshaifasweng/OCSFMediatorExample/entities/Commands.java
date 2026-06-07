package il.cshaifasweng.OCSFMediatorExample.entities;

/**
 * Protocol tokens shared by client and server (the "language" the two sides speak).
 */
public final class Commands {

    private Commands() {
    }

    // ---- Client -> Server ----
    /** Player attempts a move. Payload: Integer cell index 0..8. */
    public static final String MOVE = "move";
    /** Player asks to play another round after the game ended. No payload. */
    public static final String REMATCH = "rematch";

    // ---- Server -> Client ----
    /** You are the only player; wait for an opponent. No payload. */
    public static final String WAITING = "waiting";
    /** Game is starting. Payload: StartInfo. */
    public static final String START = "start";
    /** A move was applied. Payload: MoveInfo. */
    public static final String UPDATE = "update";
    /** Game finished. Payload: GameResult. */
    public static final String GAME_OVER = "gameOver";
    /** The opponent disconnected. No payload. */
    public static final String OPPONENT_LEFT = "opponentLeft";
    /** A game is already in progress; this client is refused. No payload. */
    public static final String FULL = "full";
    /** The attempted move was rejected. Payload: String reason. */
    public static final String INVALID = "invalid";
}
