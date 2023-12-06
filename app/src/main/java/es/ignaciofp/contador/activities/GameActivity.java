package es.ignaciofp.contador.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.services.GameService;
import es.ignaciofp.contador.utils.AppConstants;
import es.ignaciofp.contador.utils.CustomBigInteger;

public class GameActivity extends AppCompatActivity {

    private static final BigInteger GAME_BI_MAX_VALUE = new BigInteger("999999999999999999999999999999999"); // Hardcoded BigInteger limit
    private SharedPreferences.Editor editor;

    private GameService gameService;

    // Views
    private TextView textCoins;
    private TextView textCoinRateValue;
    private ImageView image_coin;

    SoundPool soundPool;
    int soundClickId;
    private MediaPlayer mediaPlayer;
    private final ExecutorService EXECUTOR_MUSIC_POOL = Executors.newFixedThreadPool(1);
    private final ExecutorService EXECUTOR_LOOP_POOL = Executors.newFixedThreadPool(2);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle bundle = getIntent().getExtras();

        User user;
        if (bundle != null) {
            GameService.resetInstance();
            user = (User) bundle.getSerializable(AppConstants.USER_KEY);
            gameService = GameService.getInstance(this, user);
        } else {
            gameService = GameService.getInstance(this, null);
            user = gameService.getUser();
        }

        // Initialization of sound and music
        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundClickId = soundPool.load(this, R.raw.sound_coin_click, 1);
        mediaPlayer = MediaPlayer.create(this, R.raw.main_theme);
        mediaPlayer.setLooping(true);

        // View assignment
        TextView textClickValue = findViewById(R.id.text_click_value);
        textClickValue.setText(user.getClickValue().withSuffix("§/click"));

        TextView textAutoTouchValue = findViewById(R.id.text_auto_click);
        textAutoTouchValue.setText(user.getAutoClickValue().withSuffix("§/s"));

        textCoinRateValue = findViewById(R.id.text_coin_rate);
        textCoinRateValue.setText(gameService.getCoinRate().withSuffix("§/s"));

        textCoins = findViewById(R.id.text_coins);
        image_coin = findViewById(R.id.image_actionable_coins);

        // If the actual quantity of coins is 0 them the text value of text_coins will be
        // the assigned in the strings.xml file
        if (!user.getCoins().equals(BigInteger.ZERO)) {
            textCoins.setText(user.getCoins().withSuffix("§"));
        }

        updateClickImageView();
        if (user.getHasMaxValue()) onGameEndDialogCreator();
        initializeLoop();
    }

    /**
     * Based on the user choose of setting the music on or off, starts or doesn't start the music.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences(AppConstants.OPTIONS_PREF_KEY, MODE_PRIVATE);
        if (sharedPref.getBoolean(AppConstants.OPTIONS_MUSIC_TAG, true)) {
            mediaPlayer.start();
        }
    }

    /**
     * Saves the game state of the user to the database and pauses the music.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameService.saveData())
            Toast.makeText(this, getString(R.string.savegame_succesfully_toast), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getString(R.string.savegame_error_toast), Toast.LENGTH_SHORT).show();
    }

    /**
     * Releases all the resources used by this activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        soundPool.stop(soundClickId);
        soundPool.release();
        EXECUTOR_MUSIC_POOL.shutdown();
        EXECUTOR_LOOP_POOL.shutdown();
        EXECUTOR_LOOP_POOL.shutdownNow();
    }

    @SuppressWarnings("all")
    private void initializeLoop() {
        EXECUTOR_LOOP_POOL.submit(() -> {
            try {
                while (true) {
                    runOnUiThread(() -> textCoinRateValue.setText(gameService.calculateCoinRate().withSuffix("§/s"))); // Coin rate
                    runOnUiThread(this::updateClickImageView); // Updating the coin image
                    runOnUiThread(() -> textCoins.setText(gameService.calculateAutoCoins().withSuffix("§")));
                    Thread.sleep(100);
                    gameService.resetCoinRate();
                    Thread.sleep(900);
                }
            } catch (InterruptedException ignored) {
            }
        });
    }

    /**
     * If the user touches the coin image, then coins value will be the clickValue at that
     * moment added to the coins the user collected. Also plays an animation on the image
     *
     * @param view the view that has been clicked
     */
    public void addOnClick(View view) {
        EXECUTOR_MUSIC_POOL.submit(() -> {
            soundPool.play(soundClickId, 1, 1, 0, 0, 1);

            // Image animation
            ScaleAnimation fade_in = new ScaleAnimation(0.7f, 1.2f, 0.7f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(100);
            image_coin.startAnimation(fade_in);
        });

        // Updating the coins value
        CustomBigInteger updatedCoins = gameService.addCoins(gameService.getUser().getClickValue());

        // Updating the coins text view value
        textCoins.setText(updatedCoins.withSuffix("§"));
        if (updatedCoins.compareTo(AppConstants.BIG_INTEGER_MAX_VALUE) >= 0) {
            gameService.getUser().setHasMaxValue(true);
            onGameEndDialogCreator();
        }
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
     * Makes a scale dividing the price of the basic upgrade prices and depending how many coins
     * the user has sets an image. Just a visual reminder of how much buying power the user has.
     */
    private void updateClickImageView() {
        User user = gameService.getUser();
        CustomBigInteger coins = user.getCoins();
        CustomBigInteger basicPrice = user.getBasicPrice();

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
        dialogConstructor.setTitle(getString(R.string.game_reset_dialog_title)).setMessage(getString(R.string.game_reset_dialog_message)).setIcon(R.drawable.ic_gen_reset).setPositiveButton(getString(R.string.gen_dialog_pos_button), (dialog, which) -> {
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
            gameService.getUser().setHasMaxValue(false);
            resetValues();
            recreate();
        }).show();
    }

    /**
     * Calls resetData method of both GameService and ShopService to reset all values
     */
    private void resetValues() {
        gameService.resetData();
        recreate();
    }

    /**
     * Called when the user clicks on the return button. Goes back to the HomeActivity
     *
     * @param view the view where it's being called
     */
    public void returnOnClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}