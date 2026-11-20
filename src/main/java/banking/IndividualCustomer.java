package banking;

public final class IndividualCustomer extends Customer {
    private final String name;
    private final String surname;
    private final String idNumber;

    public IndividualCustomer(String customerID, String address, String phone,
            String username, String passwordHash, String name,
            String surname, String idNumber)
     {
        super(customerID, address, phone, username, passwordHash);
        this.name = name;
        this.surname = surname;
        this.idNumber =idNumber;
     }
    public String getName()
     { return name; }
    public String getSurname() 
    { return surname; }
    public String getIdNumber()
     { return idNumber; }

    @Override
    public String getDisplayName() {
        return name + " " + surname;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}