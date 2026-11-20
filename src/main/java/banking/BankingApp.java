package banking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankingApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.createTables();

        CustomerRepository customers = new CustomerRepository();
        AccountRepository accounts = new AccountRepository();
        TransactionRepository transactions = new TransactionRepository();
        AccountController accountController =
                new AccountController(accounts, transactions);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/banking/views/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.configure(stage, customers, accounts,
                transactions, accountController);

        stage.setTitle("My Bank");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}