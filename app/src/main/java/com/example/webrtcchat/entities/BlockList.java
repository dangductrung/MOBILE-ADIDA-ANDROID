package com.example.webrtcchat.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class BlockList {
    public HashMap<String, String> params;
    BlockList() {
        params = new HashMap<String, String>();
    }

    BlockList(HashMap<String, String> params) {
        this.params = params;
    }

}
