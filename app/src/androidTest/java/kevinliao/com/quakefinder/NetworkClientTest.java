package kevinliao.com.quakefinder;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kevinliao.com.quakefinder.network.DownloadCallback;
import kevinliao.com.quakefinder.network.Earthquake;
import kevinliao.com.quakefinder.network.NetworkClient;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetworkClientTest {
    NetworkClient mClient;
    @Before
    public void setupNetworkClient() {
        mClient = NetworkClient.getInstance();
    }

    @Test
    public void testGetEarthquakeByTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String start = dateFormat.format(cal.getTime());
        final Object syncObject = new Object();
        mClient.getEarthquakeByTime(start, new DownloadCallback<List<Earthquake>>() {
            @Override
            public void onSuccess(List<Earthquake> result) {
                for (Earthquake event : result) {
                    System.out.println();
                }
                assertNotNull(result);
                assertTrue(result.size() > 0);
                synchronized (syncObject) {
                    syncObject.notify();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        synchronized (syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
