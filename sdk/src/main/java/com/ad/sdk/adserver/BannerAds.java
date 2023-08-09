package com.ad.sdk.adserver;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ad.sdk.mtrack.Device_settings;
import com.ad.sdk.utils.LoadData;
import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyZone;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ads.Banner;
import com.chartboost.sdk.callbacks.BannerCallback;
import com.chartboost.sdk.events.CacheError;
import com.chartboost.sdk.events.CacheEvent;
import com.chartboost.sdk.events.ClickError;
import com.chartboost.sdk.events.ClickEvent;
import com.chartboost.sdk.events.ImpressionEvent;
import com.chartboost.sdk.events.ShowError;
import com.chartboost.sdk.events.ShowEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BannerAds {
    ArrayList<WebView> multipleViewList = new ArrayList<WebView>();

    Banner chartboostBanner;

    String click, imp, request;

    int adRefreshCount = 0;

    public BannerAds() {
    }

    public void loadAdShow(Context context) {
        Intent i = new Intent(context, LoadActivity.class);

        context.startActivity(i);
      /*  AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        //FrameLayout frameLayout = (FrameLayout)findViewById(R.id.adView1);
        layout.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

    }


    //AdMob Load AD
    public void loadmediationAdShow(Context context, AdView mAdView, WebView view, String adunit) {

        if (view != null) {
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(adunit);
            view.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }

    }

    //Adcolony Load AD
    public void loadmediationAdShow_adcolony(Context context, WebView view) {
        if (view != null) {
            AdView mAdView = new AdView(context);
            view.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }


    //ChartBoost Load AD
    public void loadmediationChartBoost(Context context, WebView view) {
        if (view != null) {
            AdView mAdView = new AdView(context);
            view.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }

    //IronSource Load AD
    public void loadMediationIronSource(Context context, WebView view) {
        if (view != null) {
            AdView mAdView = new AdView(context);
            view.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }

    //Unity ADS
    public void loadMediationUnityAds(Context context, WebView view) {
        if (view != null) {
            AdView mAdView = new AdView(context);
            view.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }


    public void loadBannerAd(Context adViewContext, ArrayList<WebView> multipleViewList) {
        try {

            if (multipleViewList.size() > 0 && adViewContext != null) {
                this.multipleViewList = multipleViewList;

                System.out.println("@@ multiView.size() = " + multipleViewList.size());

                System.out.println("@@ local save data = " + new LoadData().adResponseValues(adViewContext).size());

                //loadmediationAdShow(adViewContext,multipleViewList.get(0),new AdResponse().adunit);
                SharedPreferences sharedPreferences = adViewContext.getSharedPreferences("MediationBanner", MODE_PRIVATE);
                String ad_network_type = sharedPreferences.getString("Mediation_ad_network_type", "");
                String ad_unit = sharedPreferences.getString("Mediation_adunit", "");
                String app_id = sharedPreferences.getString("Mediation_app_id", "");
                String app_signature = sharedPreferences.getString("Mediation_app_signature", "");
                String gameID = sharedPreferences.getString("Mediation_app_gameId", "");
                String placementId = sharedPreferences.getString("Mediation_app_placementId", "");
                String testMode = sharedPreferences.getString("Mediation_app_testMode", "");
                String zoneId = sharedPreferences.getString("Mediation_app_zoneid", "");
                click = sharedPreferences.getString("Mediation_click", "");
                imp = sharedPreferences.getString("Mediation_impression", "");
                request = sharedPreferences.getString("Mediation_request", "");


                Log.e("ad_unit", "abMob :" + ad_unit);

                if (new LoadData().getMediationNetworkStatus(adViewContext).equalsIgnoreCase("mediation")) {

                    //AdColony
                    if (ad_network_type.equalsIgnoreCase("Adcolony")) {

                        for (int i = 0; i < multipleViewList.size(); i++) {
                            adRefreshCount = 0;
                            MediationTracking(adViewContext, request);

                            loadAdColony(adViewContext, app_id, zoneId, multipleViewList.get(i));

                        }
                    }

                    //IronSource
                    else if (ad_network_type.equalsIgnoreCase("IronSource")) {

                        for (int i = 0; i < multipleViewList.size(); i++) {
                            adRefreshCount = 0;
                            MediationTracking(adViewContext, request);

                            loadironSource(adViewContext, app_id, multipleViewList.get(i));


                        }
                    }
                    //ChartBoost
                    else if (ad_network_type.equalsIgnoreCase("ChartBoost")) {

                        for (int i = 0; i < multipleViewList.size(); i++) {
                            adRefreshCount = 0;
                            MediationTracking(adViewContext, request);

                            loadChartBoost(adViewContext, app_id, app_signature, multipleViewList.get(i));


                        }
                    }

                    //Unity Ads
                    else if (ad_network_type.equalsIgnoreCase("Unity")) {
                        for (int i = 0; i < multipleViewList.size(); i++) {
                            adRefreshCount = 0;
                            MediationTracking(adViewContext, request);

                            loadUnityAd(adViewContext, testMode, gameID, placementId, multipleViewList.get(i));


                        }
                    }

                    //AdMob
                    else {
                        for (int i = 0; i < multipleViewList.size(); i++) {
                            adRefreshCount = 0;
                            MediationTracking(adViewContext, request);

                            loadAdMobBanner(adViewContext, multipleViewList.get(i), ad_unit);


                        }
                    }


                } else {

                    for (int i = 0; i < new LoadData().adResponseValues(adViewContext).size(); i++) {
                        if (i < multipleViewList.size()) {
                            for (int k = 0; k < multipleViewList.size(); k++) {
                                if (k < new LoadData().adResponseValues(adViewContext).size()) {
                                    Log.d("@@ mulitZone ", "ENCODE HTML CODE:" + new LoadData().adResponseValues(adViewContext).get(k).getAd_tag());
                                    //String HtmlCode = Html.fromHtml(adResult.getAdtag()).toString();
                                    String HtmlCode = new LoadData().adResponseValues(adViewContext).get(k).getAd_tag();
                                    int maxLogSize = 4000;
                                    for (int j = 0; j <= HtmlCode.length() / maxLogSize; j++) {
                                        int start = j * maxLogSize;
                                        int end = (j + 1) * maxLogSize;
                                        end = end > HtmlCode.length() ? HtmlCode.length() : end;
                                        Log.d("mulitZone", "HTML CODE:" + HtmlCode.substring(start, end));
                                    }
                                    //WebView htmlAd = new WebView(context);
                                    multipleViewList.get(k).setBackgroundColor(0);
                                    multipleViewList.get(k).setPadding(0, 0, 0, 0);
                                    multipleViewList.get(k).getSettings().setJavaScriptEnabled(true);
                                    String html = "<!DOCTYPE html><html>" + "<style type='text/css'>" + "html,body {margin: 0;padding: 0;width: 100%;height: 100%;}" + "html {display: table;}" + "body {display: table-cell;vertical-align: middle;text-align: center;}" + "img{display: inline;height: auto;max-width: 100%;}" + "</style>" + "<body style= \"width=\"100%\";height=\"100%\";initial-scale=\"1.0\"; maximum-scale=\"1.0\"; user-scalable=\"no\";>" + HtmlCode + "</body></html>";

                                    System.out.println("@@ html" + html);
                                    multipleViewList.get(k).loadData(html, "text/html", "UTF-8");
                                    //ad_container_params = new LayoutParams(-2, -2);
                                    //zoneArrayList.get(0).getMultipleView().setLayoutParams(ad_container_params);
                                    multipleViewList.get(k).setClickable(true);
                                    multipleViewList.get(k).setVerticalScrollBarEnabled(false);
                                    multipleViewList.get(k).setHorizontalScrollBarEnabled(false);
                                    //ad_container.removeAllViews();
                                    //ad_container.addView(htmlAd);
                                }
                            }
                        }

                    }
                }


            }

        } catch (Exception e) {
            Log.d("SDK", "Banner Ad Exception:" + e);
        }


    }


    //AdColony
    private void loadAdColony(Context adViewContext, String AppID, String ZoneID, WebView view) {
        AdColonyAdViewListener listener;
        AdColonyAdOptions adOptions;
        final String TAG = "AdColonyBannerDemo";

        RelativeLayout adContainer = new RelativeLayout(adViewContext);

        adOptions = new AdColonyAdOptions();

        AdColonyAppOptions appOptions = new AdColonyAppOptions();
        AdColony.configure((Activity) adViewContext, appOptions, AppID);

        listener = new AdColonyAdViewListener() {
            @Override
            public void onRequestFilled(AdColonyAdView adColonyAdView) {
                AdColonyAdView adView;
                Log.d(TAG, "onRequestFilled");
                adContainer.addView(adColonyAdView);
                view.addView(adContainer);
                adRefreshCount = 0;
                MediationTracking(adViewContext, imp);
                adRefreshCount = 1;
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                super.onRequestNotFilled(zone);
                Log.d(TAG, "onRequestNotFilled");

                SharedPreferences sharedPreferences = adViewContext.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(adViewContext).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(adViewContext);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);

                ArrayList<WebView> multiView = new ArrayList<>();

                multiView.add(view);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BannerAds().loadBannerAd(adViewContext, multiView);
                        Device_settings.getSettings(adViewContext).mediation = "1";
                    }
                }, 1500);


            }

            @Override
            public void onOpened(AdColonyAdView ad) {
                super.onOpened(ad);
                Log.d(TAG, "onOpened");
            }

            @Override
            public void onClosed(AdColonyAdView ad) {
                super.onClosed(ad);
                Log.d(TAG, "onClosed");
            }

            @Override
            public void onClicked(AdColonyAdView ad) {
                super.onClicked(ad);
                Log.d(TAG, "onClicked");
                adRefreshCount = 0;
                MediationTracking(adViewContext, click);
                adRefreshCount = 1;
            }

            @Override
            public void onLeftApplication(AdColonyAdView ad) {
                super.onLeftApplication(ad);
                Log.d(TAG, "onLeftApplication");
            }
        };

        AdColony.requestAdView(ZoneID, listener, AdColonyAdSize.BANNER, adOptions);

        loadmediationAdShow_adcolony(adViewContext, view);


    }


    //ChatBoost
    private void loadChartBoost(Context adContext, String AppID, String AppSignature, WebView view) {

        RelativeLayout adContainer = new RelativeLayout(adContext);

        chartboostBanner = new Banner(adContext, "start", Banner.BannerSize.STANDARD, new BannerCallback() {
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
            public void onAdClicked(@NonNull ClickEvent clickEvent, @Nullable ClickError clickError) {

                adRefreshCount = 0;
                MediationTracking(adContext, click);
                adRefreshCount = 1;
            }

            @Override
            public void onImpressionRecorded(@NonNull ImpressionEvent impressionEvent) {

            }
        }, null);
        Chartboost.startWithAppId(adContext, AppID, AppSignature, startError -> {
            if (startError == null) {
                Log.i("ChartBoost Status", "ChartBoost SDK is initialized Successfully");

            } else {
                Log.i("ChartBoost Status", "SDK initialized with error:" + startError.getCode().name());


                SharedPreferences sharedPreferences = adContext.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(adContext).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(adContext);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);

                ArrayList<WebView> multiView = new ArrayList<>();

                multiView.add(view);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BannerAds().loadBannerAd(adContext, multiView);
                        Device_settings.getSettings(adContext).mediation = "1";
                    }
                }, 1500);


            }
        });


        adContainer.addView(chartboostBanner);
        view.addView(adContainer);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostBanner.cache();

            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chartboostBanner.show();
                adRefreshCount = 0;
                MediationTracking(adContext, imp);
                adRefreshCount = 1;
            }
        }, 4000);

        loadmediationChartBoost(adContext, view);


    }


    //IronSource
    private void loadironSource(Context adContext, String AppID, WebView view) {
        FrameLayout ironContainer = new FrameLayout(adContext);

        IronSource.init((Activity) adContext, AppID);

        IronSourceBannerLayout ironImageBanner = IronSource.createBanner((Activity) adContext, ISBannerSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);


        Log.i("IronSource Status", "IronSource SDK is initialized Successfully");

        ironImageBanner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                Log.i("IronSource Status", "IronSource Banner Loaded");
                adRefreshCount = 0;
                MediationTracking(adContext, imp);
                adRefreshCount = 1;
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
                Log.i("IronSource Status", "IronSource Banner Failed" + ironSourceError.getErrorMessage());

                SharedPreferences sharedPreferences = adContext.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(adContext).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(adContext);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);

                ArrayList<WebView> multiView = new ArrayList<>();

                multiView.add(view);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BannerAds().loadBannerAd(adContext, multiView);
                        Device_settings.getSettings(adContext).mediation = "1";
                    }
                }, 1500);


            }

            @Override
            public void onBannerAdClicked() {

                adRefreshCount = 0;
                MediationTracking(adContext, click);
                adRefreshCount = 1;

            }

            @Override
            public void onBannerAdScreenPresented() {

            }

            @Override
            public void onBannerAdScreenDismissed() {

            }

            @Override
            public void onBannerAdLeftApplication() {

            }
        });


        ironContainer.addView(ironImageBanner, 0, layoutParams);
        IronSource.loadBanner(ironImageBanner);


        view.addView(ironContainer);

        loadMediationIronSource(adContext, view);

    }

    //Unity
    private void loadUnityAd(Context adContext, String testMode, String UnityGameID, String PlacementID, WebView view) {

        final BannerView[] bannerView = {null};
        RelativeLayout bannerLayout = new RelativeLayout(adContext);

        UnityAds.initialize(adContext, UnityGameID, Boolean.parseBoolean(testMode));

        bannerView[0] = new BannerView((Activity) adContext, PlacementID, new UnityBannerSize(320, 50));
        final BannerView[] finalBannerView = {bannerView[0]};
        finalBannerView[0].setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                // Called when the banner is loaded.
                Log.v("UnityAdsExample", "onBannerLoaded: " + bannerAdView.getPlacementId());
                adRefreshCount = 0;
                MediationTracking(adContext, imp);
                adRefreshCount = 1;

            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                Log.e("UnityAdsExample", "Unity Ads failed to load banner for " + bannerAdView.getPlacementId() + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage);


                finalBannerView[0] = null;

                SharedPreferences sharedPreferences = adContext.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(adContext).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(adContext);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);

                ArrayList<WebView> multiView = new ArrayList<>();

                multiView.add(view);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BannerAds().loadBannerAd(adContext, multiView);
                        Device_settings.getSettings(adContext).mediation = "1";
                    }
                }, 1500);


            }

            @Override
            public void onBannerClick(BannerView bannerAdView) {
                // Called when a banner is clicked.
                Log.v("UnityAdsExample", "onBannerClick: " + bannerAdView.getPlacementId());

                adRefreshCount = 0;
                MediationTracking(adContext, click);
                adRefreshCount = 1;
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerAdView) {
                // Called when the banner links out of the application.
                Log.v("UnityAdsExample", "onBannerLeftApplication: " + bannerAdView.getPlacementId());
            }

        });
        bannerView[0].load();
        bannerLayout.addView(bannerView[0]);

        view.addView(bannerLayout);

        loadMediationUnityAds(adContext, view);

    }

    public void loadAdMobBanner(Context context, WebView webView, String AD_UNIT_ID) {
        String TAG = "BannerAdMob";

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "Initialize Completed.");
            }
        });

        AdView adView = new AdView(context);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Log.e(TAG, "Ad Load Status :" + "Faliure Error : " + loadAdError.getMessage());


                SharedPreferences sharedPreferences = context.getSharedPreferences("Djaxdemo", MODE_PRIVATE);
                String zone_id = sharedPreferences.getString("Zone_ID", "");

                Device_settings.getSettings(context).mediation = "0";

                com.ad.sdk.adserver.AdView adv1 = new com.ad.sdk.adserver.AdView(context);
                adv1.setZoneid(zone_id);
                adv1.LoadAd(adv1);

                ArrayList<WebView> multiView = new ArrayList<>();

                multiView.add(webView);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BannerAds().loadBannerAd(context, multiView);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                Log.e("AdMob Load Status", "Ad Shown..." + "");


            }

            @Override
            public void onAdImpression() {


                Log.e("Imp URL", "Ad Imp : " + imp);
                Log.e("Imp URL", "Ad Imp : " + request);

                adRefreshCount = 0;
                MediationTracking(context, imp);
                adRefreshCount = 1;
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();

                Log.e("Click URL", "Ad Click : " + click);

                adRefreshCount = 0;
                MediationTracking(context, click);
                adRefreshCount = 1;

                Log.e("AdMob Click", "Clicked" + "");

            }
        });

        loadmediationAdShow(context, adView, webView, AD_UNIT_ID);

    }


    public void MediationTracking(Context context, String URL) {

        if (adRefreshCount == 0) {
            CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
            CronetEngine cronetEngine = myBuilder.build();


            Executor executor = Executors.newSingleThreadExecutor();

            UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(URL, new MyUrlRequestCallback(), executor);

            UrlRequest request = requestBuilder.build();

            request.start();

            adRefreshCount = 1;

        }

    }

}




