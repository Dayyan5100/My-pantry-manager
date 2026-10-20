package com.example.mypantrymanage;

public class RecipeIngredient{

    // stores the ingredient details needed for a recipe
    private final String name;

    private final double quantity;
    private final String unit;


    // creates a recipe ingredient with its required amount
    public RecipeIngredient(String name, double quantity, String unit) {

        this.name= name;
        this.quantity =  quantity;

        this.unit   = unit;
    }


    // returns the ingredient name
    public String getName(){
        return name;
    }



    // returns the required quantity
    public double getQuantity()  {

        return quantity;
    }


    // returns the measurement unit
    public String getUnit(){
        return unit;
    }
}