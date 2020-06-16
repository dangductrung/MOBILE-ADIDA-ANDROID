package com.adida.chatapp.history;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.HistoryCell;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HistoryPage extends Activity {
    ImageButton btnBackButton;
    Context context = null;
    ListView listHistoryView;
    private HistoryRowCell customRowCell;
    ArrayList<HistoryCell> listArray = new ArrayList<HistoryCell>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        setContentView(R.layout.activity_history);

        btnBackButton = (ImageButton)findViewById(R.id.btnHistoryBack);
        listHistoryView = (ListView)findViewById(R.id.listHistoryView);

        setData();
        addAction();

        //customRowCell.notifyDataSetChanged();
    }

    private void addAction() {
        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setData() {
        String localUuid= SharePref.getInstance(context).getUuid();

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.history).child(localUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                if (data == null) {
                    customRowCell = new HistoryRowCell(context, listArray);
                    listHistoryView.setAdapter(customRowCell);
                    return;
                }
                Set<String> keys = data.keySet();
                for (String s : keys) {
                    HashMap<String, String> history = data.get(s);
                    Iterator<String> itr = history.keySet().iterator();
                    while(itr.hasNext()) {
                        String time = itr.next();
                        String remoteId = history.get(time);
                        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(remoteId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                listArray.add(new HistoryCell(user.email, time));

                                customRowCell = new HistoryRowCell(context, listArray);
                                listHistoryView.setAdapter(customRowCell);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
