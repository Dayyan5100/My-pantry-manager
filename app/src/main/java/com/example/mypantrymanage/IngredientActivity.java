package com.example.mypantrymanage;
import android.os.Bundle;
public class IngredientActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // loads the ingredient screen and its title
        setupScreen(
                R.layout.activity_ingredient,
                R.string.title_ingredient
        );
    }
}