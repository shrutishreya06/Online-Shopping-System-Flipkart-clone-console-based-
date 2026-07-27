import java.util.ArrayList;
import java.util.Date;

/**
 * Order.java
 *
 * Represents a completed order placed by a customer at checkout time.
 * Holds a list of OrderItem "snapshots" plus overall order metadata,
 * including the payment method and any coupon that was applied.
 */
public class Order {

    private static int nextOrderId = 1001; // start order numbers at a friendly looking number

    private final int orderId;
    private final String username;
    private final ArrayList<OrderItem> items;
    private final double itemsSubtotal;   // total of all items, before the coupon discount
    private final String couponCode;      // "NONE" if no coupon was used
    private final double couponDiscount;  // amount taken off because of the coupon
    private final double finalAmount;     // what the customer actually pays
    private final Date orderDate;
    private final PaymentMethod paymentMethod;
    private String status; // e.g. "PLACED", "DELIVERED" - kept simple for this project

    /** Used when a brand-new order is placed during checkout. */
    public Order(String username, ArrayList<OrderItem> items, double itemsSubtotal, String couponCode,
                 double couponDiscount, double finalAmount, PaymentMethod paymentMethod) {
        this.orderId = nextOrderId++;
        this.username = username;
        this.items = items;
        this.itemsSubtotal = itemsSubtotal;
        this.couponCode = couponCode;
        this.couponDiscount = couponDiscount;
        this.finalAmount = finalAmount;
        this.orderDate = new Date();
        this.paymentMethod = paymentMethod;
        this.status = "PLACED";
    }

    /**
     * Used only while loading orders back from orders.txt when the
     * program starts, so the order keeps its original id, date and
     * status instead of getting a brand new one.
     */
    public Order(int orderId, String username, ArrayList<OrderItem> items, double itemsSubtotal, String couponCode,
                 double couponDiscount, double finalAmount, PaymentMethod paymentMethod, long orderDateMillis, String status) {
        this.orderId = orderId;
        this.username = username;
        this.items = items;
        this.itemsSubtotal = itemsSubtotal;
        this.couponCode = couponCode;
        this.couponDiscount = couponDiscount;
        this.finalAmount = finalAmount;
        this.orderDate = new Date(orderDateMillis);
        this.paymentMethod = paymentMethod;
        this.status = status;
        // make sure future new orders don't reuse an id that was loaded from file
        if (orderId >= nextOrderId) {
            nextOrderId = orderId + 1;
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public double getItemsSubtotal() {
        return itemsSubtotal;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    /** The final amount the customer pays, after any coupon discount. */
    public double getTotalAmount() {
        return finalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private boolean hasCoupon() {
        return couponCode != null && !couponCode.equals("NONE") && couponDiscount > 0;
    }

    /** Pretty, multi-line printout used in "Order History". */
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("Order #").append(orderId)
                .append("   Date: ").append(orderDate)
                .append("   Status: ").append(status).append("\n");
        sb.append("Payment Mode: ").append(paymentMethod.getDisplayName()).append("\n");
        sb.append("----------------------------------------\n");
        for (OrderItem item : items) {
            sb.append(item).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append("Items Subtotal: Rs.").append(String.format("%.2f", itemsSubtotal)).append("\n");
        if (hasCoupon()) {
            sb.append("Coupon Applied : ").append(couponCode)
                    .append(" (-Rs.").append(String.format("%.2f", couponDiscount)).append(")\n");
        }
        sb.append("Total: Rs.").append(String.format("%.2f", finalAmount)).append("\n");
        sb.append("========================================");
        return sb.toString();
    }

    /**
     * Builds a simple console invoice for this order.
     * customerName is passed in separately because Order does not
     * keep a reference to the User object (only the username).
     */
    public String toInvoiceString(String customerName) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("            FLIPKART CONSOLE STORE       \n");
        sb.append("               ORDER INVOICE              \n");
        sb.append("========================================\n");
        sb.append("Order ID       : ").append(orderId).append("\n");
        sb.append("Customer Name  : ").append(customerName).append("\n");
        sb.append("Date           : ").append(orderDate).append("\n");
        sb.append("Payment Method : ").append(paymentMethod.getDisplayName()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %-4s %-10s %-8s %-10s%n", "Product", "Qty", "Price", "Disc%", "Amount"));
        sb.append("----------------------------------------\n");
        for (OrderItem item : items) {
            sb.append(String.format("%-18s %-4d %-10.2f %-8.2f %-10.2f%n",
                    item.getProductName(), item.getQuantity(), item.getOriginalPrice(),
                    item.getDiscountPercent(), item.getSubtotal()));
        }
        sb.append("----------------------------------------\n");
        sb.append("Items Subtotal : Rs.").append(String.format("%.2f", itemsSubtotal)).append("\n");
        if (hasCoupon()) {
            sb.append("Coupon         : ").append(couponCode)
                    .append(" (-Rs.").append(String.format("%.2f", couponDiscount)).append(")\n");
        }
        sb.append("Final Amount   : Rs.").append(String.format("%.2f", finalAmount)).append("\n");
        sb.append("========================================\n");
        sb.append("        Thank you for shopping!          \n");
        sb.append("========================================");
        return sb.toString();
    }
}
