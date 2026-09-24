package org.example.models;

/**
 * Immutable typed data model representing checkout customer information.
 */
public class CheckoutCustomer {

    private final String firstName;
    private final String lastName;
    private final String postalCode;

    public CheckoutCustomer(String firstName, String lastName, String postalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
    }

    public static CheckoutCustomer standardCustomer() {
        return new CheckoutCustomer("John", "Doe", "12345");
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }
}