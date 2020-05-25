package com.example.webrtcchat.firebase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.webrtcchat.ChatApplication;
import com.example.webrtcchat.RTCPeerConnectionWrapper;
import com.example.webrtcchat.entities.IceCandidate;
import com.example.webrtcchat.entities.SDPInfo;
import com.example.webrtcchat.entities.User;
import com.example.webrtcchat.keys.FirebaseKeys;
import com.example.webrtcchat.sharepref.SharePref;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.HashMap;

public class FirebaseManager {
    private static FirebaseManager instance;
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    private FirebaseDatabase mDatabase;

    private Context context;

    private FirebaseManager(){
        mDatabase = FirebaseDatabase.getInstance();
    }

    void listenEvent() {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
        //TODO: Receive offer & ice-server
        mDatabase.getReference(FirebaseKeys.SDPOffers).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
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

//        mDatabase.getReference(FirebaseKeys.SDPOffers).child(localUuid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
//
//                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
//                wrapper.receiveAnswer(sdpInfo.description);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

        mDatabase.getReference(FirebaseKeys.IceCandidates).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final IceCandidate iceCandidate = dataSnapshot.getValue(IceCandidate.class);
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(iceCandidate.uuid);
                wrapper.receiveIceCandidate(iceCandidate.sdpMLineIndex,iceCandidate.sdpMid,iceCandidate.sdp);
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
        mDatabase.getReference(FirebaseKeys.SDPAnswers).child(localUuid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final SDPInfo sdpInfo = dataSnapshot.getValue(SDPInfo.class);
                RTCPeerConnectionWrapper wrapper= ChatApplication.getInstance().getUserPeerConnections().get(sdpInfo.uuid);
                wrapper.receiveAnswer(sdpInfo.description);
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

    public void createNewUser(String email) {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
        User user = new User();
        user.email = email;
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profiles).child(localUuid).setValue(user);
    }

    public void updateUser(User user, Context context) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profiles).child(SharePref.getInstance(context).getUuid()).setValue(user);
    }

    public void getUserList() {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profiles).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, User> param = (HashMap<String, User>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setState(boolean state) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.states).child(SharePref.getInstance(context).getUuid()).setValue(state);
    }

    public void sendSDP(String remoteUserID,String sdp,String firebaseKey)
    {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
        SDPInfo sdpInfo= new SDPInfo();
        // TODO: Set current user uuid
        sdpInfo.uuid = localUuid;
        sdpInfo.description = sdp;

        mDatabase.getReference(firebaseKey).child(remoteUserID).setValue(sdpInfo);
    }

    public void sendIceCandidate(String remoteUserID,int sdpMLineIndex,String sdpMid,String sdp)
    {
        String localUuid=SharePref.getInstance(ChatApplication.getContext()).getUuid();
        IceCandidate iceCandidate= new IceCandidate();

        iceCandidate.sdpMLineIndex=sdpMLineIndex;
        iceCandidate.sdpMid=sdpMid;
        iceCandidate.sdp=sdp;
        iceCandidate.uuid=localUuid;

        mDatabase.getReference(FirebaseKeys.IceCandidates).child(remoteUserID).setValue(iceCandidate);
    }

}

