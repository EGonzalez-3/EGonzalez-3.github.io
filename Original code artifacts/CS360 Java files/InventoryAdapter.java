package com.example.cs360project2option1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * InventoryAdapter bridges the SQLite database data to the RecyclerView Grid UI.
 * This class is responsible for the 'Read' and 'Delete' operations of the CRUD cycle.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    /* These variables hold the data we fetched from SQLite, the context
       needed for UI actions, and the database helper to handle deletions. */
    private final List<InventoryItem> mItems;
    private final Context mContext;
    private final DatabaseHelper mDb;

    /**
     * This is the primary constructor that initializes the adapter with the
     * data list and the database helper needed for deletions.
     */
    public InventoryAdapter(Context context, List<InventoryItem> items, DatabaseHelper db) {
        /* This part sets up our local variables using the data passed
           in from the DatabaseActivity so the list can be displayed. */
        this.mContext = context;
        this.mItems = items;
        this.mDb = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* This inflates the grid_item XML file for each entry. It basically
           converts our design into a real View object the app can use. */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* This gets the specific inventory item from our list based
           on the current row position we are currently drawing. */
        InventoryItem item = mItems.get(position);

        /* Here we are taking the name and quantity values from the item
           object and putting them into the TextViews on the screen. */
        holder.nameTv.setText(item.getName());
        holder.qtyTv.setText("Qty: " + item.getQuantity());

        /* This is the long-click listener that allows the user to delete
           an item from the database by pressing and holding on the item. */
        holder.itemView.setOnLongClickListener(v -> {

            /* This finds the exact position of the item in the list. It is
               important to do this so we don't delete the wrong item by mistake. */
            int currentPos = holder.getAdapterPosition();

            if (currentPos != RecyclerView.NO_POSITION) {
                /* This line removes the item from the actual SQLite
                   database using its unique ID number. */
                mDb.deleteItem(mItems.get(currentPos).getId());

                /* After deleting from the database, we also have to remove
                   it from our local list to keep the UI in sync. */
                mItems.remove(currentPos);

                /* These lines tell the RecyclerView that the item is gone
                   so it can play the delete animation and refresh the grid. */
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, mItems.size());

                /* This shows a simple Toast message at the bottom of the
                   screen to confirm that the item was deleted successfully. */
                Toast.makeText(mContext, "Item successfully removed from database", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        /* This tells the RecyclerView how many total items are in our
           list so it knows how many grid squares it needs to create. */
        return mItems != null ? mItems.size() : 0;
    }

    /**
     * The ViewHolder class identifies the specific UI elements within
     * the grid_item layout so they can be populated with data.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /* These variables represent the actual text boxes in our grid_item layout. */
        final TextView nameTv;
        final TextView qtyTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /* This connects our Java variables to the IDs we created in
               grid_item.xml so we can fill them with database data. */
            nameTv = itemView.findViewById(R.id.itemName);
            qtyTv = itemView.findViewById(R.id.itemQuantity);
        }
    }
}