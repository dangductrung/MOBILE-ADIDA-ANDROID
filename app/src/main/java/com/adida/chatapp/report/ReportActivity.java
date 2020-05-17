package com.adida.chatapp.report;

import com.adida.chatapp.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ReportActivity extends AppCompatActivity {

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
    }

    private void showYesNoDialog(){

        // prepare click event
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
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
