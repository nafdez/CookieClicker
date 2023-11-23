package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterRanking;
import es.ignaciofp.contador.models.Ranking;
import es.ignaciofp.contador.services.RankingService;
import es.ignaciofp.contador.utils.AppConstants;

public class RankingActivity extends AppCompatActivity {

    private RankingService RANKING_SERVICE;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        RANKING_SERVICE = RankingService.getInstance(this);

        ListView rankingListView = findViewById(R.id.listview_ranking);

        List<Ranking> rankingList = RANKING_SERVICE.getRankingList();

        rankingListView.setAdapter(new AdapterRanking(this, R.layout.item_ranking, R.id.text_blank_ranking, rankingList));

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences(AppConstants.OPTIONS_PREF_KEY, MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this, R.raw.ranking_theme);
        if (sharedPref.getBoolean(AppConstants.OPTIONS_MUSIC_TAG, true)) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    public void returnOnClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}