package es.ignaciofp.contador.models;

import java.io.Serializable;

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
}
