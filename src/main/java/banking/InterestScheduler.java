package banking;

import java.util.List;

public class InterestScheduler {
    private final AccountRepository accounts;

    public InterestScheduler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public int applyToCustomer(Customer customer) {
        int count = 0;

        for (Account account : accounts.findByCustomer(customer)) {
            if (account instanceof InterestBearing interestAccount) {
                double interest = interestAccount.calculateInterest();

                if (interest > 0) {
                    interestAccount.payInterest();
                    List<Transactions> history = account.getTransactions();
                    Transactions newest = history.get(history.size() - 1);
                    accounts.saveBalanceAndTransaction(account, newest);
                    count++;
                }
            }
        }
        return count;
    }
}