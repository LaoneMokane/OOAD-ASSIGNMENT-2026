package banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
    public void save(Account account) {
        String employerName = "";
        String employerAddress = "";

        if (account instanceof ChequeAccount cheque) {
            employerName = cheque.getEmployerName();
            employerAddress = cheque.getEmployerAddress();
        }

        String sql = """
            INSERT INTO accounts(
                account_number, customer_id, account_type,
                balance, employer_name, employer_address
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, account.getAccountNO());
            p.setString(2, account.getCustomer().getCustomerID());
            p.setString(3, account.getAccountType());
            p.setDouble(4, account.getBalance());
            p.setString(5, account.getBranch());
            p.setString(6, employerName);
            p.setString(7, employerAddress);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Account could not be saved.", e);
        }
    }

    public List<Account> findByCustomer(Customer customer) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, customer.getCustomerID());

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(readAccount(r, customer));
            }

            customer.replaceAccounts(list);
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Accounts could not be loaded.", e);
        }
    }

    public Optional<Account> findByNumber(
            Customer customer, String accountNumber) {
        String sql = """
            SELECT * FROM accounts
            WHERE account_number = ? AND customer_id = ?
            """;

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, accountNumber);
            p.setString(2, customer.getCustomerID());

            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return Optional.empty();
                return Optional.of(readAccount(r, customer));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Account could not be found.", e);
        }
    }

    public void saveBalanceAndTransaction(Account account,
                                           Transactions transaction) {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);

            String sql =
                "UPDATE accounts SET balance = ? WHERE account_number = ?";

            try (PreparedStatement p = c.prepareStatement(sql)) {
                p.setDouble(1, account.getBalance());
                p.setString(2, account.getAccountNO());
                p.executeUpdate();

                TransactionRepository.save(c, transaction);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Account update failed.", e);
        }
    }

    private Account readAccount(ResultSet r, Customer customer)
            throws SQLException {
        String type = r.getString("account_type");
        String number = r.getString("account_number");
        double balance = r.getDouble("balance");
        String branch = r.getString("branch");

        if ("SAVINGS".equals(type)) {
            return new SavingsAccount(number, balance, branch, customer);
        }
        if ("INVESTMENT".equals(type)) {
            return new InvestmentAccount(number, balance, branch, customer);
        }
        if ("CHEQUE".equals(type)) {
            return new ChequeAccount(number, balance, branch, customer,
                r.getString("employer_name"),
                r.getString("employer_address"));
        }

        throw new SQLException("Unknown account type.");
    }
}