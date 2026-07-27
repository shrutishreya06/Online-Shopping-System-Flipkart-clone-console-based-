import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * UserService.java
 *
 * Handles all business logic related to Users: registration and login.
 * Uses a HashMap<String, User> so we can look up a user by username
 * in O(1) time instead of scanning a list every time someone logs in.
 *
 * Notice the map is declared as HashMap<String, User>, but the actual
 * objects stored inside are either Customer or Admin objects - this is
 * polymorphism: we work with the general User type, but each object
 * still behaves like its real (more specific) type when needed.
 *
 * Accounts are also saved to users.txt so they are not lost when the
 * program is closed and reopened.
 */
public class UserService {

    private static final String USER_FILE = "users.txt";

    // key = username, value = User object (really a Customer or an Admin)
    private final HashMap<String, User> usersByUsername = new HashMap<>();

    public UserService() {
        loadUsersFromFile();

        // Seed one ready-made admin account so the admin panel is reachable
        // immediately, but only if it wasn't already loaded from the file
        // (this happens on the very first run of the program).
        if (!usersByUsername.containsKey("admin")) {
            Admin admin = new Admin("admin", "admin123", "admin@shop.com");
            usersByUsername.put(admin.getUsername(), admin);
            saveUsersToFile();
        }
    }

    /**
     * Registers a new customer account.
     * Returns null if the username is already taken.
     */
    public User register(String username, String password, String email) {
        if (usersByUsername.containsKey(username)) {
            return null; // username already exists
        }
        User newUser = new Customer(username, password, email);
        usersByUsername.put(username, newUser);
        saveUsersToFile();
        return newUser;
    }

    /**
     * Attempts to log a user in.
     * Returns the matching User object on success, or null on failure
     * (wrong username or wrong password).
     */
    public User login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return usersByUsername.containsKey(username);
    }

    // ---------------------------------------------------------------
    // FILE HANDLING - keeps users.txt in sync with the accounts
    // ---------------------------------------------------------------

    private void loadUsersFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(USER_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split("\\|", 4);
                if (parts.length < 4) {
                    continue; // skip a broken line instead of crashing
                }
                String username = parts[0];
                String password = parts[1];
                String email = parts[2];
                String role = parts[3];

                User user;
                if (role.equals("ADMIN")) {
                    user = new Admin(username, password, email);
                } else {
                    user = new Customer(username, password, email);
                }
                usersByUsername.put(username, user);
            }
        } catch (IOException e) {
            // users.txt does not exist yet on the very first run - that's expected
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // nothing much we can do here
                }
            }
        }
    }

    private void saveUsersToFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(USER_FILE, false); // false = overwrite with the latest accounts
            for (User user : usersByUsername.values()) {
                writer.write(user.getUsername() + "|" + user.getPassword() + "|" + user.getEmail()
                        + "|" + user.getRole() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Note: could not save users to file (" + e.getMessage() + ")");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // nothing much we can do here
                }
            }
        }
    }
}
