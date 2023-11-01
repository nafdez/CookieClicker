package es.ignaciofp.contador.models;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * Takes all fields that are CustomBigInteger on this class and converts them into a map.
     * Big code theft case here.
     * <p>
     * <a href="https://stackoverflow.com/questions/21062625/how-to-convert-object-of-any-class-to-a-map-in-java">...</a>
     *
     * @return a map of the attributes of an object
     * @throws IllegalArgumentException an exception is thrown
     */
    public Map<String, CustomBigInteger> toMap() throws IllegalAccessException {
        Map<String, CustomBigInteger> map = new LinkedHashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(CustomBigInteger.class)) {
                Log.d("SHOP_DATA", String.format("%s: %s", field.getName(), field.get(this)));
                map.put(field.getName(), new CustomBigInteger(field.get(this).toString()));
            }
        }
        return map;
    }

}
