package com.example.mypantrymanage;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;


@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest   {

    private static final String TEST_DATABASE =  "pantry_test.db";

    private Context context;
    private DatabaseHelper helper;


    // runs before each test to prepare the database
    @Before
    public void setUp()  {

        context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        // removes any data left from earlier tests
        context.deleteDatabase( TEST_DATABASE );

        helper=  new DatabaseHelper(context, TEST_DATABASE);
    }



    @After
    public void tearDown() {

        helper.close();

        // deletes the test database without changing the real pantry
        context.deleteDatabase(TEST_DATABASE);
    }



    @Test
    public void pantryCrudAndReopeningWork()   {

        // checks that the pantry starts with no ingredients
        assertTrue(helper.getAllPantryItems().isEmpty());


        // creates a new ingredient and stores its id
        long id =helper.addPantryItem(
                "Tomato",
                2,
                "count"
        );

        assertTrue( id > 0 );


        PantryItem savedItem= helper.getPantryItem(id); // reads the saved ingredient

        assertNotNull(savedItem);
        assertEquals("Tomato", savedItem.getName());
        assertEquals(2.0,  savedItem.getQuantity(), 0.000001);
        assertEquals("count",savedItem.getUnit());

        assertEquals(1, helper.getAllPantryItems().size());



        // closes and opens the database again
        helper.close();

        helper = new DatabaseHelper( context, TEST_DATABASE );


        PantryItem reopenedItem =helper.getPantryItem(id);

        assertNotNull( reopenedItem ); // checks that the ingredient was not lost
        assertEquals("Tomato", reopenedItem.getName());
        assertEquals(2.0, reopenedItem.getQuantity(),  0.000001);


        // changes the saved ingredient details
        int updatedRows= helper.updatePantryItem(
                id,
                "Tomatoes",
                5,
                "count"
        );

        assertEquals(1, updatedRows);


        PantryItem updatedItem =  helper.getPantryItem(id);

        assertNotNull(updatedItem);
        assertEquals("Tomatoes", updatedItem.getName());
        assertEquals(5.0, updatedItem.getQuantity(), 0.000001);



        long otherId= helper.addPantryItem( "Rice", 500, "g" );

        // removes the tomato ingredient
        assertEquals(1,helper.deletePantryItem(id));
        assertNull( helper.getPantryItem(id) );

        // the other ingredient should still be in the database
        assertNotNull(helper.getPantryItem(otherId));
        assertEquals( 1, helper.getAllPantryItems().size() );
    }




    @Test
    public void missingIngredientIdIsHandled() {

        // uses an id that is not in the database
        assertNull(helper.getPantryItem(9999));


        assertEquals(
                0,
                helper.updatePantryItem(9999, "Rice", 100, "g")
        );


        assertEquals(0,  helper.deletePantryItem(9999));
    }



    @Test(expected =IllegalArgumentException.class)
    public void zeroQuantityIsRejected() {

        // checks that zero cannot be saved as a quantity
        helper.addPantryItem("Rice", 0, "g");
    }




    @Test(expected= IllegalArgumentException.class)
    public void blankNameIsRejected()   {

        helper.addPantryItem( "   ", 100, "g" );
    }



    @Test( expected =  IllegalArgumentException.class )
    public void unsupportedUnitIsRejected() {

        // tests a unit that is not part of the allowed units
        helper.addPantryItem(
                "Rice",
                100,
                "unknown"
        );
    }




    @Test
    public void fifteenRecipesAreSeededWithoutDuplicatesOnReopening()  {

        // gets all the recipes currently in the test database
        ArrayList<Recipe> recipes= helper.getAllRecipes();

        assertEquals(15, recipes.size());


        // hashset is used because it does not allow duplicate names
        HashSet<String> names =new HashSet<>();


        // goes through each recipe to check its information
        for (Recipe recipe : recipes) {
            assertTrue(recipe.getId() > 0);
            assertTrue( names.add(recipe.getName()) ); // checks for duplicates

            assertTrue(!recipe.getSteps().trim().isEmpty());
            assertTrue( !recipe.getIngredients().isEmpty() );


            // checks the ingredients stored under each recipe
            for(RecipeIngredient ingredient : recipe.getIngredients()) {

                assertTrue(!ingredient.getName().trim().isEmpty());

                assertTrue(ingredient.getQuantity() > 0);
            }
        }



        // checks if some of the main recipes are present
        assertTrue(names.contains("tomato toasted sandwich"));

        assertTrue(names.contains("pasta"));
        assertTrue( names.contains("egg salad") );

        assertTrue(names.contains("chicken wrap"));

        assertTrue(names.contains("butter chicken"));
        assertTrue( names.contains("sticky wings") );

        assertTrue(names.contains("butternut soup"));



        helper.close();

        helper= new DatabaseHelper(context, TEST_DATABASE);


        // checks that recipes are not added twice when reopening
        assertEquals( 15, helper.getAllRecipes().size() );
    }




    @Test
    public void versionOneUpgradeKeepsPantryAndAddsRecipes() {

        // adds rice so we can check if it remains after the upgrade
        long pantryId =helper.addPantryItem("Rice", 500, "g");


        SQLiteDatabase db= helper.getWritableDatabase();


        // removes recipe records to simulate the earlier database
        db.delete(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, null);

        db.delete(DatabaseHelper.TABLE_RECIPES,null, null);


        // changes the stored version number back to one
        db.setVersion(1);

        helper.close();


        helper= new DatabaseHelper(context, TEST_DATABASE);


        // checks whether all 15 recipes were added
        assertEquals(15,helper.getAllRecipes().size());


        PantryItem rice = helper.getPantryItem(pantryId);

        assertNotNull(rice);
        assertEquals("Rice", rice.getName());
        assertEquals(500.0, rice.getQuantity(), 0.000001);



        // confirms that the database was upgraded to version two
        assertEquals(2,  helper.getReadableDatabase().getVersion());



    }


    public ArrayList<Recipe> getSuggestedRecipes(){

        return RecipeMatcher.findMatches(
                getAllRecipes(),
                getAllPantryItems()
        ); // compares stored recipes against the current pantry


    }

}