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
    public static final CustomBigInteger DEFAULT_BASIC_PRICE = new CustomBigInteger("100");
    public static final CustomBigInteger DEFAULT_MEGA_PRICE = new CustomBigInteger("1000");
    public static final CustomBigInteger DEFAULT_AUTO_PRICE = new CustomBigInteger("450");
    public static final CustomBigInteger DEFAULT_MEGA_AUTO_PRICE = new CustomBigInteger("2670");

    // KEYS
    public static final String USER_KEY = "user_key";
}
