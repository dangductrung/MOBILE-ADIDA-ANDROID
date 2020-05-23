package com.adida.chatapp.profile;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adida.chatapp.R;
import com.adida.chatapp.statistic.Statistic;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends Activity {

    ImageButton btnBackButton, btnSignOutButton, btnHome, btnCharacter, btnSearch;
    TextView txtName, txtPlace, txtAboutMe, txtAboutMeDetail;
    Button btnEdit, btnStatistic;
    CircleImageView imageView;
    View navigationBar, mainView, tabBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        setup();
        prepareUI();
        addAction();
    }

    private void setup(){
        btnBackButton = (ImageButton)findViewById(R.id.btnBackButton);
        btnSignOutButton = (ImageButton)findViewById(R.id.btnSignOutButton);
        btnHome = (ImageButton)findViewById(R.id.btnHome);
        btnCharacter = (ImageButton)findViewById(R.id.btnCharacter);
        btnSearch = (ImageButton)findViewById(R.id.btnSearch);

        txtName = (TextView)findViewById(R.id.txtName);
        txtPlace = (TextView)findViewById(R.id.txtPlace);
        txtAboutMe = (TextView)findViewById(R.id.aboutMe);
        txtAboutMeDetail = (TextView)findViewById(R.id.aboutMeDetail);

        btnEdit = (Button)findViewById(R.id.btnEdit);
        btnStatistic = (Button)findViewById(R.id.btnStatistic);

        imageView = (CircleImageView)findViewById(R.id.profile_image);

        navigationBar = (View)findViewById(R.id.navigationBar);
        mainView = (View)findViewById(R.id.mainView);
        tabBar = (View)findViewById(R.id.tabBar);
    }

    private void prepareUI(){
        btnStatistic.setBackgroundColor(Color.parseColor("#0064EF"));
        btnEdit.setBackgroundColor(Color.parseColor("#0064EF"));

        btnEdit.setTextColor(Color.parseColor("#FFFFFF"));
        btnStatistic.setTextColor(Color.parseColor("#FFFFFF"));

        btnBackButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnSignOutButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnHome.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnCharacter.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnSearch.setBackgroundColor(Color.parseColor("#FFFFFF"));

        navigationBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //mainView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tabBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }


    private void addAction(){
        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toStatisticPage();
            }
        });
    }

    private void toStatisticPage(){
        Intent intent = new Intent(this, Statistic.class);
        startActivity(intent);
    }
}
