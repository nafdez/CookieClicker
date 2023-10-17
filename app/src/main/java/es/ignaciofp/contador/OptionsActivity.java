package es.ignaciofp.contador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class OptionsActivity extends AppCompatActivity {

    SwitchMaterial toggleThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toggleThemeSwitch = findViewById(R.id.themeSwitch);
        toggleThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // setting theme to night mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            else {
                // setting theme to light theme
                AppCompatDelegate.setDefaultNightMode (AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }
}