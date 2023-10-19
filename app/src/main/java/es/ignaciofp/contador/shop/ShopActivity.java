package es.ignaciofp.contador.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import es.ignaciofp.contador.MainActivity;
import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.Item;
import es.ignaciofp.contador.models.Option;
import es.ignaciofp.contador.recyclerview.AdapterItems;
import es.ignaciofp.contador.recyclerview.ItemDecorator;
import es.ignaciofp.contador.recyclerview.RecyclerItemClickListener;

public class ShopActivity extends AppCompatActivity implements RecyclerView.OnItemClickListener {

    private static final String TAG = "ShopActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ArrayList<Item> itemList;
    private AdapterItems adapterItems;
    private RecyclerView itemRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        sharedPref = getPreferences(MODE_PRIVATE);
        editor = sharedPref.edit();

        itemRecycler = findViewById(R.id.itemRecyclerView);
        itemRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        itemRecycler.addItemDecoration(new ItemDecorator(8, 6, 0, 0));
        itemRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, itemRecycler, this::onItemClick));

        itemList = new ArrayList<>();

        // Generating upgrade options
        generateItemButton("basic", "Toque", "+1", basicPrice);
        generateItemButton("auto", "Auto-toque", "+1", autoPrice);
        generateItemButton("mega", "Mega toque", "+0.35%", megaPrice);
        generateItemButton("mega_auto", "Mega auto-toque", "+0.35%", megaAutoPrice);

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putBoolean(option.getTag(), option.isChecked());
        editor.apply();
    }

    private void generateItemButton(String buttonTag, String name, String description, BigInteger price) {
        MaterialButton button = new MaterialButton(this);
        button.setTag(buttonTag);
        itemList.add(new Item(this, name, description, price, button, false));
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

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongItemClick(View view, int position) {
        Log.d(TAG, String.format("onClickEvent: %s - %s", itemList.get(position), "HOLA"));
        BigInteger newPrice;
        if ((newPrice = MainActivity.this.onRecyclerButtonClick(itemList.get(position).getButton())).compareTo(BigInteger.ZERO) > 0) {
            itemList.get(position).setPrice(newPrice);
        }
    }
}