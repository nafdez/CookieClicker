package es.ignaciofp.contador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private final BigInteger basicBasePrice = BigInteger.valueOf(100);
    private final BigInteger megaBasePrice = BigInteger.valueOf(1000);
    private final BigInteger autoBasePrice = BigInteger.valueOf(450);
    private final BigInteger megaAutoBasePrice = BigInteger.valueOf(2670);
    private BigInteger basicPrice = basicBasePrice;
    private BigInteger megaPrice = megaBasePrice;
    private BigInteger autoPrice = autoBasePrice;
    private BigInteger megaAutoPrice = megaAutoBasePrice;
    private BigInteger coinRate = new BigInteger("0");


    private TextView currentCoinsTextView;
    private TextView clickValueTextView;
    private TextView autoTouchValueTextView;
    private ImageView clickImageView;
    private TextView coinRateValueTextView;


    private BigInteger coins = BigInteger.valueOf(0);
    private BigInteger clickValue = BigInteger.valueOf(1);
    private BigInteger autoClickValue = BigInteger.valueOf(0);

    private ArrayList<Item> itemList;
    private AdapterItems adapterItems;
    private RecyclerView itemRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        // View assignment
        currentCoinsTextView = findViewById(R.id.numberTextView);
        clickValueTextView = findViewById(R.id.touchValueTextView);
        autoTouchValueTextView = findViewById(R.id.autoTouchValueTextView);
        autoTouchValueTextView.setText(valueWithSuffix(autoClickValue, "§/s"));
        clickImageView = findViewById(R.id.clickImageView);
        coinRateValueTextView = findViewById(R.id.coinRateValueTextView);
        coinRateValueTextView.setText(valueWithSuffix(coinRate, "§/s"));

        // RecyclerView
        itemRecycler = findViewById(R.id.itemRecyclerView);
        itemRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        itemRecycler.addItemDecoration(new ItemDecorator(8, 6, 0, 0));
        itemRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, itemRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, String.format("onClickEvent: %s - %s", itemList.get(position), "HOLA"));
                BigInteger newPrice;
                if ((newPrice = MainActivity.this.onRecyclerButtonClick(itemList.get(position).getButton())).compareTo(BigInteger.ZERO) > 0) {
                    itemList.get(position).setPrice(newPrice);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        itemList = new ArrayList<>();

        // Generating upgrade options
        generateItemButton("basic", "Toque", "+1", basicPrice);
        generateItemButton("auto", "Auto-toque", "+1", autoPrice);
        generateItemButton("mega", "Mega toque", "+0.35%", megaPrice);
        generateItemButton("mega_auto", "Mega auto-toque", "+0.35%", megaAutoPrice);

        clickValueTextView.setText(valueWithSuffix(clickValue, "§/click"));

        gameLoop();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();

        editor.putBoolean(option.getTag(), option.isChecked());
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
                    updateDisabledButtons();
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


    private void generateItemButton(String buttonTag, String name, String description, BigInteger price) {
        MaterialButton button = new MaterialButton(this);
        button.setTag(buttonTag);
        itemList.add(new Item(MainActivity.this, name, description, price, button, false));
    }

    private void updateUI() {
        adapterItems = new AdapterItems(itemList);
        itemRecycler.setAdapter(adapterItems);
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
        if (coins.compareTo(basicPrice.divide(new BigInteger("2"))) < 0) {
            clickImageView.setImageResource(R.drawable.coin_icon);
        } else if (coins.compareTo(basicPrice) < 0) {
            clickImageView.setImageResource(R.drawable.coin_icon_2);
        } else {
            clickImageView.setImageResource(R.drawable.coin_icon_3);
        }
    }

    private void updateDisabledButtons() {
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (coins.compareTo(item.getPrice()) >= 0) {
                final int position = i;
                runOnUiThread(() -> {
                    item.setEnabled(true);
                    adapterItems.notifyItemChanged(position, item);
                });
            } else {
                final int position = i;
                runOnUiThread(() -> {
                    item.setEnabled(false);
                    adapterItems.notifyItemChanged(position, item);
                });
            }
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

}
