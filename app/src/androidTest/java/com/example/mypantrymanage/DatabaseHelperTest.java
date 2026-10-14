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


@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest   {


    private static final String TEST_DATABASE=   "pantry_test.db";


    private Context context;
    private DatabaseHelper helper;



    @Before
    public void setUp()   {

        context= InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();


        context.deleteDatabase(TEST_DATABASE); // starts with a clean test database


        helper=  new DatabaseHelper(context, TEST_DATABASE);

    }



    @After
    public void tearDown() {

        helper.close();
        context.deleteDatabase(TEST_DATABASE); // leaves the real pantry untouched
    }



    @Test
    public void pantryCrudAndReopeningWork()   {
        assertTrue( helper.getAllPantryItems().isEmpty() );
        long id= helper.addPantryItem(
                "Tomato",
                2,
                "count"
        ); // create

        assertTrue(id >0);

        PantryItem savedItem =helper.getPantryItem(id); // read

        assertNotNull( savedItem );

        assertEquals("Tomato", savedItem.getName() );

        assertEquals( 2.0, savedItem.getQuantity(), 0.000001);

        assertEquals("count",   savedItem.getUnit());

        assertEquals(1, helper.getAllPantryItems().size() );



        helper.close(); // closes the database connection
        helper = new DatabaseHelper( context, TEST_DATABASE );
        PantryItem reopenedItem= helper.getPantryItem(id);
        assertNotNull(reopenedItem); // confirms the record survived reopening
        assertEquals( "Tomato", reopenedItem.getName());
        assertEquals(2.0, reopenedItem.getQuantity(),  0.000001);
        int updatedRows= helper.updatePantryItem(
                id,
                "Tomatoes",
                5,
                "count"
        ); // update


        assertEquals( 1, updatedRows );
        PantryItem updatedItem =  helper.getPantryItem(id);
        assertNotNull(updatedItem);
        assertEquals("Tomatoes",updatedItem.getName() );
        assertEquals( 5.0, updatedItem.getQuantity(), 0.000001);



        long otherId= helper.addPantryItem( "Rice", 500, "g" );


        assertEquals(1, helper.deletePantryItem(id) ); // delete
        assertNull( helper.getPantryItem(id) );
        assertNotNull(helper.getPantryItem(otherId)); // other ingredients remain
        assertEquals( 1, helper.getAllPantryItems().size() );
    }



    @Test
    public void missingIngredientIdIsHandled()      {

        assertNull( helper.getPantryItem(9999) );
        assertEquals(
                0,
                helper.updatePantryItem(9999, "Rice", 100, "g")
        );
        assertEquals(0,  helper.deletePantryItem(9999) );

    }



    @Test(expected = IllegalArgumentException.class)
    public void zeroQuantityIsRejected() {

        helper.addPantryItem( "Rice", 0, "g" );

    }



    @Test(expected= IllegalArgumentException.class)
    public void blankNameIsRejected()    {
        helper.addPantryItem("   ",  100, "g");
    }



    @Test( expected = IllegalArgumentException.class )
    public void unsupportedUnitIsRejected() {

        helper.addPantryItem(
                "Rice",
                100,
                "unknown"
        );

    }

}
