package es.ignaciofp.contador.shop;

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
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

import es.ignaciofp.contador.MainActivity;
import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.Item;
import es.ignaciofp.contador.models.Option;
import es.ignaciofp.contador.recyclerview.AdapterItems;
import es.ignaciofp.contador.recyclerview.ItemDecorator;
import es.ignaciofp.contador.recyclerview.RecyclerItemClickListener;

public class ShopActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    private static final String TAG = "ShopActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ArrayList<Item> itemList;
    private AdapterItems adapterItems;
    private RecyclerView itemRecycler;

    private final BigInteger basicBasePrice = BigInteger.valueOf(100);
    private final BigInteger megaBasePrice = BigInteger.valueOf(1000);
    private final BigInteger autoBasePrice = BigInteger.valueOf(450);
    private final BigInteger megaAutoBasePrice = BigInteger.valueOf(2670);
    private BigInteger basicPrice = basicBasePrice;
    private BigInteger megaPrice = megaBasePrice;
    private BigInteger autoPrice = autoBasePrice;
    private BigInteger megaAutoPrice = megaAutoBasePrice;
    private BigInteger coins;
    private BigInteger clickValue;
    private BigInteger autoClickValue;

    TextView coinsTextView;
    private TextView clickValueTextView;
    private TextView autoTouchValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Bundle bundle = getIntent().getExtras();

        coins = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.coins_value), "0"));
        clickValue = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.clickvalue_value), "1"));
        autoClickValue = new BigInteger(Objects.requireNonNull(bundle).getString(getString(R.string.autoclickvalue_value), "0"));

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        coinsTextView = findViewById(R.id.currentCoinsShopTextView);
        coinsTextView.setText(valueWithSuffix(coins, "§"));
        clickValueTextView = findViewById(R.id.touchValueTextView);
        clickValueTextView.setText(valueWithSuffix(clickValue, "§/click"));
        autoTouchValueTextView = findViewById(R.id.autoTouchValueTextView);
        autoTouchValueTextView.setText(valueWithSuffix(autoClickValue, "§/s"));

        basicPrice = new BigInteger(sharedPref.getString(getString(R.string.basic_price), basicBasePrice.toString()));
        megaPrice = new BigInteger(sharedPref.getString(getString(R.string.mega_price), megaBasePrice.toString()));
        autoPrice = new BigInteger(sharedPref.getString(getString(R.string.auto_price), autoBasePrice.toString()));
        megaAutoPrice = new BigInteger(sharedPref.getString(getString(R.string.mega_auto_price), megaAutoBasePrice.toString()));

        itemRecycler = findViewById(R.id.itemRecyclerView);
        itemRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        itemRecycler.addItemDecoration(new ItemDecorator(8, 6, 0, 0));
        itemRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, itemRecycler, this));

        itemList = new ArrayList<>();

        // Generating upgrade options
        generateItemButton("basic", getString(R.string.basic_upgrade_text), "+1", basicPrice);
        generateItemButton("auto", getString(R.string.auto_upgrade_text), "+1", autoPrice);
        generateItemButton("mega", getString(R.string.mega_upgrade_text), "+0.35%", megaPrice);
        generateItemButton("mega_auto", getString(R.string.mega_auto_upgrade), "+0.35%", megaAutoPrice);

        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString(getString(R.string.basic_price), basicPrice.toString());
        editor.putString(getString(R.string.mega_price), megaPrice.toString());
        editor.putString(getString(R.string.auto_price), autoPrice.toString());
        editor.putString(getString(R.string.mega_auto_price), megaAutoPrice.toString());
        editor.apply();
    }

    private void updateUI() {
        adapterItems = new AdapterItems(itemList);
        itemRecycler.setAdapter(adapterItems);
        new Thread(this::updateDisabledButtons).start();
    }

    private void generateItemButton(String buttonTag, String name, String description, BigInteger price) {
        MaterialButton button = new MaterialButton(this);
        button.setTag(buttonTag);
        itemList.add(new Item(this, name, description, price, button, false));
    }

    @SuppressLint("DefaultLocale")
    private String valueWithSuffix(BigInteger value, String msg) {
        if (value.compareTo(BigInteger.valueOf(1000)) < 0) {
            return String.format("%s%s", value.intValue(), msg);
        }

        int exp = (int) (Math.log(value.intValue()) / Math.log(1000));

        return String.format("%.2f%c%s", value.intValue() / Math.pow(1000, exp), "kMBTCQ".charAt(exp - 1), msg);
    }


    public BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigDecimal valueFactor) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).multiply(priceFactor).toBigInteger());
            actualClickValue = new BigDecimal(actualClickValue).multiply(valueFactor).toBigInteger();

            coinsTextView.setText(valueWithSuffix(coins, "§"));
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
    public BigInteger[] onPurchaseAction(Button button, TextView infoTextView, String msg, BigInteger actualClickValue, BigInteger price, BigInteger basePrice, BigDecimal priceFactor, BigInteger toAddValue) {
        if (coins.compareTo(price) >= 0) {
            coins = coins.subtract(price);

            price = basePrice.add(new BigDecimal(price).divide(priceFactor, 0, RoundingMode.CEILING).toBigInteger());
            actualClickValue = actualClickValue.add(toAddValue);

            coinsTextView.setText(valueWithSuffix(coins, "§"));
            button.setText(valueWithSuffix(price, "§"));
            infoTextView.setText(valueWithSuffix(actualClickValue, msg));

            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            button.startAnimation(fade_in);
            new Thread(this::updateDisabledButtons).start();
        }
        return new BigInteger[]{actualClickValue, price};
    }

    @Override
    public void onItemClick(View view, int position) {
        Button button = (Button) itemList.get(position).getButton();
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

        if (newPrice.compareTo(BigInteger.valueOf(0)) >= 0) {
            itemList.get(position).setPrice(newPrice);
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
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

    public void returnOnClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getString(R.string.coins_value), coins.toString());
        intent.putExtra(getString(R.string.clickvalue_value), clickValue.toString());
        intent.putExtra(getString(R.string.autoclickvalue_value), autoClickValue.toString());
        startActivity(intent);
        finish();
    }
}