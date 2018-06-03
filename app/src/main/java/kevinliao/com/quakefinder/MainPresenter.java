package kevinliao.com.quakefinder;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import kevinliao.com.quakefinder.data.EventDatabaseHelper;
import kevinliao.com.quakefinder.network.DownloadCallback;
import kevinliao.com.quakefinder.network.Earthquake;
import kevinliao.com.quakefinder.network.NetworkClient;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private MainContract.View mView;
    private EventDatabaseHelper mDatabaseHelper;
    private NetworkClient mNetworkClient;
    private List<Earthquake> mEventList = new ArrayList<>();
    private boolean mIsLoading;
    Comparator<Earthquake> mComparator = new Comparator<Earthquake>() {
        @Override
        public int compare(Earthquake o1, Earthquake o2) {
            long a = o1.getTimeStamp();
            long b = o2.getTimeStamp();
            if (a == b) return 0;
            return b - a > 0 ? 1 : -1;
        }
    };

    public MainPresenter(EventDatabaseHelper eventDatabaseHelper
            , NetworkClient networkClient) {
        this.mDatabaseHelper = eventDatabaseHelper;
        this.mNetworkClient = networkClient;
    }

    @Override
    public void takeView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void loadEvent(String startDate) {
        mView.showProgressbar();
        mNetworkClient.getEarthquakeByTime(startDate, new DownloadCallback<List<Earthquake>>() {
            @Override
            public void onSuccess(List<Earthquake> result) {
                mEventList.addAll(new ArrayList<>(result));
                mDatabaseHelper.addEvents(result);
                Collections.sort(mEventList, mComparator);
                mView.showEvent(mEventList);
                mView.hideProgressbar();
            }

            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
                mView.hideProgressbar();
                mView.showNetworkErrorMessage();
                mIsLoading = false;
            }
        });
    }

    @Override
    public int loadEventFromDatabase(long start, long end) {
        if (mIsLoading) return -1;
        mIsLoading = true;
        mView.showProgressbar();
        List<Earthquake> list = mDatabaseHelper.getEventsAfterTimestamp(start, end - 1);
        if (list != null && list.size() != 0) {
            updateCurrentList(list);
            return 1;
        }

        mIsLoading = false;
        return 0;
    }

    @Override
    public void loadEventFromCloud(long start, long end) {
        if (mIsLoading) return;
        mIsLoading = true;
        String startDate = convertTimestampToDate(start, 0);
        String endDate = convertTimestampToDate(end, 0);
        mNetworkClient.getEarthquakeByTime(startDate, endDate, new DownloadCallback<List<Earthquake>>() {
            @Override
            public void onSuccess(List<Earthquake> result) {
                mDatabaseHelper.addEvents(result);
                updateCurrentList(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
                mView.hideProgressbar();
                mView.showNetworkErrorMessage();
                mIsLoading = false;

            }
        });
    }

    private void updateCurrentList(List<Earthquake> newList) {
        newList.removeAll(mEventList);
        mEventList.addAll(newList);
        Collections.sort(mEventList, mComparator);
        mView.showEvent(mEventList);
        mView.hideProgressbar();
        mIsLoading = false;
    }

    private String convertTimestampToDate(long mill, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mill - offset);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

}
