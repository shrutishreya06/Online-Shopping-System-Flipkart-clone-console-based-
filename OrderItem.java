/**
 * OrderItem.java
 *
 * Represents one line item inside a placed Order.
 *
 * Notice this class does NOT store a reference to the live Product
 * object. Instead it copies (snapshots) the name, price and discount
 * at the moment of purchase. This way, if the admin later changes the
 * product's price, old orders in the order history still show the
 * price the customer actually paid.
 */
public class OrderItem {

    private final int productId;
    private final String productName;
    private final double originalPrice;
    private final double discountPercent;
    private final double finalPrice;
    private final int quantity;

    public OrderItem(int productId, String productName, double originalPrice, double discountPercent,
                      double finalPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.discountPercent = discountPercent;
        this.finalPrice = finalPrice;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return finalPrice * quantity;
    }

    @Override
    public String toString() {
        return productName + " x" + quantity
                + "  MRP: Rs." + originalPrice
                + "  Off: " + discountPercent + "%"
                + "  Price: Rs." + finalPrice
                + "  Subtotal: Rs." + getSubtotal();
    }

    /**
     * Turns this order item into one plain text line, used when saving
     * orders.txt. Fields are separated by commas since a product name
     * is very unlikely to contain one.
     */
    public String toFileLine() {
        return productId + "," + productName + "," + originalPrice + "," + discountPercent + "," + finalPrice + "," + quantity;
    }

    /**
     * Rebuilds an OrderItem from one line saved by toFileLine().
     * Used only while loading orders.txt when the program starts.
     */
    public static OrderItem fromFileLine(String line) {
        String[] parts = line.split(",", 6);
        int productId = Integer.parseInt(parts[0]);
        String productName = parts[1];
        double originalPrice = Double.parseDouble(parts[2]);
        double discountPercent = Double.parseDouble(parts[3]);
        double finalPrice = Double.parseDouble(parts[4]);
        int quantity = Integer.parseInt(parts[5]);
        return new OrderItem(productId, productName, originalPrice, discountPercent, finalPrice, quantity);
    }
}
