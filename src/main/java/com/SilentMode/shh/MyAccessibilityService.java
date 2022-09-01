package com.SilentMode.shh;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MyAccessibilityService extends AccessibilityService {

    private Timer mDoubleClickTimer;
    private boolean possibleDoubleClick = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();


        // the service listens for both pressing and releasing the key
        // so the below code executes twice, i.e. you would encounter two Toasts
        // in order to avoid this, we wrap the code inside an if statement
        // which executes only when the key is released
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if(mDoubleClickTimer!=null) {mDoubleClickTimer.cancel();}
                if(!possibleDoubleClick){
                    possibleDoubleClick = true;
                    mDoubleClickTimer = new Timer();
                    mDoubleClickTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //single click detected
                            //handle it here
                            possibleDoubleClick = false;
                        }
                    },300);
                }else{
                    //double click detected
                    Log.d("Check", " Double Tap KeyUp");
                    Toast.makeText(this, "DOUBLE TAP KeyUp - Sound Mode", Toast.LENGTH_SHORT).show();
                    AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 400 milliseconds
                    v.vibrate(400);
                    //handle it here
                    possibleDoubleClick = false;
                }
//... other key processing if you need it
                return false;

                }

            else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if(mDoubleClickTimer!=null) {mDoubleClickTimer.cancel();}
                if(!possibleDoubleClick){
                    possibleDoubleClick = true;
                    mDoubleClickTimer = new Timer();
                    mDoubleClickTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //single click detected
                            //handle it here
                            possibleDoubleClick = false;
                        }
                    },300);
                }else{
                    //double click detected
                    Log.d("Check", "DOUBLE TAP KeyDown");
                    Toast.makeText(this, "DOUBLE TAP KeyDown - Silent Mode", Toast.LENGTH_LONG).show();
                    AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                    //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 400 milliseconds
                    //v.vibrate(800);
                    //handle it here
                    possibleDoubleClick = false;
                }
//... other key processing if you need it
                return false;
            }

        }
        return super.onKeyEvent(event);
    }
}