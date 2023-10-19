package es.ignaciofp.contador;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    private List<Option> optionList;
    boolean checkedDefaultValue = false;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        ListView optionListView = findViewById(R.id.options_listView);

        optionList = new ArrayList<>();

        sharedPref.getAll().forEach((key, value) -> Log.d("hola", String.format("%s: %s", key, value)));

        addOption(getResources().getString(R.string.toggle_theme_option_name), getResources().getString(R.string.toggle_theme_option_description), getResources().getString(R.string.toggle_theme_tag));
        addOption("Sample name", "Sample desc", "sampleSwitch1");
        addOption("Sample name", "Sample desc", "sampleSwitch2");

        optionListView.setAdapter(new AdapterOptions(this, R.layout.option_list, R.id.blanktxtview, optionList));

    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < optionList.size(); i++) {
            Option option = optionList.get(i);
            editor.putBoolean(option.getTag(), option.isChecked());
            Log.d("hola", String.format("[EDITOR]%s: %s", option.getTag(), option.isChecked()));
        }
        editor.apply();
    }

    /**
     * Checks if there is a previously saved state of the switch (based in tag) and adds the option
     * to the list
     *
     * @param name name of the option
     * @param desc description of the option
     * @param tag tag of the option
     */
    private void addOption(String name, String desc, String tag) {
        boolean isChecked = sharedPref.getBoolean(tag, checkedDefaultValue);

        //TODO: Make sure that each option does what it says if "isChecked" is true

        optionList.add(new Option(name, desc, tag, isChecked));
    }

    /**
     * Checks which switch was activated (or deactivated) and perform an action based on that
     * @param compoundButton the switch that is being clicked
     * @param isChecked whether the switch is checked or not
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getTag().toString()) {
            case "toggleTheme":
                toggleTheme(isChecked);
                toggleChecked(compoundButton, isChecked);
                break;
        }
        toggleChecked(compoundButton, isChecked);
    }

    /**
     * This method finds the switch's Option object and sets isChecked accordingly to its current state.
     *
     * @param compoundButton the button who changed the state
     * @param isChecked      whether the switch it's be checked or not
     */
    private void toggleChecked(CompoundButton compoundButton, boolean isChecked) {
        for (Option option : optionList) {
            if (option.getTag().equals(compoundButton.getTag())) {
                option.setChecked(isChecked);
            }
        }
    }

    /**
     * If isChecked is true sets the dark theme for the application and if not then sets the light
     * mode
     * @param isChecked whether the switch is checked or not
     */
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