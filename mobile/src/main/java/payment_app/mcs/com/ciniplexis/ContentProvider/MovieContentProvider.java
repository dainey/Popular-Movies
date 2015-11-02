package payment_app.mcs.com.ciniplexis.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;


import payment_app.mcs.com.ciniplexis.Contracts.DataContracts;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;
import payment_app.mcs.com.ciniplexis.Data.MovieDBHelper;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieContentProvider extends ContentProvider {

    private MovieDBHelper dbHelper;
    private static final UriMatcher myUriMatcher = buildUriMatcher();

    public static final int MOVIE = 0;
    public static final int MOVIE_BY_POPULARITY = 1;
    public static final int MOVIE_BETWEEN_DATES = 2;
    public static final int MOVIE_BY_RATING = 3;
    public static final int MOVIE_BY_DATE = 4;
    public static final int MOVIE_BY_ID = 5;
    public static final int MOVIE_FILTER_TITLE = 6;
    public static final int REVIEW = 7;

    public static final SQLiteQueryBuilder movieQueryBuilder = new SQLiteQueryBuilder();
    public static final SQLiteQueryBuilder movieReviewQueryBuilder = new SQLiteQueryBuilder();

    static {
        movieQueryBuilder.setTables(MovieEntry.TABLE_NAME);
        movieReviewQueryBuilder.setTables(MovieEntry.TABLE_NAME + " INNER JOIN " + ReviewEntry.TABLE_NAME +
                " ON " + MovieEntry.TABLE_NAME + "." + MovieEntry._ID +
                " = " + ReviewEntry.TABLE_NAME + "." + ReviewEntry.FK_COLUMN_MOVIE_ID);
    }

    public static UriMatcher buildUriMatcher() {

        UriMatcher myMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = DataContracts.getContentAuthority();
        String moviePath = MovieEntry.getMoviePath();
        String reviewPath = ReviewEntry.getReviewPath();
        myMatcher.addURI(authority, moviePath, MOVIE);
        myMatcher.addURI(authority, reviewPath, REVIEW);
        myMatcher.addURI(authority, moviePath + "/sort/popularity", MOVIE_BY_POPULARITY);
        myMatcher.addURI(authority, moviePath + "/betweenDates/?start_date=*&end_date=*", MOVIE_BETWEEN_DATES);
        myMatcher.addURI(authority, moviePath + "/betweenDates/*", MOVIE_BETWEEN_DATES);

        myMatcher.addURI(authority, moviePath + "/sort/rating", MOVIE_BY_RATING);
        myMatcher.addURI(authority, moviePath + "/sort/date", MOVIE_BY_DATE);
        myMatcher.addURI(authority, moviePath + "/f_name/*", MOVIE_FILTER_TITLE);
        myMatcher.addURI(authority, moviePath + "/#", MOVIE_BY_ID);

        return myMatcher;

    }

    private Cursor getMovieByDate(String[] projection, String selection, String[] selectionArgs) {
        String sortMoviesByDate = MovieEntry.COLUMN_RELEASE_DATE + " DESC ";
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortMoviesByDate);
    }

    private Cursor getMovieByPopularity(String[] projection, String selection, String[] selectionArgs) {
        String sortMostPopularMovies = MovieEntry.COLUMN_AVERAGE_POPULARITY + " DESC ";
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortMostPopularMovies);
    }

    private Cursor getMovieByRating(String[] projection, String selection, String[] selectionArgs) {
        String sortMovieByRatings = MovieEntry.COLUMN_RATING + " DESC ";
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortMovieByRatings);
    }

    private Cursor getMoviesBetweenDates(String[] projection, String[] dates, String sortOrder) {
        String seeMoviesBetweenDates = MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_RELEASE_DATE
                + " BETWEEN ? AND ?";
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                seeMoviesBetweenDates,
                dates,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieById(String[] projection, String[] selectionArgs) {
        String selectMovieById = MovieEntry._ID + "=? ";
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selectMovieById,
                selectionArgs,
                null,
                null,
                null);
    }

    private Cursor filterMovieTitle(String[] projection, String[] selectionArgs) {
        String whereMovieNameLike = MovieEntry.COLUMN_TITLE + " like ? OR "
                + MovieEntry.COLUMN_TITLE + " like ? OR "
                + MovieEntry.COLUMN_TITLE + " like ? ";
        String title = selectionArgs[0];
        String[] arg = new String[]{"%" + title, title + "%", "%" + title + "%"};
        return movieQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                whereMovieNameLike,
                arg,
                null,
                null,
                MovieEntry.COLUMN_RELEASE_DATE + " DESC ");
    }

    private Cursor getMovieReviews(String[] projection, String[] selectionArgs) {
        String whereMovieId = MovieEntry.TABLE_NAME + "." + MovieEntry._ID + "= ? ";
        String sortReviewsByDate = ReviewEntry.COLUMN_COMMENT_POST_DATE + " DESC ";
        return movieReviewQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                whereMovieId,
                selectionArgs,
                null,
                null,
                sortReviewsByDate);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int opt = myUriMatcher.match(uri);
        switch (opt) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_TYPE;
            case MOVIE_BY_DATE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_POPULARITY:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_RATING:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_BETWEEN_DATES:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_BY_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_FILTER_TITLE:
                return MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor myCursor;

        final int opt = myUriMatcher.match(uri);
        switch (opt) {

            case REVIEW:
                myCursor = getMovieReviews(projection, selectionArgs);
                break;

            case MOVIE_BY_DATE:
                myCursor = getMovieByDate(projection, selection, selectionArgs);
                break;

            case MOVIE:
            case MOVIE_BY_POPULARITY:
                myCursor = getMovieByPopularity(projection, selection, selectionArgs);
                break;

            case MOVIE_BY_RATING:
                myCursor = getMovieByRating(projection, selection, selectionArgs);
                break;

            case MOVIE_BETWEEN_DATES:
                String[] args = new String[2];
                args[0] = uri.getQueryParameter("start_date");
                args[1] = uri.getQueryParameter("end_date");
                myCursor = getMoviesBetweenDates(projection, args, sortOrder);
                break;

            case MOVIE_BY_ID:
                myCursor = getMovieById(projection, new String[]{uri.getLastPathSegment()});
                break;
            case MOVIE_FILTER_TITLE:
                selectionArgs = new String[]{uri.getLastPathSegment()};
                myCursor = filterMovieTitle(projection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown select Uri: " + uri);
        }

        if (getContext() != null)
            myCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return myCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int opt = myUriMatcher.match(uri);

        Uri returnUri;
        long _rowId;

        switch (opt) {
            case MOVIE:
                _rowId = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_rowId > 0)
                    returnUri = MovieEntry.buildMovieUri(_rowId);
                else
                    throw new android.database.SQLException("Failed to add movie into " + uri);

                break;
            case REVIEW:
                _rowId = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (_rowId > 0)
                    returnUri = ReviewEntry.buildReviewUri(_rowId);
                else
                    throw new android.database.SQLException("Failed to add review into " + uri);

                break;
            default:
                throw new UnsupportedOperationException("Unknown insert Uri: " + uri);
        }


        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numOfRows;
        final int match = myUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                numOfRows = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                numOfRows = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown update Uri: " + uri);
        }

        if (getContext() != null && (numOfRows != 0 || selection == null))
            getContext().getContentResolver().notifyChange(uri, null);
        return numOfRows;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numOfRows;
        final int match = myUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                numOfRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                numOfRows = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown delete Uri: " + uri);
        }

        if ((numOfRows != 0 || selection == null) && getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return numOfRows;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int opt = myUriMatcher.match(uri);

        int retCount;


        switch (opt) {
            case MOVIE:
                retCount = doInsert(db, MovieEntry.TABLE_NAME, null, values);
                break;

            case REVIEW:
                retCount = doInsert(db, ReviewEntry.TABLE_NAME, null, values);
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);

        return retCount;


    }

    private int doInsert(SQLiteDatabase db, String table, String colHack, ContentValues[] columns) {

        if (db == null || !db.isOpen())
            return -1;

        int retCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues column : columns) {
                long _id = db.insert(table, null, column);
                if (_id != -1)
                    retCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return retCount;

    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}
