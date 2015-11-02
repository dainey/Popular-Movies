package payment_app.mcs.com.ciniplexis.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;
import payment_app.mcs.com.ciniplexis.Utils.PollingCheck;

/**
 * Created by ogayle on 28/10/2015.
 */
public class TestUtilities extends AndroidTestCase {

    public static ContentValues createMovieContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry._ID, 1);
        cv.put(MovieEntry.COLUMN_TITLE, "Triplets");
        cv.put(MovieEntry.COLUMN_PLOT, "There were once three sisters and they were triplets");
        cv.put(MovieEntry.COLUMN_RATING, 8);
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, "2015-08-28");
        cv.put(MovieEntry.COLUMN_PRICE, 120);
        cv.put(MovieEntry.COLUMN_IMAGE_URL, "image_url");
        cv.put(MovieEntry.COLUMN_AVERAGE_POPULARITY, 50);
        cv.put(MovieEntry.COLUMN_IS_PURCHASED, 0);//(false)

        return cv;
    }

    public static ContentValues updateMovieContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry._ID, 1);
        cv.put(MovieEntry.COLUMN_TITLE, "Triplets II");
        cv.put(MovieEntry.COLUMN_PLOT, "There were once three sisters and they were triplets again");
        cv.put(MovieEntry.COLUMN_RATING, 8);
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, "2015-09-20");
        cv.put(MovieEntry.COLUMN_PRICE, 120);
        cv.put(MovieEntry.COLUMN_IMAGE_URL, "image_url");
        cv.put(MovieEntry.COLUMN_AVERAGE_POPULARITY, 50);
        cv.put(MovieEntry.COLUMN_IS_PURCHASED, 0);//(false)

        return cv;
    }

    public static ContentValues createReviewContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ReviewEntry._ID, 1);
        cv.put(ReviewEntry.USERNAME, "dain");
        cv.put(ReviewEntry.COLUMN_COMMENT_POST, "movie wasnt bad at all");
        cv.put(ReviewEntry.COLUMN_COMMENT_POST_DATE, "2015-09-21");
        cv.put(ReviewEntry.FK_COLUMN_MOVIE_ID, 1);
        return cv;
    }


    public static ContentValues updateReviewContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ReviewEntry._ID, 1);
        cv.put(ReviewEntry.USERNAME, "Odaine");
        cv.put(ReviewEntry.COLUMN_COMMENT_POST, "movie wasn't bad at all");
        cv.put(ReviewEntry.COLUMN_COMMENT_POST_DATE, "2015-08-29");
        cv.put(ReviewEntry.FK_COLUMN_MOVIE_ID, 1);
        return cv;
    }


    public static long insertMovieContentValues(Context _context, ContentValues movie) {
        MovieDBHelper mDb = new MovieDBHelper(_context);
        SQLiteDatabase db = mDb.getWritableDatabase();

        return db.insert(MovieEntry.TABLE_NAME, null, movie);
    }

    public static long insertReviewContentValues(Context _context, ContentValues review) {
        MovieDBHelper mDb = new MovieDBHelper(_context);
        SQLiteDatabase db = mDb.getWritableDatabase();

        return db.insert(ReviewEntry.TABLE_NAME, null, review);
    }

    public static void validateCursor(String error, Cursor cursor, ContentValues expected) {
        assertTrue("Empty Cursor returned " + error, cursor.moveToFirst());
        validateCurrentRecord(error, cursor, expected);
        cursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor cursor, ContentValues expected) {
        Set<Map.Entry<String, Object>> valueSet = expected.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = cursor.getColumnIndex(columnName);
            assertFalse("Column " + columnName + " not found " + error, index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Values " + entry.getValue().toString() + " did not match the expected value " + expectedValue +
                    " . " + error, expectedValue, cursor.getString(index));
        }
    }


    static TestContentObserver getTestContentObserver(){
        return TestContentObserver.getTestContentObserver();
    }



    static class TestContentObserver extends ContentObserver{
        final HandlerThread handlerThread;
        boolean contentChanged;

        private TestContentObserver(HandlerThread ht){
            super(new Handler(ht.getLooper()));
            handlerThread = ht;
        }


        static TestContentObserver getTestContentObserver(){
            HandlerThread ht = new HandlerThread("myContentObserver Thread");
            ht.start();
            return new TestContentObserver(ht);
        }


        //this is the default method on some android devices
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            contentChanged = true;
        }


        public void waitForNotificationOrFail(){
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return contentChanged;
                }
            }.run();
            handlerThread.quit();
        }



    }

}
