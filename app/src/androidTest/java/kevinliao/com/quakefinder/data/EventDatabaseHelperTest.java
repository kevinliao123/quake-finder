package kevinliao.com.quakefinder.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kevinliao.com.quakefinder.Injection;
import kevinliao.com.quakefinder.network.Earthquake;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventDatabaseHelperTest {
    EventDatabaseHelper mHelper;
    Comparator<Earthquake> mComparator = new Comparator<Earthquake>() {
        @Override
        public int compare(Earthquake o1, Earthquake o2) {
            return (int) (o2.getTimeStamp() - o1.getTimeStamp());
        }
    };

    @Before
    public void setupEventDatabaseHelperTest() {
        mHelper = Injection.provideEventDatabaseHelper(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testGetAllEvents() {
        List<Earthquake> list = fakeEventList();
        mHelper.addEvents(list);
        List<Earthquake> newList = mHelper.getAllEvents();
        assertEquals(list.size(), newList.size());
        Collections.sort(list, mComparator);
        Collections.sort(newList, mComparator);
        for (int i = 0; i < list.size(); i++) {
            assertTrue(list.get(i).equals(newList.get(i)));
        }
    }

    @Test
    public void testGetEventsByTimestamp() {
        List<Earthquake> list = mHelper.getEventsAfterTimestamp(1527868800000l);
        assertEquals(4, list.size());
        for (Earthquake event : list) {
            assertTrue(event.getTimeStamp() > 1527868800000l);
        }
    }

    @Test
    public void testDeleteEventsByTimestamp() {
        mHelper.deleteEventsByTimestamp(Calendar.getInstance().getTimeInMillis());
        List<Earthquake> list = fakeEventList();
        mHelper.addEvents(list);
        mHelper.deleteEventsByTimestamp(Calendar.getInstance().getTimeInMillis());
        List<Earthquake> newList = mHelper.getAllEvents();
        assertTrue(newList.size() == 0);
    }

    @Test
    public void testGetEventsById() {
        mHelper.deleteEventsByTimestamp(Calendar.getInstance().getTimeInMillis());
        List<Earthquake> list = fakeEventList();
        mHelper.addEvents(list);
        mHelper.addEvents(list);
        List<Earthquake> newList = mHelper.getEventsAfterTimestamp(1527894067941l,1527894067943l);
        assertTrue(newList.size() == 2);
    }

    private List<Earthquake> fakeEventList() {
        Earthquake event1 = new Earthquake("hv70219532", "7km SW of Volcano, Hawaii", 1427894067940l,
                "https://earthquake.usgs.gov/earthquakes/eventpage/hv70219532", -155.2904968, 19.4064999);
        Earthquake event2 = new Earthquake("ak19696829", "7km SW of Volcano, Hawaii", 1527892907060l,
                "https://earthquake.usgs.gov/earthquakes/eventpage/hv70219532", -155.2904968, 19.4064999);
        Earthquake event3 = new Earthquake("hv70219487", "7km SW of Volcano, Hawaii", 1527894067941l,
                "https://earthquake.usgs.gov/earthquakes/eventpage/hv70219532", -155.2904968, 19.4064999);
        Earthquake event4 = new Earthquake("hv70219467", "7km SW of Volcano, Hawaii", 1527894067942l,
                "https://earthquake.usgs.gov/earthquakes/eventpage/hv70219532", -155.2904968, 19.4064999);
        Earthquake event5 = new Earthquake("hv70219462", "7km SW of Volcano, Hawaii", 1527894067943l,
                "https://earthquake.usgs.gov/earthquakes/eventpage/hv70219532", -155.2904968, 19.4064999);
        List<Earthquake> res = new ArrayList<>();
        res.add(event1);
        res.add(event2);
        res.add(event3);
        res.add(event4);
        res.add(event5);
        return res;
    }
}