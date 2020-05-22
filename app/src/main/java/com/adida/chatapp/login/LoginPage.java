package com.adida.chatapp.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.adida.chatapp.R;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.signup.SignupPage;
import com.adida.chatapp.stringhelper.StringHelper;
import com.adida.chatapp.stringkeys.StringKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    Button signupButton, loginButton;
    EditText userName, password;
    TextView errorMessage, errorPassword;
    Intent signupIntent;
    FirebaseAuth mAuth;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        signupButton = (Button) findViewById(R.id.signupButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        userName = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        errorMessage = (TextView) findViewById(R.id.errorUsernameMessage);
        errorPassword = (TextView) findViewById(R.id.errorPasswordMessage);

        signupIntent = new Intent(this, SignupPage.class);

        intent = new Intent(this, MainActivity.class);
        this.setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        errorMessage.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            userName.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_border) );
            password.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_border) );
        } else {
            userName.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
            password.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
        }
    }

    private void setup() {
        mAuth = FirebaseAuth.getInstance();
        this.setupButtonClicked();
        this.setupKeyboard();
    }

    private void onLoginButtonClicked() {
        if(!checkValidate()) {
            Toast.makeText(LoginPage.this, "Email Invalid", Toast.LENGTH_SHORT).show();
            showInvalidEditTField();
            return;
        }
        mAuth.signInWithEmailAndPassword(userName.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    saveId(user.getUid());
                    startActivity(intent);
                } else {
                    signInFail();
                    Toast.makeText(LoginPage.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupButtonClicked() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
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

    private void saveId(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences(StringKeys.SPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uuid", id).commit();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean checkValidate() {
        return StringHelper.isEmailValid(userName.getText().toString());
    }

    public void showInvalidEditTField() {
        errorMessage.setVisibility(View.VISIBLE);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            userName.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border) );
        } else {
            userName.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border));
        }
    }

    public void signInFail() {
        errorMessage.setVisibility(View.VISIBLE);
        errorPassword.setVisibility(View.VISIBLE);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            userName.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border) );
            password.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border) );
        } else {
            userName.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border));
            password.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border));
        }
    }
}
