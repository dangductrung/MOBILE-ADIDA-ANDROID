package com.example.webrtcchat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static org.webrtc.SessionDescription.Type.ANSWER;

public class WebRTCPeerConnection {
    private static final String TAG = "SamplePeerConnectionAct";
    private PeerConnection peerConnection;
    private SessionDescription tempSS;

    //Socket instance to connect to signaling server
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.148:8080");
        } catch (URISyntaxException e) {
            Log.e("abc", "index=" + e);
        }
    }

    public WebRTCPeerConnection(Context context,PeerConnectionFactory factory) throws URISyntaxException {
        //Ice server
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }


            //Triggered on calling setLocalDescription
            //Ice candidates show potential paths for peer to reach each other
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate: send ice candidate");
                //Create a message that contains IceCandidate information
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "candidate");
                    message.put("label", iceCandidate.sdpMLineIndex);
                    message.put("id", iceCandidate.sdpMid);
                    message.put("candidate", iceCandidate.sdp);

                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
                    //Send IceCandidate message
                    mSocket.emit("ice-candidate",message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }

            //Triggered when a remote peer adds a track to their stream
            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                //Get media from stream and create a video track
                MediaStream remoteMediaStream = mediaStreams[0];
                VideoTrack v= remoteMediaStream.videoTracks.get(0);
                v.setEnabled(true);

                //Show remote stream in remote surface
                SurfaceViewRenderer surfaceRemote = (SurfaceViewRenderer) ((Activity) context).findViewById(R.id.surfaceRemote);
                v.addSink(surfaceRemote);
            }

        };

        //create actual peerConnection
        peerConnection=factory.createPeerConnection(iceServers,pcObserver);

        //Init and create socket connection
        try{
            initSocketConnection();
            mSocket.connect();
        }
        catch (URISyntaxException e){

        }

    }

    private void initSocketConnection() throws URISyntaxException {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            //On connect, send a message to join room
            @Override
            public void call(Object... args) {
                mSocket.emit("join-room","abc123");
            }
        });

        //On receiving an offer message, set received data as the remote description
        // and create an answer to send back
        mSocket.on("offer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                peerConnection.setRemoteDescription(new SimpleSdpObserver(),new SessionDescription(SessionDescription.Type.OFFER,args[0].toString()));

                peerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription){
                        mSocket.emit("answer",sessionDescription.description);

                        peerConnection.setLocalDescription(new SimpleSdpObserver(),sessionDescription);
                    }
                },new MediaConstraints());
            }
        });

        //On receiving an answer message, set both local and remote description to establish a connection
        mSocket.on("answer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                peerConnection.setLocalDescription(new SimpleSdpObserver(),tempSS);
                peerConnection.setRemoteDescription(new SimpleSdpObserver(),new SessionDescription(ANSWER,args[0].toString()));

            }
        });

        //On receiving an IceCandidate from a remote peer, add this IceCandidate
        mSocket.on("ice-candidate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject message = (JSONObject) args[0];
                try {
                    IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                    peerConnection.addIceCandidate(candidate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startStreamingVideo(VideoTrack cameraVideoTrack) {
        peerConnection.addTrack(cameraVideoTrack);
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        //After joining a room, a peer will send offers to other peer in a room,
        peerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                Log.d(TAG, sessionDescription.description.replace(" ",""));

                mSocket.emit("offer",sessionDescription.description);

                //Save sessionDescription so in case a peer answer to this offer, the peer can set
                //this as localDescription
                tempSS= sessionDescription;
                //peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
            }
        }, sdpMediaConstraints);
    }




}
