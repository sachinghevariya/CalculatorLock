package plugin.adsdk.service;

import static android.os.Build.VERSION.SDK_INT;
import static plugin.adsdk.service.AdsUtility.config;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Date;
import java.util.List;

import plugin.adsdk.R;
import plugin.adsdk.extras.BaseLauncherActivity;
import plugin.adsdk.extras.NetworkChangeReceiver;
import plugin.adsdk.extras.ThankYouBottomSheet;
import plugin.adsdk.service.utils.ValidationHandler;

@SuppressWarnings("unused")
//@Obfuscate
public class BaseActivity extends AppCompatActivity {
    public static final String PARAM_FOR_RESULT = "FOR_RESULT";
    public static final String PARAM_REQUEST_CODE = "REQUEST_CODE";
    public static final int REQUEST_CODE_PERMISSION = 3256;
    public static final int REQUEST_CODE_APP_UPDATE = 7588;

    private static Dialog progressDialog;
    private NetworkChangeReceiver receiver = null;

    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showDialog() {
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing()) {
            progressDialog.show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void setupNetworkChangeReceiver() {
        receiver = new NetworkChangeReceiver(this::networkStateChanged);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!(this instanceof BaseLauncherActivity) && ValidationHandler.hasDebuggingEnabled(this))
//            ValidationHandler.showDeveloperDialog(this, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void networkStateChanged(NetworkChangeReceiver.NetworkState state) {
    }

    protected void bannerAd() {
        bannerAd(findViewById(R.id.banner_ad_container));
    }

