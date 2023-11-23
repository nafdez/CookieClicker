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
import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.services.GameService;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;
import es.ignaciofp.contador.utils.RecyclerUpgradeClickListener;
import es.ignaciofp.contador.utils.UpgradeDecorator;

public class ShopActivity extends AppCompatActivity implements RecyclerUpgradeClickListener.OnItemClickListener {

    private GameService gameService;

    // Recycler view
    private ArrayList<Upgrade> upgradeList;
    private AdapterUpgrade adapterUpgrade;
    private RecyclerView upgradesRecycler;

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

        gameService = GameService.getInstance(this, null);
        User user = gameService.getUser();

        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundUpgradeId = soundPool.load(this, R.raw.sound_upgrade_buy, 1);

        // View Assignment
        textCoins = findViewById(R.id.text_shop_coins);
        textCoins.setText(user.getCoins().withSuffix("§"));
        textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(user.getClickValue().withSuffix("§/click"));
        textAutoClickValue = findViewById(R.id.text_auto_click);
        textAutoClickValue.setText(user.getAutoClickValue().withSuffix("§/s"));

        // Setting recycler view
        upgradesRecycler = findViewById(R.id.recycler_upgrades);
        upgradesRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        upgradesRecycler.addItemDecoration(new UpgradeDecorator(8, 6, 0, 0));
        upgradesRecycler.addOnItemTouchListener(new RecyclerUpgradeClickListener(this, upgradesRecycler, this));

        upgradeList = new ArrayList<>();

        // Generating upgrade options
        generateUpgradeButton("basic", getString(R.string.shop_upgrade_basic_text), "+1", user.getBasicPrice());
        generateUpgradeButton("auto", getString(R.string.shop_upgrade_auto_text), "+1", user.getAutoPrice());
        generateUpgradeButton("mega", getString(R.string.shop_upgrade_mega_text), "+0.35%", user.getMegaPrice());
        generateUpgradeButton("mega_auto", getString(R.string.shop_upgrade_mega_auto_text), "+0.35%", user.getMegaAutoPrice());

        updateUI();
        gameLoop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameService.saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.stop(soundUpgradeId);
        soundPool.release();
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

        User user = gameService.getUser();

        switch (button.getTag().toString()) {
            case "basic":
                values = onPurchaseAction(button, textClickValue, "§/click", user.getClickValue(), user.getBasicPrice(), AppConstants.DEFAULT_BASIC_PRICE, new BigDecimal("1.03"), new BigInteger("1"));
                user.setClickValue(values[0]);
                user.setBasicPrice(values[1]);
                newPrice = user.getBasicPrice();
                break;
            case "mega":
                values = onPurchaseAction(button, textClickValue, "§/click", user.getClickValue(), user.getMegaPrice(), AppConstants.DEFAULT_MEGA_PRICE, new BigDecimal("1.07"), new BigDecimal("1.35"));
                user.setClickValue(values[0]);
                user.setMegaPrice(values[1]);
                newPrice = user.getMegaPrice();
                break;
            case "auto":
                values = onPurchaseAction(button, textAutoClickValue, "§/s", user.getAutoClickValue(), user.getAutoPrice(), AppConstants.DEFAULT_AUTO_PRICE, new BigDecimal("1.05"), new BigInteger("1"));
                user.setAutoClickValue(values[0]);
                user.setAutoPrice(values[1]);
                newPrice = user.getAutoPrice();
                break;
            case "mega_auto":
                values = onPurchaseAction(button, textAutoClickValue, "§/s", user.getAutoClickValue(), user.getMegaAutoPrice(), AppConstants.DEFAULT_MEGA_AUTO_PRICE, new BigDecimal("1.08"), new BigDecimal("1.35"));
                user.setAutoClickValue(values[0]);
                user.setMegaAutoPrice(values[1]);
                newPrice = user.getMegaAutoPrice();
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
        startActivity(intent);
        finish();
    }

    /**
     * Each second adds the auto click value to the coins.
     */
    @SuppressWarnings("BusyWait")
    private void gameLoop() {
        User user = gameService.getUser();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (user.getAutoClickValue().compareTo(BigInteger.valueOf(0)) > 0) {
                        user.setCoins(user.getCoins().add(user.getAutoClickValue()));
                        runOnUiThread(() -> textCoins.setText(user.getCoins().withSuffix("§")));
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
        User user = gameService.getUser();
        if (user.getCoins().compareTo(price) >= 0) {
            EXECUTOR_POOL.submit(() -> {
                soundPool.play(soundUpgradeId, 1, 1, 1, 0, 1);
            });
            user.setCoins(user.getCoins().subtract(price));

            price = basePrice.add(new BigDecimal(price).multiply(priceFactor).toBigInteger());
            actualClickValue = CustomBigInteger.toCustomBigInteger(new BigDecimal(actualClickValue).multiply(valueFactor));

            textCoins.setText(user.getCoins().withSuffix("§"));
            button.setText(price.withSuffix("§"));
            infoTextView.setText(actualClickValue.withSuffix(msg));

            new Thread(this::updateDisabledButtons).start();
        }
        return new CustomBigInteger[]{actualClickValue, price};
    }

    // -1 <; 0 ==; 1 >;
    private CustomBigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, CustomBigInteger actualClickValue, CustomBigInteger price, CustomBigInteger basePrice, BigDecimal priceFactor, BigInteger toAddValue) {
        User user = gameService.getUser();
        if (user.getCoins().compareTo(price) >= 0) {
            EXECUTOR_POOL.submit(() -> {
               soundPool.play(soundUpgradeId, 1, 1, 1, 0, 1);
            });
            user.setCoins(user.getCoins().subtract(price));

            price = basePrice.add(new BigDecimal(price).divide(priceFactor, 0, RoundingMode.CEILING).toBigInteger());
            actualClickValue = actualClickValue.add(toAddValue);

            textCoins.setText(user.getCoins().withSuffix("§"));
            button.setText(price.withSuffix("§"));
            infoTextView.setText(actualClickValue.withSuffix(msg));

            new Thread(this::updateDisabledButtons).start();
        }
        return new CustomBigInteger[]{actualClickValue, price};
    }

    private void updateDisabledButtons() {
        User user = gameService.getUser();
        for (int i = 0; i < upgradeList.size(); i++) {
            Upgrade upgrade = upgradeList.get(i);
            if (user.getCoins().compareTo(upgrade.getPrice()) >= 0) {
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