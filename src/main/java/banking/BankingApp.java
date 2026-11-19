package banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankingApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database database = new Database();
        database.addSampleCustomer();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/banking/login-view.fxml"));

        Scene scene = new Scene(loader.load(), 900, 600);

        LoginController controller = loader.getController();
        controller.setDatabase(database);
        controller.setStage(stage);

        stage.setTitle("My Bank");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}