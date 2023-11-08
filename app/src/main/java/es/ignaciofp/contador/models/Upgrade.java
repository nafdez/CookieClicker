package es.ignaciofp.contador.models;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import es.ignaciofp.contador.utils.CustomBigInteger;

public class Upgrade {

    private String name;
    private final String description;
    private CustomBigInteger price;
    private final MaterialButton button;
    private boolean isEnabled;
    private final Activity context;

    public Upgrade(Activity context, String name, String description, CustomBigInteger price, MaterialButton button, boolean isEnabled) {
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

    public CustomBigInteger getPrice() {
        return price;
    }

    public void setPrice(CustomBigInteger price) {
        this.price = price;
    }

    public MaterialButton getButton() {
        return button;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getFormattedPrice() {
        return price.withSuffix("§");
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        button.setEnabled(isEnabled);
    }

    @NonNull
    @Override
    public String toString() {
        return "Upgrade{" + "name='" + name + '\'' + ", value='" + description + '\'' + ", price=" + price + '}';
    }
}
