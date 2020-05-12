package com.adida.chatapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.adida.chatapp.R;
import com.adida.chatapp.signup.SignupPage;

public class LoginPage extends AppCompatActivity {
    Button signupButton, loginButton;
    EditText userName, password;
    Intent signupIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        signupButton = (Button) findViewById(R.id.signupButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        userName = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);

        signupIntent = new Intent(this, SignupPage.class);

        this.setup();
    }

    private void setup() {
        this.setupButtonClicked();
        this.setupKeyboard();
    }

    private void setupButtonClicked() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
    }

    private void setupKeyboard() {
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }}
            });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }}
        });
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
