package payment_app.mcs.com.ciniplexis.Data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;

/**
 * Created by ogayle on 27/10/2015.
 */
public class TestDB extends AndroidTestCase {

    void deleteDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteDatabase();
    }


    public void testDBCreation() throws Throwable {
        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(MovieEntry.TABLE_NAME);
        tableNames.add(ReviewEntry.TABLE_NAME);

        deleteDatabase();

        SQLiteDatabase db = new MovieDBHelper(this.getContext()).getWritableDatabase();

        assertEquals(true, db.isOpen());

        //Check to see if the tables were created
        Cursor cursor = db.rawQuery("Select name From sqlite_master Where type ='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", cursor.moveToFirst());

        //Verify that the tables have been created
        do {
            tableNames.remove(cursor.getString(0));
        } while (cursor.moveToNext());


        assertTrue("Error: both movie and review tables were not created", tableNames.isEmpty());

        //check that all the columns exist and are correctly named
        cursor = db.rawQuery("PRAGMA table_info( " + MovieEntry.TABLE_NAME + " )", null);

        HashSet<String> movieTableColumns = new HashSet<>();
        movieTableColumns.add(MovieEntry._ID);
        movieTableColumns.add(MovieEntry.COLUMN_TITLE);
        movieTableColumns.add(MovieEntry.COLUMN_PLOT);
        movieTableColumns.add(MovieEntry.COLUMN_RATING);
        movieTableColumns.add(MovieEntry.COLUMN_RELEASE_DATE);
        movieTableColumns.add(MovieEntry.COLUMN_IMAGE_URL);
        movieTableColumns.add(MovieEntry.COLUMN_PRICE);
        movieTableColumns.add(MovieEntry.COLUMN_AVERAGE_POPULARITY);
        movieTableColumns.add(MovieEntry.COLUMN_IS_PURCHASED);
        cursor.moveToFirst();
        int colIndex = cursor.getColumnIndex("name");

        do {
            String column = cursor.getString(colIndex);
            movieTableColumns.remove(column);
        } while (cursor.moveToNext());

        assertTrue("Error: All the columns for the movie table does not exist", movieTableColumns.isEmpty());


        cursor = db.rawQuery("PRAGMA table_info( " + ReviewEntry.TABLE_NAME + " )", null);

        HashSet<String> reviewTableColumns = new HashSet<>();
        reviewTableColumns.add(ReviewEntry._ID);
        reviewTableColumns.add(ReviewEntry.USERNAME);
        reviewTableColumns.add(ReviewEntry.COLUMN_COMMENT_POST);
        reviewTableColumns.add(ReviewEntry.COLUMN_COMMENT_POST_DATE);
        reviewTableColumns.add(ReviewEntry.FK_COLUMN_MOVIE_ID);

        cursor.moveToFirst();
        colIndex = cursor.getColumnIndex("name");
        do {
            String column = cursor.getString(colIndex);
            reviewTableColumns.remove(column);
        } while (cursor.moveToNext());

        assertTrue("Error: All the columns for the review table does not exist", reviewTableColumns.isEmpty());

        cursor.close();
        db.close();

    }


    public void testMovieTable() {

        SQLiteDatabase db = new MovieDBHelper(this.getContext()).getWritableDatabase();


        long rowID = db.insert(MovieEntry.TABLE_NAME, null, TestUtilities.createMovieContentValues());

        assertTrue(rowID != -1);

        Cursor movieCursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);

        movieCursor.moveToFirst();

        int index = movieCursor.getColumnIndex(MovieEntry._ID);
        assertEquals(movieCursor.getInt(index), 1);

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        assertEquals(movieCursor.getString(index), "Triplets");

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_PLOT);
        assertEquals(movieCursor.getString(index), "There were once three sisters and they were triplets");

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_RATING);
        assertEquals(movieCursor.getDouble(index), 8.0);

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
        assertEquals(movieCursor.getString(index), "2015-08-21");

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_PRICE);
        assertEquals(movieCursor.getDouble(index), 120.0);

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL);
        assertEquals(movieCursor.getString(index), "image_url");

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_AVERAGE_POPULARITY);
        assertEquals(movieCursor.getDouble(index), 50.0);

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_IS_PURCHASED);
        assertEquals(movieCursor.getInt(index), 0);

        movieCursor.close();
        db.close();


    }

    public void testReviewTable() {

        SQLiteDatabase db = new MovieDBHelper(this.getContext()).getWritableDatabase();


        long rowId = db.insert(ReviewEntry.TABLE_NAME, null, TestUtilities.createReviewContentValues());

        assertNotSame(-1, rowId);

        Cursor reviewCursor = db.query(ReviewEntry.TABLE_NAME, null, null, null, null, null, null);

        reviewCursor.moveToFirst();

        int index = reviewCursor.getColumnIndex(ReviewEntry._ID);
        assertEquals(reviewCursor.getInt(index), 1);

        index = reviewCursor.getColumnIndex(ReviewEntry.USERNAME);
        assertEquals(reviewCursor.getString(index), "dain");

        index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_COMMENT_POST);
        assertEquals(reviewCursor.getString(index), "movie wasnt bad at all");

        index = reviewCursor.getColumnIndex(ReviewEntry.FK_COLUMN_MOVIE_ID);
        assertEquals(reviewCursor.getInt(index), 1);

        index = reviewCursor.getColumnIndex(ReviewEntry.COLUMN_COMMENT_POST_DATE);
        assertEquals(reviewCursor.getString(index), "2015-08-21");


        reviewCursor.close();
        db.close();


    }

}
