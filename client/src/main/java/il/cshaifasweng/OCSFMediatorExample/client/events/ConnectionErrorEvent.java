package il.cshaifasweng.OCSFMediatorExample.client.events;

/** Something went wrong with the connection (e.g. game full, connection lost). */
public class ConnectionErrorEvent {
    private final String message;

    public ConnectionErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
