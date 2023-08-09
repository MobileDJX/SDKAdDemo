package com.ad.sdk.adserver;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ad.sdk.adserver.Listener.RewardedAdListener;
import com.ad.sdk.mtrack.Device_settings;
import com.ad.sdk.utils.LoadData;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Rewarded;
import com.chartboost.sdk.callbacks.RewardedCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.DismissEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.RewardEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.UnityAds;

public class Rewardedvideo implements IUnityAdsInitializationListener, RewardedCallback {

    RewardedAd rewardedAd;

    String rewardedItem;
    int rewardAmount;


    public static final String TAG = "RewardedVideo";
    public static RewardedAdListener rewardadListener = null;


    //ChartBoost
    private Rewarded chartboostRewarded = null;


    public RewardedAdListener getRewardadListen() {
        return rewardadListener;
    }

    public void setRewardadListen(RewardedAdListener rewardadListen) {
        this.rewardadListener = rewardadListen;
    }

    public Rewardedvideo() {
    }

    public void loadRewardedAd(Context context, RewardedAdListener listener) {
        setRewardadListen(listener);
        try {
            Activity activity = (Activity) context;
            SharedPreferences sharedPreferencesMediation = context.getSharedPreferences("MediationRewardedVideo", MODE_PRIVATE);
            String ad_unit = sharedPreferencesMediation.getString("Mediation_adunit", "");
            String ad_network_type = sharedPreferencesMediation.getString("Mediation_ad_network_type", "");
            String app_id = sharedPreferencesMediation.getString("Mediation_app_id", "");
            String app_signature = sharedPreferencesMediation.getString("Mediation_app_signature", "");
            String gameID = sharedPreferencesMediation.getString("Mediation_app_gameId", "");
            String placementId = sharedPreferencesMediation.getString("Mediation_app_placementId", "");
            String testMode = sharedPreferencesMediation.getString("Mediation_app_testMode", "");
            String zoneId = sharedPreferencesMediation.getString("Mediation_app_zoneid", "");


            Log.e("@@", "Ad_Unit :" + ad_unit);
            Log.e("@@", "Ad_network_type :" + ad_network_type);

            if (new LoadData().getMediationNetworkStatus(context).equalsIgnoreCase("mediation")) {

                if (ad_network_type.equalsIgnoreCase("AdMob")) {
                    loadAdMobAd(context, activity, ad_unit);
                }

                if (ad_network_type.equalsIgnoreCase("Adcolony")) {

                    loadAdColony(context, app_id, zoneId);
                }

                if (ad_network_type.equalsIgnoreCase("ChartBoost")) {
                    loadChartBoost(context, app_id, app_signature);
                }

                if (ad_network_type.equalsIgnoreCase("IronSource")) {

                    loadIronSource(context, app_id);
                }

                if (ad_network_type.equalsIgnoreCase("Unity")) {

                    loadUnity(context, gameID, placementId, testMode);
                }
            } else {
                SharedPreferences sharedPreferences = context.getSharedPreferences("RewardedVideo", MODE_PRIVATE);
                String ad_url = sharedPreferences.getString("RewardedVideo_URL", "");

                System.out.println("@@ RewardedVideo_URL ad_url " + ad_url);
                if (ad_url.length() > 0) {
                    Intent i = new Intent(context, RewardedLoadActivity.class);
                    i.putExtra("Lis", String.valueOf(listener));
                    context.startActivity(i);
                } else {
                    Log.d("SDK", "No Ads:");
                }
            }


        } catch (Exception e) {
            Log.d("SDK", "Rewardedvideo Ad Exception:" + e);
        }
    }




