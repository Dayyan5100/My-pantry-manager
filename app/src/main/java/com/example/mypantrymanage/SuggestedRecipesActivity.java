package com.example.mypantrymanage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class SuggestedRecipesActivity extends BaseActivity {

    private DatabaseHelper databaseHelper;
    private TextView suggestionsStatus;
    private ListView recipesList;

    private ArrayList<Recipe> matchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScreen(
                R.layout.activity_suggested_recipes,
                R.string.title_suggestions
        );

        // connects the screen to the XML views
        suggestionsStatus = findViewById(R.id.text_suggestions_status);
        recipesList = findViewById(R.id.list_suggested_recipes);

        // opens the existing SQLite database
        databaseHelper = new DatabaseHelper(this);
        matchingRecipes = new ArrayList<>();

        // opens the recipe selected by the user
        recipesList.setOnItemClickListener((parent,view, position, id) -> {

            Recipe selectedRecipe = matchingRecipes.get(position);

            Intent intent =new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );

            // passes the actual recipe database ID
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_ID,
                    selectedRecipe.getId()
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        // reloads suggestions when the screen becomes visible
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes()  {

        matchingRecipes.clear();

        try {
            // retrieves pantry items and recipes from SQLite
            ArrayList<PantryItem> pantryItems=
                    databaseHelper.getAllPantryItems();

            ArrayList<Recipe> allRecipes=
                    databaseHelper.getAllRecipes();

            // checks which recipes can be made
            for (Recipe recipe : allRecipes) {

                if (canMakeRecipe(recipe,pantryItems)) {
                    matchingRecipes.add(recipe);
                }
            }

            ArrayList<String> recipeNames = new ArrayList<>();
            for (Recipe recipe : matchingRecipes) {
                recipeNames.add(recipe.getName());
            }

            // connects the matching names to the ListView
            ArrayAdapter<String> adapter= new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    recipeNames
            );

            recipesList.setAdapter(adapter);

            if (matchingRecipes.isEmpty()){

                suggestionsStatus.setText(
                        "No recipes match your pantry yet. " +
                                "Add more ingredients to get started."
                );

            } else {

                suggestionsStatus.setText(
                        matchingRecipes.size() + " recipe(s) available:"
                );
            }

        } catch (Exception e) {

            matchingRecipes.clear();
            recipesList.setAdapter(null);

            suggestionsStatus.setText(
                    "Could not load recipes. Please try again."
            );
        }
    }

    // checks that every recipe ingredient is available
    private boolean canMakeRecipe(
            Recipe recipe,
            ArrayList<PantryItem> pantryItems
    ) {

        for (RecipeIngredient required :  recipe.getIngredients()) {

            double availableQuantity= 0;

            for (PantryItem pantryItem: pantryItems) {

                // matches ingredient names
                String pantryName =normaliseName(pantryItem.getName());
                String requiredName =normaliseName(required.getName());

                if (!pantryName.equals(requiredName)) {
                    continue;
                }

                // only adds quantities with compatible units
                if (sameUnitType(
                        pantryItem.getUnit(),
                        required.getUnit()
                )) {

                    availableQuantity +=   convertQuantity(
                            pantryItem.getQuantity(),
                            pantryItem.getUnit()
                    );
                }
            }

            double requiredQuantity  =   convertQuantity(
                    required.getQuantity(),
                    required.getUnit()
            );

            // one missing ingredient means the whole recipe fails
            if (availableQuantity <   requiredQuantity) {
                return false;
            }
        }

        return true;
    }

    // handles basic singular/plural differences
    private String normaliseName(String name){

        String result  =  name.trim().toLowerCase(Locale.ROOT);

        if (result.endsWith("tomatoes")) {
            return result.substring(0, result.length() - 8) + "tomato";
        }

        if (result.endsWith("potatoes")) {
            return result.substring(0, result.length() - 8) +"potato";
        }

        if (result.endsWith("ies") && result.length() > 3) {
            return result.substring(0, result.length() - 3)+ "y";
        }

        if (result.endsWith("s") && !result.endsWith("ss")
                && result.length() > 3) {
            return result.substring(0, result.length()-1);
        }

        return result;
    }

    // checks whether measurement units can be compared
    private boolean sameUnitType(String first, String second) {

        if (first.equals(second)){

            return true;
        }

        boolean firstWeight =first.equals("g") || first.equals("kg");
        boolean secondWeight= second.equals("g") || second.equals("kg");

        boolean firstVolume = first.equals("ml") || first.equals("l");
        boolean secondVolume = second.equals("ml") || second.equals("l");

        return (firstWeight&& secondWeight)
                || (firstVolume && secondVolume);
    }

    // converts kilograms and litres into smaller units
    private double convertQuantity(double quantity, String unit) {

        if (unit.equals("kg") || unit.equals("l")) {
            return quantity * 1000;
        }

        return quantity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
