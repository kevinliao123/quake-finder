package kevinliao.com.quakefinder;

import android.content.Context;

import kevinliao.com.quakefinder.data.EventDatabaseHelper;
import kevinliao.com.quakefinder.network.NetworkClient;

public class Injection {
    public static NetworkClient provideNetworkClient() {
        return NetworkClient.getInstance();
    }

    public static EventDatabaseHelper provideEventDatabaseHelper(Context context) {
        return EventDatabaseHelper.getInstance(context);
    }
}
