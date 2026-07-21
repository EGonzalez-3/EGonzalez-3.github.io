package com.example.cs360project2option1;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class InventoryViewModel extends AndroidViewModel {
    private final InventoryRepository repository;
    private final MutableLiveData<List<InventoryItem>> inventoryList;
    private final MutableLiveData<String> statusMessage;
    private final MutableLiveData<String> itemAddedEvent;

    public InventoryViewModel(Application application) {
        super(application);
        repository = new InventoryRepository(application);
        inventoryList = new MutableLiveData<>();
        statusMessage = new MutableLiveData<>();
        itemAddedEvent = new MutableLiveData<>();
        loadItems();
    }

    public LiveData<List<InventoryItem>> getInventoryList() { return inventoryList; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<String> getItemAddedEvent() { return itemAddedEvent; }

    public void addItem(String name, String qtyStr) {
        if (name == null || name.trim().isEmpty() || qtyStr == null || qtyStr.trim().isEmpty()) {
            statusMessage.setValue("Please fill out both fields");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr.trim());

            // Strict Input Validation using Regex as planned
            if (name.trim().matches("^[a-zA-Z0-9 ]+$") && qty > 0) {
                repository.insertItem(name.trim(), qty);
                loadItems(); // Refresh the list
                itemAddedEvent.setValue(name.trim()); // Trigger SMS check
            } else {
                statusMessage.setValue("Invalid Input: Please use alphanumeric characters and ensure quantity is greater than 0.");
            }
        } catch (NumberFormatException e) {
            statusMessage.setValue("Please enter a valid number for quantity");
        }
    }

    public void deleteItem(int id) {
        repository.deleteItem(id);
        loadItems();
        statusMessage.setValue("Item successfully removed from database");
    }

    private void loadItems() {
        inventoryList.postValue(repository.getAllItems());
    }
}
