package com.adida.chatapp.statistic;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.keys.HistoryKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;
public class StatisticPage extends AppCompatActivity {
    ImageButton backButton;
    TextView countChat, countSession;
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
<<<<<<< Updated upstream
                userName.setText(user.name.isEmpty() ? user.email : user.name);

                

=======
<<<<<<< Updated upstream
                userName.setText(user.name);
=======
                userName.setText(user.name.isEmpty() ? user.email : user.name);

                FirebaseDatabase.getInstance().getReference(FirebaseKeys.history).child(SharePref.getInstance(getApplicationContext()).getUuid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                        countChat.setText("Trong 1 tháng bạn đã chat: "+ 0 +" lần");
                        countSession.setText("Trong 1 tháng bạn đã mở : "+ 0 +" cuộc hội thoại");
                        if (data == null) {
                            return;
                        }
                        Set<String> keys = data.keySet();
                        for (String s : keys) {
                            HashMap<String, String> history = data.get(s);
                            if (s.compareTo(HistoryKeys.CHAT) == 0) {
                                countChat.setText("Trong 1 tháng bạn đã chat: "+ history.keySet().size() +" lần");
                            } else
                            {
                                countSession.setText("Trong 1 tháng bạn đã mở : "+ history.keySet().size() +" cuộc hội thoại");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

>>>>>>> Stashed changes
>>>>>>> Stashed changes
                countChat.setText("Trong 1 tháng bạn đã chat: "+ user.countChatMessage +" lần");
                countSession.setText("Trong 1 tháng bạn đã mở : "+ user.countCreateConnection +" cuộc hội thoại");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
