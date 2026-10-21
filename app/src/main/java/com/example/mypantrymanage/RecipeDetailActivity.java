package com.example.mypantrymanage;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID =
            "com.example.mypantrymanage.RECIPE_ID";


    private static final String TAG= "RecipeDetail";

    private long recipeId;

    // variables used for the recipe detail screen
    private TextView statusText;
    private TextView nameText;

    private TextView ingredientsText;
    private TextView methodText;


    private Button retryButton;
    private View detailContent;


    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupScreen(
                R.layout.activity_recipe_detail,
                R.string.title_recipe_detail
        );


        statusText= findViewById(R.id.text_recipe_status);
        nameText =  findViewById(R.id.text_recipe_name);


        ingredientsText= findViewById(R.id.text_recipe_ingredients);
        methodText =findViewById(R.id.text_recipe_method);

        retryButton=  findViewById(R.id.button_retry_recipe);
        detailContent = findViewById(R.id.scroll_recipe_detail);


        // gets the selected recipe id from the previous activity
        recipeId= getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        retryButton.setOnClickListener(view -> loadRecipe());


        // checks if a valid recipe id was passed
        if (recipeId <= 0) {

            statusText.setText(R.string.recipe_not_found);

            return; // stops if the recipe id is missing
        }


        loadRecipe();
    }



    private void loadRecipe()   {

        // displays the loading message before reading the database
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(R.string.recipe_loading);

        retryButton.setVisibility( View.GONE );

        detailContent.setVisibility(View.GONE);



        Context appContext =getApplicationContext();


        databaseExecutor.execute(() -> {

            DatabaseHelper helper= new DatabaseHelper(appContext);

            Recipe loadedRecipe = null;
            boolean failed= false;


            try {

                // searches the database for the selected recipe
                loadedRecipe= helper.getRecipe(recipeId);

            } catch (SQLiteException exception) {

                failed = true;

                Log.e(TAG, "Could not load recipe detail", exception);

            } finally {

                helper.close(); // closes the database connection
            }



            final Recipe recipe=loadedRecipe;
            final boolean loadFailed=  failed;


            runOnUiThread(() -> {

                // checks if the activity is still open
                if (isFinishing() || isDestroyed()) {
                    return;
                }


                if(loadFailed) {

                    statusText.setText(R.string.recipe_load_failed);

                    retryButton.setVisibility(View.VISIBLE);

                    return;
                }


                if (recipe == null) {

                    statusText.setText(R.string.recipe_not_found);
                    return;
                }


                // displays the recipe after it has loaded
                showRecipe(recipe);
            });
        });
    }




    private void showRecipe(Recipe recipe) {

        nameText.setText(recipe.getName());

        methodText.setText( recipe.getSteps() );


        // used to build the ingredient list as text
        StringBuilder ingredientLines= new StringBuilder();


        // loops through all ingredients required for the recipe
        for (RecipeIngredient ingredient : recipe.getIngredients()) {

            if(ingredientLines.length() > 0) {

                // adds a space between ingredient lines
                ingredientLines.append("\n\n");
            }


            // removes unnecessary decimal zeros from quantities
            String quantity= BigDecimal
                    .valueOf(ingredient.getQuantity())
                    .stripTrailingZeros()
                    .toPlainString();


            ingredientLines.append(
                    getString(
                            R.string.recipe_ingredient_line,
                            ingredient.getName(),
                            quantity,
                            ingredient.getUnit()
                    )
            );
        }


        ingredientsText.setText(ingredientLines.toString());
        // hides the loading message and shows the recipe details
        statusText.setVisibility(View.GONE);
        detailContent.setVisibility(View.VISIBLE);
    }



    @Override
    protected void onDestroy() {
        databaseExecutor.shutdown();
        super.onDestroy();

    }
}