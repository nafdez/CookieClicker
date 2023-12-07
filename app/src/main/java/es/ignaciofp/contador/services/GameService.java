package es.ignaciofp.contador.services;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameService {

    Context context;
    private static GameService instance;
    private final User user;
    private final UserService userService;
    private CustomBigInteger coinRate = new CustomBigInteger("0");
    private final ExecutorService EXECUTOR_LOOP_POOL = Executors.newSingleThreadExecutor();

    public GameService(Context context, User user) {
        this.user = user;
        this.userService = UserService.getInstance(context);
        Context applicationContext = context.getApplicationContext();
    }

    public static GameService getInstance(Context context, User user) {
        if (instance == null) {
            instance = new GameService(context, user);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public User getUser() {
        return user;
    }

    public CustomBigInteger getCoinRate() {
        return coinRate;
    }

    /**
     * Saves the current state of the game to the database.
     */
    public boolean saveData() {
        return userService.updateUser(user);
    }

    /**
     * Resets all the data from the user
     */
    public void resetData() {
        user.setCoins(AppConstants.DEFAULT_COINS);
        user.setClickValue(AppConstants.DEFAULT_CLICK_VALUE);
        user.setAutoClickValue(AppConstants.DEFAULT_AUTO_CLICK_VALUE);
        user.setBasicPrice(AppConstants.DEFAULT_BASIC_PRICE);
        user.setMegaPrice(AppConstants.DEFAULT_MEGA_PRICE);
        user.setAutoPrice(AppConstants.DEFAULT_AUTO_PRICE);
        user.setMegaAutoPrice(AppConstants.DEFAULT_MEGA_AUTO_PRICE);
        user.setHasMaxValue(false);
    }

    /**
     * Adds to the user coins the value of the auto click.
     *
     * @return the current coins of the user
     */
    public CustomBigInteger calculateAutoCoins() {
        setCoinRate(coinRate.add(user.getAutoClickValue()));
        return addCoins(user.getAutoClickValue());
    }

    /**
     * Adds the given value to the user coins and to the coin-rate for later use.
     *
     * @param val value being added to coins
     * @return the current coins of the user
     */
    public CustomBigInteger addCoins(CustomBigInteger val) {
        user.setCoins(user.getCoins().add(val)); // Adding coins
        setCoinRate(coinRate.add(val)); // Adding to coin rate
        return user.getCoins();
    }

    ////////////////////////////////COINRATE////////////////////////////////////////

    /**
     * Calculates the coin rate
     *
     * @return the current coin rate
     */
    public CustomBigInteger calculateCoinRate() {
        setCoinRate(coinRate.add(user.getAutoClickValue()));
        return coinRate;
    }

    /**
     * Sets the coin rate to 0
     */
    public void resetCoinRate() {
        coinRate = new CustomBigInteger("0");
    }

    private synchronized void setCoinRate(CustomBigInteger val) {
        coinRate = val;
    }
}
