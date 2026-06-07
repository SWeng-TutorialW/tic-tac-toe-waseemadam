package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the connect screen. Lets the user type the server host/IP and
 * port (needed for the two-machine demo), opens the OCSF connection and then
 * switches to the game screen.
 */
public class PrimaryController {

    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private Button connectButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        hostField.setText("127.0.0.1");
        portField.setText("3000");
        statusLabel.setText("");
    }

    @FXML
    private void onConnect() {
        String host = hostField.getText() == null ? "" : hostField.getText().trim();
        String portText = portField.getText() == null ? "" : portField.getText().trim();

        if (host.isEmpty()) {
            statusLabel.setText("Please enter the server host or IP.");
            return;
        }
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Port must be a whole number.");
            return;
        }

        SimpleClient client = SimpleClient.getClient();
        client.setHost(host);
        client.setPort(port);

        connectButton.setDisable(true);
        statusLabel.setText("Connecting to " + host + ":" + port + " ...");

        try {
            if (!client.isConnected()) {
                client.openConnection();
            }
            App.setRoot("game");
        } catch (IOException ex) {
            statusLabel.setText("Could not connect: " + ex.getMessage());
            connectButton.setDisable(false);
        }
    }
}
