package com.adida.chatapp.callscreen;

import androidx.appcompat.app.AppCompatActivity;
import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.adida.chatapp.webrtc_connector.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class CallScreenActivity extends AppCompatActivity {

    public static void open(Context context, User user,boolean isAnswer) {
        Intent actCall = new Intent(context, CallScreenActivity.class);
        actCall.putExtra("remoteUserId",user.uuid);
        actCall.putExtra("type",isAnswer);
        context.startActivity(actCall);
    }

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

        RTCPeerConnectionWrapper wrapper;



        if(!ChatApplication.getInstance().getUserPeerConnections().containsKey(extrasUserId)){
            wrapper= new RTCPeerConnectionWrapper(extrasUserId,this);
            ChatApplication.getInstance().getUserPeerConnections().put(extrasUserId,wrapper);
        }
        else{
            wrapper= ChatApplication.getInstance().getUserPeerConnections().get(extrasUserId);
        }

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick",wrapper.getConnectionState());
            }
        });

        wrapper.setCallContext(this);
        wrapper.addTrack(videoTrackFromCamera);
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

        PeerConnectionFactory factory= ChatApplication.getInstance().getPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFromCamera=factory.createVideoTrack(remoteUserId, videoSource);

        videoTrackFromCamera.setEnabled(true);
        videoTrackFromCamera.addSink(surfaceLocal);
    }


    public void addRemoteVideoTrack(VideoTrack v){
        v.addSink(surfaceRemote);
    }
}
