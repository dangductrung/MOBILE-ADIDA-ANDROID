package com.adida.chatapp.download;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//https://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {

        Log.e("QT doinBackground", "url: " + sUrl[0]);

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        String src = sUrl[0];
        String dest = sUrl[1];
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            Log.e("QT doinBackground", "size: " + fileLength);

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(dest);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                getClass().getName());
//        mWakeLock.acquire();

//        mProgressDialog.show();
        Log.e("QT onPreExecute", "before download");
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setMax(100);
//        mProgressDialog.setProgress(progress[0]);


        ((DemoDownload)context).txtInfo.setText("\n " + progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
//        mWakeLock.release();
//        mProgressDialog.dismiss();
        Log.e("QT onPreExecute", "result: " + result);
        if (result != null){
            ((DemoDownload)context).txtInfo.append("\n failed!");
        }
//            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else{
            ((DemoDownload)context).txtInfo.append("\n successful!");

        }
//            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
    }

}
