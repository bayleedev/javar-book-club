import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class Item {

    public String name;

    public int perUnitPennyCost;

    public Item (String name, int perUnitPennyCost) {
        this.name = name;
        this.perUnitPennyCost = perUnitPennyCost;
    }

    public int getHashCode () {
        return 0; // do something with name and perUnitPennyCost
    }

    public int getPerUnitPennyCost () {
        return perUnitPennyCost;
    }

}

class Order {

    // value is quantity
    private HashMap<Item, Integer> items = new HashMap<Item, Integer>();

    public synchronized void addItem(int quantity, String name, int perUnitPennyCost) {
        Item key = new Item(name, perUnitPennyCost);
        if (items.containsKey(key)) {
            items.put(key, items.get(key) + quantity);
        } else {
            items.put(key, quantity);
        }
    }

    public synchronized void removeItem(int quantity, String name, int perUnitPennyCost) {
        Item key = new Item(name, perUnitPennyCost);
        if (items.containsKey(key)) {
            items.put(key, items.get(key) - quantity);
        }
    }

    public synchronized int getTotalInPennies() {
        int sum = 0;
        for (Map.Entry<Item, Integer> keyValue : items.entrySet()) {
            Item item = keyValue.getKey();
            int quantity = keyValue.getValue();
            sum += (item.getPerUnitPennyCost() * quantity);
        }
        return sum;
    }
}

class Register {
    private ArrayList<Order> orders = new ArrayList<Order>();

    synchronized void addOrder(Order order) {
        orders.add(order);
    }

    int close() {
        ArrayList<Order> closedOrders;

        synchronized (this) {
            closedOrders = (ArrayList<Order>)orders.clone();
            orders = new ArrayList<Order>();
        }

        int sum = 0;

        for (Order o : closedOrders) {
            sum += o.getTotalInPennies();
        }

        return sum;
    }
}

class Restaurant implements DistributedCashRegister2 {

    private AtomicInteger currentOrderId = new AtomicInteger(0);
    private HashMap<Integer, Order> openOrders = new HashMap<Integer, Order>();
    private ArrayList<Register> registers = new ArrayList<Register>();

    public void Restaurant (int numberOfRegisters) {
        synchronized (registers) {
            for (int i = 0; i < numberOfRegisters; i++) {
                registers.add(new Register());
            }
        }
    }

    public int openOrder() {
        int nextOrderId = currentOrderId.incrementAndGet();
        synchronized (openOrders) {
            openOrders.put(nextOrderId, new Order());
        }
        return nextOrderId;
    }

    public void addItem(int quantity, String name, int perUnitPennyCost, int orderId) {
        getOpenOrder(orderId).addItem(quantity, name, perUnitPennyCost);
    }

    public void removeItem(int quantity, String name, int perUnitPennyCost, int orderId) {
        getOpenOrder(orderId).removeItem(quantity, name, perUnitPennyCost);
    }

    public int totalOrderInPennies(int orderId) {
        return getOpenOrder(orderId).getTotalInPennies();
    }

    public int closeOrder(int orderId, PaymentMethod paymentMethod) {
        Order order;
        synchronized (openOrders) {
            order = openOrders.get(orderId);
            openOrders.remove(orderId);
        }
        if (paymentMethod == PaymentMethod.CASH) {
            int register = magic(orderId);
            getRegister(register).addOrder(order);
        }
        return order.getTotalInPennies();
    }

    public int closeOutAllRegisters() {
        int total = 0;
        for (Register register : registers) {
            total += register.close();
        }
        return total;
    }

    private Order getOpenOrder (int orderId) {
        synchronized (openOrders) {
            return openOrders.get(orderId);
        }
    }

    private Register getRegister (int registerId) {
        synchronized (registers) {
            return registers.get(registerId);
        }
    }

    private int magic (int orderId) {
        int registerCount;
        synchronized (registers) {
            registerCount = registers.size();
        }
        return orderId % registerCount;
    }
}
