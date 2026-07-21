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

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize Adapter with the deletion callback
        adapter = new InventoryAdapter(item -> viewModel.deleteItem(item.getId()));
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Observe Inventory List for UI Updates
        viewModel.getInventoryList().observe(this, items -> {
            if (items != null) {
                adapter.setItems(items);
            }
        });

        // Observe Status Messages for Toasts (Errors or Success)
        viewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe Successful Additions to trigger SMS Notifications
        viewModel.getItemAddedEvent().observe(this, itemName -> {
            if (itemName != null) {
                itemNameInput.setText("");
                itemQtyInput.setText("");
                checkSmsPermissionAndNotify(itemName);
            }
        });

        // Add Button Click Listener delegates validation and saving to ViewModel
        addButton.setOnClickListener(v -> {
            String name = itemNameInput.getText().toString();
            String qtyStr = itemQtyInput.getText().toString();
            viewModel.addItem(name, qtyStr);
        });
    }

    // --- SMS Notification Logic ---

    private void checkSmsPermissionAndNotify(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            sendSmsAlert(itemName);
        }
    }

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