package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.events.ConnectionErrorEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.GameOverEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.OpponentLeftEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.StartEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.UpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.WaitingEvent;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Commands;
import il.cshaifasweng.OCSFMediatorExample.entities.GameResult;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveInfo;
import il.cshaifasweng.OCSFMediatorExample.entities.StartInfo;

import java.io.IOException;

/**
 * The client side of OCSF for this app (a singleton). It turns each message
 * arriving from the server into an EventBus event, decoupling the network
 * thread from the JavaFX controllers (the publisher/subscriber "mediator" role
 * EventBus plays). Lifecycle events (waiting/start/error) are posted as sticky
 * so a controller that registers a moment later still receives the latest one.
 */
public class SimpleClient extends AbstractClient {

    private static SimpleClient client = null;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    public static SimpleClient getClient() {
        if (client == null) {
            client = new SimpleClient("localhost", 3000);
        }
        return client;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (!(msg instanceof Message)) {
            return;
        }
        Message message = (Message) msg;
        String command = message.getMessage();
        if (command == null) {
            return;
        }
        EventBus bus = EventBus.getDefault();
        switch (command) {
            case Commands.WAITING:
                bus.postSticky(new WaitingEvent());
                break;
            case Commands.START:
                bus.postSticky(new StartEvent((StartInfo) message.getObject()));
                break;
            case Commands.UPDATE:
                bus.post(new UpdateEvent((MoveInfo) message.getObject()));
                break;
            case Commands.GAME_OVER:
                bus.post(new GameOverEvent((GameResult) message.getObject()));
                break;
            case Commands.OPPONENT_LEFT:
                bus.post(new OpponentLeftEvent());
                break;
            case Commands.FULL:
                bus.postSticky(new ConnectionErrorEvent(
                        "A game is already in progress (two players are connected)."));
                break;
            case Commands.INVALID:
                // The server is authoritative; an illegal click simply has no effect.
                break;
            default:
                break;
        }
    }

    @Override
    protected void connectionException(Exception exception) {
        EventBus.getDefault().postSticky(
                new ConnectionErrorEvent("Connection to the server was lost: " + exception.getMessage()));
    }

    public void sendMove(int index) throws IOException {
        sendToServer(new Message(Commands.MOVE, Integer.valueOf(index)));
    }

    public void sendRematch() throws IOException {
        sendToServer(new Message(Commands.REMATCH, null));
    }
}
