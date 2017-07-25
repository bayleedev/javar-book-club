import java.util.ArrayList;
import java.util.HashMap;

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

}

class Order {

    // value is quantity
    HashMap<Item, Integer> items = new HashMap<Item, Integer>();


    public void addItem(int quantity, String name, int perUnitPennyCost) {
        Item key = new Item(name, perUnitPennyCost);
        if (items.containsKey(key)) {
            items.put(key, items.get(key) + quantity);
        } else {
            items.put(key, quantity);
        }
    }


    public void removeItem(int quantity, String name, int perUnitPennyCost) {
        Item key = new Item(name, perUnitPennyCost);
        if (items.containsKey(key)) {
            items.put(key, items.get(key) - quantity);
        }
    }

    public int getTotalInPennies() {
        return 0;
    }
}

class Register {
    ArrayList<Order> orders = new ArrayList<Order>();

    public void addOrder(Order order) {

    }

    public int close() {

    }
}

class Restaurant implements DistributedCashRegister2 {

    HashMap<Integer, Order> openOrders = new HashMap<Integer, Order>();

    // Holds closed cash orders
    ArrayList<Register> registers = new ArrayList<Register>();

    public int openOrder() {
        return 0;
    }

    public void addItem(int quantity, String name, int perUnitPennyCost, int orderId) {
        openOrders.get(orderId).addItem(quantity, name, perUnitPennyCost);
    }

    public void removeItem(int quantity, String name, int perUnitPennyCost, int orderId) {
        openOrders.get(orderId).removeItem(quantity, name, perUnitPennyCost);

    }

    public int totalOrderInPennies(int orderId) {
        return openOrders.get(orderId).getTotalInPennies();
    }

    public int closeOrder(int orderId, PaymentMethod paymentMethod) {
        Order order = openOrders.get(orderId);
        openOrders.remove(orderId);
        if (paymentMethod == PaymentMethod.CASH) {
            int register = magic(orderId);
            registers.get(register).addOrder(order);
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
}