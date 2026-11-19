package banking;

import java.util.List;

public class InterestScheduler {
    private AccountRepository accounts;

    public InterestScheduler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public int applyToCustomer(Customer customer) {
        int numberOfAccounts = 0;
        List<Account> list = accounts.findByCustomer(customer);

        for (Account account : list) {
            if (account instanceof InterestBearing) {
                InterestBearing interestAccount =
                    (InterestBearing) account;

                double interest = interestAccount.calculateInterest();

                if (interest > 0) {
                    interestAccount.payInterest();

                    List<Transactions> transactions =
                        account.getTransactions();
                    Transactions newest =
                        transactions.get(transactions.size() - 1);

                    accounts.saveTransactionAndBalance(account, newest);
                    numberOfAccounts++;
                }
            }
        }

        return numberOfAccounts;
    }
}