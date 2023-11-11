package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.SoundPool;
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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterUpgrade;
import es.ignaciofp.contador.models.Upgrade;
import es.ignaciofp.contador.utils.CustomBigInteger;
import es.ignaciofp.contador.utils.RecyclerUpgradeClickListener;
import es.ignaciofp.contador.utils.UpgradeDecorator;

public class ShopActivity extends AppCompatActivity implements RecyclerUpgradeClickListener.OnItemClickListener {

    // Recycler view
    private ArrayList<Upgrade> upgradeList;
    private AdapterUpgrade adapterUpgrade;
    private RecyclerView upgradesRecycler;

    // Base prices
    private final CustomBigInteger basicBasePrice = new CustomBigInteger("100");
    private final CustomBigInteger megaBasePrice = new CustomBigInteger("1000");
    private final CustomBigInteger autoBasePrice = new CustomBigInteger("450");
    private final CustomBigInteger megaAutoBasePrice = new CustomBigInteger("2670");

    // Actual prices
    private CustomBigInteger basicPrice;
    private CustomBigInteger megaPrice;
    private CustomBigInteger autoPrice;
    private CustomBigInteger megaAutoPrice;

    // Values
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;

    // Views
    TextView textCoins;
    private TextView textClickValue;
    private TextView textAutoClickValue;

    SoundPool soundPool;
    int soundUpgradeId;
    private final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Getting values from bundle
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            coins = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_COINS), "0"));
            clickValue = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_CLICK_VALUE), "1"));
            autoClickValue = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));
            basicPrice = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_BASIC_PRICE), basicBasePrice.toString()));
            megaPrice = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_MEGA_PRICE), megaBasePrice.toString()));
            autoPrice = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_AUTO_PRICE), autoBasePrice.toString()));
            megaAutoPrice = new CustomBigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_MEGA_AUTO_PRICE), megaAutoBasePrice.toString()));
        } else {
            coins = new CustomBigInteger("0");
            clickValue = new CustomBigInteger("1");
            autoClickValue = new CustomBigInteger("0");
            basicPrice = basicBasePrice;
            megaPrice = megaBasePrice;
            autoPrice = autoBasePrice;
            megaAutoPrice = megaAutoBasePrice;
        }

        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundUpgradeId = soundPool.load(this, R.raw.sound_upgrade_buy, 1);

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
        generateUpgradeButton("basic", getString(R.string.shop_upgrade_basic_text), "+1", basicPrice);
        generateUpgradeButton("auto", getString(R.string.shop_upgrade_auto_text), "+1", autoPrice);
        generateUpgradeButton("mega", getString(R.string.shop_upgrade_mega_text), "+0.35%", megaPrice);
        generateUpgradeButton("mega_auto", getString(R.string.shop_upgrade_mega_auto_text), "+0.35%", megaAutoPrice);

        updateUI();
        gameLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        CustomBigInteger[] values;
        CustomBigInteger newPrice = new CustomBigInteger("0");

        switch (button.getTag().toString()) {
            case "basic":
                values = onPurchaseAction(button, textClickValue, "§/click", clickValue, basicPrice, basicBasePrice, new BigDecimal("1.03"), new BigInteger("1"));
                clickValue = values[0];
                basicPrice = values[1];
                newPrice = basicPrice;
                break;
            case "mega":
                values = onPurchaseAction(button, textClickValue, "§/click", clickValue, megaPrice, megaBasePrice, new BigDecimal("1.07"), new BigDecimal("1.35"));
                clickValue = values[0];
                megaPrice = values[1];
                newPrice = megaPrice;
                break;
            case "auto":
                values = onPurchaseAction(button, textAutoClickValue, "§/s", autoClickValue, autoPrice, autoBasePrice, new BigDecimal("1.05"), new BigInteger("1"));
                autoClickValue = values[0];
                autoPrice = values[1];
                newPrice = autoPrice;
                break;
            case "mega_auto":
                values = onPurchaseAction(button, textAutoClickValue, "§/s", autoClickValue, megaAutoPrice, megaAutoBasePrice, new BigDecimal("1.08"), new BigDecimal("1.35"));
                autoClickValue = values[0];
                megaAutoPrice = values[1];
                newPrice = megaAutoPrice;
                break;
        }

        if (newPrice.compareTo(BigInteger.valueOf(0)) >= 0) {
            upgradeList.get(position).setPrice(newPrice);
        }
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
        intent.putExtra(getString(R.string.PREF_MEGA_PRICE), megaPrice.toString());
        intent.putExtra(getString(R.string.PREF_AUTO_PRICE), autoPrice.toString());
        intent.putExtra(getString(R.string.PREF_MEGA_AUTO_PRICE), megaAutoPrice.toString());
        startActivity(intent);
        finish();
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

    private CustomBigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, CustomBigInteger actualClickValue, CustomBigInteger price, CustomBigInteger basePrice, BigDecimal priceFactor, BigDecimal valueFactor) {
        if (coins.compareTo(price) >= 0) {
            EXECUTOR_POOL.submit(() -> {
                soundPool.play(soundUpgradeId, 1, 1, 1, 0, 1);
            });
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).multiply(priceFactor).toBigInteger());
            actualClickValue = CustomBigInteger.toCustomBigInteger(new BigDecimal(actualClickValue).multiply(valueFactor));

            textCoins.setText(coins.withSuffix("§"));
            button.setText(price.withSuffix("§"));
            infoTextView.setText(actualClickValue.withSuffix(msg));

            new Thread(this::updateDisabledButtons).start();
        }
        return new CustomBigInteger[]{actualClickValue, price};
    }

    // -1 <; 0 ==; 1 >;
    private CustomBigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, CustomBigInteger actualClickValue, CustomBigInteger price, CustomBigInteger basePrice, BigDecimal priceFactor, BigInteger toAddValue) {
        if (coins.compareTo(price) >= 0) {
            EXECUTOR_POOL.submit(() -> {
               soundPool.play(soundUpgradeId, 1, 1, 1, 0, 1);
            });
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).divide(priceFactor, 0, RoundingMode.CEILING).toBigInteger());
            actualClickValue = actualClickValue.add(toAddValue);

            textCoins.setText(coins.withSuffix("§"));
            button.setText(price.withSuffix("§"));
            infoTextView.setText(actualClickValue.withSuffix(msg));

            new Thread(this::updateDisabledButtons).start();
        }
        return new CustomBigInteger[]{actualClickValue, price};
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