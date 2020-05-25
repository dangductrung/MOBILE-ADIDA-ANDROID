package com.example.webrtcchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.webrtcchat.chatui.DefaultDialogsActivity;
import com.example.webrtcchat.sharepref.SharePref;

import org.webrtc.Camera2Enumerator;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    private EglBase rootEglBase;
    //Video track from camera
    private VideoTrack videoTrackFromCamera;

    //Activity views
    private SurfaceViewRenderer surfaceLocal;
    private SurfaceViewRenderer surfaceRemote;

    //WebRTC Peer connection instance
    //private WebRTCPeerConnection peerConnection;
    public TextView txtChatBox;
    public EditText txtToSend;
    public Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootEglBase = EglBase.create();

        //peerConnectionFactory= ChatApplication.getInstance().getPeerConnectionFactory();

        //Find and initial surface
        surfaceLocal= (SurfaceViewRenderer)findViewById(R.id.surfaceLocal);
        surfaceRemote= (SurfaceViewRenderer)findViewById(R.id.surfaceRemote);
        txtChatBox= (TextView) findViewById(R.id.txtChatBox);
        txtToSend= (EditText) findViewById(R.id.txtToSend);
        btnSend= (Button) findViewById(R.id.btnChatSend);
        initializeSurfaceViews();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String textToSend=txtToSend.getText().toString();
//
//                if(!textToSend.isEmpty()){
//                    peerConnection.sendMessage(textToSend);
//                }
            }
        });

        //Create local video track
        //createLocalVideoTrack();
        //Start streaming local video
        //peerConnection.startStreamingVideo(videoTrackFromCamera);
    }

    private void initializeSurfaceViews() {
        surfaceLocal=Utils.InitSurfaceViewRenderer(surfaceLocal,rootEglBase);
        surfaceRemote=Utils.InitSurfaceViewRenderer(surfaceRemote,rootEglBase);
    }

//    private void createLocalVideoTrack() {
//        VideoCapturer videoCapturer = Utils.createVideoCapturer(this);
//        videoTrackFromCamera = createVideoTrack(videoCapturer,rootEglBase);
//        videoTrackFromCamera.setEnabled(true);
//        //Add the local surface as a sink of the video track
//        videoTrackFromCamera.addSink(surfaceLocal);
//    }

    //Create a video track
//    public VideoTrack createVideoTrack(VideoCapturer videoCapturer, EglBase eglBase){
//        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
//        VideoSource videoSource =peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
//        videoCapturer.initialize(surfaceTextureHelper, this.getApplicationContext(), videoSource.getCapturerObserver());
//        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
//
//        return peerConnectionFactory.createVideoTrack("ARMDAMSv0", videoSource);
//    }
}
