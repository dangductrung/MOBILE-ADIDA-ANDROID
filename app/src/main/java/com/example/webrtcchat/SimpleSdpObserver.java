package com.example.webrtcchat;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

//Simple SdpObserver used setting description
public class SimpleSdpObserver implements SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
    }

    @Override
    public void onSetSuccess() {
    }

    @Override
    public void onCreateFailure(String s) {
    }

    @Override
    public void onSetFailure(String s) {
    }

}


