package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX entry point. Shows the connect screen first; {@link #setRoot(String)}
 * swaps the scene's root to the game screen once the user connects.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 360, 470);
        stage.setTitle("Tic-Tac-Toe (OCSF + EventBus)");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        try {
            SimpleClient client = SimpleClient.getClient();
            if (client.isConnected()) {
                client.closeConnection();
            }
        } catch (Exception ignored) {
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
