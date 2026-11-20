package banking;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Comparator;
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

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> accountNumberColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, Double> accountBalanceColumn;
    @FXML private TableColumn<Account, String> accountStatusColumn;

    @FXML private TableView<RecentTransaction> recentTable;
    @FXML private TableColumn<RecentTransaction, String> recentDateColumn;
    @FXML private TableColumn<RecentTransaction, String> recentTypeColumn;
    @FXML private TableColumn<RecentTransaction, String> recentAccountColumn;
    @FXML private TableColumn<RecentTransaction, Double> recentAmountColumn;

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

        configureAccountTable();
        configureRecentTable();
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

        welcomeLabel.setText("Welcome back, " + customer.getDisplayName());
        showDashboard();
    }

    private void configureAccountTable() {
        accountNumberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountNO()));

        accountTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAccountType()));

        accountBalanceColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getBalance()));

        accountStatusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper("Active"));

        accountBalanceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null
                        ? null
                        : String.format("P %.2f", value));
            }
        });
    }

    private void configureRecentTable() {
        recentDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().date));

        recentTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().type));

        recentAccountColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().account));

        recentAmountColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().amount));

        recentAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null
                        ? null
                        : String.format("P %.2f", value));
            }
        });
    }

    private void showOnly(Pane selected) {
        for (Pane pane : List.of(
                dashboardPane,
                accountPane,
                openAccountPane,
                transactionsPane,
                historyPane)) {
            boolean active = pane == selected;
            pane.setVisible(active);
            pane.setManaged(active);
        }

        message.setText("");
    }

    @FXML
    private void showDashboard() {
        refreshAccounts();
        showOnly(dashboardPane);
    }

    @FXML
    private void showMyAccount() {
        refreshAccounts();
        showOnly(accountPane);
    }

    @FXML
    private void showOpenAccount() {
        showOnly(openAccountPane);
    }

    @FXML
    private void showTransactions() {
        refreshAccounts();
        showOnly(transactionsPane);
    }

    @FXML
    private void showTransactionHistory() {
        refreshAccounts();
        showOnly(historyPane);
    }

    @FXML
    private void openAccount() {
        try {
            accountController.openAccount(
                    customer,
                    accountType.getValue(),
                    Double.parseDouble(openingBalance.getText()),
                    branch.getText(),
                    employerName.getText(),
                    employerAddress.getText()
            );

            openAccountMessage.setText("Account opened successfully.");
            openingBalance.clear();
            refreshAccounts();
        } catch (Exception e) {
            openAccountMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void submitTransaction() {
        try {
            String selectedAccount = transactionAccount.getValue();

            if (selectedAccount == null) {
                throw new IllegalArgumentException("Choose an account.");
            }

            String number = selectedAccount.substring(
                    0, selectedAccount.indexOf(" | "));
            double amount = Double.parseDouble(transactionAmount.getText());

            if ("DEPOSIT".equals(transactionType.getValue())) {
                accountController.deposit(customer, number, amount);
            } else {
                accountController.withdraw(customer, number, amount);
            }

            transactionMessage.setText("Transaction completed.");
            transactionAmount.clear();
            refreshAccounts();
        } catch (Exception e) {
            transactionMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void loadHistory() {
        try {
            String selectedAccount = historyAccount.getValue();

            if (selectedAccount == null) {
                throw new IllegalArgumentException("Choose an account.");
            }

            String number = selectedAccount.substring(
                    0, selectedAccount.indexOf(" | "));

            List<TransactionRepository.TransactionRow> rows =
                    accountController.history(number);

            StringBuilder result = new StringBuilder();

            for (var row : rows) {
                result.append(row.date)
                        .append(" | ")
                        .append(row.type)
                        .append(" | P")
                        .append(String.format("%.2f", row.amount))
                        .append(" | ")
                        .append(row.description)
                        .append(System.lineSeparator());
            }

            historyText.setText(rows.isEmpty()
                    ? "No transactions yet."
                    : result.toString());
        } catch (Exception e) {
            historyText.setText(e.getMessage());
        }
    }

    @FXML
    private void signout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/banking/views/login-view.fxml"));

            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();

            controller.configure(
                    stage, customers, accounts, transactions,
                    accountController);

            stage.setScene(scene);
            stage.setTitle("Banking System Login");
        } catch (Exception e) {
            message.setText(e.getMessage());
        }
    }

    private void refreshAccounts() {
        List<Account> list = accountController.accountsFor(customer);

        accountTable.getItems().setAll(list);

        totalAccountsLabel.setText(String.valueOf(list.size()));

        double totalBalance = list.stream()
                .mapToDouble(Account::getBalance)
                .sum();

        totalBalanceLabel.setText(
                String.format("P %.2f", totalBalance));

        List<String> accountChoices = list.stream()
                .map(account -> account.getAccountNO()
                        + " | " + account.getAccountType())
                .toList();

        transactionAccount.getItems().setAll(accountChoices);
        historyAccount.getItems().setAll(accountChoices);

        List<RecentTransaction> recent = new ArrayList<>();

        for (Account account : list) {
            List<TransactionRepository.TransactionRow> rows =
                    accountController.history(account.getAccountNO());

            for (var row : rows) {
                recent.add(new RecentTransaction(
                        row.date,
                        row.type,
                        account.getAccountNO(),
                        row.amount));
            }
        }

        recent.sort(
                Comparator.comparing(
                        (RecentTransaction row) -> row.date).reversed());

        recentTable.getItems().setAll(
                recent.stream().limit(5).toList());
    }

    private static final class RecentTransaction {
        private final String date;
        private final String type;
        private final String account;
        private final double amount;

        private RecentTransaction(
                String date, String type, String account, double amount) {
            this.date = date;
            this.type = type;
            this.account = account;
            this.amount = amount;
        }
    }
}