package payment_app.mcs.com.ciniplexis.Data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;


import payment_app.mcs.com.ciniplexis.ContentProvider.MovieContentProvider;
import payment_app.mcs.com.ciniplexis.Contracts.DataContracts;
import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;

/**
 * Created by ogayle on 27/10/2015.
 */
public class TestMovieContentProvider extends AndroidTestCase {


    public void deleteAllRecordsFromMovieContentProvider() {
        mContext.getContentResolver()
                .delete(ReviewEntry.CONTENT_URI,
                        null,
                        null);

        mContext.getContentResolver()
                .delete(MovieEntry.CONTENT_URI,
                        null,
                        null);


        Cursor mCursor = mContext.getContentResolver()
                .query(MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        assertEquals("Error: Records were not deleted from the Movie table", 0, mCursor.getCount());

        mCursor.close();

        mCursor = mContext.getContentResolver()
                .query(ReviewEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        assertEquals("Error: Records were not deleted from the Review table", 0, mCursor.getCount());

        mCursor.close();

    }

    public void deleteAllRecordsFromDB() {
        MovieDBHelper mDb = new MovieDBHelper(mContext);
        SQLiteDatabase db = mDb.getWritableDatabase();

        db.delete(ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
        mDb.close();
    }


    public void deleteAllRecords() {
        deleteAllRecordsFromMovieContentProvider();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    /**
     * Test to ensure that the content provider is correctly registered
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieContentProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals(String.format("Error: MovieContentProvider registered with authority: %s instead of authority %s",
                            providerInfo.authority,
                            DataContracts.getContentAuthority()),
                    providerInfo.authority, DataContracts.getContentAuthority());

        } catch (PackageManager.NameNotFoundException nnf) {
            assertTrue("Error: MovieContentProvider not Registered at " + mContext.getPackageName(), false);
        }
    }

    /**
     * Test to ensure that the content provider is returns the correct type for each type of Uri it can handle
     */
    public void testGetType() {
        Uri actualContent = MovieEntry.CONTENT_URI;
        String expectedContent = MovieEntry.CONTENT_TYPE;

        String actualContentString = mContext.getContentResolver().getType(actualContent);


        assertEquals("Error: the MovieEntry.CONTENT_URI should return MovieEntry.CONTENT_TYPE", expectedContent, actualContentString);


        String testPopularity = "popularity";
        String testRating = "rating";
        String testDate = "date";
        String testNowShowing = "now_showing";
        String testMovieWithReview = "details/I am Legend";

        actualContentString = mContext.getContentResolver().getType(MovieEntry.buildMovieBySortCriteria(testPopularity));
        assertEquals("Error: the MovieEntry.CONTENT_URI should return MovieEntry.CONTENT_TYPE", MovieEntry.CONTENT_TYPE, actualContentString);

        actualContentString = mContext.getContentResolver().getType(MovieEntry.buildMovieBySortCriteria(testRating));
        assertEquals("Error: the MovieEntry.CONTENT_URI should return MovieEntry.CONTENT_TYPE", MovieEntry.CONTENT_TYPE, actualContentString);

        actualContentString = mContext.getContentResolver().getType(MovieEntry.buildMovieBySortCriteria(testDate));
        assertEquals("Error: the MovieEntry.CONTENT_URI should return MovieEntry.CONTENT_TYPE", MovieEntry.CONTENT_TYPE, actualContentString);

        actualContentString = mContext.getContentResolver().getType(MovieEntry.buildMovieBySortCriteria(testNowShowing));
        assertEquals("Error: the MovieEntry.CONTENT_URI should return MovieEntry.CONTENT_TYPE", MovieEntry.CONTENT_TYPE, actualContentString);

    }


    /**
     * Test uses the database to validate the insert function of the content provider and then read to confirm
     */

    public void testBasicInsertMovieQuery() {
        //MovieDBHelper mDb = new MovieDBHelper(mContext);
        //SQLiteDatabase db = mDb.getWritableDatabase();

        ContentValues movieCV = TestUtilities.createMovieContentValues();

        long insertResult = TestUtilities.insertMovieContentValues(mContext, movieCV);
        assertTrue("Error: Failure to insert movie record ", insertResult != -1);


        //test basic query

        Cursor myCursor = mContext.getContentResolver()
                .query(MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null,
                        null);


        TestUtilities.validateCursor("testBasicInsertMovieQuery: Movie", myCursor, movieCV);

        //getNotificationUri was added in at API level 19
        if (Build.VERSION.SDK_INT >= 19)
            assertEquals("Error: Movie query did not properly set Notification Uri", myCursor.getNotificationUri(), MovieEntry.CONTENT_URI);


        myCursor.close();

        // db.close();

    }


    /**
     * Test uses the database to validate the insert function of the content provider and then read to confirm
     */

    public void testBasicInsertReviewQuery() {


        ContentValues reviewCV = TestUtilities.createReviewContentValues();
        ContentValues movieCV = TestUtilities.createMovieContentValues();

        long insertResult = TestUtilities.insertMovieContentValues(mContext, movieCV);
        assertTrue("Error: Failure to insert movie record ", insertResult != -1);


        assertTrue("Error: Failure to insert review record ", TestUtilities.insertReviewContentValues(mContext, reviewCV) != -1);

        //test basic query

        Cursor myCursor = mContext.getContentResolver()
                .query(ReviewEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testBasicInsertReviewQuery: Review", myCursor, reviewCV);

        if (Build.VERSION.SDK_INT >= 19)
            assertEquals("Error: Review query did not properly set Notification Uri", myCursor.getNotificationUri(), ReviewEntry.CONTENT_URI);

    }


    public void testInsertReadProvider() {

        ContentValues movieCV = TestUtilities.createMovieContentValues();

        //Register content observer for the insert being performed
        TestUtilities.TestContentObserver contentObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, contentObserver);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieCV);

        //check to see if the content observer was indeed notified
        contentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(contentObserver);

        long movieRowId = ContentUris.parseId(movieUri);

        //confirm the insert
        assertTrue(movieRowId != -1);

        // Confirm the contents of the insert

        Cursor myCursor = mContext.getContentResolver()
                .query(MovieEntry.CONTENT_URI,
                        null,//leaving null returns all columns
                        null,//column for where clause
                        null,//value for where clause
                        null);//sort order

        TestUtilities.validateCursor("testInsertReader for movies", myCursor, movieCV);


        //On confirmation we can add Reviews to the movie

        ContentValues reviewCV = TestUtilities.createReviewContentValues();

        TestUtilities.TestContentObserver reviewContentObserver = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewContentObserver);

        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewCV);

        reviewContentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewContentObserver);

        long reviewId = ContentUris.parseId(reviewUri);

        assertTrue(reviewId != -1);

        Cursor reviewCursor = mContext.getContentResolver()
                .query(ReviewEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Review", reviewCursor, reviewCV);


        String testTitle = "details/Triplets";

        Cursor testCursor = mContext.getContentResolver()
                .query(MovieEntry.buildMovieBySortCriteria("popularity"),
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Movie Popularity", testCursor, movieCV);


        testCursor.close();

        testCursor = mContext.getContentResolver()
                .query(MovieEntry.buildMovieBySortCriteria("rating"),
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Movie Ratings", testCursor, movieCV);

        testCursor.close();

        testCursor = mContext.getContentResolver()
                .query(MovieEntry.buildMovieBySortCriteria("date"),
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Movie Date", testCursor, movieCV);

        testCursor.close();
        Uri uri = MovieEntry.buildMovieUri(1);
        testCursor = mContext.getContentResolver()
                .query(uri,
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Movie By Id", testCursor, movieCV);

        testCursor.close();

        testCursor = mContext.getContentResolver()
                .query(MovieEntry.buildMovieBySortCriteria("now_showing"),
                        null,
                        null,
                        null,
                        null);

        TestUtilities.validateCursor("testInsertReader for Movie Now Showing", testCursor, movieCV);
        testCursor.close();
    }

    public void testUpdateReaderProvider() {

       // testBasicInsertReviewQuery();
        ContentValues movieCV = TestUtilities.updateMovieContentValues();

        TestUtilities.TestContentObserver movieCObserver = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieCObserver);
        mContext.getContentResolver().update(MovieEntry.CONTENT_URI, movieCV, null, null);

        movieCObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieCObserver);

        Cursor testCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
        TestUtilities.validateCursor("testUpdateReaderProvider for Movie", testCursor, movieCV);

        testCursor.close();


        ContentValues reviewCV = TestUtilities.updateReviewContentValues();

        TestUtilities.TestContentObserver reviewCObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewCObserver);
        mContext.getContentResolver().update(ReviewEntry.CONTENT_URI, reviewCV, null, null);

        reviewCObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewCObserver);

        testCursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.validateCursor("testUpdateReader for Review", testCursor, reviewCV);


    }


    public void testDeleteReaderProvider() {

         testBasicInsertReviewQuery();

        TestUtilities.TestContentObserver movieCObserver = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieCObserver);
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);

        movieCObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieCObserver);

        Cursor testCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
        assertTrue(testCursor.getCount()==0);

        testCursor.close();


        TestUtilities.TestContentObserver reviewCObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewCObserver);
        mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI, null, null);

        reviewCObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewCObserver);

        testCursor = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);

        assertTrue(testCursor.getCount() == 0);


    }


}
