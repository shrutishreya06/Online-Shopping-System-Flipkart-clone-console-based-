/**
 * User.java
 *
 * Base class for anyone who can log in to the system.
 * This class is now abstract - it only holds the data and behaviour that
 * is common to every user, while each concrete type (Customer, Admin)
 * decides for itself what "isAdmin()" means. This is a simple example
 * of using inheritance instead of only checking a role field everywhere.
 */
public abstract class User {

    // static counter shared by ALL User objects, used to auto-generate ids
    private static int nextId = 1;

    protected final int id;
    protected String username;
    protected String password; // NOTE: stored in plain text only for learning purposes.
    protected String email;
    protected Role role;

    protected User(String username, String password, String email, Role role) {
        this.id = nextId++;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    /**
     * Checks whether the supplied password matches this user's password.
     * Keeping this logic inside the User class (rather than comparing
     * fields from outside) is another example of encapsulation.
     */
    public boolean checkPassword(String candidatePassword) {
        return this.password.equals(candidatePassword);
    }

    /**
     * Every subclass must say for itself whether it is an admin or not.
     * Customer always returns false, Admin always returns true - this
     * is method overriding in action, replacing a plain "role == ADMIN"
     * check with real inheritance.
     */
    public abstract boolean isAdmin();

    @Override
    public String toString() {
        return "User#" + id + " [username=" + username + ", email=" + email + ", role=" + role + "]";
    }
}
