package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.ConnectionErrorEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.GameOverEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.OpponentLeftEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.StartEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.UpdateEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.WaitingEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Board;
import il.cshaifasweng.OCSFMediatorExample.entities.GameResult;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveInfo;
import il.cshaifasweng.OCSFMediatorExample.entities.StartInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Controller for the game screen. Registers with EventBus and reacts to the
 * events posted by {@link SimpleClient}. All UI mutations run via
 * {@link Platform#runLater} because EventBus delivers events on the OCSF network
 * thread, not the JavaFX application thread.
 */
public class GameController {

    @FXML private Button b0;
    @FXML private Button b1;
    @FXML private Button b2;
    @FXML private Button b3;
    @FXML private Button b4;
    @FXML private Button b5;
    @FXML private Button b6;
    @FXML private Button b7;
    @FXML private Button b8;
    @FXML private Label statusLabel;
    @FXML private Label markLabel;
    @FXML private Button rematchButton;

    private final Button[] cells = new Button[9];
    private char myMark = Board.EMPTY;
    private boolean myTurn = false;
    private boolean gameOver = false;

    @FXML
    private void initialize() {
        cells[0] = b0; cells[1] = b1; cells[2] = b2;
        cells[3] = b3; cells[4] = b4; cells[5] = b5;
        cells[6] = b6; cells[7] = b7; cells[8] = b8;
        for (int i = 0; i < 9; i++) {
            final int index = i;
            cells[i].setText("");
            cells[i].setOnAction(event -> onCellClicked(index));
            cells[i].setDisable(true);
        }
        hideRematch();
        markLabel.setText("");
        statusLabel.setText("Connecting...");
        EventBus.getDefault().register(this);
    }

    private void onCellClicked(int index) {
        if (gameOver || !myTurn) {
            return;
        }
        if (!cells[index].getText().isEmpty()) {
            return;
        }
        myTurn = false;
        setBoardEnabled(false);
        statusLabel.setText("Waiting for the server...");
        try {
            SimpleClient.getClient().sendMove(index);
        } catch (Exception ex) {
            statusLabel.setText("Could not send move: " + ex.getMessage());
        }
    }

    @Subscribe(sticky = true)
    public void onWaiting(WaitingEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        Platform.runLater(() -> {
            gameOver = false;
            myTurn = false;
            resetBoardUI();
            markLabel.setText("");
            statusLabel.setText("Waiting for an opponent to join...");
            hideRematch();
        });
    }

    @Subscribe(sticky = true)
    public void onStart(StartEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        final StartInfo info = event.getInfo();
        Platform.runLater(() -> {
            myMark = info.getYourMark();
            myTurn = info.isYourTurn();
            gameOver = false;
            resetBoardUI();
            markLabel.setText("You are \"" + myMark + "\"");
            hideRematch();
            updateTurnStatus();
        });
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        final MoveInfo move = event.getInfo();
        Platform.runLater(() -> {
            Button cell = cells[move.getIndex()];
            cell.setText(String.valueOf(move.getMark()));
            cell.getStyleClass().removeAll("x-mark", "o-mark");
            cell.getStyleClass().add(move.getMark() == Board.X ? "x-mark" : "o-mark");
            cell.setDisable(true);
            myTurn = (move.getNextTurn() == myMark);
            if (!gameOver) {
                updateTurnStatus();
            }
        });
    }

    @Subscribe
    public void onGameOver(GameOverEvent event) {
        final GameResult result = event.getResult();
        Platform.runLater(() -> {
            gameOver = true;
            myTurn = false;
            setBoardEnabled(false);
            if (result.isDraw()) {
                statusLabel.setText("It's a draw!");
            } else if (result.getWinner() == myMark) {
                statusLabel.setText("You win!");
            } else {
                statusLabel.setText("You lose.");
            }
            if (result.getLine() != null) {
                for (int idx : result.getLine()) {
                    cells[idx].getStyleClass().add("win-cell");
                }
            }
            showRematch();
        });
    }

    @Subscribe
    public void onOpponentLeft(OpponentLeftEvent event) {
        Platform.runLater(() -> {
            gameOver = true;
            myTurn = false;
            setBoardEnabled(false);
            statusLabel.setText("Your opponent left. Waiting for a new opponent...");
            hideRematch();
        });
    }

    @Subscribe(sticky = true)
    public void onConnectionError(ConnectionErrorEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        Platform.runLater(() -> {
            gameOver = true;
            myTurn = false;
            setBoardEnabled(false);
            statusLabel.setText(event.getMessage());
            hideRematch();
        });
    }

    @FXML
    private void onRematch() {
        try {
            SimpleClient.getClient().sendRematch();
            statusLabel.setText("Waiting for your opponent to accept the rematch...");
            rematchButton.setDisable(true);
        } catch (Exception ex) {
            statusLabel.setText("Could not request a rematch: " + ex.getMessage());
        }
    }

    private void updateTurnStatus() {
        statusLabel.setText(myTurn ? "Your turn (" + myMark + ")" : "Opponent's turn...");
        setBoardEnabled(myTurn);
    }

    private void setBoardEnabled(boolean enabled) {
        for (Button cell : cells) {
            cell.setDisable(!enabled || !cell.getText().isEmpty());
        }
    }

    private void resetBoardUI() {
        for (Button cell : cells) {
            cell.setText("");
            cell.getStyleClass().removeAll("x-mark", "o-mark", "win-cell");
            cell.setDisable(true);
        }
    }

    private void showRematch() {
        rematchButton.setDisable(false);
        rematchButton.setVisible(true);
        rematchButton.setManaged(true);
    }

    private void hideRematch() {
        rematchButton.setVisible(false);
        rematchButton.setManaged(false);
    }
}
