package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Map;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterUpgrade;
import es.ignaciofp.contador.models.Upgrade;
import es.ignaciofp.contador.services.GameService;
import es.ignaciofp.contador.services.ShopService;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;
import es.ignaciofp.contador.utils.RecyclerUpgradeClickListener;
import es.ignaciofp.contador.utils.UpgradeDecorator;

public class ShopActivity extends AppCompatActivity implements RecyclerUpgradeClickListener.OnItemClickListener {

    // Services
    private ShopService SHOP_SERVICE;
    private GameService GAME_SERVICE;
    private AppConstants APP_CONSTANTS;


    // Recycler view
    private ArrayList<Upgrade> upgradeList;
    private AdapterUpgrade adapterUpgrade;
    private RecyclerView upgradesRecycler;

    // Views
    private TextView textCoins;
    private TextView textClickValue;
    private TextView textAutoClickValue;

    // Media

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        SHOP_SERVICE = ShopService.getInstance(this);
        GAME_SERVICE = GameService.getInstance(this);
        APP_CONSTANTS = AppConstants.getInstance(this);

        // Setting recycler view
        upgradesRecycler = findViewById(R.id.recycler_upgrades);
        upgradesRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        upgradesRecycler.addItemDecoration(new UpgradeDecorator(8, 6, 0, 0));
        upgradesRecycler.addOnItemTouchListener(new RecyclerUpgradeClickListener(this, upgradesRecycler, this));
        upgradeList = new ArrayList<>();

        // Getting prices for initial setting
        CustomBigInteger basicPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_BASIC_KEY);
        CustomBigInteger megaPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_MEGA_KEY);
        CustomBigInteger autoPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_AUTO_KEY);
        CustomBigInteger autoMegaAutoPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_MEGA_AUTO_KEY);

        // Generating upgrade options
        generateUpgradeButton(AppConstants.UPGRADE_BASIC_KEY, getString(R.string.shop_upgrade_basic_text), "+1", basicPrice);
        generateUpgradeButton(AppConstants.UPGRADE_MEGA_KEY, getString(R.string.shop_upgrade_mega_text), "+0.35%", megaPrice);
        generateUpgradeButton(AppConstants.UPGRADE_AUTO_KEY, getString(R.string.shop_upgrade_auto_text), "+1", autoPrice);
        generateUpgradeButton(AppConstants.UPGRADE_MEGA_AUTO_KEY, getString(R.string.shop_upgrade_mega_auto_text), "+0.35%", autoMegaAutoPrice);

        // View Assignment
        textCoins = findViewById(R.id.text_shop_coins);
        textClickValue = findViewById(R.id.text_click_value);
        textAutoClickValue = findViewById(R.id.text_auto_click);

        updateValues();
        updateUI();
        new Thread(this::autoClickLoop).start();
        // Starting music
        APP_CONSTANTS.getMP_MAIN_THEME().start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        APP_CONSTANTS.getMP_MAIN_THEME().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SHOP_SERVICE.saveData(this);
        GAME_SERVICE.saveData(this);
        APP_CONSTANTS.getMP_MAIN_THEME().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        APP_CONSTANTS.getMP_MAIN_THEME().stop();
    }

    /**
     * Checks which button was being clicked and executes an action based on that.
     * Also updates the price of the button that was being clicked.
     *
     * @param view     the view being clicked
     * @param position the position on the list of the view being clicked
     */
    @Override
    public void onItemClick(View view, int position) {
        Map<String, CustomBigInteger> values = SHOP_SERVICE.onButtonClick(upgradeList.get(position).getButton());

        if (!values.isEmpty()) {
            // Price
            upgradeList.get(position).setPrice(values.get(AppConstants.AUX_PRICE_KEY));
        }

        updateValues();

        new Thread(this::updateDisabledButtons).start();
    }

    @Override
    public void onLongItemClick(View view, int position) {
        // TODO: on long click perform multiple clicks until user desist touching the screen
        view.performClick();
    }

    /**
     * If user click on the return icon it will save the current state and start GameActivity again.
     *
     * @param view the view being clicked
     */
    public void returnOnClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Each second adds the auto click value to the coins.
     */
    @SuppressWarnings("BusyWait")
    private void autoClickLoop() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            String coins = GAME_SERVICE.calculateAutoCoins();
            runOnUiThread(() -> textCoins.setText(coins));
        }
    }

    /**
     * Updates the UI
     */
    private void updateUI() {
        adapterUpgrade = new AdapterUpgrade(upgradeList);
        upgradesRecycler.setAdapter(adapterUpgrade);
        new Thread(this::updateDisabledButtons).start();
    }

    private void updateValues() {
        textCoins.setText(GAME_SERVICE.getValue(AppConstants.COINS_KEY).withSuffix("§"));
        textClickValue.setText(GAME_SERVICE.getValue(AppConstants.CLICK_VALUE_KEY).withSuffix("§/click"));
        textAutoClickValue.setText(GAME_SERVICE.getValue(AppConstants.AUTO_CLICK_VALUE_KEY).withSuffix("§/s"));
    }

    /**
     * Generates a new upgrade button, sets it's tag (based on the prefs keys of its value)
     * and adds it to the list.
     *
     * @param buttonTag   the tag of the button
     * @param name        the name of the upgrade
     * @param description the description of the upgrade
     * @param price       the price of the upgrade
     */
    private void generateUpgradeButton(String buttonTag, String name, String description, CustomBigInteger price) {
        MaterialButton button = new MaterialButton(this);
        button.setTag(buttonTag);
        upgradeList.add(new Upgrade(this, name, description, price, button, false));
    }

    private void updateDisabledButtons() {
        for (int i = 0; i < upgradeList.size(); i++) {
            Upgrade upgrade = upgradeList.get(i);
            if (GAME_SERVICE.getValue(AppConstants.COINS_KEY).compareTo(upgrade.getPrice()) >= 0) {
                final int position = i;
                runOnUiThread(() -> {
                    upgrade.setEnabled(true);
                    adapterUpgrade.notifyItemChanged(position, upgrade);
                });
            } else {
                final int position = i;
                runOnUiThread(() -> {
                    upgrade.setEnabled(false);
                    adapterUpgrade.notifyItemChanged(position, upgrade);
                });
            }
        }

    }

}