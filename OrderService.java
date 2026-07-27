import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * OrderService.java
 *
 * Handles checkout (turning a Cart into a permanent Order) and
 * keeps track of every user's order history.
 *
 * Uses a HashMap<String, ArrayList<Order>> so we can quickly find
 * "all orders placed by username X" without scanning every order
 * ever placed by every customer.
 *
 * Every order is also saved to orders.txt, and reloaded automatically
 * the next time the program starts, so order history is not lost.
 */
public class OrderService {

    private static final String ORDER_FILE = "orders.txt";

    // key = username, value = that user's list of past orders
    private final HashMap<String, ArrayList<Order>> orderHistory = new HashMap<>();

    public OrderService() {
        loadOrdersFromFile();
    }

    /**
     * Converts the given cart into a placed Order for the given user,
     * reducing product stock along the way. An optional coupon code
     * can be applied on top of the item-level discounts.
     *
     * Throws InsufficientStockException if any item in the cart no
     * longer has enough stock (nothing is charged/deducted in that
     * case - all or nothing).
     */
    public Order checkout(String username, Cart cart, ProductService productService,
                           PaymentMethod paymentMethod, String couponCode) throws InsufficientStockException {

        if (cart.isEmpty()) {
            return null;
        }

        // First pass: make sure every item can actually be fulfilled.
        for (CartItem cartItem : cart.getItems()) {
            Product product = productService.getProductById(cartItem.getProduct().getId());
            if (product == null || product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for '" + cartItem.getProduct().getName() + "'");
            }
        }

        // Second pass: everything is available, so commit the order.
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        double itemsSubtotal = 0.0;
        for (CartItem cartItem : cart.getItems()) {
            Product product = productService.getProductById(cartItem.getProduct().getId());
            product.reduceStock(cartItem.getQuantity());

            OrderItem snapshot = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDiscountPercent(),
                    product.getFinalPrice(),
                    cartItem.getQuantity()
            );
            orderItems.add(snapshot);
            itemsSubtotal += snapshot.getSubtotal();
        }

        double couponDiscount = calculateCouponDiscount(couponCode, itemsSubtotal);
        double finalAmount = itemsSubtotal - couponDiscount;
        if (finalAmount < 0) {
            finalAmount = 0;
        }
        String cleanCoupon = (couponCode == null || couponCode.isBlank()) ? "NONE" : couponCode.trim().toUpperCase();

        Order order = new Order(username, orderItems, itemsSubtotal, cleanCoupon, couponDiscount, finalAmount, paymentMethod);

        // create the list for this user the first time they order, then reuse it
        ArrayList<Order> ordersForUser = orderHistory.get(username);
        if (ordersForUser == null) {
            ordersForUser = new ArrayList<>();
            orderHistory.put(username, ordersForUser);
        }
        ordersForUser.add(order);

        cart.clear(); // empty the cart now that checkout succeeded
        saveOrdersToFile();
        return order;
    }

    /**
     * Works out how much discount a coupon code gives on the given subtotal.
     * Returns 0 if the code is blank or not recognised.
     *   SAVE10  -> 10% off the subtotal
     *   FLAT100 -> flat Rs.100 off (never more than the subtotal itself)
     */
    private double calculateCouponDiscount(String couponCode, double subtotal) {
        if (couponCode == null || couponCode.isBlank()) {
            return 0.0;
        }
        String code = couponCode.trim().toUpperCase();
        if (code.equals("SAVE10")) {
            return subtotal * 0.10;
        } else if (code.equals("FLAT100")) {
            return Math.min(100.0, subtotal);
        } else {
            return 0.0; // unknown coupon code, simply no discount
        }
    }

    /** Returns the order history for a user (empty list if they have none yet). */
    public ArrayList<Order> getOrderHistory(String username) {
        ArrayList<Order> orders = orderHistory.get(username);
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders;
    }

    // ---------------------------------------------------------------
    // FILE HANDLING - keeps orders.txt in sync with order history
    // ---------------------------------------------------------------

    private void loadOrdersFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(ORDER_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                // limit 10 so nothing inside the last field (the item list) gets cut off
                String[] parts = line.split("\\|", 10);
                if (parts.length < 10) {
                    continue; // skip a broken line instead of crashing
                }

                int orderId = Integer.parseInt(parts[0]);
                String username = parts[1];
                long dateMillis = Long.parseLong(parts[2]);
                PaymentMethod paymentMethod = PaymentMethod.valueOf(parts[3]);
                String couponCode = parts[4];
                double couponDiscount = Double.parseDouble(parts[5]);
                double itemsSubtotal = Double.parseDouble(parts[6]);
                double finalAmount = Double.parseDouble(parts[7]);
                String status = parts[8];
                String itemsPart = parts[9];

                ArrayList<OrderItem> items = new ArrayList<>();
                if (!itemsPart.isBlank()) {
                    String[] itemLines = itemsPart.split(";");
                    for (String itemLine : itemLines) {
                        items.add(OrderItem.fromFileLine(itemLine));
                    }
                }

                Order order = new Order(orderId, username, items, itemsSubtotal, couponCode, couponDiscount,
                        finalAmount, paymentMethod, dateMillis, status);

                ArrayList<Order> ordersForUser = orderHistory.get(username);
                if (ordersForUser == null) {
                    ordersForUser = new ArrayList<>();
                    orderHistory.put(username, ordersForUser);
                }
                ordersForUser.add(order);
            }
        } catch (IOException e) {
            // orders.txt does not exist yet on the very first run - that's expected
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

    private void saveOrdersToFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(ORDER_FILE, false); // false = overwrite with the latest history
            for (ArrayList<Order> ordersForUser : orderHistory.values()) {
                for (Order order : ordersForUser) {
                    StringBuilder itemsPart = new StringBuilder();
                    ArrayList<OrderItem> items = order.getItems();
                    for (int i = 0; i < items.size(); i++) {
                        itemsPart.append(items.get(i).toFileLine());
                        if (i < items.size() - 1) {
                            itemsPart.append(";");
                        }
                    }
                    writer.write(order.getOrderId() + "|" + order.getUsername() + "|" + order.getOrderDate().getTime()
                            + "|" + order.getPaymentMethod() + "|" + order.getCouponCode() + "|" + order.getCouponDiscount()
                            + "|" + order.getItemsSubtotal() + "|" + order.getTotalAmount() + "|" + order.getStatus()
                            + "|" + itemsPart + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Note: could not save orders to file (" + e.getMessage() + ")");
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
