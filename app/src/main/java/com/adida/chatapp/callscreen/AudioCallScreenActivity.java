package com.adida.chatapp.callscreen;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.User;
import com.adida.chatapp.extendapplication.ChatApplication;
import com.adida.chatapp.webrtc_connector.RTCPeerConnectionWrapper;
import com.adida.chatapp.webrtc_connector.Utils;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class AudioCallScreenActivity extends AppCompatActivity {
    public static void open(Context context, String userId,boolean isAnswer) {
        Intent actCall = new Intent(context, AudioCallScreenActivity.class);
        actCall.putExtra("remoteUserId",userId);
        actCall.putExtra("type",isAnswer);
        context.startActivity(actCall);
    }

    public static void open(Context context, String userId,String sdp,boolean isAnswer) {
        Intent actCall = new Intent(context, AudioCallScreenActivity.class);
        actCall.putExtra("remoteUserId",userId);
        actCall.putExtra("sdp",sdp);
        actCall.putExtra("type",isAnswer);
        context.startActivity(actCall);
    }

    private Button btnEndCall;

    AudioTrack audioTrackLocal;
    AudioTrack audioTrackRemote;

    RTCPeerConnectionWrapper wrapper;
    private String remoteUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        btnEndCall=findViewById(R.id.btnEndCall);

        Bundle b = getIntent().getExtras();
        String extrasUserId=b.getString("remoteUserId");
        remoteUserId=extrasUserId;
        String sdp=b.getString("sdp");
        boolean isAnswer=b.getBoolean("type");

        createLocalAudioTrack();

        if(!ChatApplication.getInstance().getUserPeerConnections().containsKey(extrasUserId)){
            wrapper= new RTCPeerConnectionWrapper(extrasUserId,this);
            wrapper.startDataChannel();
            ChatApplication.getInstance().getUserPeerConnections().put(extrasUserId,wrapper);
        }
        else{
            wrapper= ChatApplication.getInstance().getUserPeerConnections().get(extrasUserId);
            wrapper.resetPeerConnection();
            wrapper.startDataChannel();
        }

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StopCall();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        wrapper.setCallContext(this);
        wrapper.addTrack(audioTrackLocal);

        if(isAnswer){
            wrapper.setRemoteDescription(sdp);
            wrapper.createAnswer();
        }
        else{
            wrapper.createOffer();
        }
    }

    private void createLocalAudioTrack(){
        PeerConnectionFactory factory= ChatApplication.getInstance().getPeerConnectionFactory();
        AudioSource audioSource=factory.createAudioSource(new MediaConstraints());
        audioTrackLocal=factory.createAudioTrack("ARDAMSa0",audioSource);
    }


    public void StopCall() throws InterruptedException {
        if(wrapper!=null){
            if(audioTrackRemote!=null)
                audioTrackRemote.setEnabled(false);
        }
        finish();
    }

    public void addRemoteAudioTrack(AudioTrack v){
        audioTrackRemote=v;
    }
}

