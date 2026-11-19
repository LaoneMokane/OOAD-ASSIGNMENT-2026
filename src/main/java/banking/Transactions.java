package banking;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Transactions {
    private final String id = UUID.randomUUID().toString();
    private final String type;
    private final double amount;
    private final LocalDateTime date = LocalDateTime.now();
    private final String description;
    private final Account account;

    public Transactions(String type, double amount, String description,
                        Account account) {
        if (type == null || type.isBlank() || account == null
                || !Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Invalid transaction.");
        }
        this.type = type;
        this.amount = Math.round(amount * 100.0) / 100.0;
        this.description = description == null ? "" : description;
        this.account = account;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public String getDescription() { return description; }
    public Account getAccount() { return account; }

    public void save() {
        account.addTransaction(this);
    }

    public String getDetails() {
        return id + " | " + type + " | P"
            + String.format("%.2f", amount) + " | " + date
            + " | " + description;
    }
}