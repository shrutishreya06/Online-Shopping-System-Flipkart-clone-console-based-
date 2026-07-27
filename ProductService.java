import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ProductService.java
 *
 * Manages the full product catalog. This is the class the Admin
 * panel talks to for adding/updating/deleting products, and that
 * the customer-facing screens talk to for listing/searching products.
 *
 * Uses a HashMap<Integer, Product> so that looking up a product by
 * its id (e.g. "add product #5 to cart") is fast, while ArrayLists
 * are used whenever we need an ordered collection of results
 * (full listing, search results, etc).
 *
 * The catalog is also saved to products.txt so it survives a restart
 * of the program.
 */
public class ProductService {

    private static final String PRODUCT_FILE = "products.txt";

    // key = product id, value = Product object
    private final HashMap<Integer, Product> productMap = new HashMap<>();

    public ProductService() {
        loadProductsFromFile();
        // if the file did not exist yet (very first run), start with a few sample products
        if (productMap.isEmpty()) {
            seedSampleProducts();
        }
    }

    /** Pre-loads a few sample products so the store isn't empty on first run. */
    private void seedSampleProducts() {
        addProduct("Wireless Mouse", "Logitech", "Electronics", 599.00, 10, 4.2, 50, "Ergonomic 2.4GHz wireless mouse");
        addProduct("Mechanical Keyboard", "Redgear", "Electronics", 2499.00, 15, 4.4, 30, "RGB backlit mechanical keyboard");
        addProduct("Cotton T-Shirt", "Levis", "Clothing", 399.00, 20, 3.9, 100, "Men's plain round-neck cotton t-shirt");
        addProduct("Running Shoes", "Nike", "Footwear", 1999.00, 25, 4.6, 40, "Lightweight breathable running shoes");
        addProduct("Non-stick Pan", "Prestige", "Home & Kitchen", 899.00, 5, 4.0, 25, "26cm non-stick frying pan");
        addProduct("Java Programming Book", "McGraw Hill", "Books", 549.00, 10, 4.7, 60, "Beginner-friendly guide to Java");
        addProduct("Bluetooth Earbuds", "boAt", "Electronics", 1499.00, 30, 4.1, 3, "Wireless earbuds with mic");
        addProduct("Formal Shirt", "Allen Solly", "Clothing", 1199.00, 10, 3.7, 0, "Slim fit formal shirt for office wear");
    }

    /** Adds a brand-new product to the catalog (used by Admin). */
    public Product addProduct(String name, String brand, String category, double price,
                               double discountPercent, double rating, int stock, String description) {
        Product product = new Product(name, brand, category, price, discountPercent, rating, stock, description);
        productMap.put(product.getId(), product);
        saveProductsToFile();
        return product;
    }

    /** Updates an existing product's details (used by Admin). Returns false if id not found. */
    public boolean updateProduct(int id, String name, String brand, String category, Double price,
                                  Double discountPercent, Double rating, Integer stock, String description) {
        Product product = productMap.get(id);
        if (product == null) {
            return false;
        }
        if (name != null && !name.isBlank()) product.setName(name);
        if (brand != null && !brand.isBlank()) product.setBrand(brand);
        if (category != null && !category.isBlank()) product.setCategory(category);
        if (price != null) product.setPrice(price);
        if (discountPercent != null) product.setDiscountPercent(discountPercent);
        if (rating != null) product.setRating(rating);
        if (stock != null) product.setStockQuantity(stock);
        if (description != null && !description.isBlank()) product.setDescription(description);
        saveProductsToFile();
        return true;
    }

    /** Removes a product from the catalog (used by Admin). */
    public boolean deleteProduct(int id) {
        boolean removed = productMap.remove(id) != null;
        if (removed) {
            saveProductsToFile();
        }
        return removed;
    }

    public Product getProductById(int id) {
        return productMap.get(id);
    }

    public int getTotalProductCount() {
        return productMap.size();
    }

