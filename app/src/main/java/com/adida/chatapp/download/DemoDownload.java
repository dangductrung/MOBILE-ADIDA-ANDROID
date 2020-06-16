package com.adida.chatapp.download;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.adida.chatapp.R;

import java.io.File;

public class DemoDownload extends AppCompatActivity {

    public TextView txtInfo;
    Button btnDownload;

//    String urlToFile = "https://file-examples.com/wp-content/uploads/2017/02/zip_5MB.zip";
    String urlToFile = "https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg";
//    static String APP_PATH = "/sdcard/chatapp/";
    static String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_download);

        // create file path, etc...
        createPathIfNotExist(APP_PATH);
        String destPath = APP_PATH + "/" + getFileName(urlToFile);
        // check if filename is image
        //...

        txtInfo = findViewById(R.id.txtDownload);
        btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDownload(urlToFile, destPath);
            }
        });
        btnDownload.setText("input: " + urlToFile
                + "\n output: " + destPath
                + "\n click to download");



        // check permissions
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
        ) {

            txtInfo.setText("enough permissions, you can download");
        } else {
            txtInfo.setText("No permission, please grant this app for Internet and write storage");
        }
    }

    private void startDownload(String urlToFile, String destPath){
        DownloadTask downloadTask = new DownloadTask(this);
        downloadTask.execute(urlToFile, destPath);
    }

    private static String getFileName(String path){
        int pos = path.lastIndexOf("/");
        return path.substring(pos);
    }

    private static void createPathIfNotExist(String path){
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
