package es.ignaciofp.contador.services;

import android.content.Context;
import android.content.SharedPreferences;

import es.ignaciofp.contador.models.ShopData;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class ShopService {

    // Implementing Singleton
    private static ShopService instance;

    // Models we're going to use
    private final ShopData SHOP_DATA = ShopData.getInstance();

    // Keys
    public final String PREFS_NAME = "shop_prefs";
    private final String UPGRADE_BASIC_KEY = "upgrade_basic";
    private final String UPGRADE_MEGA_KEY = "upgrade_mega";
    private final String UPGRADE_AUTO_KEY = "upgrade_auto";
    private final String UPGRADE_MEGA_AUTO_KEY = "upgrade_mega_auto";

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
}
