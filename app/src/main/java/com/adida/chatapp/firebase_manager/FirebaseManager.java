package com.adida.chatapp.firebase_manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class FirebaseManager {
    private static FirebaseManager instance;
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
        addListenEvent();
    }

    public void sendSDP(String remoteUserID,String sdp,String firebaseKey)
    {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
        SDPInfo sdpInfo= new SDPInfo();
        // TODO: Set current user uuid
        sdpInfo.uuid = localUuid;
        sdpInfo.description = sdp;

        FirebaseDatabase.getInstance().getReference(firebaseKey).child(remoteUserID).child(localUuid).setValue(sdpInfo);
    }

    public void sendIceCandidate(String remoteUserID,int sdpMLineIndex,String sdpMid,String sdp)
    {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
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

    void addListenEvent() {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers);
        //TODO: Receive offer & ice-server

        FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
                wrapper.receiveOffer(sdpInfo.description);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).removeValue();
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
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(iceCandidate.uuid);
                wrapper.receiveIceCandidate(iceCandidate.sdpMLineIndex,iceCandidate.sdpMid,iceCandidate.sdp);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).removeValue();
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
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
                wrapper.receiveAnswer(sdpInfo.description);
                FirebaseDatabase.getInstance().getReference(FirebaseKeys.SDPOffers).child(localUuid).removeValue();
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
