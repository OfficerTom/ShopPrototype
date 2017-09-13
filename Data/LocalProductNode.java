package p.officertom.shop.Data;

import p.officertom.shop.ShopInstance;

public class LocalProductNode extends ProductNode {
    private ShopInstance thisShopInstance;
    private int balance;
    private int minimum;
    private int sold;
    private int onOrder;
    private double price;

    public void convert(ShopInstance thisShopInstance, int balance, int minimum, int sold, int onOrder) {
        this.thisShopInstance = thisShopInstance;
        this.balance = balance;
        this.minimum = minimum;
        this.sold = sold;
        this.onOrder = onOrder;
        updatePrice();
    }

    public void convert(ShopInstance thisShopInstance, int balance, int minimum, int sold, int onOrder, double price) {
        this.thisShopInstance = thisShopInstance;
        this.balance = balance;
        this.minimum = minimum;
        this.sold = sold;
        this.onOrder = onOrder;
        this.price = price;
    }

    public void updatePrice() {
        price = thisShopInstance.getProfitMargin() -
                ((2 / Math.PI)
                        * (1.0 - (thisShopInstance.getProfitMargin() - 1.0))
                        * Math.atan(Math.pow((double) balance / (double) minimum, 0.5)));
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
        updatePrice();
    }

    public void addBalance(int balance) {
        this.balance += balance;
        updatePrice();
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public void resetSold() {
        sold = 0;
    }

    public int getOnOrder() {
        return onOrder;
    }

    public void setOnOrder(int onOrder) {
        this.onOrder = onOrder;
    }

    public void resetOnOrder() {
        onOrder = 0;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
