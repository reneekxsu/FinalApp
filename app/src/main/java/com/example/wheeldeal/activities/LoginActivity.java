package com.example.wheeldeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
/**
 * Overview:
 * This is the first activity that launches in the app. If the user is already logged in, an intent
 * is launched to MainActivity. Otherwise, the user will see the login screen, with the option to
 * sign up as well. In order to improve navigation between LoginActivity, SignUpActivity, and
 * MainActivity, the goal was to let the user go back from the sign up screen to the log in if they
 * wished to, but they would not be able to go back if the sign up was complete. When the user
 * clicks the back button after logging in or signing up fully, the back button should not take them
 * to either the login or sign up screen. To do this, we saved an instance of this login activity,
 * as we do not want to finish it from within if the user chooses to sign up instead, so we perform
 * that action in SignUpActivity instead.
 */
public class LoginActivity extends AppCompatActivity {

    // Global variable declarations
    public static final String TAG = "LoginActivity";
    public static LoginActivity instance = null;
    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignup;
    private TextInputLayout tilUsername, tilPassword;
    QueryClient queryClient = new QueryClient();


    /**
     * @brief Upon creation of this activity, we want to initialize our global variables, and set
     * our listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set activity instance
        instance = this;

        // Check if user is already logged in
        if (ParseUser.getCurrentUser()!=null){
            // Since user is already logged in, set fromLogin to false, and go to MainActivity
            goMainActivity(false);
        }

        // Initialize layout/view variables
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        tilPassword = findViewById(R.id.tilLoginPassword);
        tilUsername = findViewById(R.id.tilLoginUsername);

        // Set button listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLoginInfo();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    /**
     * @brief Process login information by extracting EditText username and password strings
     */
    public void processLoginInfo(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        loginUser(username, password);
    }

    /**
     * @brief Logs in the ParseUser using the provided username and password strings. On success,
     *        we want to start the MainActivity, and on failure, we notify the user that the
     *        login information is incorrect.
     * @param username Username that has been entered at time of button click
     * @param password Password that has been entered at time of button click
     */
    private void loginUser(String username, String password) {
        Log.i(TAG, "Trying to login user " + username);
        // Try to log in the user using provided credentials
        queryClient.logInUser(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null){
                    // Login failed
                    Log.e(TAG, "login issue", e);
                    tilShowError();
                    Toast.makeText(LoginActivity.this,"Wrong username or password",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // Login was successful
                    goMainActivity(true);
                    Toast.makeText(LoginActivity.this,"Login success",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * @brief Launches an intent to the MainActivity after login is complete.
     * @param fromLogin Boolean determining whether the user is entering the MainActivity from
     *                  just logging in, or if they were already logged in when they launched the
     *                  app. This is used to determine whether the Welcome screen should be showed.
     */
    private void goMainActivity(boolean fromLogin) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("flag", fromLogin);
        startActivity(i);
        // Allows back button not to lead us back to login
        finish();
    }

    /**
     * @brief We override this method so that we can also finish this activity from SignUpActivity
     * when needed.
     */
    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    /**
     * @brief Takes user to SignUpActivity
     */
    public void goToSignUp(){
        Log.i(TAG, "onClick sign up button");
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        // Go to signup activity
        startActivity(i);
        // Finish is not called such that user can go back to login if sign up not complete
    }

    /**
     * @brief Upon login failure, TextInputLayouts for username and password will reflect this error
     */
    public void tilShowError(){
        tilUsername.setError("Please re-enter username");
        tilPassword.setError("Please re-enter password");
    }

}