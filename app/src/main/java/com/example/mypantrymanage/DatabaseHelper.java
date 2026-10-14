package com.example.mypantrymanage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_pantry_manage.db";
    private static final int DATABASE_VERSION= 1; // initial database structure

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES= "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS  =  "recipe_ingredients";



    public DatabaseHelper(Context context) {
        this(context,DATABASE_NAME); // opens the application's database
    }
    // allows automated tests to use a separate database
    DatabaseHelper(Context context,String databaseName){
        super(
                context.getApplicationContext(),
                databaseName,
                null,
                DATABASE_VERSION
        );

    }
    @Override
    public void onConfigure(SQLiteDatabase db){
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // enforces recipe relationships
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL CHECK(length(trim(name)) > 0), " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL " +
                        "CHECK(unit IN ('count', 'g', 'kg', 'ml', 'l'))" +
                        ")"
        ); // stores the user's pantry ingredients
        db.execSQL(
                "CREATE TABLE " +  TABLE_RECIPES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL CHECK(length(trim(name)) > 0), " +
                        "steps TEXT NOT NULL CHECK(length(trim(steps)) > 0)" +
                        ")"
        ); // stores recipe names and preparation steps


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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {

        // a future structure change needs a migration that preserves user data
        throw new SQLiteException(
                "Database migration required from version "
                        + oldVersion +  " to " + newVersion
        );

    }

    public long addPantryItem(String name, double quantity, String unit) {
        ContentValues values= createPantryValues(name, quantity, unit);
        return getWritableDatabase().insertOrThrow(
                TABLE_PANTRY,
                null,
                values
        ); // returns the new record's database id


    }
    public ArrayList<PantryItem>getAllPantryItems(){
        return getAllPantryItems(false); // default order: newest first
    }

    public ArrayList<PantryItem> getAllPantryItems(boolean alphabetical) {

        ArrayList<PantryItem> items =  new ArrayList<>();


        String sortOrder= alphabetical
                ? "name COLLATE NOCASE ASC, id DESC"
                : "id DESC"; // supports the future settings preference

        try (Cursor cursor= getReadableDatabase().query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        )) {
            while (cursor.moveToNext()) {
                items.add(readPantryItem(cursor)); // converts each row to an object
            }
        } // automatically closes the cursor
        return items; // returns an empty list if the pantry has no records
    }

    public PantryItem getPantryItem(long id) {


        try (Cursor cursor=getReadableDatabase().query(
                TABLE_PANTRY,
                null,
                "id = ?",
                new String[]{String.valueOf(id)}, // supplies the id safely
                null,
                null,
                null
        )) {
            if(cursor.moveToFirst()) {
                return readPantryItem(cursor);
            }
        }

        return null; // the record may have been deleted or may not exist
    }

    public int updatePantryItem(
            long id,
            String name,
            double quantity,
            String unit
    ) {

        ContentValues values =createPantryValues(name, quantity, unit);
        return getWritableDatabase().update(
                TABLE_PANTRY,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        ); // returns 1 if updated, or 0 if the id was not found
    }

    public int deletePantryItem(long id){

        return getWritableDatabase().delete(
                TABLE_PANTRY,
                "id = ?",
                new String[]{String.valueOf(id)}
        ); // deletes only the selected record

    }

    private ContentValues createPantryValues(
            String name,
            double quantity,
            String unit
    ) {


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

        if(!isSupportedUnit(unit)){
            throw new IllegalArgumentException(
                    "Choose count, g, kg, ml or l."
            );
        }


        ContentValues values=new ContentValues(); // column-value pairs

        values.put("name", name.trim());
        values.put("quantity",  quantity);
        values.put("unit", unit);


        return values;
    }

    private boolean isSupportedUnit(String unit) {
        return "count".equals(unit)
                || "g".equals(unit)
                || "kg".equals(unit)
                || "ml".equals(unit)
                ||  "l".equals(unit);
    }

    private PantryItem readPantryItem(Cursor cursor){

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


        return new PantryItem(id, name,  quantity, unit);


    }

}