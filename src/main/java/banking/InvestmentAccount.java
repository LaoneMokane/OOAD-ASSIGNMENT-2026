package banking;

public final class InvestmentAccount extends Account
        implements InterestBearing {
    private static final double MINIMUM_OPENING_DEPOSIT = 500.00;
    private static final double MONTHLY_RATE = 0.05; // 5%

    public InvestmentAccount(String number, double balance,
                             String branch, Customer customer) {
        super(number, balance, branch, customer);
        if (balance < MINIMUM_OPENING_DEPOSIT) {
            throw new IllegalArgumentException(
                "An investment account requires at least P500.00 to open."
            );
        }
    }

    @Override
    public String getAccountType() {
        return "INVESTMENT";
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