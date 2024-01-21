package plugin.adsdk.service.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Despicable on 7/11/2020.
 */
public class ListModel {

    public String packageName = "";
    public String accountName = "";
    public String privacyPolicyUrl = "";
    public String base64InAppKey = "";
    public boolean onDemandAppOpen = false;
    public boolean initialAppOpen = false;
    public boolean countAppOpenInterval = false;
    public boolean blockInitialAppOpen = false;
    public String appOpenOptimization = "";
    public boolean allowAdIntervalAppOpen = false;
    public int adIntervalTimeAppOpen = 0;

    public int activityCount = 0;
    public boolean adOnBack = false;
    public boolean preloadInterstitial = false;
    public boolean replaceInterWithAppOpen = false;
    public boolean alternateAds = false;
    public boolean allowAdIntervalInterstitial = false;
    public int adIntervalTimeInterstitial = 0;
    public int listNativeCount = 6;

    public int nativeHeightPercentage;
    public boolean preloadNative;
    public boolean preloadBanner;
    public boolean flashingNative = false;
    public String forcedNativeColor = "";

    public String screenCount;
    public int repeatScreenCount;
    public List<String> startScreens = new ArrayList<>();
    public int startScreenRepeatCount = 0;

    public List<String> webSiteLink = new ArrayList<>();

    public String video = "";


    public boolean qurekaEnabled = false;

    public String qurekaURL = "https://425.live.qureka.com/";

    public String qurekaButtons = "";


    public boolean forcedAppUpdate = false;

    /***********limited controls***********/


    public String limitedCountries = "";
    public boolean limitedOnDemandAppOpen = false;
    public boolean limitedInitialAppOpen = false;
    public boolean limitedCountAppOpenInterval = false;
    public boolean limitedBlockInitialAppOpen = false;
    public String limitedAppOpenOptimization = "";
    public boolean limitedAllowAdIntervalAppOpen = false;
    public int limitedAdIntervalTimeAppOpen = 0;

    public int limitedActivityCount = 0;
    public boolean limitedAdOnBack = false;
    public boolean limitedPreloadInterstitial = false;
    public boolean limitedReplaceInterWithAppOpen = false;
    public boolean limitedAlternateAds = false;
    public boolean limitedAllowAdIntervalInterstitial = false;
    public int limitedAdIntervalTimeInterstitial = 0;
    public int limitedListNativeCount = 6;
    public int limitedNativeHeightPercentage = 40;
    public boolean limitedPreloadNative;
    public boolean limitedFlashingNative = false;
    public String limitedForcedNativeColor = "";
    public String limitedScreenCount;
    public int limitedRepeatScreenCount;
    public boolean limitedQurekaEnabled = false;
    public String limitedQurekaButtons = "";
    public CommonModel adMob = new CommonModel();

    public void migrateToNoAds() {
        screenCount = "";
        startScreens = Collections.emptyList();
        startScreenRepeatCount = 0;
        adMob.nativeAd = "";
        adMob.bannerAd = "";
        adMob.appOpenId = "";
        adMob.interstitialAd = "";
        adMob.videoAd = "";
        activityCount = 1000;
        adOnBack = false;
        onDemandAppOpen = false;
        flashingNative = false;
        limitedCountries = "";
        listNativeCount = 0;
        qurekaEnabled = false;
        blockInitialAppOpen = false;
        appOpenOptimization = "";
        alternateAds = false;
        allowAdIntervalAppOpen = false;
        adIntervalTimeInterstitial = 1000;
        allowAdIntervalInterstitial = false;
        adIntervalTimeAppOpen = 3600;
    }

    public void addLimits() {
        //ao
        onDemandAppOpen = limitedOnDemandAppOpen;
        initialAppOpen = limitedInitialAppOpen;
        countAppOpenInterval = limitedCountAppOpenInterval;
        blockInitialAppOpen = limitedBlockInitialAppOpen;
        appOpenOptimization = limitedAppOpenOptimization;
        allowAdIntervalAppOpen = limitedAllowAdIntervalAppOpen;
        adIntervalTimeAppOpen = limitedAdIntervalTimeAppOpen;
        //inter
        activityCount = limitedActivityCount;
        adOnBack = limitedAdOnBack;
        preloadInterstitial = limitedPreloadInterstitial;
        replaceInterWithAppOpen = limitedReplaceInterWithAppOpen;
        alternateAds = limitedAlternateAds;
        allowAdIntervalInterstitial = limitedAllowAdIntervalInterstitial;
        adIntervalTimeInterstitial = limitedAdIntervalTimeInterstitial;
        //native
        listNativeCount = limitedListNativeCount;
        nativeHeightPercentage = limitedNativeHeightPercentage;
        preloadNative = limitedPreloadNative;
        flashingNative = limitedFlashingNative;
        forcedNativeColor = limitedForcedNativeColor;
        //extras
        screenCount = limitedScreenCount;
        repeatScreenCount = limitedRepeatScreenCount;
        //qureka
        qurekaEnabled = limitedQurekaEnabled;
        qurekaButtons = limitedQurekaButtons;

    }
}