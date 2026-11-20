package banking;

import java.util.List;
import java.util.UUID;

public final class AccountController {
    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public AccountController(AccountRepository accounts,
                             TransactionRepository transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public List<Account> accountsFor(Customer customer) {
        return accounts.findByCustomer(customer);
    }

    public Account openAccount(Customer customer, String type,
                               double openingBalance, String branch,
                               String employerName, String employerAddress) {
        String number = "BW" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 10).toUpperCase();

        Account account;
        if ("SAVINGS".equals(type)) {
            account = new SavingsAccount(
                    number, openingBalance, branch, customer);
        } else if ("INVESTMENT".equals(type)) {
            account = new InvestmentAccount(
                    number, openingBalance, branch, customer);
        } else if ("CHEQUE".equals(type)) {
            account = new ChequeAccount(number, openingBalance, branch,
                    customer, employerName, employerAddress);
        } else {
            throw new IllegalArgumentException("Choose an account type.");
        }

        accounts.save(account);
        customer.openAccount(account);
        return account;
    }

    public Account deposit(Customer customer, String number, double amount) {
        return transact(customer, number, amount, true);
    }

    public Account withdraw(Customer customer, String number, double amount) {
        return transact(customer, number, amount, false);
    }

    private Account transact(Customer customer, String number,
                             double amount, boolean isDeposit) {
        Account account = accounts.findByNumber(customer, number)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found."));

        if (isDeposit) {
            account.deposit(amount);
        } else {
            account.withdraw(amount);
        }

        List<Transactions> history = account.getTransactions();
        accounts.saveBalanceAndTransaction(
                account, history.get(history.size() - 1));
        return account;
    }

    public List<TransactionRepository.TransactionRow> history(String number) {
        return transactions.findByAccount(number);
    }

    public int applyInterest(Customer customer) {
        return new InterestScheduler(accounts).applyToCustomer(customer);
    }
}