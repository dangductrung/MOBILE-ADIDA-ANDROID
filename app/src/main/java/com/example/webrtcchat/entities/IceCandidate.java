package com.example.webrtcchat.entities;

public class IceCandidate {
    public String sdp;
    public String sdpMid;
    public int sdpMLineIndex;
    public String uuid;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public IceCandidate() {
    }

    //JSONObject iceCandidate,
    public IceCandidate(String uuid,int sdpMLineIndex,String sdpMid,String sdp) {
        this.uuid=uuid;
        this.sdpMLineIndex=sdpMLineIndex;
        this.sdpMid=sdpMid;
        this.sdp=sdp;
    }
}
