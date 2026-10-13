package com.example.mypantrymanage;

import android.os.Bundle;
import android.widget.TextView;

public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_NAME =
            "com.example.mypantrymanage.RECIPE_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScreen(
                R.layout.activity_recipe_detail,
                R.string.title_recipe_detail
        );

        TextView recipeNameView =findViewById(R.id.text_recipe_name);

        // reads the value sent by suggestedrecipesactivity
        String recipeName= getIntent().getStringExtra(EXTRA_RECIPE_NAME);


        if (recipeName != null &&  !recipeName.trim().isEmpty()) {
            recipeNameView.setText(recipeName);
        }

    }
}
