package com.ad.sdk.adserver;

import static android.content.Context.MODE_PRIVATE;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.ad.sdk.R;
import com.ad.sdk.adserver.Listener.InterstitialImageAdListener;
import com.ad.sdk.mtrack.Device_settings;
import com.ad.sdk.utils.LoadData;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Interstitial_image {
    PopupWindow pop;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "Interstitial_image";


    String click, imp, request;

    public Interstitial_image() {
    }

    public void loadInterstital(Context context, InterstitialImageAdListener listener) {
        try {
            Activity activity = (Activity) context;
            SharedPreferences sharedPreferences = context.getSharedPreferences("MediationInterstitial", MODE_PRIVATE);
            String ad_unit = sharedPreferences.getString("Mediation_adunit", "");
            String ad_network_type = sharedPreferences.getString("Mediation_ad_network_type", "");
            String app_id = sharedPreferences.getString("Mediation_app_id", "");
            String app_signature = sharedPreferences.getString("Mediation_app_signature", "");
            String gameID = sharedPreferences.getString("Mediation_app_gameId", "");
            String placementId = sharedPreferences.getString("Mediation_app_placementId", "");
            String testMode = sharedPreferences.getString("Mediation_app_testMode", "");
            click = sharedPreferences.getString("Mediation_click", "");
            imp = sharedPreferences.getString("Mediation_impression", "");
            request = sharedPreferences.getString("Mediation_request", "");


            if (new LoadData().getMediationNetworkStatus(context).equalsIgnoreCase("mediation")) {

                if (ad_network_type.equalsIgnoreCase("AdMob")) {
                    MediationTracking(context, imp);
                    new Interstitial_image().loadInterstitialImageMediationAdMob(context, activity, ad_unit);

                }

            } else {


                RelativeLayout relativelayout = new RelativeLayout(activity);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                //requestWindowFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customview = layoutInflater.inflate(R.layout.popup, null);
                ImageView close = (ImageView) customview.findViewById(R.id.img_close_btn);
                WebView webView = (WebView) customview.findViewById(R.id.webview);

                // String HtmlCode = "<script type='text/javascript' src='http://rvphp.djaxbidder.com/avista_testing/www/delivery/floatal.php?zoneid=1097&width=&height=&keywords=&lattitude=&longitude=&systemtype=&ip=&layerstyle=&screenwidth=&screenheight=&displaywidth=&displayheight=&displaytype=&devicemodel=&devicebrand=&deviceos=&deviceosversion=&is_js_enabled=&carrier=&country=&countryname=&region=&city=&useragent=&language=&postalcode=&device_appid=&device_app_cat=&device_app_sha1=&device_app_md5=&device_app_dpidsha1=&device_app_dpidmd5=&device_app_ipv6=&udid=&timezone=&dataspeed=&connection=keep-alive&connectiontype=&Viewername=&Vieweremail=&Viewerphone=&Viewergender=Male&Viewerage=26&layerstyle=simple&request_id=&viewerid=&hide=0&trail=0&stickyness=2'></script>";
                //String HtmlCode = new LoadData().adResponseValues(context).get(k).getAd_tag();
                String HtmlCode = new LoadData().getInterstitialImage(context);
                if (HtmlCode.length() > 0) {
                    if (HtmlCode.length() > 4000) {
                        Log.v(TAG, "sb.length = " + HtmlCode.length());
                        int chunkCount = HtmlCode.length() / 4000;     // integer division
                        for (int i = 0; i <= chunkCount; i++) {
                            int max = 4000 * (i + 1);
                            if (max >= HtmlCode.length()) {
                                Log.v(TAG, "chunk " + i + " of " + chunkCount + ":" + HtmlCode.substring(4000 * i));
                            } else {
                                Log.v(TAG, "chunk " + i + " of " + chunkCount + ":" + HtmlCode.substring(4000 * i, max));
                            }
                        }
                    }
                    Log.d("mSDK Debug", "HTML CODE:" + HtmlCode);
                    webView.setBackgroundColor(0);
                    webView.setPadding(0, 0, 0, 0);
                    webView.getSettings().setJavaScriptEnabled(true);
                    //String html = "<!DOCTYPE html><html><body style= \"width=\"100%\";height=\"100%\";initial-scale=\"1.0\"; maximum-scale=\"1.0\"; user-scalable=\"no\";\">"+HtmlCode+"</body></html>";
                    String html = "<!DOCTYPE html><html><style type='text/css'>html,body {margin: 0;padding: 0;width: 100%;height: 100%;}html {display: table;}body {display: table-cell;vertical-align: middle;text-align: center;}</style><body style= \"width=\"100%\";height=\"100%\";initial-scale=\"1.0\"; maximum-scale=\"1.0\"; user-scalable=\"no\";\">" + HtmlCode + "</body></html>";
                    //htmlAd.loadData(html, "text/html", "UTF-8");
                    webView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");

                    webView.setClickable(true);
                    webView.setVerticalScrollBarEnabled(false);
                    webView.setHorizontalScrollBarEnabled(false);

                    pop = new PopupWindow(customview, ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
                    pop.showAtLocation(relativelayout, Gravity.CENTER, 0, 0);
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    listener.onInterstitialAdShown();
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onInterstitialAdDismissed();
                            pop.dismiss();
                        }
                    });


                    listener.onInterstitialAdLoaded();
                }
            }


        } catch (Exception e) {
            Log.d("SDK", "Interstital Image Ad Exception:" + e);
        }


    }


    //AdMob
    public void loadInterstitialImageMediationAdMob(Context context, Activity activity, String adunit) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        com.google.android.gms.ads.AdRequest adRequest = new AdRequest.Builder().build();


        mInterstitialAd.load(context, adunit, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;


                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();

                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.");

                        MediationTracking(context, click);
                    }
                });

                Log.i(TAG, "onAdLoaded");

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);

                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
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
                        new Interstitial_image().loadInterstital(context, (InterstitialImageAdListener) context);
                        Device_settings.getSettings(context).mediation = "1";
                    }
                }, 1500);


            }
        });


    }


    public void MediationTracking(Context context, String URL) {

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(context);
        CronetEngine cronetEngine = myBuilder.build();


        Executor executor = Executors.newSingleThreadExecutor();

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(URL, new MyUrlRequestCallback(), executor);

        UrlRequest request = requestBuilder.build();

        request.start();

    }

    class MyUrlRequestCallback extends UrlRequest.Callback {
        private static final String TAG = "MyUrlRequestCallback";

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
            Log.i(TAG, "onRedirectReceived method called.");
            // You should call the request.followRedirect() method to continue
            // processing the request.
            request.followRedirect();
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onResponseStarted method called.");
            // You should call the request.read() method before the request can be
            // further processed. The following instruction provides a ByteBuffer object
            // with a capacity of 102400 bytes for the read() method. The same buffer
            // with data is passed to the onReadCompleted() method.
            request.read(ByteBuffer.allocateDirect(102400));
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            Log.i(TAG, "onReadCompleted method called.");
            // You should keep reading the request until there's no more data.
            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onSucceeded method called.");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

        }
    }

}
