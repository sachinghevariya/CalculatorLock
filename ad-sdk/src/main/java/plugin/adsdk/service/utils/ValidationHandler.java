package plugin.adsdk.service.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.scottyab.rootbeer.RootBeer;

import plugin.adsdk.BuildConfig;
import plugin.adsdk.R;
import plugin.adsdk.service.AdsUtility;
import plugin.adsdk.service.BaseCallback;

public class ValidationHandler {

    private ValidationHandler() {
    }

    private static boolean isRooted(Context context) {
        return new RootBeer(context).isRooted();
    }

    private static boolean hasVPNConnected(Context context) {
        boolean vpnInUse = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
                return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            }
            for (Network network : connectivityManager.getAllNetworks()) {
                NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    vpnInUse = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vpnInUse;
    }

    public static boolean hasDebuggingEnabled(Context context) {
        return !BuildConfig.DEBUG && Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
    }

    public static void validateOptions(Activity context, BaseCallback callback) {
        if (isRooted(context)) {
            showRootDialog(context);
        } else if (hasDebuggingEnabled(context)) {
            showDeveloperDialog(context, callback);
        } else if (hasVPNConnected(context)) {
            showVPNDialog(context, callback);
        } else {
            callback.completed();
        }
    }

    private static void showVPNDialog(Activity context, BaseCallback callback) {
        String validationText = "Please turn off VPN and try again.";
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_debugging_vpn);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.tvValidatoinDescription)).setText(validationText);
        dialog.findViewById(R.id.actionExit).setVisibility(View.GONE);
        dialog.findViewById(R.id.actionRetry).setOnClickListener(view -> {
            if (!hasVPNConnected(context)) {
                dialog.dismiss();
                validateOptions(context, callback);
            } else {
                Toast.makeText(context, "Please turn off VPN.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.findViewById(R.id.actionOpen).setOnClickListener(view -> {
            try {
                String ACTION_VPN_SETTINGS = "android.net.vpn.SETTINGS";
                Intent intent = new Intent(ACTION_VPN_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Please turn off VPN.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    public static void showDeveloperDialog(Activity context, @Nullable BaseCallback callback) {
        String validationText = "Please turn off Developer Options and try again.";
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_debugging_vpn);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.tvValidatoinDescription)).setText(validationText);
        dialog.findViewById(R.id.actionExit).setVisibility(View.GONE);
        dialog.findViewById(R.id.actionOpen).setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Please turn off Developer Options.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        dialog.findViewById(R.id.actionRetry).setOnClickListener(view -> {
            if (!hasDebuggingEnabled(context)) {
                dialog.dismiss();
                if (callback != null) {
                    validateOptions(context, callback);
                }
            } else {
                Toast.makeText(context, "Please turn off Developer Options.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private static void showRootDialog(Activity context) {
        String validationText = "Application is not supported for rooted devices.";
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_debugging_vpn);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.tvValidatoinDescription)).setText(validationText);
        dialog.findViewById(R.id.actionOpen).setVisibility(View.GONE);
        dialog.findViewById(R.id.actionRetry).setVisibility(View.GONE);
        dialog.findViewById(R.id.actionExit).setOnClickListener(view -> {
            AdsUtility.destroy();
            try {
                context.finishAffinity();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        });
        dialog.show();
    }
}
