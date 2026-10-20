package com.example.mypantrymanage;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public final class RecipeSeeder{

    private RecipeSeeder(){




        // stops objects from being created from this class
    }

    // adds recipes into the database
    public static void seed(SQLiteDatabase db) {

        // tomato toasted sandwich
        addRecipe(db,
                "tomato toasted sandwich",
                "1 slice the tomato\n"
                        + "2 butter the outside of the bread slices\n" + "3 place the tomato between the slices\n" + "4 toast in a sandwich press until golden",
                ingredient("bread", 2, "count"),
                ingredient("tomato", 80, "g"),
                ingredient("butter", 10, "g")
        );


        // pasta
        addRecipe(
                db, "pasta",
                "1 boil the pasta in the water until tender, then drain\n"
                        + "2 chop the tomato and garlic\n" + "3 heat the oil and gently cook the garlic\n" + "4 add the tomato and salt; simmer until softened\n" + "5 mix the pasta into the sauce",
                ingredient("pasta", 150, "g"),
                ingredient("water",1000, "ml"),
                ingredient("tomato",200, "g"),
                ingredient("garlic", 5,"g"),
                ingredient("oil", 10,"ml"),
                ingredient("salt", 2, "g")
        );

        // egg salad
        addRecipe(db, "egg salad",
                "1 place the eggs in the water and boil until hard-boiled\n"
                        + "2 let the eggs cool, then peel and chop them\n" + "3 chop the lettuce and tomato\n" + "4 combine the eggs, lettuce and tomato\n"
                        + "5 stir in the mayonnaise",
                ingredient("egg", 2, "count"),
                ingredient("water",500, "ml"),
                ingredient("lettuce",50, "g"),
                ingredient("tomato", 80, "g"),
                ingredient("mayonnaise",30, "g")
        );



        // chicken wrap
        addRecipe(
                db,
                "chicken wrap",
                "1 shred the cooked chicken\n" + "2 chop the lettuce and tomato\n" + "3 spread mayonnaise over the wrap\n"
                        + "4 add the chicken, lettuce and tomato\n" + "5 fold in the sides and roll the wrap",
                ingredient("wrap",1, "count"),
                ingredient("cooked chicken",120, "g"),
                ingredient("lettuce", 40, "g"),
                ingredient("tomato", 60,"g"),
                ingredient("mayonnaise", 20, "g")
        );


        // butter chicken
        addRecipe(
                db, "butter chicken",
                "1 cut the chicken into pieces and coat with yoghurt " + "and half the curry powder\n"
                        + "2 chop the onion, garlic and tomato\n" + "3 melt the butter and soften the onion and garlic\n" + "4 add the remaining curry powder and tomato; " + "cook until softened\n" + "5 add the chicken, yoghurt mixture, water and salt\n" + "6 simmer until the chicken is fully cooked\n"
                        + "7 stir in the cream and gently heat through",
                ingredient("chicken", 300, "g"),
                ingredient("plain yoghurt",80, "g"),
                ingredient("curry powder",10, "g"),
                ingredient("onion",100, "g"),
                ingredient("garlic",10, "g"),
                ingredient("tomato", 200, "g"),
                ingredient("butter", 30, "g"),
                ingredient("water",100, "ml"),
                ingredient("salt", 2, "g"),
                ingredient("cream",100, "ml")
        );



        // sticky wings
        addRecipe(db,
                "sticky wings",
                "1 preheat the oven to 200 degrees celsius\n"
                        + "2 mix the honey, soy sauce, tomato sauce " + "and chopped garlic\n" + "3 coat the chicken wings with the sauce\n" + "4 arrange in a baking dish and roast, " + "turning during cooking\n" + "5 cook until the wings are fully cooked "
                        + "and the coating is sticky",
                ingredient("chicken wings",500, "g"),
                ingredient("honey", 40, "g"),
                ingredient("soy sauce", 30, "ml"),
                ingredient("tomato sauce", 40, "g"),
                ingredient("garlic", 5, "g")

        );


        // butternut soup
        addRecipe(
                db,
                "butternut soup",
                "1 peel and cube the butternut; chop the onion and garlic\n" + "2 heat the oil and soften the onion and garlic\n"
                        + "3 add the butternut, water and salt\n"
                        + "4 simmer until the butternut is tender\n"
                        + "5 blend carefully until smooth\n" + "6 stir in the cream and gently heat through",
                ingredient("butternut", 500, "g"),
                ingredient("onion", 100,"g"),
                ingredient("garlic", 5, "g"),
                ingredient("oil",10, "ml"),
                ingredient("water",600,"ml"),
                ingredient("salt",3, "g"),
                ingredient("cream", 100, "ml")
        );





        // cheese toasted sandwich
        addRecipe(db, "cheese toasted sandwich",
                "1 butter the outside of the bread slices\n"
                        + "2 place the cheese between the slices\n"
                        + "3 toast until golden and the cheese has melted",
                ingredient("bread", 2, "count"),
                ingredient("cheese", 50, "g"),
                ingredient("butter", 10, "g")
        );



        // cheese and tomato toasted sandwich
        addRecipe(
                db,
                "cheese and tomato toasted sandwich",
                "1 slice the tomato\n" + "2 butter the outside of the bread slices\n"
                        + "3 add the cheese and tomato between the slices\n"
                        + "4 toast until golden and the cheese has melted",
                ingredient("bread", 2,"count"),
                ingredient("cheese",40, "g"),
                ingredient("tomato",60, "g"),
                ingredient("butter", 10,"g")
        );


        // garlic butter pasta
        addRecipe(db,
                "garlic butter pasta",
                "1 boil the pasta in the water until tender, then drain\n"
                        + "2 chop the garlic\n" + "3 melt the butter and gently cook the garlic\n"
                        + "4 toss in the pasta and salt",
                ingredient("pasta",150,"g"),
                ingredient("water",1000, "ml"),
                ingredient("garlic",10, "g"),
                ingredient("butter", 25,"g"),
                ingredient("salt", 2, "g")
        );



        // creamy mushroom pasta
        addRecipe(
                db, "creamy mushroom pasta",
                "1 boil the pasta in the water until tender, then drain\n"
                        + "2 slice the mushrooms and chop the garlic\n" + "3 melt the butter and cook the mushrooms and garlic\n" + "4 stir in the cream and salt; gently simmer\n"
                        + "5 mix in the cooked pasta",
                ingredient("pasta",150, "g"),
                ingredient("water",1000, "ml"),
                ingredient("mushroom", 150, "g"),
                ingredient("garlic", 5,"g"),
                ingredient("butter", 20,"g"),
                ingredient("cream",100,"ml"),
                ingredient("salt", 2, "g")
        );


        // tomato and cheese pasta
        addRecipe(db,
                "tomato and cheese pasta",
                "1 boil the pasta in the water until tender, then drain\n"
                        + "2 chop the tomato and soften it in the oil\n"
                        + "3 add the salt and cooked pasta\n"
                        + "4 stir in grated cheese until melted",
                ingredient("pasta", 150, "g"),
                ingredient("water", 1000, "ml"),
                ingredient("tomato", 200,"g"),
                ingredient("oil",10,"ml"),
                ingredient("salt", 2,"g"),
                ingredient("cheese", 50, "g")


        );



        // egg mayonnaise sandwich
        addRecipe(
                db, "egg mayonnaise sandwich",
                "1 boil the eggs in the water until hard-boiled\n"
                        + "2 let them cool, then peel and mash them\n" + "3 mix with the mayonnaise\n"
                        + "4 spread between the bread slices",
                ingredient("egg",2, "count"),
                ingredient("water",500, "ml"),
                ingredient("mayonnaise",30,"g"),
                ingredient("bread", 2, "count")
        );


        // scrambled eggs
        addRecipe(db, "scrambled eggs",
                "1 beat the eggs with the milk and salt\n"+ "2 melt the butter in a pan over gentle heat\n" + "3 add the egg mixture\n" + "4 stir gently until the eggs are cooked",
                ingredient("egg", 2, "count"),
                ingredient("milk", 30, "ml"),
                ingredient("salt",1, "g"),
                ingredient("butter",10, "g")
        );




        // chicken salad
        addRecipe(
                db,
                "chicken salad",
                "1 shred the cooked chicken\n"
                        + "2 chop the lettuce, tomato and cucumber\n" + "3 combine everything in a bowl\n"
                        + "4 stir in the mayonnaise",
                ingredient("cooked chicken",150, "g"),
                ingredient("lettuce",60, "g"),
                ingredient("tomato",80, "g"),
                ingredient("cucumber", 80,"g"),
                ingredient("mayonnaise",30, "g")
        );

    }


    // creates a recipe ingredient with the required amount
    private static RecipeIngredient ingredient(
            String name, double quantity,
            String unit
    ) {


        return new RecipeIngredient(name,  quantity, unit);
    }




    // saves the recipe details and its ingredients
    private static void addRecipe(
            SQLiteDatabase db,
            String name,
            String steps,
            RecipeIngredient... ingredients
    )   {

        // stores the recipe name and preparation steps
        ContentValues recipeValues =new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps",  steps);


        long recipeId = db.insertOrThrow(
                DatabaseHelper.TABLE_RECIPES,
                null,
                recipeValues
        ); // database creates an id for the recipe



        // goes through each ingredient in the recipe
        for(RecipeIngredient ingredient : ingredients) {

            ContentValues values= new ContentValues();

            values.put("recipe_id",recipeId);
            values.put("name",ingredient.getName());


            values.put("quantity",  ingredient.getQuantity());
            values.put("unit", ingredient.getUnit());


            // adds the ingredient to the recipe ingredients table
            db.insertOrThrow(
                    DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                    null,
                    values
            );

        }
    }
}