package banking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private ComboBox<String> customerType;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField idNumber;
    @FXML private TextField companyName;
    @FXML private TextField registrationNumber;
    @FXML private TextField contactPerson;
    @FXML private TextField address;
    @FXML private TextField phone;
    @FXML private TextField registerUsername;
    @FXML private PasswordField registerPassword;
    @FXML private Label loginMessage;
    @FXML private Label registerMessage;

    private Stage stage;
    private CustomerRepository customers;
    private AccountRepository accounts;
    private TransactionRepository transactions;
    private AccountController accountController;

    @FXML
    private void initialize() {
        customerType.getItems().setAll("INDIVIDUAL", "COMPANY");
        customerType.setValue("INDIVIDUAL");
    }

    public void configure(Stage stage, CustomerRepository customers,
                          AccountRepository accounts,
                          TransactionRepository transactions,
                          AccountController accountController) {
        this.stage = stage;
        this.customers = customers;
        this.accounts = accounts;
        this.transactions = transactions;
        this.accountController = accountController;
    }

    @FXML
    private void login() {
        try {
            Optional<Customer> result = customers.login(
                    loginUsername.getText().trim(),
                    loginPassword.getText());

            if (result.isEmpty()) {
                loginMessage.setText("Username or password is incorrect.");
                return;
            }
            showDashboard(result.get());
        } catch (Exception e) {
            loginMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void register() {
        try {
            Customer customer;
            if ("INDIVIDUAL".equals(customerType.getValue())) {
                customer = customers.registerIndividual(
                        address.getText(), phone.getText(),
                        registerUsername.getText(), registerPassword.getText(),
                        firstName.getText(), lastName.getText(),
                        idNumber.getText());
            } else {
                customer = customers.registerCompany(
                        address.getText(), phone.getText(),
                        registerUsername.getText(), registerPassword.getText(),
                        companyName.getText(), registrationNumber.getText(),
                        contactPerson.getText());
            }
            showDashboard(customer);
        } catch (Exception e) {
            registerMessage.setText(e.getMessage());
        }
    }

    private void showDashboard(Customer customer) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/banking/views/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load());

        DashboardViewController controller = loader.getController();
        controller.configure(stage, customer, customers, accounts,
                transactions, accountController);

        stage.setScene(scene);
        stage.setTitle("Banking Dashboard");
    }
}