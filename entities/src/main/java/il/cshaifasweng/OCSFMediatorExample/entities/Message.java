package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/**
 * Generic message exchanged between client and server over OCSF.
 * {@code message} carries the command (see {@link Commands}); {@code object}
 * carries an optional serializable payload (e.g. {@link StartInfo},
 * {@link MoveInfo}, {@link GameResult} or an Integer move index).
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private Object object;

    public Message() {
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Message{message='" + message + "', object=" + object + '}';
    }
}
