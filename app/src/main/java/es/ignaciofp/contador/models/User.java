package es.ignaciofp.contador.models;

import java.io.Serializable;

import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class User implements Serializable {

    private String id;
    private String name;

    // GAME DATA
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;

    // SHOP PRICES
    private CustomBigInteger basicPrice;
    private CustomBigInteger megaPrice;
    private CustomBigInteger autoPrice;
    private CustomBigInteger megaAutoPrice;

    public User(String name) {
        this(null, name, AppConstants.DEFAULT_COINS, AppConstants.DEFAULT_CLICK_VALUE, AppConstants.DEFAULT_AUTO_CLICK_VALUE, AppConstants.DEFAULT_BASIC_PRICE, AppConstants.DEFAULT_MEGA_PRICE, AppConstants.DEFAULT_AUTO_PRICE, AppConstants.DEFAULT_MEGA_AUTO_PRICE);
    }

    public User(String id, String name, CustomBigInteger coins, CustomBigInteger clickValue, CustomBigInteger autoClickValue, CustomBigInteger basicPrice, CustomBigInteger megaPrice, CustomBigInteger autoPrice, CustomBigInteger megaAutoPrice) {
        this.id = id;
        this.name = name;
        this.coins = coins;
        this.clickValue = clickValue;
        this.autoClickValue = autoClickValue;
        this.basicPrice = basicPrice;
        this.megaPrice = megaPrice;
        this.autoPrice = autoPrice;
        this.megaAutoPrice = megaAutoPrice;
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

    public CustomBigInteger getCoins() {
        return coins;
    }

    public void setCoins(CustomBigInteger coins) {
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
}
