package com.example.webrtcchat;

import android.app.Application;
import android.content.Context;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;

import java.util.HashMap;

public class ChatApplication extends Application {

    private static Context context;

    private static PeerConnectionFactory peerConnectionFactory;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  instance = this;
        context = getApplicationContext();
    }

    public static ChatApplication getInstance(){
        return (ChatApplication)context;
    }

    public static void initPeerConnectionFactory(Context activityContext){
        EglBase rootEglBase = EglBase.create();

        PeerConnectionFactory.InitializationOptions initOptions=
                PeerConnectionFactory
                        .InitializationOptions.builder(activityContext)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initOptions);

        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        PeerConnectionFactory.Options peerConnectionFactoryOptions= new PeerConnectionFactory.Options();

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(peerConnectionFactoryOptions)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
    }

    private HashMap<String, RTCPeerConnectionWrapper> connections;
    {
        connections= new HashMap<>();
    }

    public PeerConnectionFactory getPeerConnectionFactory()
    {
        return peerConnectionFactory;
    }

    public HashMap<String,RTCPeerConnectionWrapper> getUserPeerConnections()
    {
        return connections;
    }
}
