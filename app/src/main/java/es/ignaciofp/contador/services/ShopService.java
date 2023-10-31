package es.ignaciofp.contador.services;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import es.ignaciofp.contador.models.GameData;
import es.ignaciofp.contador.models.ShopData;
import es.ignaciofp.contador.models.Upgrade;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class ShopService {

    // Implementing Singleton
    private static ShopService instance;

    // Models we're going to use
    private final ShopData SHOP_DATA = ShopData.getInstance();
    private final GameData GAME_DATA = GameData.getInstance();

    // Keys
    private final String PREFS_NAME = AppConstants.SHOP_PREFS_NAME;
    private final String UPGRADE_BASIC_KEY = AppConstants.UPGRADE_BASIC_KEY;
    private final String UPGRADE_MEGA_KEY = AppConstants.UPGRADE_MEGA_KEY;
    private final String UPGRADE_AUTO_KEY = AppConstants.UPGRADE_AUTO_KEY;
    private final String UPGRADE_MEGA_AUTO_KEY = AppConstants.UPGRADE_MEGA_AUTO_KEY;

    /**
     * Constructor of this class. It needs the context in order to initialize all the data.
     *
     * @param context the context where it's being called from
     */
    private ShopService(Context context) {
        recoverData(context);
    }

    /**
     * Checks whether an instance of this class already exists and returns it.
     * If it doesn't exist it creates a new one.
     *
     * @param context the context where it's being called from
     * @return an instance of this class
     */
    public static ShopService getInstance(Context context) {
        if (instance == null) {
            instance = new ShopService(context);
        }
        return instance;
    }

    /**
     * Get a value from de SHOP_DATA
     *
     * @param key the key of the value
     * @return the value
     */
    public CustomBigInteger getValue(String key) {
        CustomBigInteger value = new CustomBigInteger("-1");
        try {
            value = SHOP_DATA.toMap().get(key);
        } catch (IllegalAccessException ignored) {
        }

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

        editor.putString(UPGRADE_BASIC_KEY, SHOP_DATA.getBasicPrice().toString());
        editor.putString(UPGRADE_MEGA_KEY, SHOP_DATA.getMegaPrice().toString());
        editor.putString(UPGRADE_AUTO_KEY, SHOP_DATA.getAutoPrice().toString());
        editor.putString(UPGRADE_MEGA_AUTO_KEY, SHOP_DATA.getMegaAutoPrice().toString());
        editor.apply();
    }

    /**
     * Removes all the shop data stored in the preferences.
     *
     * @param context the context where it's being called from
     */
    public void resetData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(UPGRADE_BASIC_KEY);
        editor.remove(UPGRADE_MEGA_KEY);
        editor.remove(UPGRADE_AUTO_KEY);
        editor.remove(UPGRADE_MEGA_AUTO_KEY);

        editor.apply();
    }

    /**
     * Recovers all the data from the preferences and if none of them are previously saved
     * uses the defaults, later it assigns them to the ShopData instance.
     * <p>
     * It should only be called at initialization of ShopService instance.
     *
     * @param context the context where it's being called from
     */
    private void recoverData(Context context) {
        Context cntx = context.getApplicationContext();
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String basic = prefs.getString(UPGRADE_BASIC_KEY, AppConstants.UPGRADE_BASIC_BASE_PRICE.toString());
        String mega = prefs.getString(UPGRADE_MEGA_KEY, AppConstants.UPGRADE_MEGA_BASE_PRICE.toString());
        String auto = prefs.getString(UPGRADE_AUTO_KEY, AppConstants.UPGRADE_AUTO_BASE_PRICE.toString());
        String megaAuto = prefs.getString(UPGRADE_MEGA_AUTO_KEY, AppConstants.UPGRADE_MEGA_AUTO_BASE_PRICE.toString());

        SHOP_DATA.setBasicPrice(new CustomBigInteger(basic));
        SHOP_DATA.setMegaPrice(new CustomBigInteger(mega));
        SHOP_DATA.setAutoPrice(new CustomBigInteger(auto));
        SHOP_DATA.setMegaAutoPrice(new CustomBigInteger(megaAuto));
    }

    private Map<String, CustomBigInteger> onPurchaseAction(String priceKey, String valueToUpdateKey, CustomBigInteger basePrice, BigDecimal priceMultiplyFactor, BigDecimal toMultiply) {
        Map<String, CustomBigInteger> data = new HashMap<>();
        try {
            CustomBigInteger coins = GAME_DATA.getCoins();
            CustomBigInteger currentPrice = SHOP_DATA.toMap().get(priceKey);
            CustomBigInteger currentValue = SHOP_DATA.toMap().get(valueToUpdateKey);


            if (coins.compareTo(currentPrice) >= 0) {
                coins = coins.subtract(currentPrice);
                GAME_DATA.setCoins(coins);

                CustomBigInteger newValue = CustomBigInteger.toCustomBigInteger(new BigDecimal(currentValue).multiply(toMultiply));
                CustomBigInteger newPrice = basePrice.add(new BigDecimal(currentPrice).multiply(priceMultiplyFactor).toBigInteger());
                data = new HashMap<>();
                data.put(AppConstants.AUX_VALUE_KEY, newValue);
                data.put(AppConstants.AUX_PRICE_KEY, newPrice);

            }

        } catch (IllegalAccessException ignored) {
        }

        return data;
    }

    public Map<String, CustomBigInteger> onButtonClick(Upgrade upgrade) {
        Map<String, CustomBigInteger> values = new HashMap<>();

        switch (upgrade.getButton().getTag().toString()) {
            case AppConstants.UPGRADE_BASIC_KEY:
                values = onPurchaseAction(AppConstants.UPGRADE_BASIC_KEY, AppConstants.CLICK_VALUE_KEY, AppConstants.UPGRADE_BASIC_BASE_PRICE, new BigDecimal("1.03"), new BigDecimal("1"));
                if (!values.isEmpty()) {
                    GAME_DATA.setClickValue(values.get(AppConstants.AUX_VALUE_KEY));
                    SHOP_DATA.setBasicPrice(values.get(AppConstants.AUX_PRICE_KEY));
                    upgrade.setPrice(SHOP_DATA.getBasicPrice());
                }
                break;
            case AppConstants.UPGRADE_MEGA_KEY:
                values = onPurchaseAction(AppConstants.UPGRADE_MEGA_KEY, AppConstants.CLICK_VALUE_KEY, AppConstants.UPGRADE_MEGA_BASE_PRICE, new BigDecimal("1.07"), new BigDecimal("1.35"));
                if (!values.isEmpty()) {
                    GAME_DATA.setClickValue(values.get(AppConstants.AUX_VALUE_KEY));
                    SHOP_DATA.setMegaPrice(values.get(AppConstants.AUX_PRICE_KEY));
                    upgrade.setPrice(SHOP_DATA.getMegaPrice());
                }
                break;
            case AppConstants.UPGRADE_AUTO_KEY:
                values = onPurchaseAction(AppConstants.UPGRADE_AUTO_KEY, AppConstants.AUTO_CLICK_VALUE_KEY, AppConstants.UPGRADE_AUTO_BASE_PRICE, new BigDecimal("1.05"), new BigDecimal("1"));
                if (!values.isEmpty()) {
                    GAME_DATA.setAutoClickValue(values.get(AppConstants.AUX_VALUE_KEY));
                    SHOP_DATA.setAutoPrice(values.get(AppConstants.AUX_PRICE_KEY));
                    upgrade.setPrice(SHOP_DATA.getAutoPrice());
                }
                break;
            case AppConstants.UPGRADE_MEGA_AUTO_KEY:
                values = onPurchaseAction(AppConstants.UPGRADE_MEGA_AUTO_KEY, AppConstants.AUTO_CLICK_VALUE_KEY, AppConstants.UPGRADE_MEGA_AUTO_BASE_PRICE, new BigDecimal("1.08"), new BigDecimal("1.35"));
                if (!values.isEmpty()) {
                    GAME_DATA.setAutoClickValue(values.get(AppConstants.AUX_VALUE_KEY));
                    SHOP_DATA.setMegaAutoPrice(values.get(AppConstants.AUX_PRICE_KEY));
                    upgrade.setPrice(SHOP_DATA.getMegaAutoPrice());
                }
                break;
        }
        return values;
    }
}
