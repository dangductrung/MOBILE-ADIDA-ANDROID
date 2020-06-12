package com.adida.chatapp.history;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.HistoryCell;
import com.adida.chatapp.search.SearchResultRowCell;


import java.util.ArrayList;

public class HistoryPage extends AppCompatActivity {
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

        customRowCell = new HistoryRowCell(context, listArray);
        listHistoryView.setAdapter(customRowCell);
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
        for (int i=0;i<5;i++) {
            listArray.add( new HistoryCell("VD"+i,"1/1/2012"));
        }
    }
}
