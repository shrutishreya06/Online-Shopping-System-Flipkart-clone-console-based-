import java.util.ArrayList;
import java.util.HashSet;

/**
 * Customer.java
 *
 * A Customer is a User who can shop - so on top of everything a User
 * already has, a Customer also owns a wishlist.
 *
 * The wishlist is stored in a HashSet<Product>, because a HashSet does
 * not allow duplicate elements - this is exactly what we want, a
 * product should not be able to appear twice in the same wishlist.
 * (Product.equals()/hashCode() are based on the product id, so the
 * HashSet correctly treats two Product objects with the same id as
 * the same wishlist entry.)
 */
public class Customer extends User {

    private HashSet<Product> wishlist;

    public Customer(String username, String password, String email) {
        super(username, password, email, Role.CUSTOMER);
        wishlist = new HashSet<>();
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    /** Returns true if the product was actually added (false if it was already there). */
    public boolean addToWishlist(Product product) {
        return wishlist.add(product);
    }

    /** Returns true if the product was actually removed. */
    public boolean removeFromWishlist(Product product) {
        return wishlist.remove(product);
    }

    public boolean isInWishlist(Product product) {
        return wishlist.contains(product);
    }

    /** Returns the wishlist as a list, just so it's easier to print in order. */
    public ArrayList<Product> getWishlist() {
        return new ArrayList<>(wishlist);
    }
}
