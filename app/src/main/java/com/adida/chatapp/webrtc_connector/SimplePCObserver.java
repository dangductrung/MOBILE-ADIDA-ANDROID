package com.adida.chatapp.webrtc_connector;

import android.os.Build;
import android.util.Log;

import com.adida.chatapp.extendapplication.ChatApplication;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;

public class SimplePCObserver {
    public static PeerConnection.Observer getPCObserver(RTCPeerConnectionWrapper rtcPeerConnectionWrapper){
        return new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {

            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                rtcPeerConnectionWrapper.createIceCandidate(iceCandidate.sdpMLineIndex,iceCandidate.sdpMid,iceCandidate.sdp);
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                rtcPeerConnectionWrapper.getMediaStream(mediaStream);
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                //End call
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                rtcPeerConnectionWrapper.setDataChannel(dataChannel);
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

                        rtcPeerConnectionWrapper.receiveDataChannelMessage(command);
                        //Update UI
                        Log.d("receive message", command);

                    }
                });
            }

            @Override
            public void onRenegotiationNeeded() {
                //if(rtcPeerConnectionWrapper.getConnectionState().equals("CONNECTED")) {
                 //   Log.d("123", "onRenegotiationNeeded: 123");
                 //   rtcPeerConnectionWrapper.createOffer();
                 //   //rtcPeerConnectionWrapper.createOffer();
                //}

            }

            //Triggered when a remote peer adds a track to their stream
            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                Log.d("onAddTrack", "onAddTrack: ");
                //Get media from stream and create a video track
                MediaStream remoteMediaStream = mediaStreams[0];

                if(remoteMediaStream.videoTracks.size()>0){
                    VideoTrack v= remoteMediaStream.videoTracks.get(0);
                    v.setEnabled(true);

                    //Show remote stream in remote surface
                    //SurfaceViewRenderer surfaceRemote = (SurfaceViewRenderer) ((Activity) context).findViewById(R.id.surfaceRemote);
                    //v.addSink(surfaceRemote);
                    rtcPeerConnectionWrapper.receiveOnAddTrackMessage(v);
                }

//                MediaStream remoteMediaStream = mediaStreams[0];
//
//                //if(remoteMediaStream.videoTracks.size()>0){
//                    VideoTrack v= remoteMediaStream.videoTracks.get(0);
//                    //if(!v.enabled())
//                        v.setEnabled(true);
//                receiveOnAddTrackMessage
//                //}
//
                if(remoteMediaStream.audioTracks.size()>0){
                    AudioTrack audioTrack= remoteMediaStream.audioTracks.get(0);
                    if(audioTrack!=null) {
                        audioTrack.setEnabled(true);
                        rtcPeerConnectionWrapper.receiveAddTrackMessage(audioTrack);
                    }
                }
            }

        };
    }
}
