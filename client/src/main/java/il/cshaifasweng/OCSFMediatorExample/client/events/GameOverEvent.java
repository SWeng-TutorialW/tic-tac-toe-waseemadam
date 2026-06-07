package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.GameResult;

/** The game ended; carries the result (winner / draw / winning line). */
public class GameOverEvent {
    private final GameResult result;

    public GameOverEvent(GameResult result) {
        this.result = result;
    }

    public GameResult getResult() {
        return result;
    }
}
