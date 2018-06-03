package kevinliao.com.quakefinder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kevinliao.com.quakefinder.network.Earthquake;

public class EventDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION_1 = 1;
    private static final String DATABASE_NAME = "earthquake_event";
    private static final String TABLE_EVENTS = "events";

    private static int INDEX_EVENT_ID;
    private static int INDEX_EVENT_PLACE;
    private static int INDEX_EVENT_TMESTAMP;
    private static int INDEX_EVENT_URL;
    private static int INDEX_EVENT_LATITUDE;
    private static int INDEX_EVENT_LONGITUDE;

    private static EventDatabaseHelper INSTANCE;


    public enum EventData {
        EVENT_ID_KEY("id"),
        PLACE_KEY("place"),
        TIMESTAMP_KEY("time"),
        URL_KEY("url"),
        LATITUDE("latitude"),
        LONGITUDE_KEY("longitude");
        private final String mKey;

        private EventData(String s) {
            mKey = s;
        }

        public String toString() {
            return mKey;
        }
    }

    public static EventDatabaseHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new EventDatabaseHelper(context);
        }
        return INSTANCE;
    }

    private EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION_1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + EventData.EVENT_ID_KEY.toString() + " TEXT PRIMARY KEY UNIQUE,"
                + EventData.PLACE_KEY.toString() + " TEXT,"
                + EventData.TIMESTAMP_KEY.toString() + " REAL,"
                + EventData.URL_KEY.toString() + " TEXT,"
                + EventData.LATITUDE.toString() + " TEXT,"
                + EventData.LONGITUDE_KEY.toString() + " TEXT"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Earthquake> getAllEvents() {
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;
        return getEvents(selectQuery);
    }

    public List<Earthquake> getEventsAfterTimestamp(long t) {
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS
                + " WHERE " + EventData.TIMESTAMP_KEY.toString() + " >" + t;
        return getEvents(selectQuery);
    }

    public List<Earthquake> getEventsAfterTimestamp(long start, long end) {
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS
                + " WHERE " + EventData.TIMESTAMP_KEY.toString() + " BETWEEN " + start + " AND " + end;
        return getEvents(selectQuery);
    }

    private List<Earthquake> getEvents(String query) {
        List<Earthquake> eventList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        collectIndex(cursor);
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.moveToFirst()) {
                do {
                    Earthquake event = generateEarthquakeEvent(cursor);
                    eventList.add(event);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return eventList;
    }

    public synchronized void addEvents(List<Earthquake> list) {
        if(list == null || list.size()==0) return;
        for (Earthquake event : list) {
            addEvent(event);
        }
    }

    public synchronized long addEvent(Earthquake event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = getContentValuesHelper(event);
        long rowId = -1;
        if (db.isOpen()) {
            rowId = db.insert(TABLE_EVENTS, null, values);
        }
        db.close();
        return rowId;
    }

    private ContentValues getContentValuesHelper(Earthquake event) {
        ContentValues values = new ContentValues();
        values.put(EventData.EVENT_ID_KEY.toString(), event.getId());
        values.put(EventData.PLACE_KEY.toString(), event.getPlace().toString());
        values.put(EventData.TIMESTAMP_KEY.toString(), event.getTimeStamp());
        values.put(EventData.URL_KEY.toString(), event.getUrl().toString());
        values.put(EventData.LATITUDE.toString(), event.getLatitude());
        values.put(EventData.LONGITUDE_KEY.toString(), event.getLongitude());
        return values;
    }

    private Earthquake generateEarthquakeEvent(Cursor cursor) {
        return new Earthquake.Builder()
                .setId(cursor.getString(INDEX_EVENT_ID))
                .setPlace(cursor.getString(INDEX_EVENT_PLACE))
                .setUrl(cursor.getString(INDEX_EVENT_URL))
                .setTime(cursor.getLong(INDEX_EVENT_TMESTAMP))
                .setLongitude(cursor.getDouble(INDEX_EVENT_LONGITUDE))
                .setLatitude(cursor.getDouble(INDEX_EVENT_LATITUDE))
                .create();
    }

    private void collectIndex(Cursor cursor) {
        INDEX_EVENT_ID = cursor.getColumnIndex(EventData.EVENT_ID_KEY.toString());
        INDEX_EVENT_PLACE = cursor.getColumnIndex(EventData.PLACE_KEY.toString());
        INDEX_EVENT_TMESTAMP = cursor.getColumnIndex(EventData.TIMESTAMP_KEY.toString());
        INDEX_EVENT_URL = cursor.getColumnIndex(EventData.URL_KEY.toString());
        INDEX_EVENT_LATITUDE = cursor.getColumnIndex(EventData.LATITUDE.toString());
        INDEX_EVENT_LONGITUDE = cursor.getColumnIndex(EventData.LONGITUDE_KEY.toString());
    }

    public int deleteEventsByTimestamp(long t) {
        SQLiteDatabase db = getWritableDatabase();
        int deletedEventRows = -1;
        if (db.isOpen())
            deletedEventRows = db.delete(TABLE_EVENTS, EventData.TIMESTAMP_KEY.toString() + " < ?", new String[]{String.valueOf(t)});
        db.close();
        return deletedEventRows;
    }
}
