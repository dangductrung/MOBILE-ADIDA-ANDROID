package com.adida.chatapp.statistic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adida.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Statistic extends Activity {

    ImageButton btnBackButton, btnSignOutButton, btnHome, btnCharacter, btnSearch;
    TextView txtName, txtPlace, txtStatisticLabel, txtStatisticChatCount, txtStatisticLikeCount, txtStatisticChatWithPeopleCount, txtStatisticOnlineCount;
    CircleImageView imageView;
    View navigationBar, mainView, tabBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
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

        txtStatisticLabel = (TextView)findViewById(R.id.txtStatisticLabel);
        txtStatisticChatCount = (TextView)findViewById(R.id.txtStatisticChatCount);
        txtStatisticLikeCount = (TextView)findViewById(R.id.txtStatisticLikeCount);
        txtStatisticChatWithPeopleCount = (TextView)findViewById(R.id.txtStatisticChatWithPeopleCount);
        txtStatisticOnlineCount = (TextView)findViewById(R.id.txtStatisticOnlineCount);

        imageView = (CircleImageView)findViewById(R.id.profile_image);

        navigationBar = (View)findViewById(R.id.navigationBar);
        mainView = (View)findViewById(R.id.mainView);
        tabBar = (View)findViewById(R.id.tabBar);
    }

    private void prepareUI(){
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
        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
    }
}
