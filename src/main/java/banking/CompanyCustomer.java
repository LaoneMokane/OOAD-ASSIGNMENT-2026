package banking;

public final class CompanyCustomer extends Customer {
    private final String companyName;
    private final String registrationNo;
    private final String contactPerson;

    public CompanyCustomer(String customerID, String address, String phone,
            String username, String passwordHash, String companyName,
            String registrationNo, String contactPerson) {
        super(customerID, address, phone, username, passwordHash);
        this.companyName = companyName;
        this.registrationNo =registrationNo;
        this.contactPerson = contactPerson;

    public String getCompanyName()
     { return companyName; }
    public String getRegistrationNo() 
    { return registrationNo; }
    public String getContactPerson()
     { return contactPerson; }

    @Override
    public String getDisplayName() {
        return companyName;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}