package com.adida.chatapp.webrtc_connector;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.adida.chatapp.R;
import com.adida.chatapp.chatscreen.DefaultMessagesActivity;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.firebase_manager.FirebaseManager;
import com.adida.chatapp.keys.FirebaseKeys;
import com.adida.chatapp.main.MainActivity;
import com.adida.chatapp.message.PendingMessage;
import com.adida.chatapp.sharepref.SharePref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RTCPeerConnectionWrapper {
    static int  count = 0;
    private String remoteUserID;
    private PeerConnection peerConnection;
    private SessionDescription tempOfferSessionDescription;
    private DataChannel dataChannel;

    private Context activityContext;
    private Context chatContext;
    public int state;

    public  RTCPeerConnectionWrapper(String remoteUserID, Context activityContext){
        this.remoteUserID=remoteUserID;
        this.activityContext=activityContext;

        peerConnection= ChatApplication.getInstance()
                .getPeerConnectionFactory()
                .createPeerConnection(getIceServers(),SimplePCObserver.getPCObserver(this));
    }

    public String getConnectionState(){
        return peerConnection.connectionState().name();
    }

    public void setChatContext(Context context){
        chatContext=context;
    }

    public void StartStreaming(VideoTrack cameraVideoTrack){
        peerConnection.addTrack(cameraVideoTrack);
    }

    public void StartDataChannel(){
        dataChannel=peerConnection.createDataChannel(remoteUserID,new DataChannel.Init());
        dataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {

            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                ByteBuffer data = buffer.data;
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                final String command = new String(bytes);

                receiveDataChannelMessage(command);
                //Update UI
                Log.d("receive message", command);
            }
        });
    }

    public void setDataChannel(DataChannel dataChannel){
        this.dataChannel=dataChannel;
    }

    public void sendDataChannelMessage(String message){
        message = SharePref.getInstance(activityContext).getUuid() + "-" + message;
        ByteBuffer data = Utils.stringToByteBuffer(message, Charset.defaultCharset());
        Log.d("send message", "sendDataChannelMessage: ");
        dataChannel.send(new DataChannel.Buffer(data, false));
    }

    public void createOffer(){
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        //After joining a room, a peer will send offers to other peer in a room,
        peerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d("123", sessionDescription.description);
                FirebaseManager.getInstance().sendSDP(remoteUserID,sessionDescription.description, FirebaseKeys.SDPOffers);
                //Save sessionDescription so in case a peer answer to this offer, the peer can set
                //this as localDescription
                tempOfferSessionDescription= sessionDescription;

                //peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
            }
        }, sdpMediaConstraints);
    }

    public void createAnswer(){
        peerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription){
                        FirebaseManager.getInstance().sendSDP(remoteUserID,sessionDescription.description, FirebaseKeys.SDPAnswers);

                        peerConnection.setLocalDescription(new SimpleSdpObserver(),sessionDescription);
                    }
                },new MediaConstraints());
    }

    public void createIceCandidate(int sdpMLineIndex,String sdpMid,String sdp){
        FirebaseManager.getInstance().sendIceCandidate(remoteUserID,sdpMLineIndex,sdpMid,sdp);

    }

    public void receiveOffer(String sdp){
        peerConnection.setRemoteDescription(new SimpleSdpObserver()
                ,new SessionDescription(SessionDescription.Type.OFFER,sdp));

        createAnswer();
    }

    public void receiveAnswer(String sdp){
        peerConnection.setLocalDescription(new SimpleSdpObserver(),tempOfferSessionDescription);

        peerConnection.setRemoteDescription(
                new SimpleSdpObserver(),
                new SessionDescription(SessionDescription.Type.ANSWER,sdp));
    }

    public void receiveIceCandidate(int sdpMLineIndex,String sdpMid,String sdp){
            IceCandidate candidate = new IceCandidate(
                    sdpMid,
                    sdpMLineIndex,
                    sdp);
            peerConnection.addIceCandidate(candidate);
    }

    public static List<PeerConnection.IceServer> getIceServers(){
        //Ice server
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());

        return iceServers;
    }

    public void receiveDataChannelMessage(String message){
        MainActivity mainActivity= (MainActivity)activityContext;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String [] tokens = message.split("-");

                DefaultMessagesActivity activityDefaultMessage= (DefaultMessagesActivity) chatContext;
                activityDefaultMessage.receiveMessage(tokens[1]);

                if (state == ActivityState.OUT) {
                    getUserInfo(tokens[0],tokens[1]);
                }
            }
        });

    }

    private void getUserInfo(String uuid, String sendingMessage) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                PendingMessage message = new PendingMessage();
                message.message = sendingMessage;
                message.sender = uuid;
                PendingMessageManager.pending.add(message);
                pushNotification("Message from "+ user.email, sendingMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void pushNotification(String title,String message) {
        // TODO: Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activityContext,"001")
                .setSmallIcon(R.drawable.vichat_icon_origin)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activityContext);
        notificationManager.notify(count, mBuilder.build());
        count += 1;
    }
}
