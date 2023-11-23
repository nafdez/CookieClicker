package es.ignaciofp.contador.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.services.UserService;
import es.ignaciofp.contador.utils.AppConstants;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private UserService userService;
    private TextInputEditText nameInputTxt;
    private TextInputEditText passwordInputTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userService = UserService.getInstance(this);

        nameInputTxt = findViewById(R.id.nameInputView);
        passwordInputTxt = findViewById(R.id.passwordInputView);
        Button playBtn = findViewById(R.id.playBtn);

        playBtn.setOnClickListener(this);
    }

    /**
     * This method encrypts a string (the password) using SHA-256
     * @param basePasswd The password to be encrypted
     * @return The encrypted password
     */
    private String encryptPassword(String basePasswd) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(basePasswd.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onClick(View v) {
        String username = nameInputTxt.getText().toString();
        String password = encryptPassword(passwordInputTxt.getText().toString());

        // If any of the EditTexts are empty, doesn't continue executing the code
        if (username.isEmpty() || password.isEmpty()) return;

        User user = userService.getUserByName(username);

        // Checks if user exists and if exists then checks if the password is correct, if not,
        // it shows an error message
        // If the user doesn't exist, it creates a new user
        if (user != null) {
            if (!user.getPassword().equals(password)) {
                Toast.makeText(this, "Username or password are incorrect", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Logging into " + username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Creating new user.", Toast.LENGTH_SHORT).show();
            user = new User(username, password);
            userService.addUser(user);
        }

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra(AppConstants.USER_KEY, user);
        startActivity(intent);
        finish();
    }

}