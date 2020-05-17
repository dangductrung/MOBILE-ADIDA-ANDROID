package com.example.webrtcchat;

import android.app.Activity;
import android.content.Context;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;

public class WebRTCPeerConnectionFactory {
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    PeerConnectionFactory factory;
    Context appContext;

    public WebRTCPeerConnectionFactory(Context context, EglBase rootEglBase) {
        appContext=context;

        PeerConnectionFactory.InitializationOptions initOptions=
                PeerConnectionFactory
                        .InitializationOptions.builder(context)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initOptions);

        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        PeerConnectionFactory.Options peerConnectionFactoryOptions= new PeerConnectionFactory.Options();

        factory = PeerConnectionFactory.builder()
                .setOptions(peerConnectionFactoryOptions)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
    }

    //Create a video track
    public VideoTrack createVideoTrack(Context activityContext, VideoCapturer videoCapturer, EglBase eglBase){
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, activityContext, videoSource.getCapturerObserver());
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        return factory.createVideoTrack("ARMDAMSv0", videoSource);
    }

    //build a WebRTCPeerConnection
    public WebRTCPeerConnection builder() throws URISyntaxException {
        return new WebRTCPeerConnection(appContext,factory);
    }
}
