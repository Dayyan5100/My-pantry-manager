package com.example.mypantrymanage;


public class PantryItem {

    private final long id;          // unique database id
    private final String name;      // ingredient display name
    private final double quantity; // amount available
    private final String unit;      // count, g, kg, ml or l

    public PantryItem(long id,String name,double quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity =  quantity;
        this.unit=   unit;
    }

    public long getId() {
        return id; // identifies the record when editing or deleting

    }


    public String getName(){
        return name;
    }
    public double getQuantity() {
        return quantity;

    }
    public String getUnit(){
        return unit;

    }

}
