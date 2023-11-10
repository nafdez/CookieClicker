package es.ignaciofp.contador.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.services.GameService;
import es.ignaciofp.contador.services.ShopService;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameActivity extends AppCompatActivity {

    // Services
    private GameService GAME_SERVICE;
    private AppConstants APP_CONSTANTS;

    // Views
    private TextView textCoins;
    private TextView textCoinRateValue;
    private ImageView image_coin;

    // Media
    public int SOUND_COIN_CLICK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GAME_SERVICE = GameService.getInstance(this);
        APP_CONSTANTS = AppConstants.getInstance(this);
        SOUND_COIN_CLICK_ID = APP_CONSTANTS.getSOUND_COIN_CLICK_ID();

        // Setting variables to assign to it's views
        CustomBigInteger coins = GAME_SERVICE.getValue(AppConstants.COINS_KEY);
        CustomBigInteger clickValue = GAME_SERVICE.getValue(AppConstants.CLICK_VALUE_KEY);
        CustomBigInteger autoClickValue = GAME_SERVICE.getValue(AppConstants.AUTO_CLICK_VALUE_KEY);
        CustomBigInteger coinRate = GAME_SERVICE.getValue(AppConstants.COIN_RATE_KEY);

        // View assignment
        TextView textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(clickValue.withSuffix("§/click"));

        TextView textAutoTouchValue = findViewById(R.id.text_auto_click);
        textAutoTouchValue.setText(autoClickValue.withSuffix("§/s"));

        textCoinRateValue = findViewById(R.id.text_coin_rate);
        textCoinRateValue.setText(coinRate.withSuffix("§/s"));

        textCoins = findViewById(R.id.text_coins);
        image_coin = findViewById(R.id.image_actionable_coins);

        // If the actual quantity of coins is 0 them the text value of text_coins will be
        // the assigned in the strings.xml file
        if (!coins.equals(BigInteger.ZERO)) {
            textCoins.setText(coins.withSuffix("§"));
        }

        updateClickImageView();
        if (GAME_SERVICE.hasReachedMaxValue()) onGameEndDialogCreator();
        gameLoop();

        // Starting music
        APP_CONSTANTS.getMP_MAIN_THEME().start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        APP_CONSTANTS.getMP_MAIN_THEME().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GAME_SERVICE.saveData(this);
        APP_CONSTANTS.getMP_MAIN_THEME().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        APP_CONSTANTS.getMP_MAIN_THEME().stop();
    }

    /**
     * If the user touches the coin image, then coins value will be the clickValue at that
     * moment added to the coins the user collected. Also plays an animation on the image
     *
     * @param view the view that has been clicked
     */
    public void addOnClick(View view) {
        // Image animation
        ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.2f, 0.7f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(100);
        image_coin.startAnimation(fade_in);

        AppConstants.SOUND_POOL.play(SOUND_COIN_CLICK_ID, 1, 1, 0, 0, 1);

        CustomBigInteger updatedCoins = GAME_SERVICE.addCoins(GAME_SERVICE.getValue(AppConstants.CLICK_VALUE_KEY));

        // Updating the coins text view value
        textCoins.setText(updatedCoins.withSuffix("§"));
        if (updatedCoins.compareTo(AppConstants.BIG_INTEGER_MAX_VALUE) >= 0)
            onGameEndDialogCreator();
        updateClickImageView();
    }

    /**
     * Starts the ShopActivity when the user clicks on the shop icon
     *
     * @param view the view that has been clicked
     */
    public void shopImageOnClick(View view) {
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Starts all the necessary loops for the game to work
     */
    private void gameLoop() {
        new Thread(this::coinRateLoop).start();
        new Thread(this::autoClickLoop).start();
    }

    /**
     * Each seconds checks the quantity of coins and updates the coin image accordingly.
     * Also within the same second adds to the coins the amount of them being auto-generated and
     * calculates the amount of money is being added up each second.
     */
    @SuppressWarnings("BusyWait")
    private void coinRateLoop() {
        while (true) {
            try {
                Thread.sleep(500);
                runOnUiThread(this::updateClickImageView); // Updating the coin image

                GAME_SERVICE.coinRate();

                //runOnUiThread(() -> textCoins.setText(GAME_SERVICE.getValue(AppConstants.COINS_KEY).withSuffix("§")));
                runOnUiThread(() -> textCoinRateValue.setText(GAME_SERVICE.getValue(AppConstants.COIN_RATE_KEY).withSuffix("§/s"))); // Coin rate

                Thread.sleep(500);

                GAME_SERVICE.resetCoinRate();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @SuppressWarnings("BusyWait")
    private void autoClickLoop() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            String coins = GAME_SERVICE.calculateAutoCoins();
            runOnUiThread(() -> textCoins.setText(coins));
        }
    }

    /**
     * Makes a scale dividing the price of the basic upgrade prices and depending how many coins
     * the user has sets an image. Just a visual reminder of how much buying power the user has.
     */
    private void updateClickImageView() {
        CustomBigInteger coins = GAME_SERVICE.getValue(AppConstants.COINS_KEY);
        CustomBigInteger basicPrice = ShopService.getInstance(this).getValue(AppConstants.UPGRADE_BASIC_KEY);

        if (coins.compareTo(basicPrice.divide(new BigInteger("2"))) < 0) { // Basic image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_1);
        } else if (coins.compareTo(basicPrice) < 0) { // Upper half image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_2);
        } else { // Over basic price image
            image_coin.setImageResource(R.drawable.ic_gen_coin_level_3);
        }
    }

    /**
     * Creates a dialog that shows information and multiple buttons that redirect to different websites
     *
     * @param view the view being clicked
     */
    public void infoImageOnClick(View view) {
        AlertDialog.Builder dialogConstructor = new AlertDialog.Builder(this);
        dialogConstructor.setMessage(getString(R.string.game_info_message_dialog)).setTitle(getString(R.string.game_info_title_dialog)).setIcon(R.drawable.ic_info).setNeutralButton("Github", (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nafdez"));
            startActivity(browserIntent);
        }).setNegativeButton(getString(R.string.game_info_donate_dialog), (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://revolut.me/ignaciyu00"));
            startActivity(browserIntent);
        }).setPositiveButton(getString(R.string.gen_dialog_pos_button), (dialog, which) -> {
            // do nothing
        }).show();
    }

    /**
     * Called when the user clicks on the reset button. Just making sure the user clicked on purpose
     * before resetting all values
     *
     * @param view the view where it's being called
     */
    public void resetOnClick(View view) {
        AlertDialog.Builder dialogConstructor = new AlertDialog.Builder(this);
        dialogConstructor.setTitle(getString(R.string.game_reset_dialog_title)).setMessage(getString(R.string.game_reset_dialog_message)).setIcon(R.drawable.ic_reset).setPositiveButton(getString(R.string.gen_dialog_pos_button), (dialog, which) -> {
            resetValues();
            recreate();
        }).setNegativeButton(getString(R.string.gen_dialog_neg_button), (dialog, which) -> {
        }).show();
    }

    /**
     * When the user reaches the limit on the game, a dialog is shown to inform the user and if he wants
     * to reset the game or not (if not, you can't enter the game again)
     */
    private void onGameEndDialogCreator() {
        AlertDialog.Builder dialogConstructor = new AlertDialog.Builder(this);
        dialogConstructor.setMessage(getString(R.string.game_max_value_dialog_message)).setTitle(getString(R.string.game_max_value_dialog_title)).setIcon(R.drawable.ic_info).setPositiveButton(getString(R.string.gen_dialog_pos_button), (dialog, which) -> finish()).setNegativeButton(getString(R.string.game_max_value_dialog_neg_button), (dialog, which) -> {
            GAME_SERVICE.setHasReachedMaxValue(false);
            resetValues();
            recreate();
        }).show();
    }

    /**
     * Calls resetData method of both GameService and ShopService to reset all values
     */
    private void resetValues() {
        GAME_SERVICE.resetData(this);
        ShopService.getInstance(this).resetData(this);
    }

    public void returnOnClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
