package com.example.cs360project2option1;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

// Enhancement: Extended AndroidViewModel to manage UI-related data in a lifecycle-conscious way,
// ensuring data is retained during configuration changes while preventing context leaks.
public class InventoryViewModel extends AndroidViewModel {
    private final InventoryRepository repository;

    // Enhancement: Utilized MutableLiveData to hold observable state.
    // This allows the ViewModel to update data internally while exposing read-only LiveData to the UI.
    private final MutableLiveData<List<InventoryItem>> inventoryList;
    private final MutableLiveData<String> statusMessage;
    private final MutableLiveData<String> itemAddedEvent;

    public InventoryViewModel(Application application) {
        super(application);
        repository = new InventoryRepository(application);
        inventoryList = new MutableLiveData<>();
        statusMessage = new MutableLiveData<>();
        itemAddedEvent = new MutableLiveData<>();
        loadItems(); // Fetch initial data securely on creation.
    }

    // Enhancement: Exposed immutable LiveData objects to the Activity to strictly enforce unidirectional data flow.
    public LiveData<List<InventoryItem>> getInventoryList() { return inventoryList; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<String> getItemAddedEvent() { return itemAddedEvent; }

    public void addItem(String name, String qtyStr) {
        // Enhancement: Added baseline null and empty-state checks to prevent NullPointerExceptions during input parsing.
        if (name == null || name.trim().isEmpty() || qtyStr == null || qtyStr.trim().isEmpty()) {
            statusMessage.setValue("Please fill out both fields");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr.trim());

            // Enhancement: Implemented strict Regex-based input validation ("^[a-zA-Z0-9 ]+$").
            // This prevents SQL injection attacks and restricts malicious characters from entering the local database.
            if (name.trim().matches("^[a-zA-Z0-9 ]+$") && qty > 0) {
                repository.insertItem(name.trim(), qty);
                loadItems(); // Automatically refresh the observable list so the UI updates instantly.
                itemAddedEvent.setValue(name.trim()); // Trigger the SMS check event.
            } else {
                statusMessage.setValue("Invalid Input: Please use alphanumeric characters and ensure quantity is greater than 0.");
            }
        } catch (NumberFormatException e) {
            // Enhancement: Caught parsing errors safely rather than crashing the application if a user enters non-integer values.
            statusMessage.setValue("Please enter a valid number for quantity");
        }
    }

    public void deleteItem(int id) {
        repository.deleteItem(id);
        loadItems();
        statusMessage.setValue("Item successfully removed from database");
    }

    private void loadItems() {
        // Enhancement: Used postValue() to ensure background threading compatibility if the database query is ever moved off the main thread.
        inventoryList.postValue(repository.getAllItems());
    }
}