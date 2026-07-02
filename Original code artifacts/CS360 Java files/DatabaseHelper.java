package com.example.cs360project2option1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper manages the SQLite database lifecycle, including table creation,
 * version management, and the core CRUD (Create, Read, Update, Delete) operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /* These constants define the database name and version number to centralize
       configuration and make future schema updates easier to manage. */
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    /* These strings store the table names as public constants to prevent
       hardcoding errors when referencing them across different classes. */
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ITEMS = "items";

    /* These constants define the column keys for the inventory table, identifying
       each record by its unique ID, name, and total quantity. */
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QTY = "quantity";

    /**
     * Constructor for the DatabaseHelper. This links the helper to the application
     * context and initializes the SQLite database file on the device.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first initialized. This method executes the SQL
     * commands necessary to build the schema for users and inventory items.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /* This command creates a dedicated table to store user credentials,
           enabling secure login functionality for the application. */
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (username TEXT PRIMARY KEY, password TEXT)");

        /* This command builds the inventory items table with an auto-incrementing
           Primary Key to ensure every item has a unique, non-null identifier. */
        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QTY + " INTEGER)");
    }

    /**
     * Handles database schema changes. If the version number increases, existing
     * tables are removed and recreated to ensure the new schema is applied.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* These lines drop the existing tables if they already exist to
           prevent schema conflicts during a version upgrade. */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

        /* This call re-runs the onCreate method to establish the fresh
           database schema structure after an upgrade. */
        onCreate(db);
    }

    // --- CRUD OPERATIONS ---

    /**
     * CREATE: Inserts a new inventory item record into the database.
     * It uses ContentValues to securely map values to their respective columns.
     */
    public void addItem(String name, int qty) {
        /* This opens a writable instance of the database to allow the
           insertion of new data into the persistent storage. */
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        /* These lines pair the provided item name and quantity with their
           corresponding columns in the database table. */
        cv.put(COL_NAME, name);
        cv.put(COL_QTY, qty);

        /* This executes the insertion into the items table and returns
            the unique ID of the newly created row. */
        db.insert(TABLE_ITEMS, null, cv);
    }

    /**
     * READ: Retrieves all records currently stored in the inventory items table.
     * Returns a Cursor which allows the Activity to iterate through the result set.
     */
    public Cursor getAllItems() {
        /* This opens a readable instance of the database to fetch
           data without making any modifications. */
        SQLiteDatabase db = this.getReadableDatabase();

        /* This query performs a full selection of all rows and columns
           within the items table to be displayed in the UI. */
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
    }

    /**
     * DELETE: Permanently removes a specific inventory item using its unique row ID.
     * This logic is invoked when a user performs a long-press in the UI grid.
     */
    public void deleteItem(int id) {
        /* This opens a writable instance of the database to allow
           the removal of an existing record. */
        SQLiteDatabase db = this.getWritableDatabase();

        /* This line removes the item from the actual SQLite
           database using its unique ID number. */
        db.delete(TABLE_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}