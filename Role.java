/**
 * Role.java
 *
 * A simple enum that represents the type of a user in the system.
 * Using an enum (instead of a plain String) prevents typos like
 * "Admin" vs "ADMIN" vs "admin" and makes the code safer.
 */
public enum Role {
    CUSTOMER,
    ADMIN
}
