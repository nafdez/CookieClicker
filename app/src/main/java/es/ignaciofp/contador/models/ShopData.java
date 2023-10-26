package es.ignaciofp.contador.models;

import java.io.Serializable;

import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class ShopData implements Serializable {

    // Implementing Singleton
    private static ShopData instance;

    // Actual prices
    private CustomBigInteger basicPrice = AppConstants.UPGRADE_BASIC_BASE_PRICE;
    private CustomBigInteger megaPrice = AppConstants.UPGRADE_MEGA_BASE_PRICE;
    private CustomBigInteger autoPrice = AppConstants.UPGRADE_AUTO_BASE_PRICE;
    private CustomBigInteger megaAutoPrice = AppConstants.UPGRADE_MEGA_AUTO_BASE_PRICE;

    private ShopData() {
    }

    public static ShopData getInstance() {
        if (instance == null) {
            instance = new ShopData();
        }
        return instance;
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
