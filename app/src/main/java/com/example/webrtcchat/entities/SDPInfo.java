package com.example.webrtcchat.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONObject;

@IgnoreExtraProperties
public class SDPInfo {
    public String description;
    //public JSONObject iceCandidate;
    public String uuid;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public SDPInfo() {
    }

    //JSONObject iceCandidate,
    public SDPInfo(String description, String uuid) {
        this.description = description;
        //this.iceCandidate = iceCandidate;
        this.uuid = uuid;
    }
}