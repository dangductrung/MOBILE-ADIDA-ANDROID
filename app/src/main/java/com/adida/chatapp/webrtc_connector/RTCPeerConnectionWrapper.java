package com.adida.chatapp.webrtc_connector;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.adida.chatapp.R;
import com.adida.chatapp.callscreen.AudioCallScreenActivity;
import com.adida.chatapp.callscreen.CallScreenActivity;
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

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
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
    private Context callContext;
    private MediaStream mediaStream;

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

    public void setCallContext(Context context){
        callContext=context;
    }

    public void addTrack(VideoTrack cameraVideoTrack){
        peerConnection.addTrack(cameraVideoTrack);
    }

    public void addTrack(AudioTrack audioTrack){
        peerConnection.addTrack(audioTrack);
    }


    public void removeTrack(VideoTrack track){
        if(mediaStream!=null){
            mediaStream.removeTrack(track);
        }

    }

    public void removeTrack(AudioTrack track){
        if(mediaStream!=null){
            mediaStream.removeTrack(track);
        }
    }

    public void removeStream(){
        //peerConnection.removeStream(mediaStream);

        List<RtpSender> transceivers= peerConnection.getSenders();
        //transceivers.removeAll(transceivers);

        Iterator iterator = transceivers.iterator();
        while(iterator.hasNext()) {
            RtpSender trans= (RtpSender) iterator.next();
            trans.setTrack(null,true);
        }

    }

    public void startDataChannel(){
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
        if(dataChannel!=null && dataChannel.state()== DataChannel.State.OPEN && message!=null && message!=""){
            message = SharePref.getInstance(activityContext).getUuid() + "-message-" + message;
            ByteBuffer data = Utils.stringToByteBuffer(message, Charset.defaultCharset());
            Log.d("send message", "sendDataChannelMessage: ");
            dataChannel.send(new DataChannel.Buffer(data, false));
        }
    }

    public void sendImageUrlMessage(String url, String uniqueId) {
        url = SharePref.getInstance(activityContext).getUuid() + "-image-" + url + "-" + uniqueId;
        ByteBuffer data = Utils.stringToByteBuffer(url, Charset.defaultCharset());
        Log.d("send message", "sendDataChannelMessage: ");
        dataChannel.send(new DataChannel.Buffer(data, false));
    }

    public void createOffer(){
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        //After joining a room, a peer will send offers to other peer in a room,
        peerConnection.createOffer(new SimpleSdpObserver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d("123", sessionDescription.description);
                FirebaseManager.getInstance().sendSDP(remoteUserID,sessionDescription.description, FirebaseKeys.SDPOffers);
                //Save sessionDescription so in case a peer answer to this offer, the peer can set
                //this as localDescription
                tempOfferSessionDescription= sessionDescription;

                //peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
            }

            @Override
            public void onCreateFailure(String s) {
                Log.d("failed", "onCreateFailure: "+s);
            }
        }, sdpMediaConstraints);
    }

    public void createAnswer(){
        peerConnection.createAnswer(new SimpleSdpObserver() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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
        //Prompt yes/no
        if(sdp.contains("m=video")){
            CallScreenActivity.open(activityContext,remoteUserID,sdp,true);
        }
        else if (sdp.contains("m=audio")){
            AudioCallScreenActivity.open(activityContext,remoteUserID,sdp,true);
        }
        else{
            peerConnection.setRemoteDescription(new SimpleSdpObserver()
                    ,new SessionDescription(SessionDescription.Type.OFFER,sdp));

            createAnswer();
        }
    }

    public void setRemoteDescription(String sdp){
        peerConnection.setRemoteDescription(new SimpleSdpObserver()
                ,new SessionDescription(SessionDescription.Type.OFFER,sdp));
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
        Activity mainActivity= (Activity)activityContext;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String [] tokens = message.split("-");

                DefaultMessagesActivity activityDefaultMessage= (DefaultMessagesActivity) chatContext;
                activityDefaultMessage.receiveMessage(message);
                if (state == ActivityState.OUT) {
                    if (message.contains("message")) {
                        getUserInfo(tokens[0],tokens[2], PendingMessage.TEXT);
                    }
                    else {
                        String url = tokens[2];
                        for (int i =3 ;i < tokens.length - 1; i++) {
                            url += "-" + tokens[i];
                        }
                        getUserInfo(tokens[0],url,PendingMessage.URL);
                        //TODO: delete image on firebase storage
                        //FirebaseStorage.getInstance().getReference().child("images/"+tokens[tokens.length - 1]).delete();
                    }
                }
            }
        });

    }

    public void resetPeerConnection() {
        peerConnection= ChatApplication.getInstance()
                .getPeerConnectionFactory()
                .createPeerConnection(getIceServers(),SimplePCObserver.getPCObserver(this));
    }

    private void getUserInfo(String uuid, String sendingMessage, int type) {
        FirebaseDatabase.getInstance().getReference(FirebaseKeys.profile).child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                PendingMessage message = new PendingMessage();
                message.message = sendingMessage;
                message.sender = uuid;
                message.type = type;
                PendingMessageManager.pending.add(message);
                pushNotification("Message from "+ user.email,type == PendingMessage.TEXT ?  sendingMessage : "image");
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
    public void receiveOnAddTrackMessage(VideoTrack videoTrack){
        Activity activity= (Activity)activityContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CallScreenActivity activityCallScreen= (CallScreenActivity) callContext;
                activityCallScreen.addRemoteVideoTrack(videoTrack);
            }
        });
    }

    public void receiveAddTrackMessage(AudioTrack audioTrack){
        audioTrack.setVolume(1);
        Activity activity= (Activity)activityContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(callContext.getClass()==CallScreenActivity.class){
                    CallScreenActivity activityCallScreen= (CallScreenActivity) callContext;
                    activityCallScreen.addRemoteAudioTrack(audioTrack);
                }
                else if(callContext.getClass()==AudioCallScreenActivity.class){
                    AudioCallScreenActivity audioCallScreenActivity=(AudioCallScreenActivity)callContext;
                    audioCallScreenActivity.addRemoteAudioTrack(audioTrack);
                }

            }
        });
    }

    public void getMediaStream(MediaStream mediaStream){
        this.mediaStream=mediaStream;
    }


}
