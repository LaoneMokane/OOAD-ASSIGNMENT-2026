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

    @FXML private Label accountTotalAccountsLabel;
    @FXML private Label accountTotalBalanceLabel;

    @FXML private Label openAccountMessage;
    @FXML private Label transactionMessage;
    @FXML private Label historyMessage;

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
    @FXML private TextField transactionDescription;

    @FXML private ComboBox<String> historyAccount;
    @FXML private TableView<TransactionRepository.TransactionRow> historyTable;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyDateColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyTypeColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, Double> historyAmountColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyDescriptionColumn;

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
        accountType.getItems().setAll(
                "SAVINGS", "INVESTMENT", "CHEQUE"
        );
        accountType.setValue("SAVINGS");

        transactionType.getItems().setAll(
                "DEPOSIT", "WITHDRAWAL"
        );
        transactionType.setValue("DEPOSIT");

        configureAccountTable();
        configureRecentTable();
        configureHistoryTable();
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

        welcomeLabel.setText(
                "Welcome back, " + customer.getDisplayName()
        );

        showDashboard();
    }

    private void configureAccountTable() {
        accountNumberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getAccountNO()
                )
        );

        accountTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getAccountType()
                )
        );

        accountBalanceColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(
                        cell.getValue().getBalance()
                )
        );

        accountStatusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper("Active")
        );

        accountBalanceColumn.setCellFactory(column -> moneyCell());
    }

    private void configureRecentTable() {
        recentDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().date)
        );

        recentTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().type)
        );

        recentAccountColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().account)
        );

        recentAmountColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().amount)
        );

        recentAmountColumn.setCellFactory(column -> moneyCell());
    }

    private void configureHistoryTable() {
        historyDateColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().date)
        );

        historyTypeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().type)
        );

        historyAmountColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().amount)
        );

        historyDescriptionColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().description)
        );

        historyAmountColumn.setCellFactory(column -> moneyCell());
    }

    private <T> TableCell<T, Double> moneyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                setText(empty || value == null
                        ? null
                        : String.format("P %.2f", value));
            }
        };
    }

    private void showOnly(Pane selected) {
        for (Pane pane : List.of(
                dashboardPane,
                accountPane,
                openAccountPane,
                transactionsPane,
                historyPane
        )) {
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
            String selected = transactionAccount.getValue();

            if (selected == null) {
                throw new IllegalArgumentException("Choose an account.");
            }

            String number = selected.substring(
                    0, selected.indexOf(" | ")
            );

            double amount =
                    Double.parseDouble(transactionAmount.getText());

            String description = transactionDescription.getText();

            if ("DEPOSIT".equals(transactionType.getValue())) {
                accountController.deposit(
                        customer, number, amount, description
                );
            } else {
                accountController.withdraw(
                        customer, number, amount, description
                );
            }

            transactionMessage.setText("Transaction completed.");
            transactionAmount.clear();
            transactionDescription.clear();
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

            String number = selected.substring(
                    0, selected.indexOf(" | ")
            );

            historyTable.getItems().setAll(
                    accountController.history(number)
            );

            historyMessage.setText("");
        } catch (Exception e) {
            historyMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void signout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/banking/views/login-view.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();

            controller.configure(
                    stage,
                    customers,
                    accounts,
                    transactions,
                    accountController
            );

            stage.setScene(scene);
            stage.setTitle("Banking System Login");
        } catch (Exception e) {
            message.setText(e.getMessage());
        }
    }

    private void refreshAccounts() {
        List<Account> accountList =
                accountController.accountsFor(customer);

        accountTable.getItems().setAll(accountList);

        totalAccountsLabel.setText(
                String.valueOf(accountList.size())
        );

        double totalBalance = accountList.stream()
                .mapToDouble(Account::getBalance)
                .sum();

        totalBalanceLabel.setText(
                String.format("P %.2f", totalBalance)
        );

        accountTotalAccountsLabel.setText(
                String.valueOf(accountList.size())
        );

        accountTotalBalanceLabel.setText(
                String.format("P %.2f", totalBalance)
        );

        List<String> choices = accountList.stream()
                .map(account -> account.getAccountNO()
                        + " | " + account.getAccountType())
                .toList();

        transactionAccount.getItems().setAll(choices);
        historyAccount.getItems().setAll(choices);

        List<RecentTransaction> recent = new ArrayList<>();

        for (Account account : accountList) {
            List<TransactionRepository.TransactionRow> rows =
                    accountController.history(account.getAccountNO());

            for (TransactionRepository.TransactionRow row : rows) {
                recent.add(new RecentTransaction(
                        row.date,
                        row.type,
                        account.getAccountNO(),
                        row.amount
                ));
            }
        }

        recent.sort(
                Comparator.comparing(
                        (RecentTransaction row) -> row.date
                ).reversed()
        );

        recentTable.getItems().setAll(
                recent.stream().limit(5).toList()
        );
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