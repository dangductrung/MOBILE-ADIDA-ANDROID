package com.example.webrtcchat;

import android.content.Context;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

public class Utils {
    public static SurfaceViewRenderer InitSurfaceViewRenderer(SurfaceViewRenderer svr, EglBase elg){
        svr.init(elg.getEglBaseContext(), null);
        svr.setEnableHardwareScaler(true);
        svr.setMirror(true);

        return svr;
    }

    public static VideoCapturer createVideoCapturer(Context context) {
        VideoCapturer videoCapturer;
        if (useCamera2(context)) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(context));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    public static VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private static boolean useCamera2(Context context) {
        return Camera2Enumerator.isSupported(context);
    }
}
