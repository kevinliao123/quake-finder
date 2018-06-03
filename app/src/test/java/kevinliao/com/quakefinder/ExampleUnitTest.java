package kevinliao.com.quakefinder;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testDateManipulation() {
        long ts = 1527892907060l;
        long time = TimeUnit.DAYS.toMillis(1);
        String today = convertTimestampToDate(ts,0);
        String yesterday = convertTimestampToDate(ts-time,0);
    }

    private String convertTimestampToDate(long mill, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mill-offset);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }
}