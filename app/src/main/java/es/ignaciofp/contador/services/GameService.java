package es.ignaciofp.contador.services;

import android.content.Context;

import java.math.BigInteger;

import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameService {

    private static GameService instance;
    private User user;
    private UserService userService;
    private CustomBigInteger coinRate = new CustomBigInteger("0");

    public GameService(Context context, User user) {
        this.user = user;
        this.userService = UserService.getInstance(context);
    }

    public static GameService getInstance(Context context, User user) {
        if(instance == null) {
            instance = new GameService(context, user);
        }
        return instance;
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
    public void saveData() {
        userService.updateUser(user);
    }

    /**
     * Resets all the data from the user
     */
    public void resetData(Context context) {
        user.setCoins(AppConstants.DEFAULT_COINS);
        user.setClickValue(AppConstants.DEFAULT_CLICK_VALUE);
        user.setAutoClickValue(AppConstants.DEFAULT_AUTO_CLICK_VALUE);
        user.setBasicPrice(AppConstants.DEFAULT_BASIC_PRICE);
        user.setMegaPrice(AppConstants.DEFAULT_MEGA_PRICE);
        user.setAutoPrice(AppConstants.DEFAULT_AUTO_PRICE);
        user.setMegaAutoPrice(AppConstants.DEFAULT_MEGA_AUTO_PRICE);
    }

    /**
     * Each second adds the auto click value to the coins.
     */
    @SuppressWarnings("all")
    public String calculateAutoCoins() {
        while (user.getAutoClickValue().compareTo(BigInteger.valueOf(0)) <= 0) ;
        addCoins(user.getAutoClickValue());
        return user.getCoins().withSuffix("§");
    }

    ////////////////////////////////COINRATE////////////////////////////////////////
    public void coinRate() {
        if (user.getAutoClickValue().compareTo(BigInteger.valueOf(0)) > 0) { // Auto-click
            coinRate = coinRate.add(user.getAutoClickValue());
        }

    }

    public void resetCoinRate() {
        coinRate = new CustomBigInteger("0");
    }

    // TODO: Doc
    public CustomBigInteger addCoins(CustomBigInteger val) {
        user.setCoins(user.getCoins().add(val)); // Adding coins
        coinRate = coinRate.add(val); // Adding to coin rate
        return user.getCoins();
    }

    private void setCoinRate(CustomBigInteger val) {
        coinRate = val;
    }

}
