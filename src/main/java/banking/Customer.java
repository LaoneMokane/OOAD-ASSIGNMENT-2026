package banking;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    private final String customerID;
    private final String address;
    private final String phone;
    private final String username;
    private final String passwordHash;
    private final List<Account> accounts = new ArrayList<>();

    protected Customer(String customerID, String address, String phone,
                       String username, String passwordHash) {
       this.customerID = customerID;
       this.address = address;
       this.phone = phone;
       this.username = username;
       this.passwordHash = passwordHash;
    }

    public String getCustomerID() 
    { return customerID; }
    public String getAddress() 
    { return address; }
    public String getPhone()
     { return phone; }
    public String getUsername() 
    { return username; }
    public String getPasswordHash() 
    { return passwordHash; }

    public void openAccount(Account account) {
        addAccount(account);
    }

    public void addAccount(Account account) {
        if (account == null || account.getCustomer() != this) {
            throw new IllegalArgumentException(
                "The account belong to a different customer."
            );
        }
 accounts.add(account);
    }

      public List<Account> getAccount() {
        return accounts;
    }

    public void replaceAccounts(List<Account> newAccounts) {
        accounts = new ArrayList<>(newAccounts);
    }

    public abstract String getDisplayName();
}   