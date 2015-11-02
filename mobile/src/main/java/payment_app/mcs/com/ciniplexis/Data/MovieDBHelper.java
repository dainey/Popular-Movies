package payment_app.mcs.com.ciniplexis.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;

/**
 * Created by ogayle on 26/10/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;


    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_REVIEW_TABLE = "Create Table " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                ReviewEntry.USERNAME + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_COMMENT_POST + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_COMMENT_POST_DATE + " TEXT NOT NULL, " +
                ReviewEntry.FK_COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ( " + ReviewEntry.FK_COLUMN_MOVIE_ID + " ) REFERENCES " + MovieEntry.TABLE_NAME + " ( " + MovieEntry._ID + " ) " +
                ")";


        final String SQL_CREATE_MOVIE_TABLE = "Create Table " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_PLOT + " TEXT, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_URL + " TEXT , " +
                MovieEntry.COLUMN_BACKGROUND_IMAGE_URL + " TEXT , " +
                MovieEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_AVERAGE_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_IS_PURCHASED + " BOOLEAN NOT NULL" +
                ")";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
