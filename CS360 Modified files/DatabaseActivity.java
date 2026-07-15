package com.example.cs360project2option1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DatabaseActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private InventoryAdapter adapter;
    private EditText itemNameInput, itemQtyInput;
    private static final int SMS_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        itemNameInput = findViewById(R.id.et_new_item_name);
        itemQtyInput = findViewById(R.id.et_new_item_qty);
        Button addButton = findViewById(R.id.btn_add_data);
        RecyclerView recyclerView = findViewById(R.id.inventory_recycler_view);

        // Enhancement: Replaced standard ListView with RecyclerView using a GridLayoutManager for a more scalable and visually organized 2-column UI.
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Enhancement: Passed a lambda function callback to the adapter to maintain separation of concerns.
        // The adapter handles the UI click, but delegates the actual database deletion logic up to the ViewModel.
        adapter = new InventoryAdapter(item -> viewModel.deleteItem(item.getId()));
        recyclerView.setAdapter(adapter);

        // Enhancement: Initialized ViewModel to decouple business logic from the Activity.
        // This ensures UI data survives configuration changes like screen rotations.
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Enhancement: Implemented LiveData Observers. The Activity now passively reacts to data changes
        // rather than manually querying the database, ensuring the UI is always in sync with the data layer.
        viewModel.getInventoryList().observe(this, items -> {
            if (items != null) {
                adapter.setItems(items);
            }
        });

        // Enhancement: Centralized error handling and user feedback. The ViewModel dictates the status message,
        // and the Activity simply displays the Toast, keeping UI logic dumb and testable.
        viewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Enhancement: Event-driven SMS triggering. Once the ViewModel confirms a successful addition,
        // the Activity clears the input fields and checks permissions before sending the alert.
        viewModel.getItemAddedEvent().observe(this, itemName -> {
            if (itemName != null) {
                itemNameInput.setText("");
                itemQtyInput.setText("");
                checkSmsPermissionAndNotify(itemName);
            }
        });

        // Enhancement: The click listener no longer handles database inserts or validation directly.
        // It simply gathers the raw input strings and passes them to the ViewModel for secure processing.
        addButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString();
            String qtyStr = itemQtyInput.getText().toString();
            viewModel.addItem(name, qtyStr);
        });
    }

    // --- SMS Notification Logic ---

    // Enhancement: Added explicit runtime permission checks to prevent app crashes on newer Android API levels
    // that require active user consent for sensitive actions like sending SMS.
    private void checkSmsPermissionAndNotify(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            sendSmsAlert(itemName);
        }
    }

    // Enhancement: Wrapped the SMS manager in a try-catch block to handle potential Telephony exceptions gracefully.
    private void sendSmsAlert(String item) {
        try {
            SmsManager sms = SmsManager.getDefault();
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