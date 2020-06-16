package com.adida.chatapp.chatscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.adida.chatapp.R;
import com.adida.chatapp.callscreen.AudioCallScreenActivity;
import com.adida.chatapp.callscreen.CallScreenActivity;
import com.adida.chatapp.chatscreen.fixtures.MessagesFixtures;
import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.utils.AppUtils;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.message.PendingMessage;
import com.adida.chatapp.webrtc_connector.ActivityState;
import com.adida.chatapp.webrtc_connector.PendingMessageManager;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.HashMap;
import java.util.UUID;

public class DefaultMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    private final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;
    ProgressDialog dialog;

    public static void open(Context context, User user) {
        Intent actMessages = new Intent(context, DefaultMessagesActivity.class);
        actMessages.putExtra("remoteUserId", user.uuid);
        actMessages.putExtra("remoteUserName", user.name);
        context.startActivity(actMessages);
        if (!ChatApplication.getInstance().getUserPeerConnections().containsKey(user.uuid)) {
            RTCPeerConnectionWrapper wrapper = new RTCPeerConnectionWrapper(user.uuid, context);
            wrapper.startDataChannel();
            ChatApplication.getInstance().getUserPeerConnections().put(user.uuid, wrapper);
            wrapper.state = ActivityState.IN;
            wrapper.createOffer();
        }
    }

    private MessagesList messagesList;
    private String remoteUserId;
    private Button btnStartCall;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_messages);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        toolbar=findViewById(R.id.chat_tool_bar);
        setSupportActionBar(toolbar);

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);

        Bundle b = getIntent().getExtras();
        String extrasUserId = b.getString("remoteUserId");
        String extrasUserName = b.getString("remoteUserName");

        if (extrasUserId != null && !extrasUserId.isEmpty()) {
            remoteUserId = extrasUserId;
            RTCPeerConnectionWrapper wrapper = ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId);
            wrapper.setChatContext(this);
        }

        getSupportActionBar().setTitle(extrasUserName);

        loadPendingMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.chat_main_actions,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chat_video) {
            CallScreenActivity.open(DefaultMessagesActivity.this,remoteUserId,false);
            return true;
        }
        else if(id ==R.id.action_chat_call){
            AudioCallScreenActivity.open(DefaultMessagesActivity.this,remoteUserId,false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadPendingMessage() {
        for (int i = 0; i < PendingMessageManager.pending.size(); ++i) {
            if (remoteUserId.equals(PendingMessageManager.pending.get(i).sender)) {
                if (PendingMessageManager.pending.get(i).type == PendingMessage.TEXT) {
                    super.messagesAdapter.addToStart(new Message(remoteUserId, MessagesFixtures.getUser("1"), PendingMessageManager.pending.get(i).message), true);
                } else {
                    super.messagesAdapter.addToStart(MessagesFixtures.getImageMessage(PendingMessageManager.pending.get(i).message,"1"), true);
                }
                PendingMessageManager.pending.remove(i);
                if (i > 0) {
                    i -= 1;
                }
            }
        }
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);

        HashMap<String, RTCPeerConnectionWrapper> a = ChatApplication.getInstance().getUserPeerConnections();

        Log.d("123", ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).getConnectionState());
        ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).sendDataChannelMessage(input.toString());
        return true;
    }

    public void receiveMessage(String message) {
        //TODO: check
        String[] tokens = message.split("-");
        if (message.contains("message")) {
            super.messagesAdapter.addToStart(new Message(remoteUserId, MessagesFixtures.getUser("1"), tokens[2]), true);
        } else {
            String url = tokens[2];
            for (int i =3 ;i < tokens.length - 1; i++) {
                url += "-" + tokens[i];
            }
            super.messagesAdapter.addToStart(MessagesFixtures.getImageMessage(url,"1"), true);
        }

    }

    @Override
    public void onAddAttachments() {
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
            String uniqueKey = UUID.randomUUID().toString();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + uniqueKey);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseStorage.getInstance().getReference().child("images/" + uniqueKey).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    messagesAdapter.addToStart(MessagesFixtures.getImageMessage(uri.toString()), true);
                                    ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).sendImageUrlMessage(uri.toString(), uniqueKey);
                                }
                            });

                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Uploaded... ");
                        }
                    });
        }
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<Message>(super.senderId, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        AppUtils.showToast(DefaultMessagesActivity.this,
                                message.getUser().getName() + " avatar click",
                                false);
                    }
                });
        this.messagesList.setAdapter(super.messagesAdapter);
    }

    @Override
    public void onStartTyping() {
        Log.v("Typing listener", getString(R.string.start_typing_status));
    }

    @Override
    public void onStopTyping() {
        Log.v("Typing listener", getString(R.string.stop_typing_status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).state = ActivityState.IN;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).state = ActivityState.OUT;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).state = ActivityState.OUT;
    }

    @Override
    public void onMessageClick(IMessage message) {
    }
}
