package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import es.ignaciofp.contador.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    /**
     * Launches AuthActivity
     * @param view the view being clicked
     */
    public void launchAuthActivity(View view) {
        startActivity(new Intent(this, AuthActivity.class));
    }

    /**
     * Launches OptionsActivity
     * @param view the view being clicked
     */
    public void launchOptionsActivity(View view) {
        startActivity(new Intent(this, OptionsActivity.class));
    }

    /**
     * Launches RankingActivity
     * @param view the view being clicked
     */
    public void launchInfoActivity(View view) {
        startActivity(new Intent(this, RankingActivity.class));
    }

    /**
     * Finishes all the activities and exits the application
     * @param view the view being clicked
     */
    public void closeApp(View view) {
        finishAffinity();
        finish();
        System.exit(0);
    }
}