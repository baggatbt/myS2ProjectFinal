package Model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Starts the application

 * @author Brandon Baggatta
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle resources = ResourceBundle.getBundle("resources.login_en");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/View/loginForm.fxml"));
        fxmlLoader.setResources(resources);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("S2 Project");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // close the database connection
        JDBC.closeConnection();
    }



    public static void main(String[] args) {
        JDBC.makeConnection();
        launch();

    }
}