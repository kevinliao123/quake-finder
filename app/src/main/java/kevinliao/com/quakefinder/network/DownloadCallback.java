package kevinliao.com.quakefinder.network;

public interface DownloadCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}

