package com.example.mypantrymanage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // loads the pantry screen and its title
        setupScreen(R.layout.activity_main,  R.string.title_pantry);


        Button addButton =findViewById(R.id.button_add_ingredient);
        Button recipesButton= findViewById(R.id.button_view_suggestions);

        // opens the add ingredient screen
        addButton.setOnClickListener(view -> {


            Intent intent = new Intent(
                    MainActivity.this,
                    IngredientActivity.class
            );

            startActivity(intent);
        });


        // opens the suggested recipes screen
        recipesButton.setOnClickListener(view ->
                openScreen(SuggestedRecipesActivity.class)
        );

    }


}
