package banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    public static class TransactionRow {
        public String type;
        public double amount;
        public String date;
        public String description;

        public TransactionRow(String type, double amount,
                              String date, String description) {
            this.type = type;
            this.amount = amount;
            this.date = date;
            this.description = description;
        }
    }

    public static void save(Connection c, Transactions t)
            throws SQLException {
        String sql = """
            INSERT INTO transactions(
                id, account_number, type, amount, date, description
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, t.getId());
            p.setString(2, t.getAccount().getAccountNO());
            p.setString(3, t.getType());
            p.setDouble(4, t.getAmount());
            p.setString(5, t.getDate().toString());
            p.setString(6, t.getDescription());
            p.executeUpdate();
        }
    }

    public List<TransactionRow> findByAccount(String accountNumber) {
        List<TransactionRow> rows = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions
            WHERE account_number = ?
            ORDER BY date DESC
            """;

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, accountNumber);

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    rows.add(new TransactionRow(
                        r.getString("type"),
                        r.getDouble("amount"),
                        r.getString("date"),
                        r.getString("description")
                    ));
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load transaction history.", e);
        }
    }
}