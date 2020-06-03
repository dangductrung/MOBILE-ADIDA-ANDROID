package com.adida.chatapp.extendapplication;

import android.content.Context;

import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;

import java.util.HashMap;

public class ChatApplication {

    private static ChatApplication instance;

    private static PeerConnectionFactory peerConnectionFactory;

    public static EglBase RootEglBase;

    public static ChatApplication getInstance(){
        if (instance == null) {
            instance = new ChatApplication();
        }
        return instance;
    }

    public static void initPeerConnectionFactory(Context activityContext){
        EglBase rootEglBase = EglBase.create();
        RootEglBase=rootEglBase;

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
        connections= new HashMap<String, RTCPeerConnectionWrapper>();
    }

    public void clearConnection() {
        connections= new HashMap<String, RTCPeerConnectionWrapper>();
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
