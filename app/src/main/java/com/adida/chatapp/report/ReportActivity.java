package com.adida.chatapp.report;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.Report;
import com.adida.chatapp.firebase_manager.FirebaseManager;


public class ReportActivity extends AppCompatActivity {
    EditText title, des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_report);

        Button b = (Button)findViewById(R.id.btnReport);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog();
            }
        });
        ImageButton back = (ImageButton) findViewById(R.id.reportBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (EditText) findViewById(R.id.edtTitle);
        des = (EditText) findViewById(R.id.edtContent);
    }

    private void showYesNoDialog(){

        // prepare click event
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Report report = new Report(title.getText().toString(), des.getText().toString());
                        FirebaseManager.getInstance().sendReport(report, getApplicationContext());
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        // prepare data input
        String title = ((EditText)findViewById(R.id.edtTitle))
                .getText()
                .toString();
        String content = ((EditText)findViewById(R.id.edtContent))
                .getText()
                .toString();


        // show
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?" + title + "\n" + content).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
