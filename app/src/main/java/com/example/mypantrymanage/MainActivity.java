package com.example.mypantrymanage;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private ListView pantryList;
    private TextView pantryStatus;
    private Button retryButton;

    private PantryAdapter pantryAdapter;


    private int loadVersion= 0; // identifies the latest loading request

    private final ExecutorService databaseExecutor=
            Executors.newSingleThreadExecutor(); // runs database reads in the background



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupScreen(
                R.layout.activity_main,
                R.string.title_pantry
        );


        Button addButton = findViewById(R.id.button_add_ingredient);

        Button recipesButton= findViewById(R.id.button_view_suggestions);


        pantryList = findViewById(R.id.list_pantry);
        pantryStatus= findViewById(R.id.text_pantry_status);

        retryButton =  findViewById(R.id.button_retry_pantry);


        pantryAdapter=new PantryAdapter(this);



        pantryList.setAdapter(pantryAdapter); // connects the custom adapter



        // opens the ingredient form when an item is selected
        pantryList.setOnItemClickListener((parent, view, position, id) -> {

            PantryItem selectedItem = pantryAdapter.getItem(position);


            Intent intent= new Intent(
                    MainActivity.this,
                    IngredientActivity.class
            );
            intent.putExtra(
                    IngredientActivity.EXTRA_INGREDIENT_ID,
                    selectedItem.getId()
            ); // identifies the ingredient to edit
            startActivity(intent);

        });
        addButton.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    IngredientActivity.class
            );


            startActivity(intent); // opens a blank ingredient form
        });
        recipesButton.setOnClickListener(view ->
                openScreen(SuggestedRecipesActivity.class)
        );
        retryButton.setOnClickListener(view -> loadPantry());
    }




    @Override
    protected void onResume(){
        super.onResume();

        loadPantry(); // refreshes ingredients and sorting when returning

    }
    private void loadPantry(){

        final int currentLoad= ++loadVersion;


        pantryList.setVisibility(View.GONE);

        retryButton.setVisibility( View.GONE );

        pantryStatus.setVisibility(View.VISIBLE);
        pantryStatus.setText(R.string.pantry_loading);


        Context appContext= getApplicationContext();



        // gets the sorting option saved in the settings screen
        final boolean alphabetical = getSharedPreferences(
                SettingsActivity.PREFERENCES_NAME,
                MODE_PRIVATE
        ).getBoolean(
                SettingsActivity.KEY_ALPHABETICAL,
                false
        ); // defaults to newest first if no setting has been saved



        databaseExecutor.execute(() -> {

            DatabaseHelper helper = new DatabaseHelper(appContext);

            ArrayList<PantryItem> loadedItems= new ArrayList<>();

            boolean loadedSuccessfully = false;


            try {

                // loads pantry items in the selected order
                loadedItems = helper.getAllPantryItems(alphabetical);

                loadedSuccessfully= true;

            } catch (SQLiteException exception) {

                Log.e(
                        TAG,
                        "Could not load pantry ingredients",
                        exception
                );

            } finally {

                helper.close(); // releases this task's database connection
            }



            final ArrayList<PantryItem> result = loadedItems;

            final boolean success= loadedSuccessfully;


            runOnUiThread(() -> {

                if (isFinishing()
                        || isDestroyed()
                        || currentLoad != loadVersion) {

                    return; // ignores outdated results or a closed screen
                }



                if(!success) {

                    pantryStatus.setText(R.string.pantry_load_failed);

                    retryButton.setVisibility(View.VISIBLE);

                    return;
                }


                pantryAdapter.setItems(result); // updates the displayed rows



                if (result.isEmpty()) {

                    pantryStatus.setText(R.string.pantry_empty);

                } else {

                    pantryStatus.setVisibility(View.GONE);

                    pantryList.setVisibility(View.VISIBLE);
                }
            });
        });
    }




    @Override
    protected void onStop() {

        loadVersion++; // invalidates pending results when leaving the screen

        super.onStop();
    }




    @Override
    protected void onDestroy() {

        databaseExecutor.shutdown(); // lets submitted work finish, then stops

        super.onDestroy();

    }



}