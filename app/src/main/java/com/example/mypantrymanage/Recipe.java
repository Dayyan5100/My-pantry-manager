package com.example.mypantrymanage;


import java.util.ArrayList;
import java.util.List;

public class Recipe {

    // variables used to store the recipe information
    private final long id;
    private final String name;

    private final String steps;
    private final ArrayList<RecipeIngredient> ingredients;



    // constructor used when creating a recipe object
    public Recipe(
            long id,
            String name,
            String steps,
            List<RecipeIngredient> ingredients
    )  {

        this.id= id;
        this.name =  name;

        this.steps  = steps;

        // creates a new list containing the recipe ingredients
        this.ingredients = new ArrayList<>(ingredients);
    }


    // returns the recipe id
    public long getId() {
        return id;
    }




    public String getName()  {

        return name;
    }


    // gets the preparation steps for the recipe
    public String getSteps() {
        return steps;
    }
    // returns the ingredients stored for this recipe
    public ArrayList<RecipeIngredient> getIngredients()   {

        return new ArrayList<>(ingredients); // returns a separate list copy

    }

}