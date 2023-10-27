package es.ignaciofp.contador.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import es.ignaciofp.contador.R;
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
     * Get a value from de GAME_DATA
     * @param key the key of the value
     * @return the value
     */
    public CustomBigInteger getValue(String key) {
        CustomBigInteger value = new CustomBigInteger("-1");
        try {
            value = GAME_DATA.toMap().get(key);
        } catch (IllegalAccessException ignored) {}

        return value;
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

        editor.remove(COINS_KEY);
        editor.remove(CLICK_VALUE_KEY);
        editor.remove(AUTO_CLICK_VALUE_KEY);

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

        GAME_DATA.setCoins(new CustomBigInteger(coins));
        GAME_DATA.setClickValue(new CustomBigInteger(clickValue));
        GAME_DATA.setAutoClickValue(new CustomBigInteger(autoClickValue));
    }
}
