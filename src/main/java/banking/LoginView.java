package banking;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginView extends Application {
    @Override
    public void start(Stage stage) {
        Database.createTables();
        AppContext.start(stage);
        SceneManager.show("login-view.fxml", "Banking System Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}