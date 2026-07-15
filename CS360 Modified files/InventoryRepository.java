package com.example.cs360project2option1;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

// Enhancement: Implemented the Repository Pattern to abstract the data layer.
// This creates a clean API for the ViewModel and allows for future expansion (e.g., adding a remote network data source)
// without modifying the business logic.
public class InventoryRepository {
    private final DatabaseHelper dbHelper;

    public InventoryRepository(Context context) {
        // Initializes the original SQLite helper, encapsulating the legacy code inside this modern architecture layer.
        dbHelper = new DatabaseHelper(context);
    }

    public void insertItem(String name, int quantity) {
        dbHelper.addItem(name, quantity);
    }

    public void deleteItem(int id) {
        dbHelper.deleteItem(id);
    }

    // Enhancement: Converted raw database Cursors into an encapsulated domain model (List<InventoryItem>).
    // This protects the higher layers from dealing with raw SQLite data structures and prevents memory leaks.
    public List<InventoryItem> getAllItems() {
        List<InventoryItem> items = new ArrayList<>();
        Cursor cursor = dbHelper.getAllItems();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int qty = cursor.getInt(2);
                items.add(new InventoryItem(id, name, qty));
            }
            // Enhancement: Explicitly closed the cursor after extraction to prevent critical memory leaks in the database connection.
            cursor.close();
        }
        return items;
    }
}
