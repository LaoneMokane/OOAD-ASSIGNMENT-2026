package banking;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class CustomerRepository {
    public Customer registerIndividual(String address, String phone,
            String username, String password, String name,
            String surname, String idNumber) {
        Customer customer = new IndividualCustomer(
            UUID.randomUUID().toString(), address, phone, username,
            PasswordHasher.hash(password), name, surname, idNumber
        );
        save(customer);
        return customer;
    }

    public Customer registerCompany(String address, String phone,
            String username, String password, String companyName,
            String registrationNumber, String contactPerson) {
        Customer customer = new CompanyCustomer(
            UUID.randomUUID().toString(), address, phone, username,
            PasswordHasher.hash(password), companyName,
            registrationNumber, contactPerson
        );
        save(customer);
        return customer;
    }

    private void save(Customer customer) {
        String sql = """
            INSERT INTO customers(
                customer_id, customer_type, address, phone,
                username, password_hash, name, surname, id_number,
                company_name, registration_number, contact_person
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, customer.getCustomerID());
            p.setString(3, customer.getAddress());
            p.setString(4, customer.getPhone());
            p.setString(5, customer.getUsername());
            p.setString(6, customer.getPasswordHash());

            if (customer instanceof IndividualCustomer person) {
                p.setString(2, "INDIVIDUAL");
                p.setString(7, person.getName());
                p.setString(8, person.getSurname());
                p.setString(9, person.getIdNumber());
                p.setNull(10, Types.VARCHAR);
                p.setNull(11, Types.VARCHAR);
                p.setNull(12, Types.VARCHAR);
            } else {
                CompanyCustomer company = (CompanyCustomer) customer;
                p.setString(2, "COMPANY");
                p.setNull(7, Types.VARCHAR);
                p.setNull(8, Types.VARCHAR);
                p.setNull(9, Types.VARCHAR);
                p.setString(10, company.getCompanyName());
                p.setString(11, company.getRegistrationNumber());
                p.setString(12, company.getContactPerson());
            }

            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                "Could not save customer. The username may already exist."
            );
        }
    }

    public Optional<Customer> login(String username, String password) {
        String sql = "SELECT * FROM customers WHERE username = ?";

        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);

            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return Optional.empty();

                if (!PasswordHasher.matches(
                        password, r.getString("password_hash"))) {
                    return Optional.empty();
                }

                return Optional.of(readCustomer(r));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login could not be checked.", e);
        }
    }

    private Customer readCustomer(ResultSet r) throws SQLException {
        String id = r.getString("customer_id");
        String address = r.getString("address");
        String phone = r.getString("phone");
        String username = r.getString("username");
        String hash = r.getString("password_hash");

        if ("INDIVIDUAL".equals(r.getString("customer_type"))) {
            return new IndividualCustomer(id, address, phone, username, hash,
                r.getString("name"), r.getString("surname"),
                r.getString("id_number"));
        }

        return new CompanyCustomer(id, address, phone, username, hash,
            r.getString("company_name"),
            r.getString("registration_number"),
            r.getString("contact_person"));
    }
}