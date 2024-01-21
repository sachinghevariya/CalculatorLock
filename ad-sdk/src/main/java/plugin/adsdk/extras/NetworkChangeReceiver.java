package plugin.adsdk.extras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import org.jetbrains.annotations.NotNull;

import plugin.adsdk.service.AdsUtility;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private final NetworkChangeListener listener;

    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean connected = AdsUtility.isNetworkConnected(context);
            listener.onNetworkChanged(connected ? NetworkState.CONNECTED : NetworkState.NOT_CONNECTED);
        }
    }

    public interface NetworkChangeListener {
        void onNetworkChanged(@NotNull NetworkState state);
    }

    public enum NetworkState {
        CONNECTED,
        NOT_CONNECTED
    }
}
