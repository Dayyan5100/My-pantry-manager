package com.example.mypantrymanage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;



public class SettingsActivity extends BaseActivity {
    // name used to store the app settings
    public static final String PREFERENCES_NAME= "pantry_settings";
    public static final String KEY_ALPHABETICAL =  "sort_alphabetically";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // loads the settings screen and its title
        setupScreen(
                R.layout.activity_settings,
                R.string.title_settings
        );


        // variables for the spinner and save button
        Spinner sortSpinner= findViewById(R.id.spinner_sort_order);

        Button saveButton=  findViewById(R.id.button_save_settings);


        SharedPreferences preferences= getSharedPreferences(
                PREFERENCES_NAME,
                MODE_PRIVATE
        ); // opens settings belonging to this app


        // gets the previously saved sorting option
        boolean alphabetical = preferences.getBoolean(
                KEY_ALPHABETICAL,
                false
        ); // defaults to newest first


        // selects the correct option in the spinner
        sortSpinner.setSelection(alphabetical ? 0 : 1);


        saveButton.setOnClickListener(view ->   {

            // checks if alphabetical sorting was selected
            boolean useAlphabetical =
                    sortSpinner.getSelectedItemPosition() == 0;


            preferences.edit()
                    .putBoolean(KEY_ALPHABETICAL,  useAlphabetical)
                    .apply(); // stores the selected preference
            // displays a message after saving
            Toast.makeText(
                    SettingsActivity.this,
                    R.string.settings_saved,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}