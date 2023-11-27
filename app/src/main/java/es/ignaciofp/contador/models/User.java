package es.ignaciofp.contador.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class User implements Serializable {

    private String id;
    private String name;
    private String password;
    private String lastSaveDate;

    // GAME DATA
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;

    // SHOP PRICES
    private CustomBigInteger basicPrice;
    private CustomBigInteger megaPrice;
    private CustomBigInteger autoPrice;
    private CustomBigInteger megaAutoPrice;
    private Boolean hasMaxValue;

    public User(String name, String password) {
        this(null, name, password, null, AppConstants.DEFAULT_COINS, AppConstants.DEFAULT_CLICK_VALUE, AppConstants.DEFAULT_AUTO_CLICK_VALUE, AppConstants.DEFAULT_BASIC_PRICE, AppConstants.DEFAULT_MEGA_PRICE, AppConstants.DEFAULT_AUTO_PRICE, AppConstants.DEFAULT_MEGA_AUTO_PRICE, false);
    }

    public User(String id, String name, String password, String lastSaveDate, CustomBigInteger coins, CustomBigInteger clickValue, CustomBigInteger autoClickValue, CustomBigInteger basicPrice, CustomBigInteger megaPrice, CustomBigInteger autoPrice, CustomBigInteger megaAutoPrice, boolean hasMaxValue) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.lastSaveDate = lastSaveDate;
        this.coins = coins;
        this.clickValue = clickValue;
        this.autoClickValue = autoClickValue;
        this.basicPrice = basicPrice;
        this.megaPrice = megaPrice;
        this.autoPrice = autoPrice;
        this.megaAutoPrice = megaAutoPrice;
        this.hasMaxValue = hasMaxValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastSaveDate() {
        return lastSaveDate;
    }

    public void setLastSaveDate(String lastSaveDate) {
        this.lastSaveDate = lastSaveDate;
    }

    public synchronized CustomBigInteger getCoins() {
        return coins;
    }

    public synchronized void setCoins(CustomBigInteger coins) {
        this.coins = coins;
    }

    public CustomBigInteger getClickValue() {
        return clickValue;
    }

    public void setClickValue(CustomBigInteger clickValue) {
        this.clickValue = clickValue;
    }

    public CustomBigInteger getAutoClickValue() {
        return autoClickValue;
    }

    public void setAutoClickValue(CustomBigInteger autoClickValue) {
        this.autoClickValue = autoClickValue;
    }

    public CustomBigInteger getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(CustomBigInteger basicPrice) {
        this.basicPrice = basicPrice;
    }

    public CustomBigInteger getMegaPrice() {
        return megaPrice;
    }

    public void setMegaPrice(CustomBigInteger megaPrice) {
        this.megaPrice = megaPrice;
    }

    public CustomBigInteger getAutoPrice() {
        return autoPrice;
    }

    public void setAutoPrice(CustomBigInteger autoPrice) {
        this.autoPrice = autoPrice;
    }

    public CustomBigInteger getMegaAutoPrice() {
        return megaAutoPrice;
    }

    public void setMegaAutoPrice(CustomBigInteger megaAutoPrice) {
        this.megaAutoPrice = megaAutoPrice;
    }

    public Boolean getHasMaxValue() {
        return hasMaxValue;
    }

    public void setHasMaxValue(Boolean hasMaxValue) {
        this.hasMaxValue = hasMaxValue;
    }

    public void updateUserLastUpdate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime lastSaveDate = LocalDateTime.now();

        this.lastSaveDate = format.format(lastSaveDate);

    }
}
