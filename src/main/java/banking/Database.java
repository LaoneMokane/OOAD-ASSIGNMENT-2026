package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:banking.db";

    public static Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        Statement statement = connection.createStatement();
        statement.execute("PRAGMA foreign_keys = ON");
        statement.close();

        return connection;
    }

    public static void createTables() {
        String customers = """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id TEXT PRIMARY KEY,
                customer_type TEXT NOT NULL,
                address TEXT NOT NULL,
                phone TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                name TEXT,
                surname TEXT,
                id_number TEXT,
                company_name TEXT,
                registration_number TEXT,
                contact_person TEXT
            )
            """;

        String accounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                account_number TEXT PRIMARY KEY,
                customer_id TEXT NOT NULL,
                account_type TEXT NOT NULL,
                balance REAL NOT NULL,
                branch TEXT NOT NULL,
                employer_name TEXT,
                employer_address TEXT,
                FOREIGN KEY(customer_id) REFERENCES customers(customer_id)
            )
            """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id TEXT PRIMARY KEY,
                account_number TEXT NOT NULL,
                type TEXT NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                description TEXT,
                FOREIGN KEY(account_number)
                    REFERENCES accounts(account_number)
            )
            """;

        try (Connection c = connect();
             Statement s = c.createStatement()) {
            s.execute(customers);
            s.execute(accounts);
            s.execute(transactions);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create database tables.", e);
        }
    }
}