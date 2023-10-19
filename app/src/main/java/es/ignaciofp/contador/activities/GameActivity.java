package es.ignaciofp.contador.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;

import es.ignaciofp.contador.R;

public class GameActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private TextView textCoins;
    private TextView textCoinRateValue;
    private ImageView image_coin;

    private BigInteger coins;
    private BigInteger clickValue;
    private BigInteger autoClickValue;
    private BigInteger coinRate = new BigInteger("0");
    private BigInteger basicPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        Bundle bundle = getIntent().getExtras();

        // bundle is null always but when coming back from the shop
        if (bundle != null) {
            coins = new BigInteger(bundle.getString(getString(R.string.PREF_COINS), "0"));
            clickValue = new BigInteger(bundle.getString(getString(R.string.PREF_CLICK_VALUE), "1"));
            autoClickValue = new BigInteger(bundle.getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));
            basicPrice = new BigInteger(bundle.getString(getString(R.string.PREF_BASIC_PRICE), "100")); // Hardcoded por no dar mucha vuelta TODO:
        } else {
            coins = new BigInteger(sharedPref.getString(getString(R.string.PREF_COINS), "0"));
            clickValue = new BigInteger(sharedPref.getString(getString(R.string.PREF_CLICK_VALUE), "1"));
            autoClickValue = new BigInteger(sharedPref.getString(getString(R.string.PREF_AUTO_CLICK_VALUE), "0"));
            basicPrice = new BigInteger(sharedPref.getString(getString(R.string.PREF_BASIC_PRICE), "100")); // Hardcoded por no dar mucha vuelta TODO:
        }

        // View assignment
        TextView textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(valueWithSuffix(clickValue, "§/click"));

        TextView textAutoTouchValue = findViewById(R.id.text_auto_click);
        textAutoTouchValue.setText(valueWithSuffix(autoClickValue, "§/s"));

        textCoinRateValue = findViewById(R.id.text_coin_rate);
        textCoinRateValue.setText(valueWithSuffix(coinRate, "§/s"));

        textCoins = findViewById(R.id.text_coins);
        image_coin = findViewById(R.id.image_actionable_coins);

        // If the actual quantity of coins is 0 them the text value of text_coins will be
        // the assigned in the strings.xml file
        if (!coins.equals(BigInteger.ZERO)) {
            textCoins.setText(valueWithSuffix(coins, "§"));
        }

        updateClickImageView();
        gameLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString(getString(R.string.PREF_COINS), coins.toString());
        editor.putString(getString(R.string.PREF_CLICK_VALUE), clickValue.toString());
        editor.putString(getString(R.string.PREF_AUTO_CLICK_VALUE), autoClickValue.toString());
        editor.apply();
    }

    /**
     * If the user touches the coin image, then coins value will be the clickValue at that
     * momemnt added to the coins the user collected. Also plays an animation on the image
     *
     * @param view the view that has been clicked
     */
    public void addOnClick(View view) {
        coins = coins.add(clickValue);
        coinRate = coinRate.add(clickValue);

        ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.2f, 0.7f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(100);
        image_coin.startAnimation(fade_in);

        textCoins.setText(valueWithSuffix(coins, "§"));
        updateClickImageView();
    }

    /**
     * Starts the ShopActivity when the user clicks on the shop icon
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
    @SuppressWarnings("busy-waiting")
    private void gameLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    runOnUiThread(this::updateClickImageView);
                    if (autoClickValue.compareTo(BigInteger.valueOf(0)) > 0) {
                        coins = coins.add(autoClickValue);
                        coinRate = coinRate.add(autoClickValue);
                        runOnUiThread(() -> textCoins.setText(valueWithSuffix(coins, "§")));
                    }

                    runOnUiThread(() -> textCoinRateValue.setText(valueWithSuffix(coinRate, "§/s")));
                    Thread.sleep(500);
                    coinRate = BigInteger.valueOf(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Converts a BigInteger value to a formatted string which simplifies the reading
     * reducing the number to five digits at most and assigning it a numeric scale and a suffix
     * @param value the value to be formatted
     * @param msg the suffix being added
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

    /**
     * Makes a scale dividing the price of the basic upgrade prices and depending how many coins
     * the user has sets an image. Just a visual reminder of how much buying power the user has.
     */
    private void updateClickImageView() {

        if (coins.compareTo(basicPrice.divide(new BigInteger("2"))) < 0) {
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_1);
        } else if (coins.compareTo(basicPrice) < 0) {
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_2);
        } else {
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_3);
        }
    }
}
