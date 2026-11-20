package banking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class DashboardViewController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalAccountsLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label message;
    @FXML private Label openAccountMessage;
    @FXML private Label transactionMessage;

    @FXML private ComboBox<String> accountType;
    @FXML private TextField openingBalance;
    @FXML private TextField branch;
    @FXML private TextField employerName;
    @FXML private TextField employerAddress;
    @FXML private ListView<Account> accountList;

    @FXML private ComboBox<String> transactionAccount;
    @FXML private ComboBox<String> transactionType;
    @FXML private TextField transactionAmount;
    @FXML private ComboBox<String> historyAccount;
    @FXML private TextArea historyText;

    @FXML private Pane dashboardPane;
    @FXML private Pane accountPane;
    @FXML private Pane openAccountPane;
    @FXML private Pane transactionsPane;
    @FXML private Pane historyPane;

    private Stage stage;
    private Customer customer;
    private CustomerRepository customers;
    private AccountRepository accounts;
    private TransactionRepository transactions;
    private AccountController accountController;

    @FXML
    private void initialize() {
        accountType.getItems().setAll("SAVINGS", "INVESTMENT", "CHEQUE");
        accountType.setValue("SAVINGS");
        transactionType.getItems().setAll("DEPOSIT", "WITHDRAWAL");
        transactionType.setValue("DEPOSIT");
    }

    public void configure(Stage stage, Customer customer,
                          CustomerRepository customers,
                          AccountRepository accounts,
                          TransactionRepository transactions,
                          AccountController accountController) {
        this.stage = stage;
        this.customer = customer;
        this.customers = customers;
        this.accounts = accounts;
        this.transactions = transactions;
        this.accountController = accountController;

        welcomeLabel.setText("WELCOME BACK, " + customer.getDisplayName());
        refreshAccounts();
        showDashboard();
    }

    private void showOnly(Pane selected) {
        for (Pane pane : List.of(dashboardPane, accountPane,
                openAccountPane, transactionsPane, historyPane)) {
            pane.setVisible(pane == selected);
            pane.setManaged(pane == selected);
        }
        message.setText("");
    }

    @FXML private void showDashboard() { showOnly(dashboardPane); }
    @FXML private void showMyAccount() {
        refreshAccounts();
        showOnly(accountPane);
    }
    @FXML private void showOpenAccount() { showOnly(openAccountPane); }
    @FXML private void showTransactions() {
        refreshAccounts();
        showOnly(transactionsPane);
    }
    @FXML private void showTransactionHistory() {
        refreshAccounts();
        showOnly(historyPane);
    }

    @FXML
    private void openAccount() {
        try {
            accountController.openAccount(customer, accountType.getValue(),
                    Double.parseDouble(openingBalance.getText()),
                    branch.getText(), employerName.getText(),
                    employerAddress.getText());
            openAccountMessage.setText("Account opened successfully.");
            refreshAccounts();
        } catch (Exception e) {
            openAccountMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void submitTransaction() {
        try {
            String selected = transactionAccount.getValue();
            if (selected == null) {
                throw new IllegalArgumentException("Choose an account.");
            }
            String number = selected.substring(0, selected.indexOf(" | "));
            double amount = Double.parseDouble(transactionAmount.getText());

            if ("DEPOSIT".equals(transactionType.getValue())) {
                accountController.deposit(customer, number, amount);
            } else {
                accountController.withdraw(customer, number, amount);
            }
            transactionMessage.setText("Transaction completed.");
            refreshAccounts();
        } catch (Exception e) {
            transactionMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void loadHistory() {
        try {
            String selected = historyAccount.getValue();
            if (selected == null) {
                throw new IllegalArgumentException("Choose an account.");
            }
            String number = selected.substring(0, selected.indexOf(" | "));
            List<TransactionRepository.TransactionRow> rows =
                    accountController.history(number);

            StringBuilder text = new StringBuilder();
            for (var row : rows) {
                text.append(row.date).append(" | ").append(row.type)
                    .append(" | P").append(String.format("%.2f", row.amount))
                    .append(" | ").append(row.description)
                    .append(System.lineSeparator());
            }
            historyText.setText(rows.isEmpty()
                    ? "No transactions yet." : text.toString());
        } catch (Exception e) {
            historyText.setText(e.getMessage());
        }
    }

    @FXML
    private void signout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/banking/views/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();
            controller.configure(stage, customers, accounts,
                    transactions, accountController);
            stage.setScene(scene);
            stage.setTitle("Banking System Login");
        } catch (Exception e) {
            message.setText(e.getMessage());
        }
    }

    private void refreshAccounts() {
        List<Account> list = accountController.accountsFor(customer);
        accountList.getItems().setAll(list);

        totalAccountsLabel.setText("TOTAL ACCOUNTS: " + list.size());
        double total = list.stream().mapToDouble(Account::getBalance).sum();
        totalBalanceLabel.setText(
                "TOTAL BALANCE: P" + String.format("%.2f", total));

        List<String> choices = list.stream()
                .map(a -> a.getAccountNO() + " | " + a.getAccountType())
                .toList();
        transactionAccount.getItems().setAll(choices);
        historyAccount.getItems().setAll(choices);
    }
}