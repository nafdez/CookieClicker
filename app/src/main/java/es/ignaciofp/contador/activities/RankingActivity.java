package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterRanking;
import es.ignaciofp.contador.models.Ranking;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        ListView rankingListView = findViewById(R.id.listview_ranking);

        List<Ranking> rankingList = new ArrayList<>();

        rankingList.add(new Ranking("Nacho", "133.90$"));
        rankingList.add(new Ranking("Pedro", "113.90$"));
        rankingList.add(new Ranking("Pablo", "103.90$"));
        rankingList.add(new Ranking("Nuria", "93.90$"));
        rankingList.add(new Ranking("Abel", "83.90$"));

        rankingListView.setAdapter(new AdapterRanking(this, R.layout.item_ranking, R.id.text_blank_ranking, rankingList));

    }

    public void returnOnClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}