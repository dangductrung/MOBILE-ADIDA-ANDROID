package com.adida.chatapp.chatscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.adida.chatapp.callscreen.CallScreenActivity;
import com.adida.chatapp.chatscreen.fixtures.MessagesFixtures;
import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.utils.AppUtils;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.adida.chatapp.R;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import com.adida.chatapp.entities.User;

import java.util.HashMap;

public class DefaultMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    public static void open(Context context,User user) {
        Intent actMessages = new Intent(context, DefaultMessagesActivity.class);
        actMessages.putExtra("remoteUserId",user.uuid);
        context.startActivity(actMessages);
        if(!ChatApplication.getInstance().getUserPeerConnections().containsKey(user.uuid)){
            RTCPeerConnectionWrapper wrapper= new RTCPeerConnectionWrapper(user.uuid,context);
            wrapper.StartDataChannel();
            ChatApplication.getInstance().getUserPeerConnections().put(user.uuid,wrapper);
            wrapper.createOffer();
        }
    }

    private MessagesList messagesList;
    private String remoteUserId;
    private Button btnStartCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_messages);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);

        Bundle b = getIntent().getExtras();
        String extrasUserId=b.getString("remoteUserId");

        if(extrasUserId!=null && !extrasUserId.isEmpty()){
            remoteUserId= extrasUserId;
            RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId);
            wrapper.setChatContext(this);
        }

        btnStartCall=(Button)findViewById(R.id.btnStartCall);

        btnStartCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CallScreenActivity.open(DefaultMessagesActivity.this,remoteUserId,false);
            }
        });
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);

        HashMap<String,RTCPeerConnectionWrapper> a=ChatApplication.getInstance().getUserPeerConnections();

        Log.d("123",ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).getConnectionState());
        ChatApplication.getInstance().getUserPeerConnections().get(remoteUserId).sendDataChannelMessage(input.toString());
        return true;
    }

    public void receiveMessage(String message){
        super.messagesAdapter.addToStart(new Message(remoteUserId,MessagesFixtures.getUser("1"),message), true);
    }

    @Override
    public void onAddAttachments() {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getImageMessage(), true);
        Log.d("add attach", "onAddAttachments: GetImage");
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
}
