/**
 * Admin.java
 *
 * An Admin is a User who manages the product catalog. It does not
 * need anything extra beyond what User already provides, but it exists
 * as its own class (instead of just a role flag) so that the "is this
 * user an admin?" question is answered through real inheritance and
 * method overriding, not a plain if-check on a role field.
 */
public class Admin extends User {

    public Admin(String username, String password, String email) {
        super(username, password, email, Role.ADMIN);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}
