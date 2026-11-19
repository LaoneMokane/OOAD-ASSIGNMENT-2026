package banking;

public final class ChequeAccount extends Account {
    private final String employerName;
    private final String employerAddress;

    public ChequeAccount(String number, double balance, String branch,
            Customer customer, String employerName, String employerAddress) {
        super(number, balance, branch, customer);
        this.employerName = required(employerName, "Employer name");
        this.employerAddress = required(employerAddress, "Employer address");
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public String getAccountType() {
        return "CHEQUE";
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}