import java.util.ArrayList;

/**
 * Cart.java
 *
 * Represents a single customer's shopping cart.
 * Internally backed by an ArrayList<CartItem> - this demonstrates
 * using Java's Collections framework to manage a dynamic list of items.
 */
public class Cart {

    private final ArrayList<CartItem> items = new ArrayList<>();

    /**
     * Adds a product to the cart. If the product is already in the
     * cart, its quantity is simply increased instead of creating a
     * duplicate line item.
     */
    public void addItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.increaseQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    /** Removes a product entirely from the cart. */
    public boolean removeItem(int productId) {
        CartItem itemToRemove = null;
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                itemToRemove = item;
                break;
            }
        }
        if (itemToRemove == null) {
            return false;
        }
        items.remove(itemToRemove);
        return true;
    }

    /** Updates the quantity of a product already in the cart. */
    public boolean updateQuantity(int productId, int newQuantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                if (newQuantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                return true;
            }
        }
        return false;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Grand total price across every item currently in the cart. */
    public double getTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    /** Empties the cart, typically called right after checkout. */
    public void clear() {
        items.clear();
    }
}
