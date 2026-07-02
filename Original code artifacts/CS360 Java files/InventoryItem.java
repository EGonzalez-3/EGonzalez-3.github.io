package com.example.cs360project2option1;

public class InventoryItem {
    private final int id;
    private final String name;
    private final int quantity;

    public InventoryItem(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
}
