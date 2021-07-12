package com.example.finalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    EditText etSignUpUsername;
    EditText etSignUpPassword;
    Button btnFinishSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        btnFinishSignUp = findViewById(R.id.btnFinishSignUp);
        btnFinishSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String username = etSignUpUsername.getText().toString();
                String password = etSignUpPassword.getText().toString();
                Log.i(TAG, "signup user called");
                signupUser(username, password);
            }
        });
    }

    private void signupUser(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    goLoginActivity();
                    Toast.makeText(SignUpActivity.this,"Signup success", Toast.LENGTH_SHORT).show();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Signup issue", e);
                    Toast.makeText(SignUpActivity.this,"Signup failed", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        // if signup is complete, allows back button not to lead to loginactivity
        if(LoginActivity.instance != null) {
            Log.i(TAG, "instance not null");
            try {
                LoginActivity.instance.finish();
            } catch (Exception e) {}
        }
        // allows back button not to lead us back to signup
        finish();
    }
}