    public void bannerAd(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.bannerAd)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestBannerAd(this, viewGroup);
    }

    public void bannerAdLanguage(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.bannerAdLanguage)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestBannerAdLanguage(this, viewGroup);
    }
    protected void mediumRectangleBannerAd() {
        mediumRectangleBannerAd(findViewById(R.id.banner_ad_container));
    }

    public void mediumRectangleBannerAd(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.bannerAd)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestMediumRectangleBanner(this, viewGroup);
    }

    protected void nativeAdMedium() {
        nativeAdMedium(findViewById(R.id.native_ad_container));
    }

    public void nativeAdMedium(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.nativeAd)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestNativeAdMedium(this, viewGroup);
    }

    protected void nativeAdSmall() {
        nativeAdSmall(findViewById(R.id.native_ad_container));
    }

    public void nativeAdSmall(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.nativeAd)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestNativeAdSmall(this, viewGroup, R.layout.ad_native_small);
    }

    protected void nativeAd() {
        nativeAd(findViewById(R.id.native_ad_container));
    }

    public void nativeAd(ViewGroup viewGroup) {
        if (TextUtils.isEmpty(config.adMob.nativeAd)) {
            viewGroup.setVisibility(View.GONE);
            return;
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this); //limiting app-open
        }
        AdsUtility.requestNativeAd(this, viewGroup);
    }

    public void showInterstitial(final Intent intent) {
        showInterstitial(() -> startActivity(intent));
    }

    public void showInterstitial(final BaseCallback callback) {
//        if (config.allowAdIntervalInterstitial) {
//            //interstitial for time based interval
//            long now = new Date().getTime();
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//            long lastAdTime = pref.getLong("last_ad_time_interstitial", now);
//            long allowedDelta = config.adIntervalTimeInterstitial * 1000L;
//            long currentAdTimeDelta = now - lastAdTime;
//            if (currentAdTimeDelta >= allowedDelta) {
//                pref.edit().putLong("last_ad_time_interstitial", now).apply();
//                AdsUtility.showInterstitial(this, callback);
//            } else {
//                callback.completed();
//            }
//            return;
//        }
        if (AdsUtility.currentActivityCount == 0) {
            if (config.replaceInterWithAppOpen) {
                //create a function which complies with on-demand as well as preload
                AppOpenManager.get().showAppOpen(callback);
            } else {
                //check for alternative inter & app-open
                if (config.alternateAds) {
                    if (AdsUtility.shouldAlternate) {
                        AdsUtility.showInterstitial(this, callback);
                        AdsUtility.shouldAlternate = false;
                    } else {
                        AppOpenManager.get().showAppOpen(callback);
                        AdsUtility.shouldAlternate = true;
                    }
                } else {
                    AdsUtility.showInterstitial(this, callback);
                }
            }
        } else {
//            AdsUtility.currentActivityCount = (AdsUtility.currentActivityCount + 1) % config.activityCount;
            callback.completed();
        }
    }

    protected void showInitialInterstitial(final BaseCallback callback) {
        AdsUtility.showInterstitial(this, callback);
    }

    //Source Needs Update
    private RewardItem rewardItem = null;

    public void showAdmobRewardedVideoAd(OnUserEarnedRewardListener callback) {
        if (TextUtils.isEmpty(config.adMob.videoAd)) {
            callback.onUserEarnedReward(rewardItem);
            return;
        }

        setupDialog();
        progressDialog.show();

        final FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                callback.onUserEarnedReward(rewardItem);
                rewardItem = null;
            }
        };

        OnUserEarnedRewardListener rewardCallback = reward -> rewardItem = reward;

        RewardedAd.load(
                this,
                config.adMob.videoAd,
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        dismissDialog();
                        Toast.makeText(BaseActivity.this, "Failed to fetch reward!", Toast.LENGTH_SHORT).show();
                        callback.onUserEarnedReward(rewardItem);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                        rewardedAd.show(BaseActivity.this, rewardCallback);
                        dismissDialog();
                    }
                });
    }

    public void startActivity(Intent intent) {
        if (intent != null) {
            if (intent.getBooleanExtra(PARAM_FOR_RESULT, false)) {
                //noinspection deprecation
                startActivityForResult(intent, intent.getIntExtra(PARAM_REQUEST_CODE, -1));
            } else {
                super.startActivity(intent);
            }
        }
    }

    void setupDialog() {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.ad_loading);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void statusBarDark() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int flags = getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // use XOR here for remove LIGHT_STATUS_BAR from flags
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    }

    //use REQUEST_CODE_PERMISSION in onActivityResult() method to re-verify permission approval
    public void checkRunTimePermission(final BaseCallback callback, Boolean showAd, String... permissionArrays) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermissions(permissionArrays)) {
                if (showAd) showInterstitial(callback);
                else callback.completed();
            } else {
                Dexter.withContext(this)
                        .withPermissions(permissionArrays)
                        .withListener(new CompositeMultiplePermissionsListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    if (showAd) showInterstitial(callback);
                                    else callback.completed();
                                } else {
                                    showPermissionSnack();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        })).onSameThread().check();
            }
        } else {
            if (showAd) showInterstitial(callback);
            else callback.completed();
        }
    }

    private void showPermissionSnack() {
        String message = "All those permissions are needed for working all functionality";
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction("Settings", view -> openSettingsDialog());
        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void openSettingsDialog() {
        final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
        final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";
        final String EXTRA_SYSTEM_ALERT_WINDOW = "permission_settings";

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW);

        Uri uri = Uri.fromParts("package", getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(uri)
                .putExtra(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
                .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_CODE_PERMISSION);
    }

    public boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showThankYouSheet() {
        ThankYouBottomSheet addPhotoBottomDialogFragment =
                ThankYouBottomSheet.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                ThankYouBottomSheet.TAG);
    }

    protected long mLastClickTime = 0;

    protected void backPressed(BaseCallback callback) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (AdsUtility.config.adOnBack) {
            showInterstitial(callback);
        } else {
            callback.completed();
        }
    }

    protected void backPressed() {
        backPressed(super::onBackPressed);
    }

    protected void exitBackPressed(BaseCallback callback) {
        if (AdsUtility.startScreenCount != 0) {
            backPressed();
        } else {
            callback.completed();
        }
    }

    protected void exitBackPressed() {
        exitBackPressed(this::showThankYouSheet);
    }

    protected ActivityResultLauncher<Intent> appOpenBlockLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> AppOpenManager.overrideAppOpenShow(false)
    );
}
