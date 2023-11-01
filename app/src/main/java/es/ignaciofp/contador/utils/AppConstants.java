package es.ignaciofp.contador.utils;

import java.math.BigInteger;

public class AppConstants {

    public static final BigInteger BIG_INTEGER_MAX_VALUE = new BigInteger("999999999999999999999999999999999"); // Hardcoded BigInteger limit

    // GAME DEFAULTS
    public static final CustomBigInteger DEFAULT_COINS = new CustomBigInteger("0");
    public static final CustomBigInteger DEFAULT_CLICK_VALUE = new CustomBigInteger("1");
    public static final CustomBigInteger DEFAULT_AUTO_CLICK_VALUE = new CustomBigInteger("0");
    public static final boolean DEFAULT_HAS_REACHED_MAX_VALUE = false;

    // SHOP DEFAULTS
    public static final CustomBigInteger UPGRADE_BASIC_BASE_PRICE = new CustomBigInteger("100");
    public static final CustomBigInteger UPGRADE_MEGA_BASE_PRICE = new CustomBigInteger("1000");
    public static final CustomBigInteger UPGRADE_AUTO_BASE_PRICE = new CustomBigInteger("450");
    public static final CustomBigInteger UPGRADE_MEGA_AUTO_BASE_PRICE = new CustomBigInteger("2670");

    // GAME KEYS
    public static final String GAME_PREFS_NAME = "game_prefs";
    public static final String COINS_KEY = "coins";
    public static final String CLICK_VALUE_KEY = "clickValue";
    public static final String AUTO_CLICK_VALUE_KEY = "autoClickValue";
    public static final String COIN_RATE_KEY = "coinRate";
    public static final String HAS_REACHED_MAX_VALUE_KEY = "hasReachedMaxValue";

    // SHOP KEYS
    public static final String SHOP_PREFS_NAME = "shop_prefs";
    public static final String UPGRADE_BASIC_KEY = "basicPrice";
    public static final String UPGRADE_MEGA_KEY = "megaPrice";
    public static final String UPGRADE_AUTO_KEY = "autoPrice";
    public static final String UPGRADE_MEGA_AUTO_KEY = "megaAutoPrice";
    public static final String AUX_VALUE_KEY = "new_value";
    public static final String AUX_PRICE_KEY = "new_price";



}
