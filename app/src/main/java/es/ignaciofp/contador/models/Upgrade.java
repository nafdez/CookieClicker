package es.ignaciofp.contador.models;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import java.math.BigInteger;

public class Upgrade {

    private String name;
    private String description;
    private BigInteger price;
    private MaterialButton button;
    private boolean isEnabled;
    private Activity context;

    public Upgrade(Activity context, String name, String description, BigInteger price, MaterialButton button, boolean isEnabled) {
        this.context = context;
        this.name = name;
        this.description = description;
        this.price = price;
        this.button = button;
        this.isEnabled = isEnabled;
    }

    public Activity getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getPrice() {
        return price;
    }

    public String getFormatedPrice() {
        return valueWithSuffix(price);
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public MaterialButton getButton() {
        return button;
    }

    public void setButton(MaterialButton button) {
        this.button = button;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        button.setEnabled(isEnabled);
    }

    @SuppressLint("DefaultLocale")
    private String valueWithSuffix(BigInteger value) {
        if (hasReachedMaxValue(value)) {
            return "MAX";
        }
        if (value.compareTo(BigInteger.valueOf(1000)) < 0) {
            return String.format("%s§", value);
        }

        int exp = (int) (Math.log(value.doubleValue()) / Math.log(1000));

        String result;
        try {
            result = String.format("%.2f%c§", value.doubleValue() / Math.pow(1000, exp), "kMGTPEZYRQ".charAt(exp - 1));
        } catch (StringIndexOutOfBoundsException e) {
            result = "MAX";
        }
        return result;
    }

    private boolean hasReachedMaxValue(BigInteger value) {
        return value.compareTo(new BigInteger("999999999999999999999999999999999")) >= 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Upgrade{" +
                "name='" + name + '\'' +
                ", value='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
