package com.ad.sdk.adserver;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ad.sdk.mtrack.Device_settings;
import com.ad.sdk.utils.LoadData;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Interstitial;
import com.chartboost.sdk.callbacks.InterstitialCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.DismissEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InitializationListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InterstitialVideo {
  /*  PopupWindow pop;
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private ImaAdsLoader adsLoader;*/


    private InterstitialAd mInterstitialAd;
    private static final String TAG = "Interstitial_Video";


    //AdColony
    AdColonyInterstitial adColonyInterstitial;
    boolean isInterstitialLoaded;
    AdColonyInterstitialListener adColony_listener;
    AdColonyAdOptions adColony_adOptions;

    //ChartBoost
    private Interstitial chartboostInterstitial = null;

    int adRefreshCount = 0;
    String click, imp, request;


    public InterstitialVideo() {


    }

    public void loadAdShow(Context context) {
        try {


            Activity activity = (Activity) context;
            SharedPreferences sharedPreferences = context.getSharedPreferences("MediationInterstitialVideo", MODE_PRIVATE);
            String ad_unit = sharedPreferences.getString("Mediation_adunit", "");
            String ad_network_type = sharedPreferences.getString("Mediation_ad_network_type", "");
            String app_id = sharedPreferences.getString("Mediation_app_id", "");
            String app_signature = sharedPreferences.getString("Mediation_app_signature", "");
            String gameID = sharedPreferences.getString("Mediation_app_gameId", "");
            String placementId = sharedPreferences.getString("Mediation_app_placementId", "");
            String testMode = sharedPreferences.getString("Mediation_app_testMode", "");
            String zoneId = sharedPreferences.getString("Mediation_app_zoneid", "");
            click = sharedPreferences.getString("Mediation_click", "");
            imp = sharedPreferences.getString("Mediation_impression", "");
            request = sharedPreferences.getString("Mediation_request", "");

            if (new LoadData().getMediationNetworkStatus(context).equalsIgnoreCase("mediation")) {

                if (ad_network_type.equalsIgnoreCase("AdMob")) {
                    MediationTracking(context, request);

                    new InterstitialVideo().loadInterstitialVideoMediationAdMob(context, activity, ad_unit, imp, click);


                } else if (ad_network_type.equalsIgnoreCase("Adcolony")) {


                    new InterstitialVideo().loadInterstitialImageMediation_AdColony(context, app_id, zoneId, imp, click, request);


                } else if (ad_network_type.equalsIgnoreCase("IronSource")) {


                    new InterstitialVideo().loadInterstitial_IronSource(context, app_id, imp, click, request);


                } else if (ad_network_type.equalsIgnoreCase("Unity")) {

                    MediationTracking(context, request);
                    new InterstitialVideo().loadUnityInterstitial(context, gameID, placementId, testMode, imp, click);


                } else if (ad_network_type.equalsIgnoreCase("ChartBoost")) {
                    MediationTracking(context, request);
                    new InterstitialVideo().loadChartBoost(context, app_id, app_signature, imp, click, request);


                }
            } else {

                SharedPreferences sharedPreferences2 = context.getSharedPreferences("InterstitialVideo", MODE_PRIVATE);
                String ad_url = sharedPreferences2.getString("InterstitialVideo_URL", "");

                if (ad_url.length() > 0) {
                    Intent i = new Intent(context, LoadActivity.class);
                    context.startActivity(i);
                } else {
                    Log.d("SDK", "No Ads");
                }
            }


        } catch (Exception e) {
            Log.d("SDK", "InterstitialVideo Ad Exception:" + e);
        }


    }


    //AdMob
    public void loadInterstitialVideoMediationAdMob(Context context, Activity activity, String adunit, String imp, String click) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e(TAG, "AdMob Initialize Status :" + "Completed");
                Log.e(TAG, "Impression URL  :" + imp);
                MediationTracking(context, imp);
            }
        });

        com.google.android.gms.ads.AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.load(context, adunit, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;

                Log.i(TAG, "onAdLoaded");
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.e(TAG, "Status :" + "Ad Clicked...");
                        Log.e(TAG, "Click URL  :" + click);
                        MediationTracking(context, click);
                    }

                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d(TAG, loadAdError.toString());
                mInterstitialAd = null;

                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new InterstitialVideo().loadAdShow(context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }
        });
    }


    //AdColony
    public void loadInterstitialImageMediation_AdColony(Context context, String APP_ID, String INTERSTITIAL_ZONE_ID, String imp, String click, String request) {

        MediationTracking(context, request);

        final String[] AD_UNIT_Zone_IDS = new String[]{INTERSTITIAL_ZONE_ID};

        AdColonyAppOptions appOptions = new AdColonyAppOptions().setKeepScreenOn(true);
        AdColony.configure((Activity) context, appOptions, APP_ID, AD_UNIT_Zone_IDS);


        adColony_listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial adIn) {


                adColonyInterstitial = adIn;

                adRefreshCount = 0;

                isInterstitialLoaded = true;

                adColonyInterstitial.show();

                MediationTracking(context, imp);


            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);

                Log.e("Adcolony Ad Status", "Interstitial Ad Is Not Loaded Yet or Request Not Filled");


                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new InterstitialVideo().loadAdShow(context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }

            @Override
            public void onOpened(AdColonyInterstitial ad) {
                super.onOpened(ad);
            }

            @Override
            public void onClosed(AdColonyInterstitial ad) {
                super.onClosed(ad);
                //request New Interstitial
            }

            @Override
            public void onClicked(AdColonyInterstitial ad) {
                super.onClicked(ad);

                adRefreshCount = 0;
                MediationTracking(context, click);
            }

            @Override
            public void onLeftApplication(AdColonyInterstitial ad) {
                super.onLeftApplication(ad);
            }

            @Override
            public void onExpiring(AdColonyInterstitial ad) {
                super.onExpiring(ad);
            }
        };

        adColony_adOptions = new AdColonyAdOptions();
        AdColony.requestInterstitial(INTERSTITIAL_ZONE_ID, adColony_listener, adColony_adOptions);

    }


    //IronSource
    public void loadInterstitial_IronSource(Context context, String AD_ID, String imp, String click, String request) {

        MediationTracking(context, request);

        IronSource.init((Activity) context, AD_ID, new InitializationListener() {
            @Override
            public void onInitializationComplete() {
                Log.e(TAG, "Iron Source : " + " Initialize Status : " + "Successfully..");
            }
        }, IronSource.AD_UNIT.INTERSTITIAL);


        IronSource.loadInterstitial();

        InterstitialListener interstitialListener = new InterstitialListener() {

            @Override
            public void onInterstitialAdReady() {
                Log.e(TAG, "Iron Source : " + " AD Ready... ");
                IronSource.showInterstitial();

            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                Log.e(TAG, "Iron Source : " + " AD Shown Failed... ");

                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new InterstitialVideo().loadAdShow(context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);


            }

            @Override
            public void onInterstitialAdOpened() {

            }

            @Override
            public void onInterstitialAdClosed() {

            }

            @Override
            public void onInterstitialAdShowSucceeded() {
                Log.e(TAG, "Iron Source : " + " AD Shown... ");
                MediationTracking(context, imp);

            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

            }

            @Override
            public void onInterstitialAdClicked() {

                adRefreshCount = 0;
                MediationTracking(context, click);

            }
        };

        IronSource.setInterstitialListener(interstitialListener);


    }


    //Unity
    void loadUnityInterstitial(Context context, String unityGameID, String adUnitID, String testMode, String imp, String click) {

        //Unity
        UnityAds.initialize(context, unityGameID, Boolean.parseBoolean(testMode), (IUnityAdsInitializationListener) context);

        //Unity Load Listener
        IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {
                UnityAds.show((Activity) context, adUnitID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
                        adRefreshCount = 0;
                        MediationTracking(context, imp);
                    }

                    @Override
                    public void onUnityAdsShowClick(String placementId) {
                        Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);
                        adRefreshCount = 0;
                        MediationTracking(context, click);
                    }

                    @Override
                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                        Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
                    }
                });
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
                        new InterstitialVideo().loadAdShow(context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);
            }
        };

        UnityAds.load(adUnitID, loadListener);
    }


    //ChartBoost
    void loadChartBoost(Context context, String appID, String appSignature, String imp, String click, String request) {


        Chartboost.startWithAppId(context, appID, appSignature, startError -> {
            if (startError == null) {
                Log.i("ChartBoost Status", "ChartBoost SDK is initialized Successfully");

                chartboostInterstitial.cache();

                if (chartboostInterstitial.isCached()) {
                    Log.i("ChartBoost_Cache", "ChartBoost Cached..Successfully");
                }


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
                        new InterstitialVideo().loadAdShow(context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);

            }
        });

        chartboostInterstitial = new Interstitial("start", new InterstitialCallback() {
            @Override
            public void onAdDismiss(@NonNull DismissEvent dismissEvent) {

            }

            @Override
            public void onAdLoaded(@NonNull CacheEvent cacheEvent, @Nullable CacheError cacheError) {
                Log.e("ChartBoost CacheEvent", "cacheEvent" + cacheEvent);
                Log.e("ChartBoost CacheEvent", "cacheError" + cacheError);

                Log.i("ChartBoost Status", "ChartBoost Ad Loaded Success");


            }

            @Override
            public void onAdRequestedToShow(@NonNull ShowEvent showEvent) {
                Log.i("ChartBoost Status", "ChartBoost Ad Request to Show");

            }

            @Override
            public void onAdShown(@NonNull ShowEvent showEvent, @Nullable ShowError showError) {
                Log.i("ChartBoost Status", "ChartBoost Ad Shown");

            }

            @Override
            public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {
                adRefreshCount = 0;
                MediationTracking(context, click);
            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {
                Log.i("ChartBoost Status", "ChartBoost Ad Impression Recorded");

            }

        }, null);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostInterstitial.cache();
            }
        }, 1200);


        if (chartboostInterstitial.isCached()) {
            Log.i("ChartBoost Status", "ChartBoost Cached");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostInterstitial.show();
            }
        }, 2000);


    }


    public void MediationTracking(Context context, String URL) {

        Log.e("Event Track", "Method :" + "Called");

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        CronetEngine cronetEngine = myBuilder.build();


        Executor executor = Executors.newSingleThreadExecutor();

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(URL, new MyUrlRequestCallback(), executor);

        UrlRequest request = requestBuilder.build();

        request.start();

        Log.e("Event Track", "Method :" + "Finished");

    }

}




