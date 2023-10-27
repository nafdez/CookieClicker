package es.ignaciofp.contador.models;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameData implements Serializable {

    private static GameData instance;
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    private GameData() {
        coins = new CustomBigInteger("0");
        clickValue = new CustomBigInteger("1");
        autoClickValue = new CustomBigInteger("0");
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
                map.put(field.getName(), new CustomBigInteger(field.get(this).toString()));
            }
        }
        return map;
    }
}
