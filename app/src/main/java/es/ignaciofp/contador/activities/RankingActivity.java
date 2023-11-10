package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterRanking;
import es.ignaciofp.contador.models.Ranking;
import es.ignaciofp.contador.services.RankingService;

public class RankingActivity extends AppCompatActivity {

    private RankingService RANKING_SERVICE;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        RANKING_SERVICE = RankingService.getInstance(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.main_theme);
        mediaPlayer.start();

        ListView rankingListView = findViewById(R.id.listview_ranking);

        List<Ranking> rankingList = RANKING_SERVICE.getRankingList();

        rankingListView.setAdapter(new AdapterRanking(this, R.layout.item_ranking, R.id.text_blank_ranking, rankingList));

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    public void returnOnClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}