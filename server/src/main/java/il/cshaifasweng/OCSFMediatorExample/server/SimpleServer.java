package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Commands;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

/**
 * The game server. Extends OCSF's {@link AbstractServer} and coordinates a
 * single two-player tic-tac-toe game: the first client waits for an opponent;
 * when two are present a {@link GameSession} randomly assigns X/O and a random
 * starter; moves are validated and broadcast to both players; a third client is
 * refused; if a player leaves, the other returns to the waiting room.
 * All callbacks are synchronized on the server, so events never interleave.
 */
public class SimpleServer extends AbstractServer {

    private ConnectionToClient waiting;
    private GameSession session;

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected synchronized void clientConnected(ConnectionToClient client) {
        log("client connected: " + describe(client));

        if (session != null && !session.isOver()) {
            send(client, new Message(Commands.FULL));
            try {
                client.close();
            } catch (Exception ignored) {
            }
            return;
        }

        if (waiting == null) {
            waiting = client;
            send(client, new Message(Commands.WAITING));
            log("waiting for a second player...");
        } else {
            session = new GameSession(waiting, client);
            waiting = null;
            session.start();
            log("two players connected; game started.");
        }
    }

    @Override
    protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (!(msg instanceof Message)) {
            return;
        }
        Message message = (Message) msg;
        String request = message.getMessage();
        if (request == null) {
            return;
        }

        if (Commands.MOVE.equals(request)) {
            if (session == null) {
                return;
            }
            int index = (message.getObject() instanceof Integer) ? (Integer) message.getObject() : -1;
            session.handleMove(client, index);
        } else if (Commands.REMATCH.equals(request)) {
            if (session == null) {
                return;
            }
            session.requestRematch(client);
        }
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        log("client disconnected");
        handleDeparture(client);
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        handleDeparture(client);
    }

    private void handleDeparture(ConnectionToClient client) {
        if (client == waiting) {
            waiting = null;
            return;
        }
        if (session != null && session.contains(client)) {
            ConnectionToClient other = session.opponentOf(client);
            session = null;
            if (other != null) {
                send(other, new Message(Commands.OPPONENT_LEFT));
                waiting = other;
                send(other, new Message(Commands.WAITING));
            }
        }
    }

    static void send(ConnectionToClient client, Message message) {
        try {
            client.sendToClient(message);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void serverStarted() {
        log("server started on port " + getPort());
    }

    @Override
    protected void serverStopped() {
        log("server stopped listening");
    }

    private static String describe(ConnectionToClient client) {
        return client.getInetAddress() == null ? "(unknown)" : client.getInetAddress().toString();
    }

    private void log(String text) {
        System.out.println("[server] " + text);
    }
}
