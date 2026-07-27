/**
 * CartItem.java
 *
 * Represents one line inside a shopping cart: a reference to a
 * Product, plus how many units the user wants to buy.
 */
public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int extra) {
        this.quantity += extra;
    }

    /** final price (after discount) * quantity for this single line item. */
    public double getSubtotal() {
        return product.getFinalPrice() * quantity;
    }

    @Override
    public String toString() {
        return product.getName() + " x" + quantity
                + "  Price: Rs." + String.format("%.2f", product.getFinalPrice())
                + "  Subtotal: Rs." + String.format("%.2f", getSubtotal());
    }
}
