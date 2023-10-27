package es.ignaciofp.contador.utils;

import java.math.BigInteger;

public class AppConstants {

    public static final BigInteger BIG_INTEGER_MAX_VALUE = new BigInteger("999999999999999999999999999999999"); // Hardcoded BigInteger limit
    public static final CustomBigInteger UPGRADE_BASIC_BASE_PRICE = new CustomBigInteger("100");
    public static final CustomBigInteger UPGRADE_MEGA_BASE_PRICE = new CustomBigInteger("1000");
    public static final CustomBigInteger UPGRADE_AUTO_BASE_PRICE = new CustomBigInteger("450");
    public static final CustomBigInteger UPGRADE_MEGA_AUTO_BASE_PRICE = new CustomBigInteger("2670");

    // GAME KEYS
    public static final String GAME_PREFS_NAME = "game_prefs";
    public static final String COINS_KEY = "coins";
    public static final String CLICK_VALUE_KEY = "click_value";
    public static final String AUTO_CLICK_VALUE_KEY = "auto_click_value";

    // SHOP KEYS
    public static final String SHOP_PREFS_NAME = "shop_prefs";
    public static final String UPGRADE_BASIC_KEY = "upgrade_basic";
    public static final String UPGRADE_MEGA_KEY = "upgrade_mega";
    public static final String UPGRADE_AUTO_KEY = "upgrade_auto";
    public static final String UPGRADE_MEGA_AUTO_KEY = "upgrade_mega_auto";
    public static final String AUX_VALUE_KEY = "new_value";
    public static final String AUX_PRICE_KEY = "new_price";



}
