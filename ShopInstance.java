package p.officertom.shop;

import p.officertom.shop.Data.LocalProductNode;
import p.officertom.shop.Data.ProductNode;

import java.util.ArrayList;

public class ShopInstance {

    private String name;
    private double profitMargin;
    private long deliveryDelay;
    private long nextDelivery;
    private long nextOrder;

    private ArrayList<LocalProductNode> inventory;

    public void tryUpdate(long currentTime) {
        if (currentTime >= nextOrder) {
            makeOrder();

            nextOrder += deliveryDelay;
            nextDelivery = nextOrder + (deliveryDelay / 2);
        }

        if (currentTime >= nextDelivery) {
            receiveOrder();

            nextDelivery += deliveryDelay;
            nextOrder = nextDelivery + (deliveryDelay / 2);
        }
    }

    private void receiveOrder() {
        int amountReceiving;
        for (LocalProductNode localProductNode : inventory) {
            amountReceiving = localProductNode.getOnOrder() * localProductNode.getPackSize();
            if (amountReceiving > 0) {
                localProductNode.addBalance(amountReceiving);
                localProductNode.resetOnOrder();
            }
        }
    }

    private void makeOrder() {
        int balance;
        int minimum;
        int sold;
        int packSize;
        int forecast;
        int amountOrdered;

        for (LocalProductNode localProductNode : inventory) {
            balance = localProductNode.getBalance();
            minimum = localProductNode.getMinimum();
            sold = localProductNode.getSold();
            packSize = localProductNode.getPackSize();

            forecast = Math.max(sold, minimum - balance);

            amountOrdered = Math.max((int) Math.round(((minimum + forecast) - balance) / (double) packSize), 0);

            //Random chance for distribution: adds 2-6 extra cases
            if ((Math.random() * 100) <= 2.0)
                amountOrdered += (int) (Math.random() * 5) + 2;

            localProductNode.setOnOrder(amountOrdered);
            localProductNode.resetSold();
        }
    }

    protected void updatePrices() {
        inventory.forEach(localProductNode -> localProductNode.updatePrice());
    }

    public void registerItem(ProductNode productNode, int balance, int minimum, int sold, int onOrder) {
        LocalProductNode localNode = (LocalProductNode) productNode;
        localNode.convert(this, balance, minimum, sold, onOrder);
        inventory.add(localNode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    public void setTimes(int deliveryDelay, long nextDelivery) {
        this.deliveryDelay = deliveryDelay * 3600000;

        this.nextDelivery =
                (System.currentTimeMillis() - (System.currentTimeMillis() % this.deliveryDelay))
                        + (nextDelivery % this.deliveryDelay)
                        + deliveryDelay;

        if (System.currentTimeMillis() < nextDelivery - (this.deliveryDelay / 2))
            nextOrder = nextDelivery - (this.deliveryDelay / 2);
        else
            nextOrder = nextDelivery + (this.deliveryDelay / 2);
    }

    public long getNextDelivery() {
        return nextDelivery;
    }

    public ArrayList<LocalProductNode> getInventory() {
        return inventory;
    }

}
