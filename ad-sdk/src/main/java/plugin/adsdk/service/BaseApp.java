package plugin.adsdk.service;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

//import io.michaelrocks.paranoid.Obfuscate;
import plugin.adsdk.service.api.ListModel;
import plugin.adsdk.service.utils.PurchaseHandler;

//@Obfuscate
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getConfig();
        MobileAds.initialize(this);
        AppOpenManager.init(this);
        PurchaseHandler.init(this);
    }

    private void getConfig() {
        if (AdsUtility.config != null) {
            return;
        }
        AdsUtility.config = new ListModel();
//        if (PurchaseHandler.hasPurchased(this)) {
//            AdsUtility.config.migrateToNoAds()
//        }
        AdsUtility.config.packageName =
                "calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault";
        AdsUtility.config.initialAppOpen = false;
        AdsUtility.config.blockInitialAppOpen = true;
        AdsUtility.config.activityCount = 0;
        AdsUtility.config.adOnBack = true;
        AdsUtility.config.privacyPolicyUrl =
                "https://sites.google.com/view/calculator-lock-hide-app-photo/home";
        AdsUtility.config.preloadNative = false;
        AdsUtility.config.preloadBanner = true;
        AdsUtility.config.preloadInterstitial = true;
        AdsUtility.config.base64InAppKey = "";
    }
}
