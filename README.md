# Flipkart Console Store

A console based online shopping system built in **Core Java** as part of my
Summer Internship (Java + DSA) with CipherSchools. It works like a mini
Flipkart/Amazon clone that runs completely inside the terminal - no
database, no internet, no external library. Product, user and order data
is now saved to plain text files, so the store's data survives closing
and reopening the program.

## Project Overview
The system has two types of users - **Customer** and **Admin** - both
now modeled through proper inheritance (`Customer extends User`,
`Admin extends User`). A customer can register, log in, browse
products, search them, maintain a wishlist, manage a cart, checkout
with a payment method and an optional coupon, and see their past
orders with a generated invoice. An Admin can manage the product
catalog (add, update, delete, view).

## Features

### Customer
- Register / Login
- View all products
- Search product by name
- Search product by category
- View full product details
- Add product to cart
- Remove product from cart
- Update quantity of a product in cart
- View cart with subtotal and total
- Checkout with an optional coupon code and a payment method
  (UPI / Credit Card / Debit Card / Cash on Delivery / Wallet)
- Auto generated console invoice after checkout (shows discount, coupon
  and final amount)
- Order history
- Sort products by price (low to high / high to low)
- Top rated products
- Low stock products
- Out of stock products
- Recently viewed products (uses a Queue)
- **Wishlist** - add / remove / view products in a personal wishlist
  (uses a `HashSet<Product>`, so a product can never appear twice)

### Admin
- Login (seeded admin account)
- View all products
- Add new product
- Update existing product
- Delete product
- View total number of products

### Product Details
Every product has: ID, Name, Brand, Category, Description, Price (MRP),
Discount %, Final Price (calculated), Rating, Stock Quantity.

### Coupons (applied at checkout, optional)
| Code | Effect |
|---|---|
| `SAVE10` | 10% off the cart subtotal |
| `FLAT100` | Flat Rs.100 off (never more than the bill itself) |

Any other code (or leaving it blank) simply means no discount is applied.

## Technologies Used
- Java (Core Java only, no frameworks)
- Collections: ArrayList, HashMap, HashSet, Queue (LinkedList)
- File Handling: FileReader/FileWriter (BufferedReader for reading) to
  persist `users.txt`, `products.txt` and `orders.txt`
- Exception Handling: try-catch + one custom exception
- Inheritance: `Customer` and `Admin` both extend `User`

No Maven/Gradle, no Spring, no external jar - just plain `.java` files
that can be compiled directly.

## Folder Structure
```
shopping-system/
 |- Role.java                    (enum: CUSTOMER / ADMIN)
 |- PaymentMethod.java           (enum: UPI / CREDIT_CARD / DEBIT_CARD / COD / WALLET)
 |- User.java                    (abstract base model - id, username, password, email, role)
 |- Customer.java                (extends User - adds the wishlist)
 |- Admin.java                   (extends User)
 |- Product.java                 (model)
 |- CartItem.java                (model)
 |- Cart.java                    (holds cart items of current session)
 |- OrderItem.java                (model - snapshot of a bought product)
 |- Order.java                   (model - a placed order + invoice text)
 |- InsufficientStockException.java (custom exception)
 |- UserService.java             (register/login logic + users.txt persistence)
 |- ProductService.java          (catalog CRUD + search + sorting + products.txt persistence)
 |- OrderService.java            (checkout + coupons + order history + orders.txt persistence)
 |- Main.java                    (console menus, program entry point)
 |- README.md
 |- Viva_Guide.md
 |- Project_Report.md
```

## How to Run

### Using IntelliJ IDEA
1. Open IntelliJ -> `Open` -> select this project folder.
2. Let IntelliJ index the files (no Maven/Gradle setup needed, it's a
   plain Java module).
3. Right click on `Main.java` -> `Run 'Main.main()'`.
4. Use the console window at the bottom to interact with the menus.

### Using Terminal / Command Prompt
```
javac *.java
java Main
```

A ready-made admin account is available on first run:
- Username: `admin`
- Password: `admin123`

## Data Persistence (New!)
Unlike before, data is no longer lost when the program closes:

- **`users.txt`** - every registered account (username, password, email,
  role). Written again automatically every time someone registers.
- **`products.txt`** - the entire product catalog. Written again
  automatically every time the admin adds, updates or deletes a product.
- **`orders.txt`** - every completed order (with its items, coupon and
  payment method). Written again automatically every time a customer
  checks out.

All three files are loaded automatically the moment the program starts.
If a file does not exist yet (the very first run), the program simply
starts empty for that file and creates it as soon as there's something
to save (for products, a few sample products are added on the very
first run so the store isn't empty).

Note: the Wishlist is intentionally **not** saved to a file - it is
part of the `Customer` object only for the current run, similar to the
shopping cart and the recently-viewed list.

## OOP Concepts Used
- **Inheritance** - `Customer` and `Admin` both extend the abstract
  `User` class, instead of the old approach of having one `User` class
  with a `role` field checked everywhere.
- **Encapsulation** - all fields are private/protected, accessed only
  through getters/setters or specific methods like `reduceStock()`.
- **Abstraction** - `Main` does not know *how* a product is stored or
  searched, it just calls `productService.searchByName(...)`.
- **Method Overriding** - `isAdmin()` is declared `abstract` in `User`
  and overridden differently in `Customer` (`false`) and `Admin`
  (`true`); `toString()` is overridden in most model classes too.
- **Enums** - `Role` and `PaymentMethod` are used instead of plain
  Strings to avoid typing mistakes.
- **equals() / hashCode()** - overridden in `Product` (based on product
  id) so the `HashSet<Product>` used by the wishlist can correctly
  detect duplicates.

## DSA Concepts Used
- **ArrayList** - product listing, search results, cart items, order
  items.
- **HashMap** - fast lookup of products by id, users by username, and
  order history by username.
- **HashSet** - the customer wishlist, which must never contain the
  same product twice.
- **Queue (LinkedList)** - keeps track of the last 5 recently viewed
  products, oldest is removed first (FIFO).
- **Bubble Sort** - manually implemented to sort products by price and
  by rating, since that's one of the core sorting algorithms taught in
  the DSA part of the internship.
- **Linear Search** - used inside search by name/category/brand.

## Future Scope
- Replace the plain text files with a real database (MySQL/SQLite).
- Add product images (would need a GUI, not possible in console).
- Add order cancellation / return flow.
- Persist the wishlist too, once user-specific files are introduced.
- Add more coupon types (e.g. minimum order value, expiry date).
- Split classes into packages (model, service, ui) as the project grows
  bigger.
