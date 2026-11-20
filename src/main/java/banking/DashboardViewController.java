package banking;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardViewController {
    @FXML private VBox dashboardPane;
    @FXML private VBox accountPane;
    @FXML private VBox openAccountPane;
    @FXML private VBox transactionPane;
    @FXML private VBox historyPane;

    @FXML private Label welcomeLabel;
    @FXML private Label totalAccountsLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label openAccountMessage;
    @FXML private Label transactionMessage;
    @FXML private Label historyMessage;

    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> accountNumberColumn;
    @FXML private TableColumn<Account, String> accountTypeColumn;
    @FXML private TableColumn<Account, Double> balanceColumn;

    @FXML private TableView<RecentTransaction> recentTable;
    @FXML private TableColumn<RecentTransaction, String> recentDateColumn;
    @FXML private TableColumn<RecentTransaction, String> recentTypeColumn;
    @FXML private TableColumn<RecentTransaction, String> recentAccountColumn;
    @FXML private TableColumn<RecentTransaction, Double> recentAmountColumn;

    @FXML private ComboBox<String> accountType;
    @FXML private TextField openingBalance;
    @FXML private TextField branch;
    @FXML private TextField employerName;
    @FXML private TextField employerAddress;

    @FXML private ComboBox<Account> transactionAccount;
    @FXML private ComboBox<String> transactionType;
    @FXML private TextField transactionAmount;
    @FXML private TextField transactionDescription;

    @FXML private ComboBox<Account> historyAccount;
    @FXML private TableView<TransactionRepository.TransactionRow> historyTable;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyDateColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyTypeColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, Double> historyAmountColumn;
    @FXML private TableColumn<TransactionRepository.TransactionRow, String> historyDescriptionColumn;

    @FXML
    public void initialize() {
        Customer customer = AppContext.getCustomer();
        welcomeLabel.setText("Welcome back, " + customer.getDisplayName());

        configureAccountTable();
        configureRecentTable();
        configureHistoryTable();
        configureAccountChoices();

        showDashboard();
        refreshData();
    }

    private void configureAccountTable() {
        accountNumberColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().getAccountNO()));
        accountTypeColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().getAccountType()));
        balanceColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getBalance()));
        balanceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("P %.2f", value));
            }
        });
    }

    private void configureRecentTable() {
        recentDateColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().date));
        recentTypeColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().type));
        recentAccountColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().account));
        recentAmountColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().amount));
        recentAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("P %.2f", value));
            }
        });
    }

    private void configureHistoryTable() {
        historyDateColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().date));
        historyTypeColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().type));
        historyAmountColumn.setCellValueFactory(
                cell -> new ReadOnlyObjectWrapper<>(cell.getValue().amount));
        historyDescriptionColumn.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().description));
        historyAmountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("P %.2f", value));
            }
        });
    }

    private void configureAccountChoices() {
        StringConverter<Account> converter = new StringConverter<>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" :
                        account.getAccountNO() + " - " + account.getAccountType();
            }

            @Override
            public Account fromString(String value) {
                return null;
            }
        };

        transactionAccount.setConverter(converter);
        historyAccount.setConverter(converter);

        accountType.getItems().setAll("SAVINGS", "INVESTMENT");
        if (AppContext.getCustomer() instanceof IndividualCustomer) {
            accountType.getItems().add("CHEQUE");
        }
        accountType.setValue("SAVINGS");

        transactionType.getItems().setAll("DEPOSIT", "WITHDRAWAL");
        transactionType.setValue("DEPOSIT");
    }

    private void refreshData() {
        List<Account> accounts =
                AppContext.getAccountController().accountsFor(AppContext.getCustomer());

        accountTable.getItems().setAll(accounts);
        transactionAccount.getItems().setAll(accounts);
        historyAccount.getItems().setAll(accounts);

        totalAccountsLabel.setText(String.valueOf(accounts.size()));
        double total = accounts.stream().mapToDouble(Account::getBalance).sum();
        totalBalanceLabel.setText(String.format("P %.2f", total));

        List<RecentTransaction> recent = new ArrayList<>();
        for (Account account : accounts) {
            for (TransactionRepository.TransactionRow row :
                    AppContext.getAccountController().history(account.getAccountNO())) {
                recent.add(new RecentTransaction(
                        row.date, row.type, account.getAccountNO(), row.amount));
            }
        }

        recent.sort(Comparator.comparing((RecentTransaction row) -> row.date).reversed());
        recentTable.getItems().setAll(recent.stream().limit(5).toList());
    }

    private void showOnly(VBox pane) {
        for (VBox item : List.of(dashboardPane, accountPane, openAccountPane,
                transactionPane, historyPane)) {
            boolean selected = item == pane;
            item.setVisible(selected);
            item.setManaged(selected);
        }
    }

    @FXML private void showDashboard() { showOnly(dashboardPane); }
    @FXML private void showMyAccount() { showOnly(accountPane); }
    @FXML private void showOpenAccount() { showOnly(openAccountPane); }
    @FXML private void showTransactions() { showOnly(transactionPane); }
    @FXML private void showTransactionHistory() { showOnly(historyPane); }

    @FXML
    private void openAccount() {
        try {
            AppContext.getAccountController().openAccount(
                    AppContext.getCustomer(),
                    accountType.getValue(),
                    Double.parseDouble(openingBalance.getText()),
                    branch.getText(),
                    employerName.getText(),
                    employerAddress.getText()
            );
            openAccountMessage.setText("Account opened successfully.");
            refreshData();
        } catch (Exception e) {
            openAccountMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void submitTransaction() {
        try {
            Account account = transactionAccount.getValue();
            if (account == null) {
                throw new IllegalArgumentException("Select an account.");
            }

            double amount = Double.parseDouble(transactionAmount.getText());
            String type = transactionType.getValue();

            if ("DEPOSIT".equals(type)) {
                AppContext.getAccountController().deposit(
                        AppContext.getCustomer(), account.getAccountNO(), amount);
            } else {
                AppContext.getAccountController().withdraw(
                        AppContext.getCustomer(), account.getAccountNO(), amount);
            }

            transactionMessage.setText("Transaction completed.");
            transactionAmount.clear();
            transactionDescription.clear();
            refreshData();
        } catch (Exception e) {
            transactionMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void loadHistory() {
        try {
            Account account = historyAccount.getValue();
            if (account == null) {
                throw new IllegalArgumentException("Select an account.");
            }

            historyTable.getItems().setAll(
                    AppContext.getAccountController().history(account.getAccountNO()));
            historyMessage.setText("");
        } catch (Exception e) {
            historyMessage.setText(e.getMessage());
        }
    }

    @FXML
    private void signout() {
        AppContext.setCustomer(null);
        SceneManager.show("login-view.fxml", "Banking System Login");
    }

    private static final class RecentTransaction {
        private final String date;
        private final String type;
        private final String account;
        private final double amount;

        private RecentTransaction(String date, String type, String account, double amount) {
            this.date = date;
            this.type = type;
            this.account = account;
            this.amount = amount;
        }
    }
}