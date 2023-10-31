package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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
    private final ShopService SHOP_SERVICE = ShopService.getInstance(this);
    private final GameService GAME_SERVICE = GameService.getInstance(this);

    // Recycler view
    private ArrayList<Upgrade> upgradeList;
    private AdapterUpgrade adapterUpgrade;
    private RecyclerView upgradesRecycler;

    // Views
    private TextView textCoins;
    private TextView textClickValue;
    private TextView textAutoClickValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Getting values from bundle
        /*Bundle bundle = getIntent().getExtras();

        coins = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_COINS), "0"));
        clickValue = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_CLICK_VALUE), "1"));
        autoClickValue = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));*/

        CustomBigInteger coins = GAME_SERVICE.getValue(AppConstants.COINS_KEY);
        CustomBigInteger clickValue = GAME_SERVICE.getValue(AppConstants.CLICK_VALUE_KEY);
        CustomBigInteger autoClickValue = GAME_SERVICE.getValue(AppConstants.AUTO_CLICK_VALUE_KEY);

        CustomBigInteger basicPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_BASIC_KEY);
        CustomBigInteger megaPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_MEGA_KEY);
        CustomBigInteger autoPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_AUTO_KEY);
        CustomBigInteger autoMegaAutoPrice = SHOP_SERVICE.getValue(AppConstants.UPGRADE_MEGA_AUTO_KEY);


        // View Assignment
        textCoins = findViewById(R.id.text_shop_coins);
        textCoins.setText(coins.withSuffix("§"));
        textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(clickValue.withSuffix("§/click"));
        textAutoClickValue = findViewById(R.id.text_auto_click);
        textAutoClickValue.setText(autoClickValue.withSuffix("§/s"));

        // Setting recycler view
        upgradesRecycler = findViewById(R.id.recycler_upgrades);
        upgradesRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        upgradesRecycler.addItemDecoration(new UpgradeDecorator(8, 6, 0, 0));
        upgradesRecycler.addOnItemTouchListener(new RecyclerUpgradeClickListener(this, upgradesRecycler, this));

        upgradeList = new ArrayList<>();

        // Generating upgrade options
        generateUpgradeButton(AppConstants.UPGRADE_BASIC_KEY, getString(R.string.shop_upgrade_basic_text), "+1", basicPrice);
        generateUpgradeButton(AppConstants.UPGRADE_MEGA_KEY, getString(R.string.shop_upgrade_mega_text), "+0.35%", megaPrice);
        generateUpgradeButton(AppConstants.UPGRADE_AUTO_KEY, getString(R.string.shop_upgrade_auto_text), "+1", autoPrice);
        generateUpgradeButton(AppConstants.UPGRADE_MEGA_AUTO_KEY, getString(R.string.shop_upgrade_mega_auto_text), "+0.35%", autoMegaAutoPrice);

        updateUI();
        gameLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePrefs();
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
        Button button = upgradeList.get(position).getButton();
        CustomBigInteger newPrice = new CustomBigInteger("0");
        Map<String, CustomBigInteger> values = SHOP_SERVICE.onButtonClick(upgradeList.get(position));


        if (newPrice.compareTo(BigInteger.valueOf(0)) >= 0) {
            upgradeList.get(position).setPrice(newPrice);
        }

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
        intent.putExtra(getString(R.string.PREF_COINS), coins.toString());
        intent.putExtra(getString(R.string.PREF_CLICK_VALUE), clickValue.toString());
        intent.putExtra(getString(R.string.PREF_AUTO_CLICK_VALUE), autoClickValue.toString());
        intent.putExtra(getString(R.string.PREF_BASIC_PRICE), basicPrice.toString());
        startActivity(intent);
        savePrefs();
        finish();
    }

    /**
     * Saves all the prices and click values to the sharedPrefs
     */
    public void savePrefs() {
        editor.putString(getString(R.string.PREF_COINS), coins.toString());
        editor.putString(getString(R.string.PREF_CLICK_VALUE), clickValue.toString());
        editor.putString(getString(R.string.PREF_AUTO_CLICK_VALUE), autoClickValue.toString());
        editor.putString(getString(R.string.PREF_BASIC_PRICE), basicPrice.toString());
        editor.putString(getString(R.string.PREF_MEGA_PRICE), megaPrice.toString());
        editor.putString(getString(R.string.PREF_AUTO_PRICE), autoPrice.toString());
        editor.putString(getString(R.string.PREF_MEGA_AUTO_PRICE), megaAutoPrice.toString());
        editor.apply();
    }

    /**
     * Each second adds the auto click value to the coins.
     */
    @SuppressWarnings("BusyWait")
    private void gameLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (autoClickValue.compareTo(BigInteger.valueOf(0)) > 0) {
                        coins = coins.add(autoClickValue);
                        runOnUiThread(() -> textCoins.setText(coins.withSuffix("§")));
                        new Thread(this::updateDisabledButtons).start();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Updates the UI
     */
    private void updateUI() {
        adapterUpgrade = new AdapterUpgrade(upgradeList);
        upgradesRecycler.setAdapter(adapterUpgrade);
        new Thread(this::updateDisabledButtons).start();
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
            if (coins.compareTo(upgrade.getPrice()) >= 0) {
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