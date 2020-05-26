package com.adida.chatapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.login.LoginPage;

public class SpashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(1000);
        startActivity(new Intent(this, LoginPage.class));
//        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
