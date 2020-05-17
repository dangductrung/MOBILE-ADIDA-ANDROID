package com.example.webrtcchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.webrtc.Camera2Enumerator;
import org.webrtc.EglBase;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private EglBase rootEglBase;
    //Video track from camera
    private VideoTrack videoTrackFromCamera;

    //Activity views
    private SurfaceViewRenderer surfaceLocal;
    private SurfaceViewRenderer surfaceRemote;

    //WebRTC Peer connection instance
    private WebRTCPeerConnectionFactory factory;
    private WebRTCPeerConnection peerConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootEglBase = EglBase.create();

        //Find and initial surface
        surfaceLocal= (SurfaceViewRenderer)findViewById(R.id.surfaceLocal);
        surfaceRemote= (SurfaceViewRenderer)findViewById(R.id.surfaceRemote);
        initializeSurfaceViews();

        //Crete peer connection factory
        factory= new WebRTCPeerConnectionFactory(this,rootEglBase);
        //Create peer connection instance
        try {
            peerConnection= factory.builder();
        } catch (URISyntaxException e) {
        }

        //Create local video track
        createLocalVideoTrack();
        //Start streaming local video
        peerConnection.startStreamingVideo(videoTrackFromCamera);
    }

    private void initializeSurfaceViews() {
        surfaceLocal=Utils.InitSurfaceViewRenderer(surfaceLocal,rootEglBase);
        surfaceRemote=Utils.InitSurfaceViewRenderer(surfaceRemote,rootEglBase);
    }

    private void createLocalVideoTrack() {
        VideoCapturer videoCapturer = Utils.createVideoCapturer(this);
        videoTrackFromCamera =factory.createVideoTrack(getApplicationContext(),videoCapturer,rootEglBase);
        videoTrackFromCamera.setEnabled(true);
        //Add the local surface as a sink of the video track
        videoTrackFromCamera.addSink(surfaceLocal);
    }
}
