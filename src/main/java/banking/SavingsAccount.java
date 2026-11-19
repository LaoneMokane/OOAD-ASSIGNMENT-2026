package banking;

public final class SavingsAccount extends Account implements InterestBearing {
    private static final double MONTHLY_RATE = 0.0005; // 0.05%

    public SavingsAccount(String number, double balance,
                          String branch, Customer customer) {
        super(number, balance, branch, customer);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public void withdraw(double amount) {
        throw new IllegalStateException(
            "Withdrawals are not allowed from a savings account."
        );
    }

    @Override
    public double calculateInterest() {
        return round(getBalance() * MONTHLY_RATE);
    }

    @Override
    public void payInterest() {
        double interest = calculateInterest();
        if (interest > 0) creditInterest(interest);
    }
}