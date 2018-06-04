package kevinliao.com.quakefinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private OnNetworkChangeListener mListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && mListener != null) {
            if(isNetworkAvailable(context)) {
                mListener.onNetworkGain();
            }
        }

    }

    public void addOnNetworkChangeListener(OnNetworkChangeListener listener) {
        mListener = listener;
    }
    private boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    interface OnNetworkChangeListener {
        void onNetworkGain();
    }
}
