package com.SilentMode.shh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.*;



public class MainActivity extends AppCompatActivity {

    private Button Settingbtn;
    private Switch Activatesw;
    int accessEnabled = 0;

    final String TAG = MainActivity.class.getSimpleName();
    RewardedVideoAd rewardedVideoAd = null;
    InterstitialAd interstitialAd;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        setContentView(R.layout.activity_main);
        rewardedVideoAd = new RewardedVideoAd(this, "1522771468195436_1522772914861958");
        //AdSettings.addTestDevice("191908ff-89f5-44ff-8998-562b1d7898fc");
        interstitialAd = new InterstitialAd(this, "1522771468195436_1537821106690472");

        Activatesw = findViewById(R.id.Activate);

        try {
            accessEnabled = Settings.Secure.getInt(this.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "accessibilityEnabled = " + accessEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }

        if (accessEnabled == 0) {
            // if not construct intent to request permission
           Activatesw.setChecked(false);
           Activatesw.setThumbTintList(getColorStateList(R.color.red));
        } else {
            // if not construct intent to request permission
            Activatesw.setChecked(true);
            Activatesw.setThumbTintList(getColorStateList(R.color.green));
        }

        Settingbtn = findViewById(R.id.Setting);
        Settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog;
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                alertDialog.setTitle("HINT");
                if (accessEnabled == 0) {
                alertDialog.setMessage("This application requires the Accessibility service to be turn on to filter key events and mute/unmute the phone them to needed actions. When you tap the Continue button, Accessibility settings will be opened. You should find Buttons 'SHH ! Silent - Mute the phone' field and enable it. \n\n-> Watch an ad to activate the feature");
                } else {
                    alertDialog.setMessage("To disable mute/unmute the phone for key events turn off 'SHH : (silent) - Mute the phone' setting on the next screen. \n\n-> Watch an ad to deactivate the feature");
                }
                alertDialog.setCancelable(false);

                alertDialog.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (!isNetworkAvailable())
                        {
                            if (!checkAccessibilityPermission()) {
                                Toast.makeText(MainActivity.this, "Permission Setting", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {

                            @Override
                            public void onError(Ad ad, AdError error) {
                                // Rewarded video ad failed to load
                                InterstialAfficher();
                                Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());

                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Rewarded video ad is loaded and ready to be displayed
                                rewardedVideoAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Rewarded video ad clicked
                                Log.d(TAG, "Rewarded video ad clicked!");
                                if (!checkAccessibilityPermission()) {
                                    Toast.makeText(MainActivity.this, "Enable/Disable Permission", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Rewarded Video ad impression - the event will fire when the
                                // video starts playing
                                Log.d(TAG, "Rewarded video ad impression logged!");
                            }

                            @Override
                            public void onRewardedVideoCompleted() {
                                // Rewarded Video View Complete - the video has been played to the end.
                                // You can use this event to initialize your reward
                                Log.d(TAG, "Rewarded video completed!");
                                if (!checkAccessibilityPermission()) {
                                    Toast.makeText(MainActivity.this, "Enable/Disable Permission", Toast.LENGTH_SHORT).show();
                                }

                                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
                                    finish();
                                }

                                // Call method to give reward
                                // giveReward();
                            }

                            @Override
                            public void onRewardedVideoClosed() {
                                // The Rewarded Video ad was closed - this can occur during the video
                                // by closing the app, or closing the end card.
                                Log.d(TAG, "Rewarded video ad closed!");
                                Toast.makeText(MainActivity.this, "Video are closed, Show another Video until the end ! ", Toast.LENGTH_SHORT).show();

                            }
                        };
                        rewardedVideoAd.loadAd(
                                rewardedVideoAd.buildLoadAdConfig()
                                        .withAdListener(rewardedVideoAdListener)
                                        .build());




                    }
                });

                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog = alertDialog.create();
                dialog.show();

            }

        });

    }

    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    public boolean checkAccessibilityPermission () {

        if (accessEnabled == 0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        } else {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void InterstialAfficher(){
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                if (!checkAccessibilityPermission()) {
                    Toast.makeText(MainActivity.this, "Enable/Disable Permission", Toast.LENGTH_SHORT).show();
                }

                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
                    finish();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                if (!checkAccessibilityPermission()) {
                    Toast.makeText(MainActivity.this, "Enable/Disable Permission", Toast.LENGTH_SHORT).show();
                }

                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
                    finish();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
                if (!checkAccessibilityPermission()) {
                    Toast.makeText(MainActivity.this, "Enable/Disable Permission", Toast.LENGTH_SHORT).show();
                }

                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
                    finish();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

}
