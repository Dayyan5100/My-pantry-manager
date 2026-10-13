package com.example.mypantrymanage;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
public class SuggestedRecipesActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupScreen(
                R.layout.activity_suggested_recipes,
                R.string.title_suggestions
        );

        Button previewButton =findViewById(R.id.button_preview_recipe);
        previewButton.setOnClickListener(view -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );

            // demonstrates passing data between activities
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_NAME,
                    getString(R.string.preview_recipe_name)
            );

            startActivity(intent);


        });

    }

}