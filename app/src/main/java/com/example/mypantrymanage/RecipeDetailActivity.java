package com.example.mypantrymanage;

import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID =
            "com.example.mypantrymanage.RECIPE_ID";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScreen(
                R.layout.activity_recipe_detail,
                R.string.title_recipe_detail
        );

        // connects the Java code to the XML views
        TextView recipeNameView = findViewById(R.id.text_recipe_name);
        TextView ingredientsView = findViewById(R.id.text_recipe_ingredients);
        TextView methodView = findViewById(R.id.text_recipe_method);

        // gets the recipe ID from the previous screen
        long recipeId = getIntent().getLongExtra(
                EXTRA_RECIPE_ID,
                -1
        );

        if (recipeId== -1) {
            recipeNameView.setText("Recipe not found");
            ingredientsView.setText("");
            methodView.setText("");
            return;
        }

        // opens the SQLite database
        databaseHelper = new DatabaseHelper(this);

        try {
            Recipe recipe = databaseHelper.getRecipe(recipeId);

            if (recipe == null) {
                recipeNameView.setText("Recipe not found");
                ingredientsView.setText("");
                methodView.setText("");
                return;
            }

            // displays the recipe name
            recipeNameView.setText(recipe.getName());

            // builds the list of required ingredients
            StringBuilder ingredientText = new StringBuilder();
            DecimalFormat format = new DecimalFormat("0.##");

            for (RecipeIngredient ingredient : recipe.getIngredients()) {

                ingredientText.append("- ")
                        .append(format.format(ingredient.getQuantity()))
                        .append(" ")
                        .append(ingredient.getUnit())
                        .append(" ")
                        .append(ingredient.getName())
                        .append("\n");
            }

            ingredientsView.setText(ingredientText.toString());

            // displays the recipe preparation steps
            methodView.setText(recipe.getSteps());

        } catch (Exception e) {

            recipeNameView.setText("Could not load recipe");
            ingredientsView.setText("");
            methodView.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (databaseHelper!= null) {
            databaseHelper.close();
        }
    }
}
