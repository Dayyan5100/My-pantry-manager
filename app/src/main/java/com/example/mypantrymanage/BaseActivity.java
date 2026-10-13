package com.example.mypantrymanage;

import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class BaseActivity extends AppCompatActivity {

    // each screen calls this method to load its layout and toolbar
    protected void setupScreen(int layoutResource, int titleResource) {

        EdgeToEdge.enable(this);
        setContentView(layoutResource);


        // hides a theme provided action bar if one exists, each layout supplies its own toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // keeps content clear of system bars, display cutouts and the keyboard
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, windowInsets) -> {

                    Insets insets= windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    |  WindowInsetsCompat.Type.displayCutout()
                                    | WindowInsetsCompat.Type.ime()
                    );

                    view.setPadding(
                            insets.left,
                            insets.top,
                            insets.right,
                            insets.bottom
                    );


                    return windowInsets;
                }
        );


        // gets the toolbar from the current screen
        Toolbar toolbar =findViewById(R.id.toolbar);

        toolbar.setTitle(titleResource);
        toolbar.inflateMenu(R.menu.navigation_menu);


        // secondary screens have a toolbar button to return
        if (!(this instanceof MainActivity)) {

            toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
            toolbar.setNavigationContentDescription(R.string.back);

            toolbar.setNavigationOnClickListener(view -> finish());
        }


        // handles the toolbar menu options
        toolbar.setOnMenuItemClickListener(item -> {

            int itemId  = item.getItemId();


            // opens the pantry screen
            if(itemId == R.id.menu_pantry) {

                openScreen(MainActivity.class);
                return true;
            }


            // opens the suggested recipes screen
            if (itemId== R.id.menu_recipes) {

                openScreen(SuggestedRecipesActivity.class);

                return true;
            }


            // opens the settings screen
            if (itemId ==R.id.menu_settings) {

                openScreen(SettingsActivity.class);
                return true;
            }

            return false;
        });
    }


    // opens another activity from the toolbar
    protected void openScreen(Class<?> screenClass) {

        // selecting the current screen does not open another copy
        if(getClass().equals(screenClass)) {
            return;
        }


        Intent intent= new Intent(this, screenClass);


        // returns to an existing screen if it is already in the activity stack
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );


        startActivity(intent);
    }
}
