package com.example.mypantrymanage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RecipeMatcher {

    private RecipeMatcher() {
        // prevents creating an unnecessary matcher object
    }

    // finds recipes that can be made using the pantry ingredients
    public static ArrayList<Recipe> findMatches(
            List<Recipe> recipes,
            List<PantryItem> pantry
    )  {

        ArrayList<Recipe> matches=new ArrayList<>();


        // goes through each recipe in the list
        for (Recipe recipe : recipes) {

            if(canMakeRecipe(recipe,pantry)) {

                matches.add(recipe); // adds only recipes with enough ingredients
            }
        }

        return matches;
    }




    // checks whether the pantry has everything needed for one recipe
    public static boolean canMakeRecipe(
            Recipe recipe,
            List<PantryItem>pantry
    ) {

        if (recipe == null ||pantry == null) {
            return false;
        }


        List<RecipeIngredient> ingredients =recipe.getIngredients();

        if(ingredients.isEmpty()){

            return false; // an empty recipe should not be suggested
        }


        // maps store the ingredient names and their amounts
        Map<String, BigDecimal> available= new HashMap<>();
        Map<String, BigDecimal> required =  new HashMap<>();


        // loops through the ingredients currently in the pantry
        for (PantryItem item : pantry) {

            if(item == null) {
                continue; // skips empty entries
            }


            String key= ingredientKey(item.getName(), item.getUnit());

            BigDecimal amount =baseQuantity(
                    item.getQuantity(),
                    item.getUnit()

            );



            if (key != null && amount != null) {

                addAmount(available, key, amount);
            }
        }



        // gets the ingredients required by the recipe
        for(RecipeIngredient ingredient : ingredients) {


            if (ingredient == null) {
                return false;
            }


            String key = ingredientKey(
                    ingredient.getName(),
                    ingredient.getUnit()

            );

            BigDecimal amount= baseQuantity(
                    ingredient.getQuantity(),
                    ingredient.getUnit()
            );


            if(key == null || amount == null) {


                return false; // rejects invalid recipe requirements
            }


            addAmount(required, key, amount);
        }



        // compares the available amounts with the required amounts
        for (Map.Entry<String, BigDecimal> entry : required.entrySet()) {

            BigDecimal pantryAmount= available.get(entry.getKey());


            if (pantryAmount == null
                    || pantryAmount.compareTo(entry.getValue()) < 0) {

                return false; // missing ingredient or not enough quantity
            }
        }


        return true; // all required ingredients are available
    }




    // adds quantities together when an ingredient appears more than once
    private static void addAmount(
            Map<String, BigDecimal> totals,
            String key,
            BigDecimal amount
    ) {

        BigDecimal previous= totals.get(key);


        if(previous == null) {

            previous = BigDecimal.ZERO;
        }


        totals.put(key, previous.add(amount));
    }
    private static String ingredientKey(String name, String unit) {

        String normalName = normaliseName(name);

        String normalUnit= normaliseUnit(unit);


        // checks for an invalid ingredient name or unit
        if(normalName.isEmpty() || normalUnit.isEmpty()) {

            return null;
        }


        if (normalUnit.equals("kg")) {

            normalUnit= "g";

        } else if(normalUnit.equals("l")) {

            normalUnit = "ml";
        }


        // joins the name and unit to create a unique key
        return normalName + "|" + normalUnit;
    }



    // converts quantities into standard units for comparison
    private static BigDecimal baseQuantity(double quantity, String unit) {

        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                ||  quantity <= 0) {

            return null;
        }


        String normalUnit= normaliseUnit(unit);
        if(normalUnit.isEmpty()) {
            return null;
        }


        BigDecimal amount = BigDecimal.valueOf(quantity);


        // changes kilograms to grams and litres to millilitres
        if (normalUnit.equals("kg") || normalUnit.equals("l")) {
            amount= amount.multiply(BigDecimal.valueOf(1000));

        }

        return amount;
    }




    // changes different unit names into a consistent format
    private static String normaliseUnit(String unit) {

        if(unit == null) {
            return "";
        }


        switch (unit.trim().toLowerCase(Locale.ROOT)) {

            case "g":
            case "gram":
            case "grams":
                return "g";


            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";


            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";


            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";


            case "count":
            case "piece":
            case "pieces":
                return "count";


            default:
                return ""; // unsupported units cannot qualify
        }
    }




    // removes extra spaces and handles different ingredient names
    private static String normaliseName(String name) {

        if (name == null) {
            return "";
        }


        String cleaned= name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");


        // checks common variations of ingredient names
        switch(cleaned) {

            case "tomatoes":
                return "tomato";


            case "eggs":
                return "egg";


            case "onions":
                return "onion";


            case "mushrooms":
                return "mushroom";


            case "potatoes":
                return "potato";


            case "wraps":
            case "tortilla":
            case "tortillas":
            case "tortilla wrap":
            case "tortilla wraps":
                return "wrap";




            case "bread slice":
            case "bread slices":
            case "sliced bread":
                return "bread";


            case "chicken wing":
                return "chicken wings";


            case "plain yogurt":
                return "plain yoghurt";


            default:
                return cleaned; // keeps other ingredient names distinct
        }

    }

}