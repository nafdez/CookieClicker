package es.ignaciofp.contador.models;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameData implements Serializable {

    private static GameData instance;
    private String playerName;
    private CustomBigInteger coins;
    private CustomBigInteger clickValue;
    private CustomBigInteger autoClickValue;
    private CustomBigInteger coinRate;
    private boolean hasReachedMaxValue;

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    private GameData() {
        coins = AppConstants.DEFAULT_COINS;
        clickValue = AppConstants.DEFAULT_CLICK_VALUE;
        autoClickValue = AppConstants.DEFAULT_AUTO_CLICK_VALUE;
        coinRate = new CustomBigInteger("0");
        hasReachedMaxValue = AppConstants.DEFAULT_HAS_REACHED_MAX_VALUE;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
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

    public CustomBigInteger getCoinRate() {
        return coinRate;
    }

    public void setCoinRate(CustomBigInteger coinRate) {
        this.coinRate = coinRate;
    }

    public boolean hasReachedMaxValue() {
        return hasReachedMaxValue;
    }

    public void setHasReachedMaxValue(boolean hasReachedMaxValue) {
        this.hasReachedMaxValue = hasReachedMaxValue;
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
                map.put(field.getName(), new CustomBigInteger(Objects.requireNonNull(field.get(this)).toString()));
            }
        }
        return map;
    }
}
