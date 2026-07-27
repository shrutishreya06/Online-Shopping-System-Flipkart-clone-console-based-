import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Main.java
 *
 * Entry point of the application. This class is only responsible for
 * the console UI (menus, printing, reading input) - all real "business
 * logic" lives in the Service classes (UserService, ProductService,
 * OrderService) and the model classes (User, Product, Cart, Order...).
 *
 * This separation of concerns is a key OOP / software design principle:
 * each class has ONE clear job.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // The three "services" are the backbone of the whole application.
    // They also load their saved data (users.txt / products.txt / orders.txt)
    // as soon as they are created, right when the program starts.
    private static final UserService userService = new UserService();
    private static final ProductService productService = new ProductService();
    private static final OrderService orderService = new OrderService();

    // Currently logged-in user, and their active shopping cart.
    private static User currentUser = null;
    private static Cart currentCart = new Cart();

    // keeps the last few products the customer opened using "view details"
    // a Queue is a good fit here because we only care about the most recent ones
    private static Queue<String> recentlyViewed = new LinkedList<>();
    private static final int RECENT_VIEW_LIMIT = 5;

    public static void main(String[] args) {
        printLine();
        System.out.println("      FLIPKART CONSOLE STORE");
        printLine();
        System.out.println("(Admin login -> username: admin / password: admin123)");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showAuthMenu();
            } else if (currentUser.isAdmin()) {
                showAdminMenu();
            } else {
                showCustomerMenu();
            }
        }

        System.out.println("Thank you for visiting Flipkart Console Store. Goodbye!");
        scanner.close();
    }

    // ---------------------------------------------------------------
    // AUTHENTICATION (Register / Login)
    // ---------------------------------------------------------------

    /** Shown when nobody is logged in yet. Returns false if the user chose to exit. */
    private static boolean showAuthMenu() {
        printLine();
        System.out.println("MAIN MENU");
        printLine();
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        int choice = readInt("Choose an option: ");

        switch (choice) {
            case 1:
                handleRegister();
                break;
            case 2:
                handleLogin();
                break;
            case 3:
                return false;
            default:
                System.out.println("Invalid option, please try again.");
        }
        return true;
    }

    private static void handleRegister() {
        System.out.println("\n-- Register New Account --");
        String username = readLine("Choose a username: ");
        if (userService.usernameExists(username)) {
            System.out.println("Sorry, that username is already taken.");
            return;
        }
        String password = readLine("Choose a password: ");
        String email = readLine("Enter your email: ");

        // register() always creates a Customer object (see UserService) and saves it to users.txt
        User newUser = userService.register(username, password, email);
        System.out.println("Registration successful! You can now log in as '" + newUser.getUsername() + "'.");
    }

    private static void handleLogin() {
        System.out.println("\n-- Login --");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        User user = userService.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        currentUser = user;
        currentCart = new Cart(); // fresh cart for this session
        recentlyViewed = new LinkedList<>();
        System.out.println("Welcome back, " + user.getUsername() + "! (Role: " + user.getRole() + ")");
    }

    // ---------------------------------------------------------------
    // CUSTOMER FLOW
    // ---------------------------------------------------------------

    private static void showCustomerMenu() {
        printLine();
        System.out.println("CUSTOMER MENU (" + currentUser.getUsername() + ")");
        printLine();
        System.out.println(" 1. View all products");
        System.out.println(" 2. Search by name");
        System.out.println(" 3. Search by category");
        System.out.println(" 4. View product details");
        System.out.println(" 5. Add to cart");
        System.out.println(" 6. Remove from cart");
        System.out.println(" 7. Update quantity in cart");
        System.out.println(" 8. View cart");
        System.out.println(" 9. Checkout");
        System.out.println("10. Order history");
        System.out.println("11. Sort products by price");
        System.out.println("12. Top rated products");
        System.out.println("13. Low stock products");
        System.out.println("14. Out of stock products");
        System.out.println("15. Recently viewed products");
        System.out.println("16. Add to wishlist");
        System.out.println("17. Remove from wishlist");
        System.out.println("18. View wishlist");
        System.out.println("19. Logout");
        int choice = readInt("Choose an option: ");

        switch (choice) {
            case 1:
                listAllProducts();
                break;
            case 2:
                searchByName();
                break;
            case 3:
                searchByCategory();
                break;
            case 4:
                viewProductDetails();
                break;
            case 5:
                addToCart();
                break;
            case 6:
                removeFromCart();
                break;
            case 7:
                updateCartQuantity();
                break;
            case 8:
                viewCart();
                break;
            case 9:
                checkout();
                break;
            case 10:
                viewOrderHistory();
                break;
            case 11:
                sortProductsByPrice();
                break;
            case 12:
                showTopRatedProducts();
                break;
            case 13:
                showLowStockProducts();
                break;
            case 14:
                showOutOfStockProducts();
                break;
            case 15:
                showRecentlyViewed();
                break;
            case 16:
                addToWishlist();
                break;
            case 17:
                removeFromWishlist();
                break;
            case 18:
                viewWishlist();
                break;
            case 19:
                logout();
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }

    private static void listAllProducts() {
        System.out.println("\n-- All Products --");
        printProductList(productService.getAllProducts());
    }

    private static void searchByName() {
        String keyword = readLine("Enter product name (or part of it): ");
        printProductList(productService.searchByName(keyword));
    }

    private static void searchByCategory() {
        String category = readLine("Enter category: ");
        printProductList(productService.searchByCategory(category));
    }

    private static void viewProductDetails() {
        int id = readInt("Enter product ID: ");
        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("No product found with ID " + id);
            return;
        }
        System.out.println(product.toDetailString());
        addToRecentlyViewed(product.getName());
    }

    private static void addToRecentlyViewed(String productName) {
        // avoid piling up duplicates one after another
        recentlyViewed.remove(productName);
        recentlyViewed.add(productName);
        if (recentlyViewed.size() > RECENT_VIEW_LIMIT) {
            recentlyViewed.poll(); // remove the oldest one from the front of the queue
        }
    }

    private static void showRecentlyViewed() {
        System.out.println("\n-- Recently Viewed Products --");
        if (recentlyViewed.isEmpty()) {
            System.out.println("You haven't viewed any product details yet.");
            return;
        }
        int count = 1;
        for (String name : recentlyViewed) {
            System.out.println(count + ". " + name);
            count++;
        }
    }

    private static void addToCart() {
        int id = readInt("Enter product ID to add to cart: ");
        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("No product found with ID " + id);
            return;
        }
        int quantity = readInt("Enter quantity: ");
        if (quantity <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }
        if (quantity > product.getStockQuantity()) {
            System.out.println("Sorry, only " + product.getStockQuantity() + " unit(s) in stock.");
            return;
        }
        currentCart.addItem(product, quantity);
        System.out.println(quantity + " x '" + product.getName() + "' added to your cart.");
    }

    private static void removeFromCart() {
        if (currentCart.isEmpty()) {
            System.out.println("Your cart is already empty.");
            return;
        }
        printCartItems();
        int id = readInt("Enter product ID to remove: ");
        boolean removed = currentCart.removeItem(id);
        if (removed) {
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("That item wasn't in your cart.");
        }
    }

    private static void updateCartQuantity() {
        if (currentCart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        printCartItems();
        int id = readInt("Enter product ID to update: ");
        int newQuantity = readInt("Enter new quantity: ");
        boolean updated = currentCart.updateQuantity(id, newQuantity);
        if (updated) {
            System.out.println("Cart updated.");
        } else {
            System.out.println("That item wasn't in your cart.");
        }
    }

    private static void printCartItems() {
        System.out.println("\n-- Your Cart --");
        for (CartItem item : currentCart.getItems()) {
            System.out.println(item);
        }
        System.out.println("Cart Total: Rs." + String.format("%.2f", currentCart.getTotal()));
    }

    private static void viewCart() {
        if (currentCart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        printCartItems();
    }

    private static void checkout() {
        System.out.println("\n-- Checkout --");
        if (currentCart.isEmpty()) {
            System.out.println("Your cart is empty. Add some products first!");
            return;
        }

        System.out.println("Order summary:");
        printCartItems();

        String confirm = readLine("Confirm order? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Checkout cancelled.");
            return;
        }

        String couponCode = readLine("Enter coupon code (or press Enter to skip): ");
        PaymentMethod paymentMethod = choosePaymentMethod();

        try {
            Order order = orderService.checkout(currentUser.getUsername(), currentCart, productService, paymentMethod, couponCode);
            if (order == null) {
                System.out.println("Checkout failed. Please try again.");
                return;
            }
            System.out.println("Order placed successfully! Your order ID is #" + order.getOrderId());
            System.out.println(order.toInvoiceString(currentUser.getUsername()));

        } catch (InsufficientStockException e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }

    private static PaymentMethod choosePaymentMethod() {
        System.out.println("Select Payment Method:");
        System.out.println("1. UPI");
        System.out.println("2. Credit Card");
        System.out.println("3. Debit Card");
        System.out.println("4. Cash on Delivery");
        System.out.println("5. Wallet");
        int choice = readInt("Enter choice: ");

        switch (choice) {
            case 1:
                return PaymentMethod.UPI;
            case 2:
                return PaymentMethod.CREDIT_CARD;
            case 3:
                return PaymentMethod.DEBIT_CARD;
            case 5:
                return PaymentMethod.WALLET;
            default:
                return PaymentMethod.COD;
        }
    }

    private static void viewOrderHistory() {
        System.out.println("\n-- Order History --");
        ArrayList<Order> orders = orderService.getOrderHistory(currentUser.getUsername());
        if (orders.isEmpty()) {
            System.out.println("You have not placed any orders yet.");
            return;
        }
        for (Order order : orders) {
            System.out.println(order.toDetailString());
        }
    }

    private static void sortProductsByPrice() {
        System.out.println("1. Low to High");
        System.out.println("2. High to Low");
        int choice = readInt("Choose an option: ");

        if (choice == 1) {
            printProductList(productService.sortByPriceLowToHigh());
        } else if (choice == 2) {
            printProductList(productService.sortByPriceHighToLow());
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void showTopRatedProducts() {
        System.out.println("\n-- Top Rated Products --");
        ArrayList<Product> sorted = productService.getTopRatedProducts();
        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            System.out.println(sorted.get(i).toShortString());
        }
    }

    private static void showLowStockProducts() {
        System.out.println("\n-- Low Stock Products (5 or less) --");
        printProductList(productService.getLowStockProducts());
    }

    private static void showOutOfStockProducts() {
        System.out.println("\n-- Out of Stock Products --");
        printProductList(productService.getOutOfStockProducts());
    }

    // ---------------------------------------------------------------
    // WISHLIST (only customers have one - see Customer.java)
    // ---------------------------------------------------------------

    private static void addToWishlist() {
        int id = readInt("Enter product ID to add to wishlist: ");
        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("No product found with ID " + id);
            return;
        }
        Customer customer = (Customer) currentUser; // safe: only customers see this menu
        boolean added = customer.addToWishlist(product);
        if (added) {
            System.out.println("'" + product.getName() + "' added to your wishlist.");
        } else {
            System.out.println("That product is already in your wishlist.");
        }
    }

    private static void removeFromWishlist() {
        Customer customer = (Customer) currentUser;
        if (customer.getWishlist().isEmpty()) {
            System.out.println("Your wishlist is empty.");
            return;
        }
        printWishlist(customer);
        int id = readInt("Enter product ID to remove: ");
        Product product = productService.getProductById(id);
        if (product == null) {
            System.out.println("No product found with ID " + id);
            return;
        }
        boolean removed = customer.removeFromWishlist(product);
        System.out.println(removed ? "Removed from wishlist." : "That product wasn't in your wishlist.");
    }

    private static void viewWishlist() {
        Customer customer = (Customer) currentUser;
        System.out.println("\n-- Your Wishlist --");
        printWishlist(customer);
    }

    private static void printWishlist(Customer customer) {
        ArrayList<Product> items = customer.getWishlist();
        if (items.isEmpty()) {
            System.out.println("Your wishlist is empty.");
            return;
        }
        for (Product product : items) {
            System.out.println(product.toShortString());
        }
    }

    private static void logout() {
        System.out.println("Logging out '" + currentUser.getUsername() + "'...");
        currentUser = null;
        currentCart = new Cart();
    }

    // ---------------------------------------------------------------
    // ADMIN FLOW
    // ---------------------------------------------------------------

    private static void showAdminMenu() {
        printLine();
        System.out.println("ADMIN MENU (" + currentUser.getUsername() + ")");
        printLine();
        System.out.println("1. View all products");
        System.out.println("2. Add new product");
        System.out.println("3. Update product");
        System.out.println("4. Delete product");
        System.out.println("5. View total products");
        System.out.println("6. Logout");
        int choice = readInt("Choose an option: ");

        switch (choice) {
            case 1:
                listAllProducts();
                break;
            case 2:
                adminAddProduct();
                break;
            case 3:
                adminUpdateProduct();
                break;
            case 4:
                adminDeleteProduct();
                break;
            case 5:
                System.out.println("Total products in catalog: " + productService.getTotalProductCount());
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }

    private static void adminAddProduct() {
        System.out.println("\n-- Add New Product --");
        String name = readLine("Name: ");
        String brand = readLine("Brand: ");
        String category = readLine("Category: ");
        double price = readDouble("Price (MRP): ");
        double discount = readDouble("Discount %: ");
        double rating = readDouble("Rating (0-5): ");
        int stock = readInt("Stock quantity: ");
        String description = readLine("Description: ");

        // addProduct() also saves the updated catalog to products.txt
        Product product = productService.addProduct(name, brand, category, price, discount, rating, stock, description);
        System.out.println("Product added with ID " + product.getId());
    }

    private static void adminUpdateProduct() {
        System.out.println("\n-- Update Product --");
        int id = readInt("Enter product ID to update: ");
        Product existing = productService.getProductById(id);
        if (existing == null) {
            System.out.println("No product found with ID " + id);
            return;
        }
        System.out.println("Leave a field blank to keep its current value.");
        System.out.println("Current details:\n" + existing.toDetailString());

        String name = readLine("New name: ");
        String brand = readLine("New brand: ");
        String category = readLine("New category: ");
        String priceStr = readLine("New price: ");
        String discountStr = readLine("New discount %: ");
        String ratingStr = readLine("New rating: ");
        String stockStr = readLine("New stock quantity: ");
        String description = readLine("New description: ");

        Double price = priceStr.isBlank() ? null : Double.parseDouble(priceStr);
        Double discount = discountStr.isBlank() ? null : Double.parseDouble(discountStr);
        Double rating = ratingStr.isBlank() ? null : Double.parseDouble(ratingStr);
        Integer stock = stockStr.isBlank() ? null : Integer.parseInt(stockStr);

        // updateProduct() also saves the updated catalog to products.txt
        boolean updated = productService.updateProduct(id, name, brand, category, price, discount, rating, stock, description);
        System.out.println(updated ? "Product updated." : "Update failed.");
    }

    private static void adminDeleteProduct() {
        int id = readInt("Enter product ID to delete: ");
        // deleteProduct() also saves the updated catalog to products.txt
        boolean deleted = productService.deleteProduct(id);
        System.out.println(deleted ? "Product deleted." : "No product found with that ID.");
    }

    // ---------------------------------------------------------------
    // SMALL HELPER METHODS (input handling + printing)
    // ---------------------------------------------------------------

    private static void printLine() {
        System.out.println("====================================");
    }

    private static void printProductList(ArrayList<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product product : products) {
            System.out.println(product.toShortString());
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
