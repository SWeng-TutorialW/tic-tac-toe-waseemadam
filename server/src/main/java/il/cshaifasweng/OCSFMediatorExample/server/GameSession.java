package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Board;
import il.cshaifasweng.OCSFMediatorExample.entities.Commands;
import il.cshaifasweng.OCSFMediatorExample.entities.GameResult;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveInfo;
import il.cshaifasweng.OCSFMediatorExample.entities.StartInfo;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.util.Random;

/**
 * One game between two players. Holds the authoritative {@link Board}, knows
 * which connection is X and which is O, validates incoming moves and broadcasts
 * the result to both players. Always used under the {@link SimpleServer} monitor.
 */
public class GameSession {

    private static final Random RANDOM = new Random();

    private final ConnectionToClient playerX;
    private final ConnectionToClient playerO;
    private final Board board = new Board();

    private boolean over = false;
    private boolean rematchX = false;
    private boolean rematchO = false;

    public GameSession(ConnectionToClient a, ConnectionToClient b) {
        if (RANDOM.nextBoolean()) {
            playerX = a;
            playerO = b;
        } else {
            playerX = b;
            playerO = a;
        }
        playerX.setInfo("mark", Board.X);
        playerO.setInfo("mark", Board.O);
    }

    /** Picks a random starter, resets the board and notifies both players. */
    public void start() {
        over = false;
        rematchX = false;
        rematchO = false;

        char starter = RANDOM.nextBoolean() ? Board.X : Board.O;
        board.reset(starter);

        SimpleServer.send(playerX, new Message(Commands.START, new StartInfo(Board.X, starter == Board.X)));
        SimpleServer.send(playerO, new Message(Commands.START, new StartInfo(Board.O, starter == Board.O)));
    }

    public boolean contains(ConnectionToClient client) {
        return client == playerX || client == playerO;
    }

    public ConnectionToClient opponentOf(ConnectionToClient client) {
        if (client == playerX) {
            return playerO;
        }
        if (client == playerO) {
            return playerX;
        }
        return null;
    }

    public boolean isOver() {
        return over;
    }

    public void handleMove(ConnectionToClient client, int index) {
        if (over) {
            return;
        }
        char mark = markOf(client);
        if (mark == Board.EMPTY) {
            return;
        }
        if (mark != board.getTurn()) {
            SimpleServer.send(client, new Message(Commands.INVALID, "Not your turn"));
            return;
        }
        if (!board.isValidMove(index)) {
            SimpleServer.send(client, new Message(Commands.INVALID, "Illegal move"));
            return;
        }

        board.place(index, mark);

        char winner = board.winner();
        boolean draw = (winner == Board.EMPTY) && board.isFull();
        char nextTurn = (mark == Board.X) ? Board.O : Board.X;
        if (winner == Board.EMPTY && !draw) {
            board.setTurn(nextTurn);
        }

        broadcast(new Message(Commands.UPDATE, new MoveInfo(index, mark, nextTurn)));

        if (winner != Board.EMPTY) {
            over = true;
            broadcast(new Message(Commands.GAME_OVER, new GameResult(winner, false, board.winningLine())));
        } else if (draw) {
            over = true;
            broadcast(new Message(Commands.GAME_OVER, new GameResult(Board.EMPTY, true, null)));
        }
    }

    public void requestRematch(ConnectionToClient client) {
        if (!over) {
            return;
        }
        char mark = markOf(client);
        if (mark == Board.X) {
            rematchX = true;
        } else if (mark == Board.O) {
            rematchO = true;
        }
        if (rematchX && rematchO) {
            start();
        }
    }

    private char markOf(ConnectionToClient client) {
        Object info = client.getInfo("mark");
        return (info instanceof Character) ? (Character) info : Board.EMPTY;
    }

    private void broadcast(Message message) {
        SimpleServer.send(playerX, message);
        SimpleServer.send(playerO, message);
    }
}
