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
public class MainActivity extends BaseActivity   {
    private static final String TAG=  "MainActivity";
    private ListView pantryList;
    private TextView pantryStatus;
    private Button retryButton;
    private PantryAdapter pantryAdapter;
    private int loadVersion = 0; // identifies the latest loading request
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor(); // reads outside the ui thread
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupScreen(
                R.layout.activity_main,
                R.string.title_pantry
        );



        Button addButton =findViewById(R.id.button_add_ingredient);


        Button recipesButton =  findViewById(
                R.id.button_view_suggestions
        );


        pantryList= findViewById(R.id.list_pantry);
        pantryStatus=   findViewById(R.id.text_pantry_status);
        retryButton=  findViewById(R.id.button_retry_pantry);
        pantryAdapter =new PantryAdapter(this);
        pantryList.setAdapter( pantryAdapter ); // connects the custom adapter
        // opens the selected ingredient for editing
        pantryList.setOnItemClickListener((parent, view, position, id) -> {
            PantryItem selectedItem = pantryAdapter.getItem(position);
            Intent intent= new Intent(
                    MainActivity.this,
                    IngredientActivity.class
            );
            intent.putExtra(
                    IngredientActivity.EXTRA_INGREDIENT_ID,
                    selectedItem.getId()
            ); // passes the selected database id
            startActivity(intent);

        });



        // opens the add ingredient screen
        addButton.setOnClickListener(view ->   {

            Intent intent = new Intent(
                    MainActivity.this,
                    IngredientActivity.class
            );

            startActivity(intent);
        });


        // opens the suggested recipes screen
        recipesButton.setOnClickListener(view ->
                openScreen( SuggestedRecipesActivity.class )
        );

        retryButton.setOnClickListener(view -> loadPantry());
    }



    @Override
    protected void onResume()  {

        super.onResume();

        loadPantry(); // reloads when opening or returning to this screen
    }



    private void loadPantry()   {

        final int currentLoad= ++loadVersion;
        pantryList.setVisibility(View.GONE);
        retryButton.setVisibility( View.GONE );
        pantryStatus.setVisibility(View.VISIBLE);
        pantryStatus.setText(
                R.string.pantry_loading
        );


        Context appContext =getApplicationContext();


        databaseExecutor.execute(() ->  {
            DatabaseHelper helper= new DatabaseHelper(appContext);
            ArrayList<PantryItem> loadedItems =  new ArrayList<>();
            boolean loadedSuccessfully= false;


            try  {

                loadedItems = helper.getAllPantryItems();
                loadedSuccessfully= true;

            } catch (SQLiteException exception) {

                Log.e(
                        TAG,
                        "Could not load pantry ingredients",
                        exception
                );

            } finally {

                helper.close(); // closes this task's database connection
            }


            final ArrayList<PantryItem> result= loadedItems;
            final boolean success = loadedSuccessfully;



            runOnUiThread(() -> {

                if (isFinishing()
                        ||  isDestroyed()
                        || currentLoad != loadVersion)   {

                    return; // ignores results from an old loading request
                }


                if(!success)  {

                    pantryStatus.setText(
                            R.string.pantry_load_failed
                    );

                    retryButton.setVisibility(View.VISIBLE);
                    return;
                }


                pantryAdapter.setItems( result );

                if(result.isEmpty()) {

                    pantryStatus.setText(
                            R.string.pantry_empty
                    );

                } else   {

                    pantryStatus.setVisibility( View.GONE );
                    pantryList.setVisibility(View.VISIBLE);
                }
            });


        });
    }




    @Override
    protected void onStop() {
        loadVersion++; // prevents an old result updating an inactive screen
        super.onStop();
    }
    @Override
    protected void onDestroy()   {
        databaseExecutor.shutdown(); // finishes submitted work, then stops
        super.onDestroy();
    }
}