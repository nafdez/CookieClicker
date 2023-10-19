package es.ignaciofp.contador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;

import es.ignaciofp.contador.models.Item;
import es.ignaciofp.contador.recyclerview.AdapterItems;
import es.ignaciofp.contador.recyclerview.ItemDecorator;
import es.ignaciofp.contador.recyclerview.RecyclerItemClickListener;
import es.ignaciofp.contador.shop.ShopActivity;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private BigInteger coinRate = new BigInteger("0");


    private TextView currentCoinsTextView;
    private TextView clickValueTextView;
    private TextView autoTouchValueTextView;
    private TextView coinRateValueTextView;
    private ImageView clickImageView;

    private BigInteger coins;
    private BigInteger clickValue;
    private BigInteger autoClickValue;

    private ArrayList<Item> itemList;
    private AdapterItems adapterItems;
    private RecyclerView itemRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            coins = new BigInteger(bundle.getString(getString(R.string.coins_value), "0"));
            clickValue = new BigInteger(bundle.getString(getString(R.string.clickvalue_value), "1"));
            autoClickValue = new BigInteger(bundle.getString(getString(R.string.autoclickvalue_value), "0"));
        } else {
            coins = new BigInteger(sharedPref.getString(getString(R.string.coins_value), "0"));
            clickValue = new BigInteger(sharedPref.getString(getString(R.string.clickvalue_value), "1"));
            autoClickValue = new BigInteger(sharedPref.getString(getString(R.string.autoclickvalue_value), "0"));
        }

        // View assignment
        currentCoinsTextView = findViewById(R.id.numberTextView);
        clickValueTextView = findViewById(R.id.touchValueTextView);
        clickValueTextView.setText(valueWithSuffix(clickValue, "§/click"));
        autoTouchValueTextView = findViewById(R.id.autoTouchValueTextView);
        autoTouchValueTextView.setText(valueWithSuffix(autoClickValue, "§/s"));
        clickImageView = findViewById(R.id.clickImageView);
        coinRateValueTextView = findViewById(R.id.coinRateValueTextView);
        coinRateValueTextView.setText(valueWithSuffix(coinRate, "§/s"));

        if (!coins.equals(BigInteger.ZERO)) {
            currentCoinsTextView.setText(valueWithSuffix(coins, "§"));
        }

        updateClickImageView();
        gameLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString(getString(R.string.coins_value), coins.toString());
        editor.putString(getString(R.string.clickvalue_value), clickValue.toString());
        editor.putString(getString(R.string.autoclickvalue_value), autoClickValue.toString());
        editor.apply();
    }

    /**
     * Cada 1000ms (1s) se actualiza el valor de coins por segundo y se añaden las monedas automaticas
     */
    private void gameLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    runOnUiThread(this::updateClickImageView);
                    if (autoClickValue.compareTo(BigInteger.valueOf(0)) > 0) {
                        coins = coins.add(autoClickValue);
                        coinRate = coinRate.add(autoClickValue);
                        runOnUiThread(() -> currentCoinsTextView.setText(valueWithSuffix(coins, "§")));
                    }

                    runOnUiThread(() -> coinRateValueTextView.setText(valueWithSuffix(coinRate, "§/s")));
                    Thread.sleep(500);
                    coinRate = BigInteger.valueOf(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("DefaultLocale")
    private String valueWithSuffix(BigInteger value, String msg) {
        if (value.compareTo(BigInteger.valueOf(1000)) < 0) {
            return String.format("%s%s", value.intValue(), msg);
        }

        int exp = (int) (Math.log(value.intValue()) / Math.log(1000));

        return String.format("%.2f%c%s", value.intValue() / Math.pow(1000, exp), "kMBTCQ".charAt(exp - 1), msg);
    }

    private void updateClickImageView() {
        BigInteger basicPrice = new BigInteger(sharedPref.getString(getString(R.string.basic_price), "100")); // Hardcoded por no dar mucha vuelta TODO:

        if (coins.compareTo(basicPrice.divide(new BigInteger("2"))) < 0) {
            clickImageView.setImageResource(R.drawable.coin_icon);
        } else if (coins.compareTo(basicPrice) < 0) {
            clickImageView.setImageResource(R.drawable.coin_icon_2);
        } else {
            clickImageView.setImageResource(R.drawable.coin_icon_3);
        }
    }


    public void addOnClick(View view) {
        coins = coins.add(clickValue);
        coinRate = coinRate.add(clickValue);

        ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.2f, 0.7f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(100);
        clickImageView.startAnimation(fade_in);

        currentCoinsTextView.setText(valueWithSuffix(coins, "§"));
        updateClickImageView();
    }

    public void shopImageOnClick(View view) {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.putExtra(getString(R.string.coins_value), coins.toString());
        intent.putExtra(getString(R.string.clickvalue_value), clickValue.toString());
        intent.putExtra(getString(R.string.autoclickvalue_value), autoClickValue.toString());
        startActivity(intent);
        finish();
    }
}
