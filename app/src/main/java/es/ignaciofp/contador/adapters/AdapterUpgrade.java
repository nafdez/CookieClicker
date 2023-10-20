package es.ignaciofp.contador.adapters;

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
import es.ignaciofp.contador.models.Upgrade;

public class AdapterUpgrade extends RecyclerView.Adapter<AdapterUpgrade.ViewHolderUpgrade> {

    ArrayList<Upgrade> upgradeList;

    public AdapterUpgrade(ArrayList<Upgrade> upgradeList) {
        this.upgradeList = upgradeList;
    }

    public void clear() {
        upgradeList.clear();
    }

    @NonNull
    @Override
    public ViewHolderUpgrade onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upgrade, parent, false);
        return new ViewHolderUpgrade(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUpgrade holder, int position) {
        holder.assignItem(upgradeList.get(position));
    }

    @Override
    public int getItemCount() {
        return upgradeList.size();
    }

    public static class ViewHolderUpgrade extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        MaterialButton button;

        public ViewHolderUpgrade(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_upgrade_name);
            description = itemView.findViewById(R.id.text_upgrade_description);
            button = itemView.findViewById(R.id.upgradeButton);
        }

        public void assignItem(@NonNull Upgrade upgrade) {
            name.setText(upgrade.getName());
            description.setText(upgrade.getDescription());
            button.setText(upgrade.getFormatedPrice());
            button.setEnabled(upgrade.isEnabled());

            if(!button.isEnabled()) {
                button.setBackgroundColor(upgrade.getContext().getColor(R.color.battleship_gray));
                button.setShadowLayer(1.6f, 1.5f, 1.3f, Color.RED);
            } else {
                button.setBackgroundColor(upgrade.getContext().getColor(R.color.dark_moss_green));
                button.setShadowLayer(0,0,0, Color.BLACK);
            }
        }
    }
}
