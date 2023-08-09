package com.djax.sdkaddemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.sdk.adserver.Listener.RewardedAdListener;
import com.ad.sdk.adserver.Rewardedvideo;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;

public class RewardActivity extends Activity implements RewardedAdListener, IUnityAdsInitializationListener {

    int current_points = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        getPoint();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                new Rewardedvideo().loadRewardedAd(RewardActivity.this, RewardActivity.this);
            }
        }, 3000);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RewardActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void Rewarded(String rewardItem, int rewardvalue) {

        System.out.println("@@ rewardvalue : " + rewardvalue);
        SharedPreferences sharedPreferences = getSharedPreferences("MediationRewardPoint", MODE_PRIVATE);
        int point = sharedPreferences.getInt("Point", 0);
        int c = rewardvalue + point;
        addPoint(c);
        ((TextView) findViewById(R.id.coins)).setText("" + c);


        Toast.makeText(getApplicationContext(), "Reward Points" + rewardvalue, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void AdLoaded() {

        System.out.println("@@ start video Ad");


    }

    @Override
    public void AdFailed() {

    }

    @Override
    public void Adclosed() {

    }

    @Override
    public void Adclicked() {

    }

    @Override
    public void Adshown() {

    }

    void addPoint(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("RewardPoint", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Point", i);
        editor.commit();

    }

    void getPoint() {
        SharedPreferences sharedPreferences = getSharedPreferences("RewardPoint", MODE_PRIVATE);
        int point = sharedPreferences.getInt("Point", 0);

        int c = current_points + point;
        // addPoint(c);
        ((TextView) findViewById(R.id.coins)).setText("" + c);
    }


    @Override
    public void onInitializationComplete() {
        Log.e("UnityAdsExample", "Unity Ads initialization Successfully");

    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);

    }
}