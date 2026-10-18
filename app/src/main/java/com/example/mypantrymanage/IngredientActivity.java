package com.example.mypantrymanage;


import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class IngredientActivity extends BaseActivity {
    public static final String EXTRA_INGREDIENT_ID =
            "com.example.mypantrymanage.INGREDIENT_ID";
    private static final String TAG =  "IngredientActivity";
    private EditText nameInput;
    private EditText quantityInput;
    private Spinner unitSpinner;
    private Button saveButton;
    private Button deleteButton;
    private TextView formStatus;
    private long ingredientId = -1; // add mode has no existing database id
    private boolean editMode= false;
    private boolean formReady =  false;
    private boolean busy = false; // prevents actions while loading or saving
    private AlertDialog deleteDialog;
    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        editMode =getIntent().hasExtra(EXTRA_INGREDIENT_ID);
        ingredientId= getIntent().getLongExtra(
                EXTRA_INGREDIENT_ID,
                -1

        );
        setupScreen(
                R.layout.activity_ingredient,
                editMode
                        ? R.string.title_edit_ingredient
                        : R.string.title_ingredient
        );


        nameInput =findViewById(R.id.edit_ingredient_name);
        quantityInput =  findViewById(R.id.edit_quantity);
        unitSpinner= findViewById(R.id.spinner_unit);
        saveButton =findViewById(R.id.button_save_ingredient);
        deleteButton=   findViewById(R.id.button_delete_ingredient);
        formStatus = findViewById(R.id.text_form_status);
        saveButton.setText(
                editMode
                        ? R.string.update_ingredient
                        : R.string.save_ingredient
        );


        deleteButton.setVisibility(
                editMode ? View.VISIBLE : View.GONE
        );
        saveButton.setOnClickListener(view -> validateAndSave());
        deleteButton.setOnClickListener(view ->  confirmDelete());
        if (editMode && ingredientId <= 0) {
            showMessageAndFinish(R.string.ingredient_not_found);
            return;
        }
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("form_ready", false)) {

            nameInput.setText(savedInstanceState.getString("name", ""));
            quantityInput.setText(
                    savedInstanceState.getString("quantity", "")
            );
            unitSpinner.setSelection(
                    savedInstanceState.getInt("unit_position", 0)
            );


            formReady= true; // restores unfinished edits after rotation
            showFormHelp();
        } else if(editMode) {
            loadIngredient();
        } else {
            formReady =true;
            showFormHelp();

        }
    }
    private void loadIngredient()  {
        setBusy(true);
        formStatus.setText(R.string.ingredient_loading);
        Context appContext= getApplicationContext();
        databaseExecutor.execute(() -> {
            DatabaseHelper helper =new DatabaseHelper(appContext);
            PantryItem loadedItem= null;
            boolean failed = false;



            try {
                loadedItem = helper.getPantryItem(ingredientId);
            } catch (SQLiteException exception) {
                failed= true;
                Log.e(TAG, "Could not load ingredient", exception);
            } finally {
                helper.close();
            }
            final PantryItem item= loadedItem;
            final boolean loadFailed =  failed;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;

                }



                if(loadFailed) {
                    showMessageAndFinish(
                            R.string.ingredient_load_failed
                    );
                    return;
                }
                if (item == null) {
                    showMessageAndFinish(
                            R.string.ingredient_not_found
                    );
                    return;
                }


                nameInput.setText(item.getName());
                quantityInput.setText(
                        BigDecimal.valueOf(item.getQuantity())
                                .stripTrailingZeros()
                                .toPlainString()
                );


                for (int position= 0;
                     position < unitSpinner.getCount();
                     position++) {
                    String unit = unitSpinner
                            .getItemAtPosition(position)
                            .toString();
                    if(unit.equals(item.getUnit())) {
                        unitSpinner.setSelection(position);
                        break;
                    }
                }


                formReady = true;
                setBusy(false);


                showFormHelp();
            });
        });
    }



    private void validateAndSave()   {

        if (busy|| !formReady) {
            return;
        }
        nameInput.setError(null);
        quantityInput.setError( null );
        showFormHelp();
        String name= nameInput.getText().toString().trim();
        String quantityText =quantityInput.getText()
                .toString().trim();
        if(name.isEmpty()) {
            nameInput.setError(
                    getString(R.string.error_ingredient_name)
            );
            nameInput.requestFocus();
            return;

        }


        if (quantityText.isEmpty()) {
            quantityInput.setError(
                    getString(R.string.error_quantity_required)

            );

            quantityInput.requestFocus();
            return;
        }


        double quantity;
        try {
            quantity= Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            showQuantityError();
            return;
        }


        if(Double.isNaN(quantity)
                ||  Double.isInfinite(quantity)
                || quantity <= 0) {
            showQuantityError();
            return;
        }


        Object selectedUnit =unitSpinner.getSelectedItem();
        if (selectedUnit == null) {

            formStatus.setText(R.string.error_unit_required);
            return;
        }


        writeIngredient(
                false,
                name,
                quantity,
                selectedUnit.toString()
        ); // false means save rather than delete
    }



    private void showQuantityError() {

        quantityInput.setError(
                getString(R.string.error_quantity_invalid)
        );

        quantityInput.requestFocus();
    }



    private void confirmDelete()  {
        if (busy || !formReady || !editMode) {
            return;
        }

        if(deleteDialog != null && deleteDialog.isShowing()) {
            return; // prevents multiple confirmation dialogs
        }


        deleteDialog =new AlertDialog.Builder(this)
                .setTitle(R.string.delete_ingredient_title)
                .setMessage(R.string.delete_ingredient_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(
                        R.string.confirm_delete,
                        (dialog, which) ->
                                writeIngredient(true, null, 0, null)
                )
                .create();


        deleteDialog.show(); // nothing is deleted until confirmation
    }

    private void writeIngredient(
            boolean deleting,
            String name,
            double quantity,
            String unit
    ) {

        if(busy || !formReady) {
            return;
        }

        setBusy(true);
        formStatus.setText(
                deleting
                        ? R.string.deleting_ingredient
                        : R.string.saving_ingredient
        );


        Context appContext =getApplicationContext();
        databaseExecutor.execute(() -> {
            DatabaseHelper helper = new DatabaseHelper(appContext);
            int result= -1; // -1 means the operation failed


            try {

                if (deleting) {

                    result= helper.deletePantryItem(ingredientId);

                } else if(editMode) {

                    result = helper.updatePantryItem(
                            ingredientId,
                            name,
                            quantity,
                            unit
                    );

                } else {

                    long newId= helper.addPantryItem(
                            name,
                            quantity,
                            unit
                    );

                    result = newId > 0 ? 1 : -1;
                }

            } catch (SQLiteException | IllegalArgumentException exception) {
                Log.e(TAG, "Could not change pantry ingredient", exception);

            } finally {
                helper.close();
            }


            final int affectedRows= result;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }


                if(affectedRows > 0) {
                    int message;
                    if(deleting) {

                        message = R.string.ingredient_deleted;
                    } else if (editMode) {
                        message= R.string.ingredient_updated;

                    } else {
                        message = R.string.ingredient_saved;
                    }


                    showMessageAndFinish(message);
                } else if(affectedRows == 0) {
                    showMessageAndFinish(
                            R.string.ingredient_not_found
                    );

                } else {
                    setBusy(false);
                    formStatus.setText(
                            deleting
                                    ? R.string.ingredient_delete_failed
                                    : R.string.ingredient_save_failed
                    );
                }
            });

        });
    }



    private void setBusy(boolean value) {
        busy= value;
        nameInput.setEnabled(!value);
        quantityInput.setEnabled(!value);
        unitSpinner.setEnabled(!value);
        saveButton.setEnabled(!value);
        deleteButton.setEnabled(!value);
    }



    private void showFormHelp() {

        formStatus.setText(
                editMode
                        ? R.string.ingredient_edit_help
                        : R.string.ingredient_form_help
        );
    }



    private void showMessageAndFinish(int messageResource) {

        Toast.makeText(
                this,
                messageResource,
                Toast.LENGTH_SHORT
        ).show();

        finish(); // mainactivity reloads its list when it resumes
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("form_ready", formReady);
        outState.putString("name", nameInput.getText().toString());

        outState.putString(
                "quantity",
                quantityInput.getText().toString()
        );

        outState.putInt(
                "unit_position",
                unitSpinner.getSelectedItemPosition()
        );


        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onDestroy() {

        if (deleteDialog != null) {
            deleteDialog.dismiss(); // releases the dialog with its screen
        }


        databaseExecutor.shutdown();

        super.onDestroy();
    }
}