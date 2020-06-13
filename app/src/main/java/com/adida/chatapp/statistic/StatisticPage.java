package com.adida.chatapp.statistic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticPage extends AppCompatActivity {
    ImageButton backButton;
    TextView countChat, countSession;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_page);


        backButton = (ImageButton) findViewById(R.id.stasBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        countChat = (TextView) findViewById(R.id.countChattxt);
        countSession = (TextView) findViewById(R.id.countSessionTxt);
        TextView userName = (TextView) findViewById(R.id.userNameText) ;
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(getApplicationContext()).getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.name.isEmpty() ? user.email : user.name);

                

                countChat.setText("Trong 1 tháng bạn đã chat: "+ user.countChatMessage +" lần");
                countSession.setText("Trong 1 tháng bạn đã mở : "+ user.countCreateConnection +" cuộc hội thoại");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
