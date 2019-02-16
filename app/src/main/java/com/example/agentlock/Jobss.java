package com.example.agentlock;

public class Jobss{
    private int id;
    private String title;
    /*private String ordered_by;*/
    private int quantity;
    private int price;
    private int image;

    public Jobss(int id, String title/*, String ordered_by*/, double quantity, double price, int image) {
        this.id = id;
        this.title = title;
        /*this.ordered_by = ordered_by;*/
        this.quantity = (int) quantity;
        this.price = (int) price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

   /* public String getOrdered_by() {
        return ordered_by;
    }*/

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }
}
