/**
 * TODO implement this interface create a threadsafe, in-memory backend for a group of cash registers at a restaurant.
 * Don't worry about persistence, since the restaurant has paper order slips in case of crash.
 * Web server will be hooked up to call the backend with multiple concurrent threads.
 * Possible but unlikely the same order will be updated at the same time from different registers.
 */
public interface DistributedCashRegister2 {
    enum PaymentMethod { CASH, CREDIT }

    // new patrons at a table will be allocated a new order id
    int openOrder();

    // add one or more items to the bill
    void addItem(int quantity, String name, int perUnitPennyCost, int orderId);

    // remove/comp one or more items from the bill
    void removeItem(int quantity, String name, int perUnitPennyCost, int orderId);

    // sum up the bill for presentation to patrons
    int totalOrderInPennies(int orderId);

    // close out the order after the bill has been paid and the patrons have left
    int closeOrder(int orderId, PaymentMethod paymentMethod );

    // Register cash drawers are replaced at the end of every shift.
    // This method is called at the end of a shift.
    // Cash drawers contain ONLY cash payments.
    // Atomically zero out all register totals and return (100% guaranteed) sum of total in pennies in all registers
    int closeOutAllRegisters();
}