package es.ignaciofp.contador;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openearMainActivity(View view) {
        launchMainActivity();
    }

    public void openOptionsActivity(View view) {
        startActivity(new Intent(this, OptionsActivity.class));
    }

    public void openInfoActivity(View view) {
        startActivity(new Intent(this, InfoActivity.class));
    }


    public void closeApp(View view) {
        finishAffinity();
        finish();
        System.exit(0);
    }
}