    /** Returns every product currently in the catalog. */
    public ArrayList<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    /** Case-insensitive search by (partial) product name. */
    public ArrayList<Product> searchByName(String keyword) {
        ArrayList<Product> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Product product : productMap.values()) {
            if (product.getName().toLowerCase().contains(lowerKeyword)) {
                results.add(product);
            }
        }
        return results;
    }

    /** Case-insensitive search by category. */
    public ArrayList<Product> searchByCategory(String category) {
        ArrayList<Product> results = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                results.add(product);
            }
        }
        return results;
    }

    /** Case-insensitive search by brand. */
    public ArrayList<Product> searchByBrand(String brand) {
        ArrayList<Product> results = new ArrayList<>();
        String lowerBrand = brand.toLowerCase();
        for (Product product : productMap.values()) {
            if (product.getBrand().toLowerCase().contains(lowerBrand)) {
                results.add(product);
            }
        }
        return results;
    }

    /**
     * Returns all products sorted by final price, low to high.
     * Uses bubble sort - simple O(n^2) algorithm, fine for a small catalog.
     */
    public ArrayList<Product> sortByPriceLowToHigh() {
        ArrayList<Product> list = getAllProducts();
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Product p1 = list.get(j);
                Product p2 = list.get(j + 1);
                if (p1.getFinalPrice() > p2.getFinalPrice()) {
                    list.set(j, p2);
                    list.set(j + 1, p1);
                }
            }
        }
        return list;
    }

    /** Same idea as above, but high to low. */
    public ArrayList<Product> sortByPriceHighToLow() {
        ArrayList<Product> list = getAllProducts();
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Product p1 = list.get(j);
                Product p2 = list.get(j + 1);
                if (p1.getFinalPrice() < p2.getFinalPrice()) {
                    list.set(j, p2);
                    list.set(j + 1, p1);
                }
            }
        }
        return list;
    }

    /** Returns products sorted by rating, highest first. */
    public ArrayList<Product> getTopRatedProducts() {
        ArrayList<Product> list = getAllProducts();
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Product p1 = list.get(j);
                Product p2 = list.get(j + 1);
                if (p1.getRating() < p2.getRating()) {
                    list.set(j, p2);
                    list.set(j + 1, p1);
                }
            }
        }
        return list;
    }

    /** Products with stock between 1 and 5 (low stock, but not out of stock). */
    public ArrayList<Product> getLowStockProducts() {
        ArrayList<Product> results = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getStockQuantity() > 0 && product.getStockQuantity() <= 5) {
                results.add(product);
            }
        }
        return results;
    }

    /** Products with zero stock left. */
    public ArrayList<Product> getOutOfStockProducts() {
        ArrayList<Product> results = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getStockQuantity() == 0) {
                results.add(product);
            }
        }
        return results;
    }

    // ---------------------------------------------------------------
    // FILE HANDLING - keeps products.txt in sync with the catalog
    // ---------------------------------------------------------------

    private void loadProductsFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PRODUCT_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                // limit 9 so that a "|" inside the description does not break the split
                String[] parts = line.split("\\|", 9);
                if (parts.length < 9) {
                    continue; // skip a broken/incomplete line instead of crashing
                }
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String brand = parts[2];
                String category = parts[3];
                double price = Double.parseDouble(parts[4]);
                double discountPercent = Double.parseDouble(parts[5]);
                double rating = Double.parseDouble(parts[6]);
                int stock = Integer.parseInt(parts[7]);
                String description = parts[8];

                Product product = new Product(id, name, brand, category, price, discountPercent, rating, stock, description);
                productMap.put(id, product);
            }
        } catch (IOException e) {
            // products.txt does not exist yet on the very first run - that's expected
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

    private void saveProductsToFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(PRODUCT_FILE, false); // false = overwrite with the latest catalog
            for (Product product : productMap.values()) {
                writer.write(product.getId() + "|" + product.getName() + "|" + product.getBrand() + "|"
                        + product.getCategory() + "|" + product.getPrice() + "|" + product.getDiscountPercent() + "|"
                        + product.getRating() + "|" + product.getStockQuantity() + "|" + product.getDescription() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Note: could not save products to file (" + e.getMessage() + ")");
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
