package es.ignaciofp.contador;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import es.ignaciofp.contador.listview.AdapterOptions;
import es.ignaciofp.contador.models.Option;

public class OptionsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private ListView optionListView;
    private List<Option> optionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        optionListView = findViewById(R.id.options_listView);

        optionList = new ArrayList<>();
        optionList.add(new Option(getResources().getString(R.string.toggle_theme_option_name), getResources().getString(R.string.toggle_theme_option_description), "toggleTheme"));
        optionList.add(new Option("Sample name", "Sample desc", "sampleSwitch"));
        optionList.add(new Option("Sample name", "Sample desc", "sampleSwitch"));

        optionListView.setAdapter(new AdapterOptions(this, R.layout.option_list, R.id.blanktxtview, optionList));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getTag().toString()) {
            case "toggleTheme":
                toggleTheme(b);
                break;
        }
    }

    private void toggleTheme(boolean isChecked) {
        if (isChecked) { // b => isChecked, if the switch if checked then b = true
            // Setting theme to night mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Setting theme to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}