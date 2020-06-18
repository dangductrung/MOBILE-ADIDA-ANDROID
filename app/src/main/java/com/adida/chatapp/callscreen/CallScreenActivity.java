package com.adida.chatapp.callscreen;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.adida.chatapp.webrtc_connector.Utils;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class CallScreenActivity extends AppCompatActivity {

    VideoCapturer videoCapturer;

//    public static void open(Context context, User user,boolean isAnswer) {
//        Intent actCall = new Intent(context, CallScreenActivity.class);
//        actCall.putExtra("remoteUserId",user.uuid);
//        actCall.putExtra("type",isAnswer);
//        context.startActivity(actCall);
//    }

    public static void open(Context context, String userId,boolean isAnswer) {
        Intent actCall = new Intent(context, CallScreenActivity.class);
        actCall.putExtra("remoteUserId",userId);
        actCall.putExtra("type",isAnswer);
        context.startActivity(actCall);
    }

    public static void open(Context context, String userId,String sdp,boolean isAnswer) {
        Intent actCall = new Intent(context, CallScreenActivity.class);
        actCall.putExtra("remoteUserId",userId);
        actCall.putExtra("sdp",sdp);
        actCall.putExtra("type",isAnswer);
        context.startActivity(actCall);
    }

    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    private SurfaceViewRenderer surfaceLocal;
    private SurfaceViewRenderer surfaceRemote;
    private Button btnEndCall;


    private EglBase rootEglBase;
    private VideoTrack videoTrackFromCamera;
    private VideoTrack videoTrackRemote;

    AudioTrack audioTrackLocal;
    AudioTrack audioTrackRemote;

    RTCPeerConnectionWrapper wrapper;
    private String remoteUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        surfaceLocal= findViewById(R.id.surfaceLocal);
        surfaceRemote= findViewById(R.id.surfaceRemote);
        btnEndCall=findViewById(R.id.btnEndCall);

        Bundle b = getIntent().getExtras();
        String extrasUserId=b.getString("remoteUserId");
        remoteUserId=extrasUserId;
        String sdp=b.getString("sdp");
        boolean isAnswer=b.getBoolean("type");

        rootEglBase=ChatApplication.RootEglBase;
        initializeSurfaceViews();
        createLocalVideoTrack();

        createLocalAudioTrack();

        if(!ChatApplication.getInstance().getUserPeerConnections().containsKey(extrasUserId)){
            wrapper= new RTCPeerConnectionWrapper(extrasUserId,this);
            wrapper.startDataChannel();
            ChatApplication.getInstance().getUserPeerConnections().put(extrasUserId,wrapper);
        }
        else{
           wrapper= ChatApplication.getInstance().getUserPeerConnections().get(extrasUserId);
            wrapper.resetPeerConnection();
            wrapper.startDataChannel();
        }

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StopCall();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        wrapper.setCallContext(this);
        wrapper.addTrack(videoTrackFromCamera);
        wrapper.addTrack(audioTrackLocal);
        Log.d("addTrack",  videoTrackFromCamera.id());

        if(isAnswer){
            wrapper.setRemoteDescription(sdp);
            wrapper.createAnswer();
        }
        else{
            wrapper.createOffer();
        }
    }

    private void initializeSurfaceViews() {
        surfaceLocal= Utils.InitSurfaceViewRenderer(surfaceLocal,rootEglBase);
        surfaceRemote=Utils.InitSurfaceViewRenderer(surfaceRemote,rootEglBase);
    }

    private void createLocalVideoTrack() {
        VideoCapturer videoCapturer = Utils.createVideoCapturer(this);
        this.videoCapturer=videoCapturer;
        PeerConnectionFactory factory= ChatApplication.getInstance().getPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera=factory.createVideoTrack(remoteUserId, videoSource);

        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addSink(surfaceLocal);
    }

    private void createLocalAudioTrack(){
        PeerConnectionFactory factory= ChatApplication.getInstance().getPeerConnectionFactory();
        AudioSource audioSource=factory.createAudioSource(new MediaConstraints());
        audioTrackLocal=factory.createAudioTrack("ARDAMSa0",audioSource);
    }


    public void addRemoteVideoTrack(VideoTrack v){
        videoTrackRemote=v;
        v.addSink(surfaceRemote);
    }

    public void addRemoteAudioTrack(AudioTrack v){
        audioTrackRemote=v;
    }

    public void StopCall() throws InterruptedException {
        if(wrapper!=null) {
            if (videoTrackRemote != null) {
                videoTrackRemote.removeSink(surfaceRemote);
                videoTrackRemote.setEnabled(false);

                if (surfaceRemote != null) {
                    surfaceRemote.release();
                }
            }
            if (videoTrackFromCamera != null) {
                videoTrackFromCamera.removeSink(surfaceLocal);
                videoTrackFromCamera.setEnabled(false);

                if (surfaceLocal != null)
                    surfaceLocal.release();
            }

            if (audioTrackRemote != null){
                audioTrackRemote.setEnabled(false);
                audioTrackLocal.setEnabled(false);

                if(wrapper!=null)
                    wrapper.removeTrack(audioTrackRemote);
            }

            //ChatApplication.RootEglBase=EglBase.create();

            //wrapper.removeTrack(videoTrackRemote);
            //wrapper.removeTrack(videoTrackFromCamera);

            //wrapper.removeStream();




//            if(surfaceLocal!=null)
//                surfaceLocal.release();
//            if(videoTrackFromCamera!=null){
//                if(wrapper!=null)
//                    wrapper.removeTrack(videoTrackFromCamera);
//                videoTrackFromCamera.dispose();
//            }
//
            if(videoCapturer!=null)
                videoCapturer.stopCapture();
                //videoCapturer.dispose();
//
//            if(surfaceRemote!=null)
//                surfaceRemote.release();
//
//            if(videoTrackRemote!=null){
//                if(wrapper!=null)
//                    wrapper.removeTrack(videoTrackRemote);
//                videoTrackRemote.dispose();
//            }
//
//            if(wrapper!=null)
//                wrapper.removeStream();
        }
        finish();
    }
}
