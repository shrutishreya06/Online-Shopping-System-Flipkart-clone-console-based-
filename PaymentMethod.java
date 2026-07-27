/**
 * PaymentMethod.java
 *
 * Enum for the payment options available at checkout.
 * Same idea as Role.java - using an enum instead of plain text
 * avoids typing mistakes like "upi" vs "UPI" vs "Upi".
 */
public enum PaymentMethod {
    UPI,
    CREDIT_CARD,
    DEBIT_CARD,
    COD,
    WALLET;

    // gives a nicer looking name for the invoice / menus
    public String getDisplayName() {
        if (this == UPI) {
            return "UPI";
        } else if (this == CREDIT_CARD) {
            return "Credit Card";
        } else if (this == DEBIT_CARD) {
            return "Debit Card";
        } else if (this == WALLET) {
            return "Wallet";
        } else {
            return "Cash on Delivery";
        }
    }
}
