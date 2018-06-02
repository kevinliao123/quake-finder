package kevinliao.com.quakefinder.network;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class NetworkClient {

    private static final String TAG = NetworkClient.class.getSimpleName();
    private static final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/";
    private static final String QUERY = "query?format=geojson&starttime=";
    private static NetworkClient mInstance;
    private NetworkHandler mHandler;

    public static NetworkClient getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkClient();
        }
        return mInstance;
    }

    private NetworkClient() {
        HandlerThread ht = new HandlerThread("NetworkHandler Thread");
        ht.start();
        mHandler = new NetworkHandler(ht.getLooper());
    }

    private class DownloadContainer {
        DownloadCallback callback;
        String url;

        public DownloadContainer(DownloadCallback callback, String url) {
            this.callback = callback;
            this.url = url;
        }
    }

    public void getEarthquakeByTime(String startDate, DownloadCallback<List<Earthquake>> callback) {
        String url = BASE_URL + QUERY + startDate;
        DownloadContainer container = new DownloadContainer(callback, url);
        mHandler.obtainMessage(HandlerMessage.DOWNLOAD.ordinal(), container)
                .sendToTarget();
    }

    public void getEarthquakeByTime(String startDate, String endDate, DownloadCallback<List<Earthquake>> callback) {
        String url = BASE_URL + QUERY + startDate + "&endtime=" + endDate;
        DownloadContainer container = new DownloadContainer(callback, url);
        mHandler.obtainMessage(HandlerMessage.DOWNLOAD.ordinal(), container).sendToTarget();
    }

    private void internalRetrieveEarthquakeList(String url, DownloadCallback<List<Earthquake>> callback) {
        List<Earthquake> list = null;
        try {
            String jsonString = makeNetworkRequest(url);
            list = convertToEarthquakeList(jsonString);
            callback.onSuccess(list);
        } catch (JSONException e) {
            callback.onFailure(e);
        } catch (IOException e) {
            callback.onFailure(e);
        }
    }


    private List<Earthquake> convertToEarthquakeList(String jsonString) throws JSONException {
        List<Earthquake> res = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.optJSONArray("features");
        int n = jsonArray.length();
        for (int i = 0; i < n; i++) {
            JSONObject features = jsonArray.optJSONObject(i);
            Earthquake earthquake = convertToEarthquake(features);
            res.add(earthquake);
        }
        return res;
    }

    private Earthquake convertToEarthquake(JSONObject features) throws JSONException {
        JSONObject property = features.optJSONObject("properties");
        JSONArray coor = features.optJSONObject("geometry").optJSONArray("coordinates");
        Earthquake.Builder builder = new Earthquake.Builder();
        return builder
                .setId(features.optString("id"))
                .setPlace(property.optString("place"))
                .setUrl(property.optString("url"))
                .setTime(property.optLong("time"))
                .setLongitude(coor.getDouble(0))
                .setLatitude(coor.getDouble(1))
                .create();

    }

    public String makeNetworkRequest(String urlString) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String res = null;
        URL url = new URL(urlString);
        connection = (HttpsURLConnection) url.openConnection();
        connection.setReadTimeout(3000);
        connection.setConnectTimeout(3000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }
        stream = connection.getInputStream();
        if (stream != null) {
            res = readStream(stream);
            stream.close();
        }
        if (connection != null) {
            connection.disconnect();
        }

        return res;
    }

    private String readStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader in = new InputStreamReader(stream,
                Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(in);
        if (bufferedReader != null) {
            int cp;
            while ((cp = bufferedReader.read()) != -1) {
                sb.append((char) cp);
            }
            bufferedReader.close();
        }
        in.close();
        return sb.toString();
    }

    private enum HandlerMessage {

        DOWNLOAD;

        private static HandlerMessage valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= HandlerMessage.values().length) {
                Log.e(TAG, "HandlerMessage.valueOf: unrecognized message: " + ordinal);
                throw new IllegalArgumentException();
            }
            return HandlerMessage.values()[ordinal];
        }
    }

    private class NetworkHandler extends Handler {

        public NetworkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            HandlerMessage m = HandlerMessage.valueOf(msg.what);
            switch (m) {
                case DOWNLOAD:
                    DownloadContainer container = (DownloadContainer) msg.obj;
                    internalRetrieveEarthquakeList(container.url, container.callback);
            }
        }
    }

}