    //AdMob Rewarded
    private void loadAdMobAd(Context context, Activity activity, String AD_UNIT_ID) {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "Initialize Completed.");
            }
        });

        if (rewardedAd == null) {

            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load((Activity) context, AD_UNIT_ID, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAds) {
                    Rewardedvideo.this.rewardedAd = rewardedAds;

                    Log.d(TAG, "onAdLoaded");

                    rewardedAds.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            Log.e("Rewarded Item :", " " + rewardItem.getType());
                            Log.e("Rewarded Amount :", " " + rewardItem.getAmount());

                            rewardedItem = rewardItem.getType();
                            rewardAmount = rewardItem.getAmount();

                            Rewardedvideo.rewardadListener.Rewarded(rewardedItem, rewardAmount);

                            SharedPreferences sharedPreferences = context.getSharedPreferences("MediationRewardPoint", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Point", rewardItem.getAmount());
                            editor.apply();

                        }
                    });

                    if (rewardAmount != 0) {
                        Toast.makeText(context, "Reward Amount : " + rewardAmount, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    Log.d(TAG, loadAdError.getMessage());

                    SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                    String zone_id = sharedPreferences.getString("Zone_ID", "");

                    Device_settings.getSettings(context).mediation = "0";

                    com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                    adv1.setZoneid(zone_id);
                    adv1.LoadAd(adv1);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new Rewardedvideo().loadRewardedAd(context, (RewardedAdListener) context);
                            Device_settings.getSettings(context).mediation = "1";
                        }
                    }, 1500);

                }
            });


        }
    }




    //AdColony
    public void loadAdColony(Context context, String APP_ID, String Rewarded_Zone_ID) {

        AdColony.configure((Activity) context, APP_ID, Rewarded_Zone_ID);

        final AdColonyInterstitial[] ad = new AdColonyInterstitial[1];

        AdColony.setRewardListener(new AdColonyRewardListener() {
            @Override
            public void onReward(@androidx.annotation.NonNull AdColonyReward adColonyReward) {

            }
        });

        AdColonyInterstitialListener listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {

                Log.d(TAG, "Adcolony Request Status :" + "Filled");


                ad[0] = adColonyInterstitial;
                ad[0].show();
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);

                Log.d(TAG, "Adcolony Request Status :" + "Not Filled");

                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Rewardedvideo().loadRewardedAd(context, (RewardedAdListener) context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }
        };
        AdColony.requestInterstitial(Rewarded_Zone_ID, listener, null);

    }



    //IronSource
    public void loadIronSource(Context context, String APP_ID) {

        IronSource.init((Activity) context, APP_ID);

        IronSource.loadRewardedVideo();

        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewardedVideoAvailabilityChanged(boolean b) {

            }

            @Override
            public void onRewardedVideoAdStarted() {

                Log.d(TAG, "IronSource Video Status :" + "Started");


            }

            @Override
            public void onRewardedVideoAdEnded() {

            }

            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {

            }

            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {


                Log.d(TAG, "IronSource Video Status :" + "Load Failed");

                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Rewardedvideo().loadRewardedAd(context, (RewardedAdListener) context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);

            }

            @Override
            public void onRewardedVideoAdClicked(Placement placement) {

            }
        });

        IronSource.showRewardedVideo();

    }



    //Unity
    public void loadUnity(Context context, String unityGameID, String adUnitId, String testMode) {

        IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                UnityAds.show((Activity) context, adUnitId);
            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);


                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Rewardedvideo().loadRewardedAd(context, (RewardedAdListener) context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }
        };
        UnityAds.initialize(context, unityGameID, Boolean.parseBoolean(testMode), (IUnityAdsInitializationListener) context);

        UnityAds.load(adUnitId, loadListener);

    }



    //ChartBoost
    public void loadChartBoost(Context context, String appID, String appSignature) {

        Chartboost.startWithAppId(context, appID, appSignature, startError -> {
            if (startError == null) {
                Log.i("ChartBoost Status", "ChartBoost SDK is initialized Successfully");
            } else {
                Log.i("ChartBoost Status", "SDK initialized with error:" + startError.getCode().name());

                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Rewardedvideo().loadRewardedAd(context, (RewardedAdListener) context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }
        });

        chartboostRewarded = new Rewarded("start", this, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostRewarded.cache();
            }
        }, 1000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostRewarded.show();
            }
        }, 5500);


    }





    @Override
    public void onInitializationComplete() {

    }


    //ChartBoost CallBacks...
    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {

    }

    @Override
    public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {

    }

    @Override
    public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {

    }

    @Override
    public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {

    }

    @Override
    public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {

    }

    @Override
    public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {

    }

    @Override
    public void onAdDismiss(@NonNull DismissEvent dismissEvent) {

    }

    @Override
    public void onRewardEarned(@NonNull RewardEvent rewardEvent) {

    }
}
