package com.example.mypantrymanage;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    // database name and current version
    private static final String DATABASE_NAME = "my_pantry_manage.db";
    private static final int DATABASE_VERSION= 2;


    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES= "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS  =  "recipe_ingredients";


    public DatabaseHelper(Context context) {
        this(context,DATABASE_NAME); // opens the application's database
    }


    // allows automated tests to use a separate database
    DatabaseHelper(Context context,String databaseName) {
        super(
                context.getApplicationContext(),
                databaseName,
                null,
                DATABASE_VERSION
        );
    }



    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true); // enforces recipe relationships
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creates the table for pantry ingredients
        db.execSQL(
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL CHECK(length(trim(name)) > 0), " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL " +
                        "CHECK(unit IN ('count', 'g', 'kg', 'ml', 'l'))" +
                        ")"

        );



        // creates the recipe table
        db.execSQL(
                "CREATE TABLE " +  TABLE_RECIPES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL CHECK(length(trim(name)) > 0), " +
                        "steps TEXT NOT NULL CHECK(length(trim(steps)) > 0)" +
                        ")"
        );







        db.execSQL(
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS  + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL CHECK(length(trim(name)) > 0), " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL " +
                        "CHECK(unit IN ('count', 'g', 'kg', 'ml', 'l')), " +
                        "FOREIGN KEY(recipe_id) REFERENCES " +
                        TABLE_RECIPES + "(id) ON DELETE CASCADE" +
                        ")"
        ); // links each required ingredient to its recipe


        RecipeSeeder.seed(db); // adds the starting recipes
    }
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {


        // checks if the database is being updated from version 1 to 2
        if (oldVersion== 1 && newVersion  == 2) {

            RecipeSeeder.seed(db); // adds recipes without deleting pantry items
            return;
        }


        // stops an unsupported database upgrade
        throw new SQLiteException(
                "Database migration required from version "
                        + oldVersion + " to " + newVersion
        );
    }



    public long addPantryItem(String name, double quantity, String unit) {

        ContentValues values= createPantryValues(name, quantity, unit);

        return getWritableDatabase().insertOrThrow(
                TABLE_PANTRY,
                null,
                values
        ); // returns the id of the newly added ingredient
    }


    public ArrayList<PantryItem>getAllPantryItems() {
        return getAllPantryItems(false); // newest ingredients first
    }



    public ArrayList<PantryItem> getAllPantryItems(boolean alphabetical) {

        // creates a list to hold the pantry items
        ArrayList<PantryItem> items =  new ArrayList<>();


        String sortOrder= alphabetical
                ? "name COLLATE NOCASE ASC, id DESC"
                : "id DESC";


        // gets the pantry records from the database
        try (Cursor cursor= getReadableDatabase().query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        )) {

            // loops through all the records returned
            while (cursor.moveToNext()) {

                items.add(readPantryItem(cursor));
            }
        }


        return items;
    }



    public PantryItem getPantryItem(long id) {

        try (Cursor cursor = getReadableDatabase().query(
                TABLE_PANTRY,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {

            // checks whether a matching ingredient was found
            if(cursor.moveToFirst()) {

                return readPantryItem(cursor);
            }
        }


        return null; // returns null if the ingredient does not exist
    }



    public int updatePantryItem(
            long id,
            String name,
            double quantity,
            String unit
    ) {

        ContentValues values =createPantryValues(name, quantity, unit);

        // updates the ingredient matching the selected id
        return getWritableDatabase().update(
                TABLE_PANTRY,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }



    public int deletePantryItem(long id) {

        return getWritableDatabase().delete(
                TABLE_PANTRY,
                "id = ?",
                new String[]{String.valueOf(id)}
        ); // only deletes the selected ingredient
    }



    private ContentValues createPantryValues(
            String name,
            double quantity,
            String unit
    ) {

        // checks if the user entered a name
        if(name == null || name.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ingredient name cannot be empty."
            );
        }


        if (Double.isNaN(quantity)
                ||  Double.isInfinite(quantity)
                || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be a positive, finite number."
            );
        }


        // checks if the selected measurement unit is supported
        if(!isSupportedUnit(unit)) {

            throw new IllegalArgumentException(
                    "Choose count, g, kg, ml or l."
            );
        }


        ContentValues values= new ContentValues();

        values.put("name", name.trim());
        values.put("quantity",  quantity);
        values.put("unit", unit);


        return values;
    }



    private boolean isSupportedUnit(String unit){

        // returns true if the unit matches one of these options
        return "count".equals(unit)
                || "g".equals(unit)
                || "kg".equals(unit)
                || "ml".equals(unit)
                ||  "l".equals(unit);
    }



    private PantryItem readPantryItem(Cursor cursor){

        // reads the id from the current database row
        long id= cursor.getLong(
                cursor.getColumnIndexOrThrow("id")
        );


        String name = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
        );

        double quantity= cursor.getDouble(
                cursor.getColumnIndexOrThrow("quantity")
        );


        String unit =  cursor.getString(
                cursor.getColumnIndexOrThrow("unit")
        );


        return new PantryItem(id,name,  quantity, unit);
    }



    // gets all recipes stored in the database
    public ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes =new ArrayList<>();


        try (Cursor cursor = getReadableDatabase().query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                "name COLLATE NOCASE ASC"
        )) {

            while (cursor.moveToNext()) {

                recipes.add(readRecipe(cursor));
            }
        }


        return recipes;
    }



    public Recipe getRecipe(long id) {

        // searches for a recipe using its database id
        try (Cursor cursor= getReadableDatabase().query(
                TABLE_RECIPES,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {

            if (cursor.moveToFirst()) {
                return readRecipe(cursor);
            }
        }


        return null; // no recipe was found
    }



    private Recipe readRecipe(Cursor cursor) {

        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow("id")
        );


        String name= cursor.getString(
                cursor.getColumnIndexOrThrow("name")
        );

        String steps = cursor.getString(
                cursor.getColumnIndexOrThrow("steps")
        );


        // creates a recipe object using the database information
        return new Recipe(
                id,
                name,
                steps,
                getRecipeIngredients(id)
        );
    }



    public ArrayList<RecipeIngredient> getRecipeIngredients(long recipeId){

        // list that will store the ingredients for a recipe
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();


        try (Cursor cursor =getReadableDatabase().query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                "id ASC"
        )) {

            // reads every ingredient linked to the recipe
            while (cursor.moveToNext()) {

                String name   = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity =cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit= cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                ingredients.add(
                        new RecipeIngredient(name, quantity, unit)
                );
            }
        }
        return ingredients;
    }

}