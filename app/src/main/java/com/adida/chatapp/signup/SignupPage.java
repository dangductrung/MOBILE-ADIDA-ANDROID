package com.adida.chatapp.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.adida.chatapp.R;
import com.adida.chatapp.login.LoginPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupPage extends Activity implements View.OnTouchListener {
    private FirebaseAuth mAuth;

    EditText username, password, repassword;
    TextView gotoLogin, errorMessage;
    Button registerButton;
    Intent gotoLoginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        gotoLoginIntent = new Intent(this, LoginPage.class);
        this.setup();

//        SharedPreferences sharedPref = getSharedPreferences(StringKeys.SPrefName, MODE_PRIVATE);
//        String test = sharedPref.getString("uuid", null);

    }

    private void setup() {
        mAuth = FirebaseAuth.getInstance();
        this.connectToComponent();
        this.setupKeyboard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void connectToComponent() {
        username = (EditText) findViewById(R.id.usernameRegisterEditText);
        password = (EditText) findViewById(R.id.passwordRegisterEditText);
        repassword = (EditText) findViewById(R.id.repasswordRegisterEditText);
        registerButton = (Button) findViewById(R.id.registerButton);
        gotoLogin = (TextView) findViewById(R.id.goToLogin);
        errorMessage = (TextView) findViewById(R.id.errorEmail);

        gotoLogin.setOnTouchListener((View.OnTouchListener) this);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButtonClicked();
            }
        });
    }

    private void registerButtonClicked() {
//        if(!checkValidate()) {
//            showInvalidEditTField();
//            return;
//        }
        mAuth.createUserWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("", "createUserWithEmail:success");
                    startActivity(gotoLoginIntent);
                } else {
                    Log.w("", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignupPage.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    showInvalidEditTField();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        errorMessage.setVisibility(View.GONE);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            errorMessage.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_border) );
        } else {
            errorMessage.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
        }
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

    public boolean checkValidate() {
//        return StringHelper.isEmailValid(errorMessage.getText().toString());
        return false;
    }

    public void showInvalidEditTField() {
        errorMessage.setVisibility(View.VISIBLE);
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            errorMessage.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border) );
        } else {
            errorMessage.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_error_border));
        }
    }
}
