package com.example.cs360project2option1;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    private final DatabaseHelper dbHelper;

    public InventoryRepository(Context context) {
        // Initializes the original SQLite helper
        dbHelper = new DatabaseHelper(context);
    }

    public void insertItem(String name, int quantity) {
        dbHelper.addItem(name, quantity);
    }

    public void deleteItem(int id) {
        dbHelper.deleteItem(id);
    }

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
            cursor.close();
        }
        return items;
    }
}
