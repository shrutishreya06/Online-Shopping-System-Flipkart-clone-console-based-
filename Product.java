/**
 * Product.java
 *
 * Represents a single product that can be listed, searched, bought,
 * and managed by an admin. Like User, this is a model class.
 */
public class Product {

    private static int nextId = 1;

    private final int id;
    private String name;
    private String brand;
    private String category;
    private double price;
    private double discountPercent;
    private double rating;
    private int stockQuantity;
    private String description;

    public Product(String name, String brand, String category, double price, double discountPercent,
                    double rating, int stockQuantity, String description) {
        this.id = nextId++;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.discountPercent = discountPercent;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    /**
     * This second constructor is used ONLY when loading products back
     * from products.txt when the program starts. It takes the exact id
     * that was saved earlier, instead of generating a new one, so a
     * product keeps the same id across restarts (important, because
     * past orders remember a product's id).
     */
    public Product(int id, String name, String brand, String category, double price, double discountPercent,
                    double rating, int stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.discountPercent = discountPercent;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.description = description;
        // make sure future brand-new products don't reuse an id that was loaded from file
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // final price is not stored, it is calculated from price and discount
    // this way the final price always stays correct even after an update
    public double getFinalPrice() {
        double discountAmount = (price * discountPercent) / 100;
        return price - discountAmount;
    }

    /**
     * Reduces stock when an order is placed.
     * Returns true if there was enough stock to fulfil the request.
     */
    public boolean reduceStock(int quantity) {
        if (quantity <= 0 || quantity > stockQuantity) {
            return false;
        }
        stockQuantity -= quantity;
        return true;
    }

    public String getStockStatus() {
        if (stockQuantity == 0) {
            return "OUT OF STOCK";
        } else if (stockQuantity <= 5) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }

    /** Short one-line summary, used in product listings. */
    public String toShortString() {
        return "[ID:" + id + "] " + name + " (" + brand + ")"
                + " | " + category
                + " | MRP: Rs." + price
                + " | Off: " + discountPercent + "%"
                + " | Price: Rs." + roundTwo(getFinalPrice())
                + " | Rating: " + rating
                + " | Stock: " + stockQuantity + " (" + getStockStatus() + ")";
    }

    /** Full detail view, used when a customer opens a single product. */
    public String toDetailString() {
        String line = "----------------------------------------";
        StringBuilder sb = new StringBuilder();
        sb.append(line).append("\n");
        sb.append("Product ID     : ").append(id).append("\n");
        sb.append("Name           : ").append(name).append("\n");
        sb.append("Brand          : ").append(brand).append("\n");
        sb.append("Category       : ").append(category).append("\n");
        sb.append("MRP            : Rs.").append(price).append("\n");
        sb.append("Discount       : ").append(discountPercent).append("%\n");
        sb.append("Final Price    : Rs.").append(roundTwo(getFinalPrice())).append("\n");
        sb.append("Rating         : ").append(rating).append(" / 5\n");
        sb.append("Stock          : ").append(stockQuantity).append(" (").append(getStockStatus()).append(")\n");
        sb.append("Description    : ").append(description).append("\n");
        sb.append(line);
        return sb.toString();
    }

    // small helper just to avoid ugly long decimal values on screen
    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Two products are considered equal if they have the same id.
     * This is important for the Customer's wishlist, which is a
     * HashSet<Product> - a HashSet relies on equals()/hashCode() to
     * detect duplicates, so without this override every Product object
     * would be treated as unique even if it represents the same product.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        Product other = (Product) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return toShortString();
    }
}
