package es.ignaciofp.contador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

public class MainActivity extends AppCompatActivity {

    BigInteger basicBasePrice = BigInteger.valueOf(100);
    BigInteger megaBasePrice = BigInteger.valueOf(1000);
    BigInteger autoBasePrice = BigInteger.valueOf(450);
    BigInteger megaAutoBasePrice = BigInteger.valueOf(2670);
    BigInteger basicPrice = basicBasePrice;
    BigInteger megaPrice = megaBasePrice;
    BigInteger autoPrice = autoBasePrice;
    BigInteger megaAutoPrice = megaAutoBasePrice;
    BigInteger coinRate = new BigInteger("0");


    TextView currentCoinsTextView;
    TextView clickValueTextView;
    TextView autoTouchValueTextView;
    ImageView clickImageView;
    TextView coinRateValueTextView;


    BigInteger coins = BigInteger.valueOf(0);
    BigInteger clickValue = BigInteger.valueOf(1);
    BigInteger autoClickValue = BigInteger.valueOf(0);

    ArrayList<Item> itemList;
    AdapterItems adapterItems;
    RecyclerView itemRecycler;
    private static final String TAG = "MainActivityTAGcv";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        adapterItems = new AdapterItems(this, itemList);
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

    public BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigDecimal valueFactor) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).multiply(priceFactor).toBigInteger());
            actualClickValue = new BigDecimal(actualClickValue).multiply(valueFactor).toBigInteger();

            button.setText(valueWithSuffix(price, "§"));
            currentCoinsTextView.setText(valueWithSuffix(coins, "§"));
            infoTextView.setText(valueWithSuffix(actualClickValue, msg));


            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            button.startAnimation(fade_in);
            new Thread(this::updateDisabledButtons).start();
        }
        return new BigInteger[]{actualClickValue, price};
    }

    // -1 <; 0 ==; 1 >;
    public BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigInteger toAddValue) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).divide(priceFactor, 0, RoundingMode.CEILING).toBigInteger());
            actualClickValue = actualClickValue.add(toAddValue);


            button.setText(valueWithSuffix(price, "§"));
            currentCoinsTextView.setText(valueWithSuffix(coins, "§"));
            infoTextView.setText(valueWithSuffix(actualClickValue, msg));

            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            button.startAnimation(fade_in);
            new Thread(this::updateDisabledButtons).start();
        }
        return new BigInteger[]{actualClickValue, price};
    }

    public BigInteger onRecyclerButtonClick(View view) {
        String TAG = "MainActivityClick";
        Button button = (Button) view;
        BigInteger[] values;
        BigInteger newPrice = new BigInteger("0");

        Log.e(TAG, "onClick: " + view.getTag());

        switch (button.getTag().toString()) {
            case "basic":
                values = onPurchaseAction(button, clickValueTextView, "§/click", clickValue, basicPrice, basicBasePrice, new BigDecimal("1.03"), new BigInteger("1"));
                clickValue = values[0];
                basicPrice = values[1];
                newPrice = basicPrice;
                break;
            case "mega":
                values = onPurchaseAction(button, clickValueTextView, "§/click", clickValue, megaPrice, megaBasePrice, new BigDecimal("1.07"), new BigDecimal("1.35"));
                clickValue = values[0];
                megaPrice = values[1];
                newPrice = megaPrice;
                break;
            case "auto":
                values = onPurchaseAction(button, autoTouchValueTextView, "§/s", autoClickValue, autoPrice, autoBasePrice, new BigDecimal("1.05"), new BigInteger("1"));
                autoClickValue = values[0];
                autoPrice = values[1];
                newPrice = autoPrice;
                break;
            case "mega_auto":
                values = onPurchaseAction(button, autoTouchValueTextView, "§/s", autoClickValue, megaAutoPrice, megaAutoBasePrice, new BigDecimal("1.08"), new BigDecimal("1.35"));
                autoClickValue = values[0];
                megaAutoPrice = values[1];
                newPrice = megaAutoPrice;
                break;
        }
        updateClickImageView();
        //updateUI();
        return newPrice.compareTo(BigInteger.valueOf(0)) >= 0 ? newPrice : BigInteger.valueOf(-1);
    }
}
