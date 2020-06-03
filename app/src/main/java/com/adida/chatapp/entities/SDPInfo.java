package com.adida.chatapp.entities;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SDPInfo {
    public enum MediaType {
        TEXT,
        VIDEO,
        AUDIO
    }

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