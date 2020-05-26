package com.adida.chatapp.chatscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adida.chatapp.chatscreen.models.Dialog;
import com.adida.chatapp.chatscreen.models.Message;
import com.adida.chatapp.chatscreen.models.User;
import com.adida.chatapp.R;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.Calendar;

public class DefaultDialogsActivity extends DemoDialogsActivity {
    public static void open(Context context) {
        context.startActivity(new Intent(context, DefaultDialogsActivity.class));

    }

    private EglBase rootEglBase;

    private DialogsList dialogsList;
    private FloatingActionButton FABAddConversation;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_dialogs);

        ChatApplication.initPeerConnectionFactory(getApplicationContext());

        context=this;
        rootEglBase=EglBase.create();

        dialogsList = findViewById(R.id.dialogsList);
        initAdapter();

        FABAddConversation=findViewById(R.id.FABAddConversation);
        FABAddConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<User> users=new ArrayList<User>();
                users.add(new User("testid1","John Foe","http://i.imgur.com/pv1tBmT.png",true));
                users.add(new User("testid2","Alex Dia","http://i.imgur.com/pv1tBmT.png",true));
//
                dialogsAdapter.addItem(new Dialog("testid12","Alex Dia","http://i.imgur.com/pv1tBmT.png",users,new Message("testid1",users.get(0),"Hi this is test", Calendar.getInstance().getTime()),3));



//                try {
//                    //WebRTCPeerConnection peerConnection=peerConnection=factory.builder();
//                    //DefaultMessagesActivity.openInit(context,peerConnection);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }


            }
        });
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        //DefaultMessagesActivity.open(this);
    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        //super.dialogsAdapter.setItems(DialogsFixtures.getDialogs());

        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(super.dialogsAdapter);
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
    }
}
