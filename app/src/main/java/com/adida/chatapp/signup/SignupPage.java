package com.adida.chatapp.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adida.chatapp.R;
import com.adida.chatapp.login.LoginPage;

public class SignupPage extends Activity implements View.OnTouchListener {
    EditText username, password, repassword;
    TextView gotoLogin;
    Button registerButton;
    Intent gotoLoginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        gotoLoginIntent = new Intent(this, LoginPage.class);
        this.setup();
    }

    private void setup() {
        this.connectToComponent();
        this.setupKeyboard();
    }

    private void connectToComponent() {
        username = (EditText) findViewById(R.id.usernameRegisterEditText);
        password = (EditText) findViewById(R.id.passwordRegisterEditText);
        repassword = (EditText) findViewById(R.id.repasswordRegisterEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        gotoLogin = (TextView) findViewById(R.id.goToLogin);
        gotoLogin.setOnTouchListener((View.OnTouchListener) this);
    }

    private void setupKeyboard() {
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        repassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(gotoLogin)) {
            startActivity(gotoLoginIntent);
        }
        return false;
    }
}
