package es.ignaciofp.contador.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameActivity extends AppCompatActivity {

    private static final BigInteger GAME_BI_MAX_VALUE = new BigInteger("999999999999999999999999999999999"); // Hardcoded BigInteger limit
    private SharedPreferences.Editor editor;

    // Views
    private TextView textCoins;
    private TextView textCoinRateValue;
    private ImageView image_coin;

    // Values
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;
    private CustomBigInteger coinRate = new CustomBigInteger("0");
    private CustomBigInteger basicPrice;
    private boolean hasReachedMaxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Getting prefs
        // Shared preferences
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        Bundle bundle = getIntent().getExtras();

        // Bundle is null always but when coming back from the shop
        if (bundle != null) {
            coins = new CustomBigInteger(bundle.getString(getString(R.string.PREF_COINS), "0"));
            clickValue = new CustomBigInteger(bundle.getString(getString(R.string.PREF_CLICK_VALUE), "1"));
            autoClickValue = new CustomBigInteger(bundle.getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));
            basicPrice = new CustomBigInteger(bundle.getString(getString(R.string.PREF_BASIC_PRICE), "100")); // Hardcoded por no dar mucha vuelta TODO:
        } else {
            coins = new CustomBigInteger(sharedPref.getString(getString(R.string.PREF_COINS), "0"));
            clickValue = new CustomBigInteger(sharedPref.getString(getString(R.string.PREF_CLICK_VALUE), "1"));
            autoClickValue = new CustomBigInteger(sharedPref.getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));
            basicPrice = new CustomBigInteger(sharedPref.getString(getString(R.string.PREF_BASIC_PRICE), "100")); // Hardcoded por no dar mucha vuelta TODO:
        }
        hasReachedMaxValue = sharedPref.getBoolean("PREF_HAS_MAX_VALUE", false);

        // View assignment
        TextView textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(clickValue.withSuffix("§/click"));

        TextView textAutoTouchValue = findViewById(R.id.text_auto_click);
        textAutoTouchValue.setText(autoClickValue.withSuffix("§/s"));

        textCoinRateValue = findViewById(R.id.text_coin_rate);
        textCoinRateValue.setText(coinRate.withSuffix("§/s"));

        textCoins = findViewById(R.id.text_coins);
        image_coin = findViewById(R.id.image_actionable_coins);

        // If the actual quantity of coins is 0 them the text value of text_coins will be
        // the assigned in the strings.xml file
        if (!coins.equals(BigInteger.ZERO)) {
            textCoins.setText(coins.withSuffix("§"));
        }

        updateClickImageView();
        if (hasReachedMaxValue) onGameEndDialogCreator();
        new Thread(this::coinRateLoop).start();
        new Thread(this::autoClickLoop).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString(getString(R.string.PREF_COINS), coins.toString());
        editor.putString(getString(R.string.PREF_CLICK_VALUE), clickValue.toString());
        editor.putString(getString(R.string.PREF_AUTO_CLICK_VALUE), autoClickValue.toString());
        editor.putBoolean("PREF_HAS_MAX_VALUE", hasReachedMaxValue);
        editor.apply();
    }

    /**
     * If the user touches the coin image, then coins value will be the clickValue at that
     * moment added to the coins the user collected. Also plays an animation on the image
     *
     * @param view the view that has been clicked
     */
    public void addOnClick(View view) {
        coins = coins.add(clickValue);
        coinRate = coinRate.add(clickValue);

        // Image animation
        ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.2f, 0.7f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(100);
        image_coin.startAnimation(fade_in);

        // Updating the coins text view value
        textCoins.setText(coins.withSuffix("§"));
        if (coins.compareTo(GAME_BI_MAX_VALUE) >= 0) onGameEndDialogCreator();
        updateClickImageView();
    }

    /**
     * Starts the ShopActivity when the user clicks on the shop icon
     *
     * @param view the view that has been clicked
     */
    public void shopImageOnClick(View view) {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(getString(R.string.PREF_COINS), coins.toString());
        intent.putExtra(getString(R.string.PREF_CLICK_VALUE), clickValue.toString());
        intent.putExtra(getString(R.string.PREF_AUTO_CLICK_VALUE), autoClickValue.toString());
        startActivity(intent);
        finish();
    }

    /**
     * Each seconds checks the quantity of coins and updates the coin image accordingly.
     * Also within the same second adds to the coins the amount of them being auto-generated and
     * calculates the amount of money is being added up each second.
     */
    @SuppressWarnings("BusyWait")
    private void coinRateLoop() {
        while (true) {
            try {
                Thread.sleep(500);
                runOnUiThread(this::updateClickImageView); // Updating the coin image

                if (autoClickValue.compareTo(BigInteger.valueOf(0)) > 0) { // Auto-click
                    coins = coins.add(autoClickValue);
                    coinRate = coinRate.add(autoClickValue);
                    runOnUiThread(() -> textCoins.setText(coins.withSuffix("§")));
                }

                runOnUiThread(() -> textCoinRateValue.setText(coinRate.withSuffix("§/s"))); // Coin rate
                Thread.sleep(500);
                coinRate = new CustomBigInteger(BigInteger.valueOf(0).toString()); // Resetting coin rate value
            } catch (InterruptedException ignored) {
            }
        }
    }

    @SuppressWarnings("BusyWait")
    private synchronized void autoClickLoop() {
        try {
            if (autoClickValue.compareTo(BigInteger.ZERO) <= 0) {
                wait();
            }

            while (true) {
                Thread.sleep(1000);
                coins = coins.add(autoClickValue);
                coinRate = coinRate.add(autoClickValue);
                runOnUiThread(() -> textCoins.setText(coins.withSuffix("§")));
            }
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Makes a scale dividing the price of the basic upgrade prices and depending how many coins
     * the user has sets an image. Just a visual reminder of how much buying power the user has.
     */
    private void updateClickImageView() {

        if (coins.compareTo(basicPrice.divide(new BigInteger("2"))) < 0) { // Basic image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_1);
        } else if (coins.compareTo(basicPrice) < 0) { // Upper half image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_2);
        } else { // Over basic price image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_3);
        }
    }

    /**
     * Creates a dialog that shows information and multiple buttons that redirect to different websites
     *
     * @param view the view being clicked
     */
    public void infoImageOnClick(View view) {
        AlertDialog.Builder dialogConstructor = new AlertDialog.Builder(this);
        dialogConstructor.setMessage(getString(R.string.game_info_message_dialog))
                .setTitle(getString(R.string.game_info_title_dialog))
                .setIcon(R.drawable.ic_info)
                .setNeutralButton("Github", (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nafdez"));
                    startActivity(browserIntent);
                })
                .setNegativeButton(getString(R.string.game_info_donate_dialog), (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://revolut.me/ignaciyu00"));
                    startActivity(browserIntent);
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    // do nothing
                }).show();
    }

    private void onGameEndDialogCreator() {
        AlertDialog.Builder dialogConstructor = new AlertDialog.Builder(this);
        dialogConstructor.setMessage("Congratulations, you've finished the game but please, get a life")
                .setTitle("Game end")
                .setIcon(R.drawable.ic_info)
                .setNegativeButton("Reset", (dialog, which) -> {
                    resetValues();
                    recreate();
                })
                .setPositiveButton("Ok", (dialog, which) -> finish()).show();
    }

    private void resetValues() {
        editor.putString(getString(R.string.PREF_COINS), "0");
        editor.putString(getString(R.string.PREF_CLICK_VALUE), "1");
        editor.putString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0");
        editor.putString(getString(R.string.PREF_BASIC_PRICE), "100");
        editor.putString(getString(R.string.PREF_MEGA_PRICE), "1000");
        editor.putString(getString(R.string.PREF_AUTO_PRICE), "450");
        editor.putString(getString(R.string.PREF_MEGA_AUTO_PRICE), "2670");
        editor.putBoolean("PREF_HAS_MAX_VALUE", false);
        hasReachedMaxValue = false;
        editor.commit();
        editor.apply();
    }
}
