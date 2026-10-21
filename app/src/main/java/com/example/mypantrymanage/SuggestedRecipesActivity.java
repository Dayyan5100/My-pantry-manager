package com.example.mypantrymanage;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SuggestedRecipesActivity extends BaseActivity {
    private static final String TAG= "SuggestedRecipes";


    // variables for the recipe screen
    private TextView statusText;
    private Button retryButton;

    private ListView recipeList;
    private ArrayAdapter<String> adapter;

    // list used to store the matching recipes
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private int loadVersion =  0;
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupScreen(
                R.layout.activity_suggested_recipes,
                R.string.title_suggestions
        );

        statusText= findViewById(R.id.text_recipes_status);
        retryButton =  findViewById(R.id.button_retry_recipes);
        recipeList= findViewById(R.id.list_recipes);


        // creates an adapter to display recipe names
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>()
        ); // uses android's built-in single-line row


        recipeList.setAdapter(adapter);


        // opens the selected recipe when tapped
        recipeList.setOnItemClickListener((parent, view, position, id) -> {

            Recipe selectedRecipe= recipes.get(position);

            Intent intent= new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );


            // sends the selected recipe id to the detail screen
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_ID,
                    selectedRecipe.getId()
            );

            startActivity(intent);
        });


        retryButton.setOnClickListener(view -> loadRecipes());
    }

    @Override
    protected void onResume(){

        super.onResume();

        loadRecipes(); // checks the pantry whenever this screen resumes
    }



    private void loadRecipes()   {

        final int currentLoad= ++loadVersion;
        // hides the list while the recipes are loading
        recipeList.setVisibility(View.GONE);
        retryButton.setVisibility( View.GONE );
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(R.string.recipes_loading);
        Context appContext =getApplicationContext();
        databaseExecutor.execute(() -> {

            DatabaseHelper helper= new DatabaseHelper(appContext);

            ArrayList<Recipe> matches =  new ArrayList<>();
            boolean success= false;


            try {

                // gets the recipes and pantry ingredients from the database
                ArrayList<Recipe> allRecipes = helper.getAllRecipes();
                ArrayList<PantryItem> pantryItems= helper.getAllPantryItems();
                // checks which recipes can be made with the available ingredients
                matches = RecipeMatcher.findMatches(allRecipes, pantryItems);

                success= true;

            } catch (SQLiteException exception) {

                Log.e(TAG, "Could not load suggested recipes", exception);

            } finally {

                helper.close();
            }


            final ArrayList<Recipe> result= matches;
            final boolean loaded = success;
            runOnUiThread(() -> {

                // checks if the screen is still active
                if (isFinishing()
                        || isDestroyed()
                        || currentLoad != loadVersion) {

                    return; // ignores an outdated loading result
                }



                if(!loaded) {

                    statusText.setText(R.string.recipes_load_failed);
                    retryButton.setVisibility(View.VISIBLE);

                    return;
                }
                // clears the old recipes before adding the new ones
                recipes.clear();

                recipes.addAll(result);

                adapter.clear();


                // loops through the matching recipes
                for (Recipe recipe : recipes) {

                    adapter.add(recipe.getName());
                }

                adapter.notifyDataSetChanged();


                if (recipes.isEmpty()) {

                    // displays a message if no recipes can be made
                    statusText.setText(R.string.recipes_no_matches);

                } else {


                    // displays the list when matching recipes are found
                    statusText.setVisibility(View.GONE);

                    recipeList.setVisibility(View.VISIBLE);
                }
            });
        });
    }



    @Override
    protected void onStop() {

        loadVersion++; // invalidates results when leaving this screen

        super.onStop();
    }



    @Override
    protected void onDestroy() {
        databaseExecutor.shutdown();
        super.onDestroy();

    }

}