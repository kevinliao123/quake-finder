package kevinliao.com.quakefinder;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kevinliao.com.quakefinder.network.Earthquake;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private static final String TAG = MainActivity.class.getSimpleName();
    MainContract.Presenter mPresenter;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    EventAdapter mAdapter;
    Handler mHandler;
    NetworkChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mRecyclerView = findViewById(R.id.event_recycler_view);
        mProgressBar = findViewById(R.id.progress_bar);
        setupRecyclerView();
        mPresenter = new MainPresenter(Injection.provideEventDatabaseHelper(getApplicationContext())
                , Injection.provideNetworkClient());
        mPresenter.takeView(this);
        setupNetworkReceiver();
    }

    private void setupNetworkReceiver() {
        mReceiver = new NetworkChangeReceiver();
        mReceiver.addOnNetworkChangeListener(() -> mPresenter.loadEvent(getCurrentDate()));
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mAdapter = new EventAdapter();
        EventAdapter.OnBottomReachedListener listener = new EventAdapter.OnBottomReachedListener() {
            @Override
            public void onBottomReached(long mill) {
                long offset = TimeUnit.DAYS.toMillis(1);
                if (mPresenter.loadEventFromDatabase(mill - offset, mill) == 0) {
                    mPresenter.loadEventFromCloud(mill - offset, mill);
                }

            }
        };
        mAdapter.setOnBottomReachedListener(listener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mPresenter.dropView();
        mPresenter = null;
    }

    @Override
    public void showEvent(final List<Earthquake> list) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.setDataset(list);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showProgressbar() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgressbar() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showNetworkErrorMessage() {
        Toast.makeText(this, "Network issue at this time, try again later!", Toast.LENGTH_SHORT).show();
    }
}
