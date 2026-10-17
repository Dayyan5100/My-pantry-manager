package com.example.mypantrymanage;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IngredientActivity extends BaseActivity {


    private static final String TAG= "IngredientActivity";
    private EditText nameInput;       // ingredient name field
    private EditText quantityInput;   // available quantity field
    private Spinner unitSpinner;      // unit selection
    private Button saveButton;        // starts validation and saving
    private TextView formStatus;      // shows saving or error feedback
    private boolean saving = false; // prevents repeated save taps
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor(); // runs database work in order



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupScreen(
                R.layout.activity_ingredient,
                R.string.title_ingredient
        );


        nameInput= findViewById(R.id.edit_ingredient_name);

        quantityInput =  findViewById(R.id.edit_quantity);
        unitSpinner= findViewById(R.id.spinner_unit);
        saveButton =findViewById(R.id.button_save_ingredient);
        formStatus=  findViewById(R.id.text_form_status);
        saveButton.setOnClickListener(view -> validateAndSave());
    }



    private void validateAndSave()   {

        if(saving)  {
            return; // a save is already running
        }


        nameInput.setError(null);
        quantityInput.setError( null ); // clears the previous quantity error
        formStatus.setText(R.string.ingredient_form_help);


        String name= nameInput.getText()
                .toString()
                .trim();


        String quantityText = quantityInput.getText().toString().trim();
        if (name.isEmpty())   {

            nameInput.setError(
                    getString(R.string.error_ingredient_name)
            );

            nameInput.requestFocus();

            return; // stops before any database operation
        }
        if(quantityText.isEmpty()) {
            quantityInput.setError(
                    getString(R.string.error_quantity_required)
            );

            quantityInput.requestFocus();

            return;
        }


        double quantity;
        try  {

            quantity= Double.parseDouble(quantityText); // text to number
        } catch (NumberFormatException exception)   {
            showQuantityError();
            return; // invalid text must not crash the application
        }


        if(Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                ||  quantity <= 0)   {

            showQuantityError();
            return;
        }


        Object selectedUnit= unitSpinner.getSelectedItem();


        if (selectedUnit == null) {
            formStatus.setText(
                    R.string.error_unit_required
            );
            return;

        }


        String unit =selectedUnit.toString();
        saveIngredient(name,   quantity, unit);
    }



    private void showQuantityError()   {
        quantityInput.setError(
                getString(R.string.error_quantity_invalid)
        );
        quantityInput.requestFocus();
    }




    private void saveIngredient(
            String name,
            double quantity,
            String unit
    )   {

        saving= true;
        saveButton.setEnabled(false); // prevents duplicate taps during saving
        formStatus.setText(
                R.string.saving_ingredient
        );


        Context appContext =getApplicationContext();
        databaseExecutor.execute(() ->  {

            DatabaseHelper helper= new DatabaseHelper(appContext);
            boolean saved = false;


            try   {
                long newId= helper.addPantryItem(
                        name,
                        quantity,
                        unit
                ); // uses the create method from commit 3


                saved= newId > 0;
            } catch (SQLiteException | IllegalArgumentException exception) {

                Log.e(
                        TAG,
                        "Could not save pantry ingredient",
                        exception
                );
            } finally  {
                helper.close(); // releases this task's database connection
            }


            final boolean saveSuccessful= saved;
            runOnUiThread(() ->   {

                if(isFinishing() || isDestroyed()) {
                    return; // does not update a screen that has closed
                }


                saving =false;
                if (saveSuccessful)  {

                    Toast.makeText(
                            IngredientActivity.this,
                            R.string.ingredient_saved,
                            Toast.LENGTH_SHORT
                    ).show();
                    finish(); // returns to the previous screen after saving
                } else   {
                    saveButton.setEnabled(true); // allows another attempt


                    formStatus.setText(
                            R.string.ingredient_save_failed
                    );
                }
            });


        });
    }




    @Override
    protected void onDestroy() {
        databaseExecutor.shutdown(); // lets submitted work finish, then stops
        super.onDestroy();
    }
}
