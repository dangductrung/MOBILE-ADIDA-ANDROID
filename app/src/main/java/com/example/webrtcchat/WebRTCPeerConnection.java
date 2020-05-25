//package com.example.webrtcchat;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//
//import com.example.webrtcchat.firebase.FirebaseManager;
//import com.example.webrtcchat.sharepref.SharePref;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.webrtc.DataChannel;
//import org.webrtc.IceCandidate;
//import org.webrtc.MediaConstraints;
//import org.webrtc.MediaStream;
//import org.webrtc.PeerConnection;
//import org.webrtc.PeerConnectionFactory;
//import org.webrtc.RtpReceiver;
//import org.webrtc.SessionDescription;
//import org.webrtc.SurfaceViewRenderer;
//import org.webrtc.VideoTrack;
//
//import java.net.URISyntaxException;
//import java.nio.ByteBuffer;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.webrtc.SessionDescription.Type.ANSWER;
//
//public class WebRTCPeerConnection {
//    private static final String TAG = "SamplePeerConnectionAct";
//    private PeerConnection peerConnection;
//    private SessionDescription tempOfferSessionDescription;
//    private DataChannel dataChannel;
//    private Context activityContext;
//
//    public WebRTCPeerConnection(Context context) throws URISyntaxException {
//        //Ice server
//        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
//        iceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
//        iceServers.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());
//        activityContext=context;
//
//        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
//            @Override
//            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
//                Log.d(TAG, "onSignalingChange: ");
//            }
//
//            @Override
//            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
//                Log.d(TAG, "onIceConnectionChange: ");
//            }
//
//            @Override
//            public void onIceConnectionReceivingChange(boolean b) {
//                Log.d(TAG, "onIceConnectionReceivingChange: ");
//            }
//
//            @Override
//            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
//                Log.d(TAG, "onIceGatheringChange: ");
//            }
//
//
//            //Triggered on calling setLocalDescription
//            //Ice candidates show potential paths for peer to reach each other
//            @Override
//            public void onIceCandidate(IceCandidate iceCandidate) {
//                Log.d(TAG, "onIceCandidate: send ice candidate");
//                //Create a message that contains IceCandidate information
//                JSONObject message = new JSONObject();
//
//                try {
//                    message.put("type", "candidate");
//                    message.put("label", iceCandidate.sdpMLineIndex);
//                    message.put("id", iceCandidate.sdpMid);
//                    message.put("candidate", iceCandidate.sdp);
//
//                    FirebaseManager.getInstance().sendIceCandidate(message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
//                Log.d(TAG, "onIceCandidatesRemoved: ");
//            }
//
//            @Override
//            public void onAddStream(MediaStream mediaStream) {
//
//            }
//
//            @Override
//            public void onRemoveStream(MediaStream mediaStream) {
//                Log.d(TAG, "onRemoveStream: ");
//            }
//
//            @Override
//            public void onDataChannel(DataChannel dataChannel) {
//
//
//                Log.d(TAG, "onDataChannel: ");
//                dataChannel.registerObserver(new DataChannel.Observer() {
//                    @Override
//                    public void onBufferedAmountChange(long l) {
//
//                    }
//
//                    @Override
//                    public void onStateChange() {
//
//                    }
//
//                    @Override
//                    public void onMessage(DataChannel.Buffer buffer) {
//                        ByteBuffer data = buffer.data;
//                        byte[] bytes = new byte[data.remaining()];
//                        data.get(bytes);
//                        final String command = new String(bytes);
//
//                        Log.d(TAG, "receivedMessage "+command);
//
//                        MainActivity thisActivity= (MainActivity)activityContext;
//
//                        thisActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                thisActivity.txtChatBox.setText(command);
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onRenegotiationNeeded() {
//                Log.d(TAG, "onRenegotiationNeeded: ");
//            }
//
//            //Triggered when a remote peer adds a track to their stream
//            @Override
//            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
//                //Get media from stream and create a video track
//                MediaStream remoteMediaStream = mediaStreams[0];
//                VideoTrack v= remoteMediaStream.videoTracks.get(0);
//                v.setEnabled(true);
//
//                //Show remote stream in remote surface
//                SurfaceViewRenderer surfaceRemote = (SurfaceViewRenderer) ((Activity) context).findViewById(R.id.surfaceRemote);
//                v.addSink(surfaceRemote);
//            }
//
//        };
//
//        //create actual peerConnection
//        //peerConnection= SharePref.getInstance(activityContext.getApplicationContext()).getPeerConnectionFactory().createPeerConnection(iceServers,pcObserver);
//    }
//
////    private void initSocketConnection() throws URISyntaxException {
////        //On receiving an offer message, set received data as the remote description
////        // and create an answer to send back
////        mSocket.on("offer", new Emitter.Listener() {
////            @Override
////            public void call(final Object... args) {
////                peerConnection.setRemoteDescription(new SimpleSdpObserver(),new SessionDescription(SessionDescription.Type.OFFER,args[0].toString()));
////
////                peerConnection.createAnswer(new SimpleSdpObserver() {
////                    @Override
////                    public void onCreateSuccess(SessionDescription sessionDescription){
////                        mSocket.emit("answer",sessionDescription.description);
////
////                        peerConnection.setLocalDescription(new SimpleSdpObserver(),sessionDescription);
////                    }
////                },new MediaConstraints());
////            }
////        });
////
////        //On receiving an answer message, set both local and remote description to establish a connection
////        mSocket.on("answer", new Emitter.Listener() {
////            @Override
////            public void call(Object... args) {
////                peerConnection.setLocalDescription(new SimpleSdpObserver(),tempSS);
////                peerConnection.setRemoteDescription(new SimpleSdpObserver(),new SessionDescription(ANSWER,args[0].toString()));
////
////            }
////        });
////
////        //On receiving an IceCandidate from a remote peer, add this IceCandidate
////        mSocket.on("ice-candidate", new Emitter.Listener() {
////            @Override
////            public void call(Object... args) {
////                JSONObject message = (JSONObject) args[0];
////                try {
////                    IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
////                    peerConnection.addIceCandidate(candidate);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
////    }
//
////    public void createOffer(){
////        MediaConstraints sdpMediaConstraints = new MediaConstraints();
////        //After joining a room, a peer will send offers to other peer in a room,
////        peerConnection.createOffer(new SimpleSdpObserver() {
////            @Override
////            public void onCreateSuccess(SessionDescription sessionDescription) {
////                Log.d(TAG, "onCreateSuccess: ");
////
////                //mSocket.emit("offer",sessionDescription.description);
////                FirebaseManager.getInstance().sendSDP(sessionDescription.description);
////                //Save sessionDescription so in case a peer answer to this offer, the peer can set
////                //this as localDescription
////                tempOfferSessionDescription= sessionDescription;
////                //peerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
////            }
////        }, sdpMediaConstraints);
////    }
//
//    public void startStreamingVideo(VideoTrack cameraVideoTrack) {
//        peerConnection.addTrack(cameraVideoTrack);
//    }
//
//    public void startDataChannel() {
//        dataChannel=peerConnection.createDataChannel("abc123",new DataChannel.Init());
//        dataChannel.registerObserver(new DataChannel.Observer() {
//            @Override
//            public void onBufferedAmountChange(long l) {
//
//            }
//
//            @Override
//            public void onStateChange() {
//
//            }
//
//            @Override
//            public void onMessage(DataChannel.Buffer buffer) {
//            }
//        });
//    }
//
//    public void sendDataChannelMessage(String message){
//        Log.d(TAG, "sendMessage: ");
//        ByteBuffer data = stringToByteBuffer("-s" + message, Charset.defaultCharset());
//        dataChannel.send(new DataChannel.Buffer(data, false));
//    }
//
//    public static ByteBuffer stringToByteBuffer(String msg, Charset charset) {
//        return ByteBuffer.wrap(msg.getBytes(charset));
//    }
//}
