package com.example.cs360project2option1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity handles the main database display in a grid format.
 * It uses a RecyclerView for performance and memory efficiency.
 */
public class DatabaseActivity extends AppCompatActivity {

    /* These are our UI components and the helper class we use to talk to SQLite. */
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private EditText itemNameInput, itemQtyInput;
    private static final int SMS_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        /* We initialize our database helper so we can start reading and writing data. */
        dbHelper = new DatabaseHelper(this);

        /* Here we are linking our Java variables to the IDs we created in the XML layout. */
        recyclerView = findViewById(R.id.inventory_recycler_view);
        itemNameInput = findViewById(R.id.et_new_item_name);
        itemQtyInput = findViewById(R.id.et_new_item_qty);
        Button addButton = findViewById(R.id.btn_add_data);

        /* This sets up our grid to have 2 columns, which is a common design for inventory apps. */
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        /* We call this to load any existing data from the database as soon as the app starts. */
        refreshDisplay();

        /* This listener handles adding new data when the 'Add' button is clicked. */
        addButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString().trim();
            String qtyStr = itemQtyInput.getText().toString().trim();

            if (!name.isEmpty() && !qtyStr.isEmpty()) {
                try {
                    int qty = Integer.parseInt(qtyStr);

                    /* This saves the user's input into our SQLite database. */
                    dbHelper.addItem(name, qty);

                    /* We clear the input boxes so they are ready for the next item. */
                    itemNameInput.setText("");
                    itemQtyInput.setText("");

                    /* This refreshes the grid so the user can see their new item immediately. */
                    refreshDisplay();

                    /* This part checks for SMS permissions before trying to send an alert. */
                    checkSmsPermissionAndNotify(name);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * READ: This method gets data from the database and updates the RecyclerView.
     */
    private void refreshDisplay() {
        /* We get a Cursor back from the database containing all our rows. */
        Cursor cursor = dbHelper.getAllItems();
        List<InventoryItem> items = new ArrayList<>();

        /* We loop through the database results and turn them into Java objects. */
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int qty = cursor.getInt(2);
                items.add(new InventoryItem(id, name, qty));
            }
            cursor.close(); /* We close the cursor to save memory. */
        }

        /* We pass the new list to our adapter and attach it to the RecyclerView. */
        adapter = new InventoryAdapter(this, items, dbHelper);
        recyclerView.setAdapter(adapter);
    }

    /**
     * This handles the runtime permission check for sending SMS alerts.
     */
    private void checkSmsPermissionAndNotify(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            /* If we don't have permission yet, we have to ask the user. */
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            /* If we already have permission, we go ahead and send the text. */
            sendSmsAlert(itemName);
        }
    }

    private void sendSmsAlert(String item) {
        try {
            SmsManager sms = SmsManager.getDefault();
            /* This sends a text to the emulator's test number. */
            sms.sendTextMessage("5551212", null, "New Item Added: " + item, null, null);
            Toast.makeText(this, "SMS Notification Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Alerts Enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}