package es.ignaciofp.contador.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.math.BigInteger;

import es.ignaciofp.contador.models.GameData;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameService {

    // Singleton
    private static GameService instance;

    // Models
    private final GameData GAME_DATA = GameData.getInstance();

    // Keys
    private static final String PREFS_NAME = AppConstants.GAME_PREFS_NAME;
    private static final String COINS_KEY = AppConstants.COINS_KEY;
    private static final String CLICK_VALUE_KEY = AppConstants.CLICK_VALUE_KEY;
    private static final String AUTO_CLICK_VALUE_KEY = AppConstants.AUTO_CLICK_VALUE_KEY;
    private static final String HAS_REACHED_MAX_VALUE_KEY = AppConstants.HAS_REACHED_MAX_VALUE_KEY;

    /**
     * Constructor of this class. It needs the context in order to initialize all the data.
     *
     * @param context the context where it's being called from
     */
    private GameService(Context context) {
        recoverData(context);
    }

    /**
     * Checks whether an instance of this class already exists and returns it.
     * If it doesn't exist it creates a new one.
     *
     * @param context the context where it's being called from
     * @return an instance of this class
     */
    public static GameService getInstance(Context context) {
        if (instance == null) {
            instance = new GameService(context);
        }
        return instance;
    }

    /**
     * Get a value from GAME_DATA
     *
     * @param key the key of the value
     * @return the value
     */
    public CustomBigInteger getValue(String key) {
        CustomBigInteger value = new CustomBigInteger("-1");
        try {
            value = GAME_DATA.toMap().get(key);
        } catch (IllegalAccessException ignored) {
        }

        return value;
    }

    public boolean hasReachedMaxValue() {
        return GAME_DATA.hasReachedMaxValue();
    }

    public void setHasReachedMaxValue(boolean value) {
        GAME_DATA.setHasReachedMaxValue(value);
    }

    /**
     * Saves the current state of the shop to the preferences.
     *
     * @param context the context where it's being called from
     */
    public void saveData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(COINS_KEY, GAME_DATA.getCoins().toString());
        editor.putString(CLICK_VALUE_KEY, GAME_DATA.getClickValue().toString());
        editor.putString(AUTO_CLICK_VALUE_KEY, GAME_DATA.getAutoClickValue().toString());
        editor.putBoolean(HAS_REACHED_MAX_VALUE_KEY, GAME_DATA.hasReachedMaxValue());
        editor.apply();
    }

    /**
     * Removes all the game data stored in the preferences.
     *
     * @param context the context where it's being called from
     */
    public void resetData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        GAME_DATA.setCoins(AppConstants.DEFAULT_COINS);
        GAME_DATA.setClickValue(AppConstants.DEFAULT_CLICK_VALUE);
        GAME_DATA.setAutoClickValue(AppConstants.DEFAULT_AUTO_CLICK_VALUE);
        GAME_DATA.setHasReachedMaxValue(AppConstants.DEFAULT_HAS_REACHED_MAX_VALUE);

        editor.clear();
        editor.apply();
    }

    /**
     * Recovers all the data from the preferences and if none of them are previously saved
     * uses the defaults, later it assigns them to the GameData instance.
     * <p>
     * It should only be called at initialization of GameService instance.
     *
     * @param context the context where it's being called from
     */
    private void recoverData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // If value doesn't exist in preferences then use the default values of the initialization of GameData
        String coins = prefs.getString(COINS_KEY, GAME_DATA.getCoins().toString());
        String clickValue = prefs.getString(CLICK_VALUE_KEY, GAME_DATA.getClickValue().toString());
        String autoClickValue = prefs.getString(AUTO_CLICK_VALUE_KEY, GAME_DATA.getAutoClickValue().toString());
        boolean hasReachedMaxValue = prefs.getBoolean(HAS_REACHED_MAX_VALUE_KEY, GAME_DATA.hasReachedMaxValue());

        GAME_DATA.setCoins(new CustomBigInteger(coins));
        GAME_DATA.setClickValue(new CustomBigInteger(clickValue));
        GAME_DATA.setAutoClickValue(new CustomBigInteger(autoClickValue));
        GAME_DATA.setHasReachedMaxValue(hasReachedMaxValue);
    }

    private void recoverData(Bundle bundle) {
        // If value doesn't exist in preferences then use the default values of the initialization of GameData
        String coins = bundle.getString(COINS_KEY, GAME_DATA.getCoins().toString());
        String clickValue = bundle.getString(CLICK_VALUE_KEY, GAME_DATA.getClickValue().toString());
        String autoClickValue = bundle.getString(AUTO_CLICK_VALUE_KEY, GAME_DATA.getAutoClickValue().toString());
        boolean hasReachedMaxValue = bundle.getBoolean(HAS_REACHED_MAX_VALUE_KEY, GAME_DATA.hasReachedMaxValue());

        GAME_DATA.setCoins(new CustomBigInteger(coins));
        GAME_DATA.setClickValue(new CustomBigInteger(clickValue));
        GAME_DATA.setAutoClickValue(new CustomBigInteger(autoClickValue));
        GAME_DATA.setHasReachedMaxValue(hasReachedMaxValue);
    }

    /**
     * Each second adds the auto click value to the coins.
     */
    @SuppressWarnings("all")
    public String calculateAutoCoins() {
        while (GAME_DATA.getAutoClickValue().compareTo(BigInteger.valueOf(0)) <= 0) ;
        addCoins(GAME_DATA.getAutoClickValue());
        return GAME_DATA.getCoins().withSuffix("§");
    }

    ////////////////////////////////COINRATE////////////////////////////////////////
    public void coinRate() {
        if (GAME_DATA.getAutoClickValue().compareTo(BigInteger.valueOf(0)) > 0) { // Auto-click
            GAME_DATA.setCoinRate(GAME_DATA.getCoinRate().add(GAME_DATA.getAutoClickValue()));
        }

    }

    public void resetCoinRate() {
        GAME_DATA.setCoinRate(new CustomBigInteger("0"));
    }

    // TODO: Doc
    public CustomBigInteger addCoins(CustomBigInteger val) {
        GAME_DATA.setCoins(GAME_DATA.getCoins().add(val)); // Adding coins
        GAME_DATA.setCoinRate(GAME_DATA.getCoinRate().add(val)); // Adding to coin rate
        return GAME_DATA.getCoins();
    }
}
