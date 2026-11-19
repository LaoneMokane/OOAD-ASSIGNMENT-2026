package banking;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginViewController {
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private ComboBox<String> customerType;

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField address;
    @FXML private TextField phone;
    @FXML private TextField registerUsername;
    @FXML private PasswordField registerPassword;
    @FXML private Label message;

    @FXML
    public void initialize() {
        customerType.getItems().addAll("INDIVIDUAL", "COMPANY");
        customerType.setValue("INDIVIDUAL");
    }

    @FXML
    private void login() {
        try {
            Optional<Customer> result =
                AppContext.getLoginController().login(
                    loginUsername.getText(),
                    loginPassword.getText()
                );

            if (result.isPresent()) {
                AppContext.setCustomer(result.get());
                SceneManager.show(
                    "dashboard-view.fxml", "Banking Dashboard"
                );
            } else {
                message.setText("Username or password is incorrect.");
            }
        } catch (Exception e) {
            message.setText(e.getMessage());
        }
    }

    @FXML
    private void register() {
        try {
            Customer customer;

            if ("INDIVIDUAL".equals(customerType.getValue())) {
                customer =
                    AppContext.getLoginController().registerIndividual(
                        address.getText(), phone.getText(),
                        registerUsername.getText(),
                        registerPassword.getText(),
                        firstName.getText(),
                        lastName.getText()
                    );
            } else {
                customer =
                    AppContext.getLoginController().registerCompany(
                        address.getText(), phone.getText(),
                        registerUsername.getText(),
                        registerPassword.getText(),
                        firstName.getText(),
                        lastName.getText()
                    );
            }

            AppContext.setCustomer(customer);
            SceneManager.show(
                "dashboard-view.fxml", "Banking Dashboard"
            );
        } catch (Exception e) {
            message.setText(e.getMessage());
        }
    }
}