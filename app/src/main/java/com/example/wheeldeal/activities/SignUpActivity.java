package com.example.wheeldeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.R;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Overview:
 * This activity launches upon the Sign Up button click from LoginActivity. Upon successfully
 * signing up a user, we take them to the LoginActivity, which will subsequently take them to the
 * MainActivity since the user is now signed in.
 */
public class SignUpActivity extends AppCompatActivity {

    // Global variable declarations
    public static final String TAG = "SignUpActivity";
    EditText etSignUpUsername, etSignUpPassword;
    Button btnFinishSignUp;
    TextInputLayout tilSignUpUsername;

    /**
     * @brief Initialize global variables and set button click listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize layout/view variables
        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        btnFinishSignUp = findViewById(R.id.btnFinishSignUp);
        tilSignUpUsername = findViewById(R.id.tilSignUpUsername);

        // Set button listener
        btnFinishSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                processSignUpInfo();
            }
        });
    }

    /**
     * @brief Process signup information by extracting EditText username and password strings
     */
    public void processSignUpInfo(){
        String username = etSignUpUsername.getText().toString();
        String password = etSignUpPassword.getText().toString();
        Log.i(TAG, "signup user called");
        signupUser(username, password);
    }

    /**
     * @brief Creates a new ParseUser using the provided username and password strings. On success,
     *        we want to start the LoginActivity, which will take us to the MainActivity, and on
     *        failure, we notify the user that the sign up information was invalid.
     * @param username Username that has been entered at time of button click
     * @param password Password that has been entered at time of button click
     */
    private void signupUser(String username, String password) {
        // Create the new ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Try to sign user up
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Sign up was successful
                    goLoginActivity();
                    Toast.makeText(SignUpActivity.this,"Signup success",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Sign up failed
                    Log.e(TAG, "Signup issue", e);
                    tilSignUpUsername.setError("This username is already taken, please choose a " +
                            "different one.");
                    Toast.makeText(SignUpActivity.this,"Signup failed",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    /**
     * @brief We go to the LoginActivity, since it will process whether the user is already signed
     * in (which they are after signing up successfully), and subsequently take the user to
     * MainActivity. Because we previously came from the LoginActivity, and that activity has not
     * been finished yet, we invoke finish() from here using the saved instance.
     */
    private void goLoginActivity() {
        // We launch the LoginActivity
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        // If signup is complete, allows back button not to lead to LoginActivity, so we finish
        // LoginActivity
        if(LoginActivity.instance != null) {
            // Since LoginActivity instance is nonnull, we know the activity was not finished
            Log.i(TAG, "instance not null");
            try {
                // Finish LoginActivity
                LoginActivity.instance.finish();
            } catch (Exception e) {
                Log.e(TAG, "previous LoginActivity instance could not be finished", e);
            }
        }
        // Allows back button not to lead us back to SignUpActivity
        finish();
    }
}