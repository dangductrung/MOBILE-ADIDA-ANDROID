package com.example.webrtcchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.webrtcchat.firebase.FirebaseManager;
import com.example.webrtcchat.keys.FirebaseKeys;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RTCPeerConnectionWrapper {
    private String remoteUserID;
    private PeerConnection peerConnection;
    private SessionDescription tempOfferSessionDescription;
    private DataChannel dataChannel;

    private Context activityContext;

    public  RTCPeerConnectionWrapper(String remoteUserID, Context activityContext){
        this.remoteUserID=remoteUserID;
        this.activityContext=activityContext;

        peerConnection= ChatApplication.getInstance()
                .getPeerConnectionFactory()
                .createPeerConnection(getIceServers(),SimplePCObserver.getPCObserver(this));
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
            }
        });
    }

    public void sendDataChannelMessage(String message){
        ByteBuffer data = Utils.stringToByteBuffer("-s" + message, Charset.defaultCharset());
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
                        FirebaseManager.getInstance().sendSDP(remoteUserID,sessionDescription.description,FirebaseKeys.SDPAnswers);

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
}
