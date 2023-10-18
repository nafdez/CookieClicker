package es.ignaciofp.contador.recyclerview;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.Item;

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ViewHolderItems> {

    private static final String TAG = "AdapterItems";
    ArrayList<Item> itemList;

    public AdapterItems(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    public void clear() {
        itemList.clear();
    }

    @NonNull
    @Override
    public ViewHolderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        Log.d(TAG, "onCreateViewHolder");
        return new ViewHolderItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItems holder, int position) {
        Log.d(TAG, String.format("onBindViewHolder: %s", itemList.get(position)));
        holder.assignItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolderItems extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        MaterialButton button;

        public ViewHolderItems(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "Creating: ViewHolderItems");
            name = itemView.findViewById(R.id.upgradeNameTextView);
            description = itemView.findViewById(R.id.upgradeValueTextView);
            button = itemView.findViewById(R.id.upgradeButton);
        }

        public void assignItem(@NonNull Item item) {
            Log.d(TAG, String.format("Assigning item: %s", item));
            name.setText(item.getName());
            description.setText(item.getDescription());
            button.setText(item.getFormatedPrice());
            button.setEnabled(item.isEnabled());

            if(!button.isEnabled()) {
                button.setBackgroundColor(item.getContext().getColor(R.color.buttonShoppingDisabled));
                button.setShadowLayer(1.6f, 1.5f, 1.3f, Color.RED);
            } else {
                button.setBackgroundColor(item.getContext().getColor(R.color.buttonShoppingEnabled));
                button.setShadowLayer(0,0,0, Color.BLACK);
            }
        }
    }
}
