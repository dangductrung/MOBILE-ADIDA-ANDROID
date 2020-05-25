package com.example.webrtcchat.sharepref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.webrtcchat.keys.StringKeys;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;

import java.util.HashMap;

public class SharePref {
    private static SharePref sharePref;
    public static SharePref getInstance(Context context) {
        if (sharePref == null) {
            sharePref = new SharePref(context);
        }
        return sharePref;
    }
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharePref(Context context) {
        this.context = context;
    }

    private SharePref() {}

    public String getUuid() {
        sharedPreferences = context.getSharedPreferences(StringKeys.SPrefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(StringKeys.uuid, null);
    }

    public void setUuid(String uuid) {
        sharedPreferences = context.getSharedPreferences(StringKeys.SPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(StringKeys.uuid, uuid).commit();
    }
}
