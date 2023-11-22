package es.ignaciofp.contador.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.User;
import es.ignaciofp.contador.services.UserService;
import es.ignaciofp.contador.utils.AppConstants;

public class AuthActivity extends AppCompatActivity {

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userService = UserService.getInstance(this);

        TextInputEditText nameInputTxt = findViewById(R.id.nameInputView);
        Button playBtn = findViewById(R.id.playBtn);

        playBtn.setOnClickListener(v -> {
            String username;
           if(!(username = nameInputTxt.getText().toString()).isEmpty()) {
               User user;

               if((user = userService.getUserByName(username)) == null) {
                   Log.d("AuthActivity", "Adding user: " + username);
                   user = new User(username);
                   userService.addUser(user);
               } else {
                   Log.d("AuthActivity", "User already exists: " + username + ", ID: " + user.getId());
               }

               Intent intent = new Intent(this, GameActivity.class);
               intent.putExtra("user_id", user.getId());
               intent.putExtra(AppConstants.USER_KEY, user);
               startActivity(intent);
               finish();
           }
        });
    }
}