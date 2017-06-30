import java.util.ArrayList;

public class Chapter3 {
    public static void main (String[] args) {
        AcmeRestaurant restaurant = new AcmeRestaurant();
        int id1 = restaurant.openOrder();
        restaurant.addItemsWorth(10, id1);
        restaurant.removeItemsWorth(2, id1);

        int id2 = restaurant.openOrder();
        restaurant.addItemsWorth(10, id2);
        restaurant.removeItemsWorth(2, id2);

        System.out.println(restaurant.dailyTakeInPennies());
    }
}

class AcmeRestaurant implements DistributedCashRegister {
    private ArrayList<Order> orders = new ArrayList<Order>();
    // private CopyOnWriteArrayList<Order> orders = new CopyOnWriteArrayList<Order>();

    public int openOrder () {
        synchronized (this.orders) {
            int nextOrderId = this.orders.size();
            this.orders.add(new Order());
            return nextOrderId;
        }
    }

    public int dailyTakeInPennies () {
        synchronized (this.orders) {
            int total = 0;
            for (Order order : this.orders) {
                total = total + order.total();
            }
            return total;
        }
    }

    private Order getOrder (int orderId) {
        synchronized (this.orders) {
            return this.orders.get(orderId);
        }
    }

    public void addItemsWorth (int pennies, int orderId) {
        Order currentOrder = this.getOrder(orderId);
        currentOrder.increment(pennies);
    }

    public void removeItemsWorth (int pennies, int orderId) {
        Order currentOrder = this.getOrder(orderId);
        currentOrder.decrement(pennies);
    }

    public int totalOrderInPennies (int orderId) {
        Order currentOrder = this.getOrder(orderId);
        return currentOrder.total();
    }

    public void closeOrder (int orderId) {
        Order currentOrder = this.getOrder(orderId);
        currentOrder.close();
    }
}

class Order {
    private int pennies = 0;
    private boolean open = true;

    synchronized void increment (int pennies) {
        this.pennies = this.pennies + pennies;
    }

    synchronized void decrement (int pennies) {
        this.pennies = this.pennies - pennies;
    }

    // this doesn't do anything....
    synchronized void close () {
        this.open = false;
    }

    synchronized int total () {
        return this.pennies;
    }
}

/**
 * TODO implement this interface create a threadsafe, in-memory backend for a group of cash registers at a restaurant.
 * Don't worry about persistence, since the restaurant has paper order slips in case of crash.
 * Web server will be hooked up to call the backend with multiple concurrent threads.
 * Possible but unlikely the same order will be updated at the same time from different registers.
 */
interface DistributedCashRegister {
    // new patrons at a table will be allocated a new order id
    int openOrder();

    // add one or more items to the bill worth the supplied amount in pennies
    void addItemsWorth( int pennies, int orderId );

    // remove/comp one or more items from the bill worth the supplied amount in pennies
    void removeItemsWorth( int pennies, int orderId );

    // sum up the bill for presentation to patrons
    int totalOrderInPennies( int orderId );

    // close out the order after the bill has been paid and the patrons have left
    void closeOrder( int orderId );

    // Sum of all restaurant bills for the current calendar day.
    int dailyTakeInPennies();
}