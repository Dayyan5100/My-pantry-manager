package com.example.mypantrymanage;

import android.os.Bundle;
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // loads the settings screen and its title
        setupScreen(
                R.layout.activity_settings,
                R.string.title_settings
        );


    }

}
