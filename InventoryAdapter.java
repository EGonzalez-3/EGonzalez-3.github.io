package com.example.cs360project2option1;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryItem> mItems = new ArrayList<>();
    private final OnItemLongClickListener listener;

    // Interface to handle delete actions in the Activity/ViewModel
    public interface OnItemLongClickListener {
        void onItemLongClick(InventoryItem item);
    }

    public InventoryAdapter(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<InventoryItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = mItems.get(position);
        holder.nameTv.setText(item.getName());
        holder.qtyTv.setText("Qty: " + item.getQuantity());

        holder.itemView.setOnLongClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                // Pass the event up to the Activity
                listener.onItemLongClick(mItems.get(currentPos));
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTv;
        final TextView qtyTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.itemName);
            qtyTv = itemView.findViewById(R.id.itemQuantity);
        }
    }
}