package com.adida.chatapp.firebase_manager;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.adida.chatapp.entities.IceCandidate;
import com.adida.chatapp.entities.Report;
import com.adida.chatapp.entities.SDPInfo;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.sharepref.SharePref;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FirebaseManager {
    private static FirebaseManager instance;
    private Context context;
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    private FirebaseManager(){}

    public void createNewUser(String email, Context context) {
        User user = new User();
        user.email = email;
        user.uuid = SharePref.getInstance(context).getUuid();
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(context).getUuid()).setValue(user);
    }

    public void updateUser(User user, Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(SharePref.getInstance(context).getUuid()).setValue(user);
    }


    public void setState(boolean state, Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.state).child(SharePref.getInstance(context).getUuid()).setValue(state);
        this.context = context;
        //addListenEvent(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendSDP(String remoteUserID, String sdp, String firebaseKey)
    {
        String localUuid=SharePref.getInstance(context).getUuid();
        SDPInfo sdpInfo= new SDPInfo();
        // TODO: Set current user uuid
        sdpInfo.uuid = localUuid;
        sdpInfo.description = sdp;
        // Create offer
        FirebaseDatabase.getInstance().getReference(firebaseKey).child(remoteUserID).child(localUuid).setValue(sdpInfo);

        //Record History
        recordHistory(remoteUserID, sdp);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void recordHistory(String remoteId, String sdp) {
        String localUuid=SharePref.getInstance(context).getUuid();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        if(sdp.contains("m=video")){
            FirebaseDatabase.getInstance().getReference(FirebaseKeys.history).child(localUuid).child(FirebaseKeys.video).child(dtf.format(now)).setValue(remoteId);
        } else {
            FirebaseDatabase.getInstance().getReference(FirebaseKeys.history).child(localUuid).child(FirebaseKeys.chat).child(dtf.format(now)).setValue(remoteId);
        }
    }

    public void sendIceCandidate(String remoteUserID,int sdpMLineIndex,String sdpMid,String sdp)
    {
        String localUuid=SharePref.getInstance(context).getUuid();
        IceCandidate iceCandidate= new IceCandidate();

        iceCandidate.sdpMLineIndex=sdpMLineIndex;
        iceCandidate.sdpMid=sdpMid;
        iceCandidate.sdp=sdp;
        iceCandidate.uuid=localUuid;

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.IceCandidates).child(remoteUserID).child(localUuid).setValue(iceCandidate);
    }

    public void sendReport(Report report,  Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.report).child(SharePref.getInstance(context).getUuid()).setValue(report);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addListenEvent(Context context) {
        String localUuid=SharePref.getInstance(context).getUuid();

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers);
        //TODO: Receive offer & ice-server

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).removeValue();
                RTCPeerConnectionWrapper wrapper;

                if ( ChatApplication.getInstance().getUserPeerConnections() != null && ChatApplication.getInstance().getUserPeerConnections().containsKey(sdpInfo.uuid)) {
                    wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
                } else{
                    wrapper = new RTCPeerConnectionWrapper(sdpInfo.uuid,context);
                    ChatApplication.getInstance().getUserPeerConnections().put(sdpInfo.uuid,wrapper);
                }

                wrapper.receiveOffer(sdpInfo.description);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        FirebaseDatabase.getInstance().getReference(FirebaseKeys.IceCandidates).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final IceCandidate iceCandidate = dataSnapshot.getValue(IceCandidate.class);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.IceCandidates).child(localUuid).removeValue();
                if (ChatApplication.getInstance().getUserPeerConnections() != null && ChatApplication.getInstance().getUserPeerConnections().containsKey(iceCandidate.uuid)) {
                    RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(iceCandidate.uuid);
                    wrapper.receiveIceCandidate(iceCandidate.sdpMLineIndex,iceCandidate.sdpMid,iceCandidate.sdp);
                }else{
                    RTCPeerConnectionWrapper wrapper = new RTCPeerConnectionWrapper(iceCandidate.uuid,context);
                    ChatApplication.getInstance().getUserPeerConnections().put(iceCandidate.uuid,wrapper);
                    wrapper.receiveIceCandidate(iceCandidate.sdpMLineIndex,iceCandidate.sdpMid,iceCandidate.sdp);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //TODO: Receive answer
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPAnswers).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPAnswers).child(localUuid).removeValue();
                if (ChatApplication.getInstance().getUserPeerConnections() != null && ChatApplication.getInstance().getUserPeerConnections().containsKey(sdpInfo.uuid)) {
                    RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
                    wrapper.receiveAnswer(sdpInfo.description);
                }else{
                    RTCPeerConnectionWrapper wrapper = new RTCPeerConnectionWrapper(sdpInfo.uuid,context);
                    ChatApplication.getInstance().getUserPeerConnections().put(sdpInfo.uuid,wrapper);
                    wrapper.receiveAnswer(sdpInfo.description);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
