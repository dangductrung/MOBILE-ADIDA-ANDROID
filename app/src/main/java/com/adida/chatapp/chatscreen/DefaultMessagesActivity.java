package com.adida.chatapp.chatscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adida.chatapp.chatscreen.fixtures.MessagesFixtures;
import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.utils.AppUtils;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.adida.chatapp.R;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class DefaultMessagesActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, DefaultMessagesActivity.class));
        RTCPeerConnectionWrapper wrapper= new RTCPeerConnectionWrapper("user2",context);
        wrapper.StartDataChannel();
        ChatApplication.getInstance().getUserPeerConnections().put("user2",wrapper);
        wrapper.createOffer();
    }

    private MessagesList messagesList;

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
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);
        return true;
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
