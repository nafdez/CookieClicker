package es.ignaciofp.contador.activities;

import android.annotation.SuppressLint;
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
import java.util.Objects;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.adapters.AdapterUpgrade;
import es.ignaciofp.contador.models.Upgrade;
import es.ignaciofp.contador.utils.RecyclerItemClickListener;
import es.ignaciofp.contador.utils.UpgradeDecorator;

public class ShopActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    private static final String TAG = "ShopActivity";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private ArrayList<Upgrade> upgradeList;
    private AdapterUpgrade adapterUpgrade;
    private RecyclerView upgradesRecycler;

    // Base prices
    private final BigInteger basicBasePrice = BigInteger.valueOf(100);
    private final BigInteger megaBasePrice = BigInteger.valueOf(1000);
    private final BigInteger autoBasePrice = BigInteger.valueOf(450);
    private final BigInteger megaAutoBasePrice = BigInteger.valueOf(2670);

    // Actual prices
    private BigInteger basicPrice = basicBasePrice;
    private BigInteger megaPrice = megaBasePrice;
    private BigInteger autoPrice = autoBasePrice;
    private BigInteger megaAutoPrice = megaAutoBasePrice;

    // Values
    private BigInteger coins;
    private BigInteger clickValue;
    private BigInteger autoClickValue;

    // Views
    TextView textCoins;
    private TextView textClickValue;
    private TextView textAutoClickValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Bundle bundle = getIntent().getExtras();

        coins = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_COINS), "0"));
        clickValue = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_CLICK_VALUE), "1"));
        autoClickValue = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        textCoins = findViewById(R.id.text_shop_coins);
        textCoins.setText(valueWithSuffix(coins, "§"));
        textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(valueWithSuffix(clickValue, "§/click"));
        textAutoClickValue = findViewById(R.id.text_auto_click);
        textAutoClickValue.setText(valueWithSuffix(autoClickValue, "§/s"));

        basicPrice = new BigInteger(sharedPref.getString(getString(R.string.PREF_BASIC_PRICE), basicBasePrice.toString()));
        megaPrice = new BigInteger(sharedPref.getString(getString(R.string.PREF_MEGA_PRICE), megaBasePrice.toString()));
        autoPrice = new BigInteger(sharedPref.getString(getString(R.string.PREF_AUTO_PRICE), autoBasePrice.toString()));
        megaAutoPrice = new BigInteger(sharedPref.getString(getString(R.string.PREF_MEGA_AUTO_PRICE), megaAutoBasePrice.toString()));

        upgradesRecycler = findViewById(R.id.recycler_upgrades);
        upgradesRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        upgradesRecycler.addItemDecoration(new UpgradeDecorator(8, 6, 0, 0));
        upgradesRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, upgradesRecycler, this));

        upgradeList = new ArrayList<>();

        // Generating upgrade options
        generateUpgradeButton("basic", getString(R.string.shop_upgrade_basic_text), "+1", basicPrice);
        generateUpgradeButton("auto", getString(R.string.shop_upgrade_auto_text), "+1", autoPrice);
        generateUpgradeButton("mega", getString(R.string.shop_upgrade_mega_text), "+0.35%", megaPrice);
        generateUpgradeButton("mega_auto", getString(R.string.shop_upgrade_mega_auto_text), "+0.35%", megaAutoPrice);

        updateUI();
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
        BigInteger[] values;
        BigInteger newPrice = new BigInteger("0");

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
    private void generateUpgradeButton(String buttonTag, String name, String description, BigInteger price) {
        MaterialButton button = new MaterialButton(this);
        button.setTag(buttonTag);
        upgradeList.add(new Upgrade(this, name, description, price, button, false));
    }

    /**
     * Converts a BigInteger value to a formatted string which simplifies the reading
     * reducing the number to five digits at most and assigning it a numeric scale and a suffix
     *
     * @param value the value to be formatted
     * @param msg   the suffix being added
     * @return the formatted string
     */
    @SuppressLint("DefaultLocale")
    private String valueWithSuffix(BigInteger value, String msg) {
        if (value.compareTo(BigInteger.valueOf(1000)) < 0) {
            return String.format("%s%s", value.intValue(), msg);
        }

        int exp = (int) (Math.log(value.intValue()) / Math.log(1000));

        return String.format("%.2f%c%s", value.intValue() / Math.pow(1000, exp), "kMBTCQ".charAt(exp - 1), msg);
    }


    private BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigDecimal valueFactor) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).multiply(priceFactor).toBigInteger());
            actualClickValue = new BigDecimal(actualClickValue).multiply(valueFactor).toBigInteger();

            textCoins.setText(valueWithSuffix(coins, "§"));
            button.setText(valueWithSuffix(price, "§"));
            infoTextView.setText(valueWithSuffix(actualClickValue, msg));

            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            button.startAnimation(fade_in);
            new Thread(this::updateDisabledButtons).start();
        }
        return new BigInteger[]{actualClickValue, price};
    }

    // -1 <; 0 ==; 1 >;
    private BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigInteger toAddValue) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).divide(priceFactor, 0, RoundingMode.CEILING).toBigInteger());
            actualClickValue = actualClickValue.add(toAddValue);

            textCoins.setText(valueWithSuffix(coins, "§"));
            button.setText(valueWithSuffix(price, "§"));
            infoTextView.setText(valueWithSuffix(actualClickValue, msg));

            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            button.startAnimation(fade_in);
            new Thread(this::updateDisabledButtons).start();
        }
        return new BigInteger[]{actualClickValue, price};
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