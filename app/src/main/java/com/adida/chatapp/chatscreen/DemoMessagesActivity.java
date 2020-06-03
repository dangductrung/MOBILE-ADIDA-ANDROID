package com.adida.chatapp.chatscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.utils.AppUtils;
import com.adida.chatapp.download.DownloadTask;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Created by troy379 on 04.04.17.
 */


public abstract class DemoMessagesActivity extends AppCompatActivity
        implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener, MessagesListAdapter.OnMessageClickListener {

    private static final int TOTAL_MESSAGES_COUNT = 100;
    static String APP_PATH = "/sdcard/chatapp/";
    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private boolean isSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(DemoMessagesActivity.this).load(url).into(imageView);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        if (!isSelected) {
            Log.e("df","fsadf");
            isSelected = true;
            Message a  = messagesAdapter.getSelectedMessages().get(0);
            if (a.getImageUrl() != null) {
                startDownload(a.getImageUrl());
            }
            messagesAdapter.unselectAllItems();
        } else {
            isSelected = false;
            return;
        }

    }

    public void startDownload(String url) {
        createPathIfNotExist(APP_PATH);
        String destPath = APP_PATH + getFileName(url);
        startDownload(url, destPath);
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


    protected void loadMessages() {
//        new Handler().postDelayed(new Runnable() { //imitation of internet connection
//            @Override
//            public void run() {
//                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
//                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//                messagesAdapter.addToEnd(messages, false);
//            }
//        }, 1000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
}
