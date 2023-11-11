package es.ignaciofp.contador.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.Ranking;

public class AdapterRanking extends ArrayAdapter<Ranking> {

    public AdapterRanking(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Ranking> ranking) {
        super(context, resource, textViewResourceId, ranking);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Ranking ranking = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_ranking, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.text_ranking_name)).setText(ranking.getName());
        ((TextView) convertView.findViewById(R.id.text_ranking_date)).setText(ranking.getDate());
        ((TextView) convertView.findViewById(R.id.text_ranking_coins)).setText(ranking.getCoins());

        return super.getView(position, convertView, parent);
    }
}