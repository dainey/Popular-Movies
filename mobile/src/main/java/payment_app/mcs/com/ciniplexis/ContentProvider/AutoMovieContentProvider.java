package payment_app.mcs.com.ciniplexis.ContentProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

import payment_app.mcs.com.ciniplexis.Data.AutoMovieDB;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.ReviewColumns;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.VideoColumns;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoCollection;

/**
 * Created by ogayle on 09/11/2015.
 */

@ContentProvider(
        authority = AutoMovieContentProvider.CONTENT_AUTHORITY,
        database = AutoMovieDB.class,
        packageName = "payment_app.mcs.com.ciniplexis.providers"
)
public class AutoMovieContentProvider {

    public static final String CONTENT_AUTHORITY = "payment_app.mcs.com.ciniplexis";
    protected static final Uri BASE_CONT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    protected static final String MOVIE_PATH = "movie";
    protected static final String REVIEW_PATH = "review";
    protected static final String VIDEO_PATH = "video";

    private static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";


    @TableEndpoint(table = AutoMovieDB.VIDEO)
    public static class Video {
        @ContentUri(
                path = VIDEO_PATH,
                type = CONTENT_DIR_TYPE + VIDEO_PATH
        )
        public static final Uri CONTENT_URI = BASE_CONT_URI.buildUpon().appendPath(VIDEO_PATH).build();

        @InexactContentUri(
                name = "VIDEO_ID",
                path = VIDEO_PATH + "/id/*",
                type = CONTENT_ITEM_TYPE + VIDEO_PATH,
                whereColumn = VideoColumns._ID,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 2

        )
        public static Uri buildVideoUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        @InexactContentUri(
                name = "BY_MOVIE_ID",
                path = VIDEO_PATH + "/#",
                type = CONTENT_DIR_TYPE + VIDEO_PATH,
                whereColumn = VideoColumns.FK_MOVIE_ID,
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 1
        )
        public static Uri buildMovieVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().build(), id);
        }


        @NotifyInsert(paths = VIDEO_PATH)
        public static Uri[] onInsert(ContentValues values) {
            final String videoId = values.getAsString(VideoColumns._ID);

            return new Uri[]{
                    //buildVideoUri(videoId),
                    CONTENT_URI

            };
        }

        @NotifyDelete(paths = VIDEO_PATH + "/id/*")
        public static Uri[] onDelete(Context context, Uri uri) {


            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final String videoId = cursor.getString(cursor.getColumnIndex(VideoColumns._ID));

            cursor.close();


            return new Uri[]{
                    // buildVideoUri(videoId),
                    CONTENT_URI
            };
        }

        @NotifyBulkInsert(paths = VIDEO_PATH)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] contentValues, long[] ids) {
            return new Uri[]{
                    uri
            };
        }


        @NotifyUpdate(paths = VIDEO_PATH + "/id/*")
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            final long id = ContentUris.parseId(uri);
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    VideoColumns._ID}, null, null, null);


            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;
            //final long updatedId = cursor.getInt(cursor.getColumnIndex(VideoColumns.FK_MOVIE_ID));
            final String updatedId = cursor.getString(cursor.getColumnIndex(VideoColumns._ID));

            cursor.close();

            return new Uri[]{
                    buildVideoUri(updatedId)
            };


        }
    }

    @TableEndpoint(table = AutoMovieDB.REVIEW)
    public static class Review {

        @ContentUri(
                path = REVIEW_PATH,
                type = CONTENT_DIR_TYPE + REVIEW_PATH,
                defaultSort = ReviewColumns.COMMENT_DATE + " DESC "
        )
        public static final Uri CONTENT_URI = BASE_CONT_URI.buildUpon().appendPath(REVIEW_PATH).build();

        @InexactContentUri(
                name = "REVIEW_ID",
                path = REVIEW_PATH + "/id/*",
                type = CONTENT_ITEM_TYPE + REVIEW_PATH,
                whereColumn = ReviewColumns._ID,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 2

        )
        public static Uri buildReviewUri(String id) {
            return CONTENT_URI.buildUpon().appendEncodedPath(id).build();
        }

        @InexactContentUri(
                name = "BY_MOVIE_ID",
                path = REVIEW_PATH + "/movie/#",
                type = CONTENT_DIR_TYPE + REVIEW_PATH,
                whereColumn = ReviewColumns.FK_MOVIE_ID,
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 2

        )
        public static Uri buildMovieReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI.buildUpon().appendEncodedPath("movie").build(), id);
        }


        @NotifyInsert(paths = REVIEW_PATH)
        public static Uri[] onInsert(ContentValues values) {
            final String reviewId = values.getAsString(ReviewColumns._ID);

            return new Uri[]{
                    buildReviewUri(reviewId),
                    CONTENT_URI

            };
        }

        @NotifyDelete(paths = REVIEW_PATH + "/id/*")
        public static Uri[] onDelete(Context context, Uri uri) {


            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final String reviewId = cursor.getString(cursor.getColumnIndex(ReviewColumns._ID));

            cursor.close();


            return new Uri[]{
                    buildReviewUri(reviewId),
                    CONTENT_URI
            };
        }

        @NotifyBulkInsert(paths = REVIEW_PATH)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] contentValues, long[] ids) {
            return new Uri[]{
                    uri
            };
        }


        @NotifyUpdate(paths = REVIEW_PATH + "/id/*")
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            final long reviewId = ContentUris.parseId(uri);
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    ReviewColumns._ID}, null, null, null);


            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final String updatedId = cursor.getString(cursor.getColumnIndex(ReviewColumns._ID));

            cursor.close();

            return new Uri[]{
                    buildReviewUri(updatedId)
            };


        }

    }

    @TableEndpoint(table = AutoMovieDB.MOVIE)
    public static class Movie {


        @ContentUri(
                path = MOVIE_PATH,
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                defaultSort = MovieColumns.POPULARITY + " DESC "
        )
        public static final Uri CONTENT_URI = BASE_CONT_URI.buildUpon().appendPath(MOVIE_PATH).build();


        @InexactContentUri(
                name = "MOVIE_ID",
                path = MOVIE_PATH + "/#",
                type = CONTENT_ITEM_TYPE + MOVIE_PATH,
                whereColumn = MovieColumns._ID,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 1

        )
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        @InexactContentUri(
                name = "SORT_BY_FAVORITE",
                path = MOVIE_PATH + "/sort/favorite",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = {},
                where = MovieColumns.IS_FAVORITE ,
                defaultSort = MovieColumns.RELEASE_DATE + " DESC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = {}
        )
        public static Uri buildMovieFavoriteView() {
            return CONTENT_URI.buildUpon().appendEncodedPath("sort/favorite").build();
        }

        @InexactContentUri(
                name = "SORT_BY_POPULARITY",
                path = MOVIE_PATH + "/sort/popularity",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = {},
                defaultSort = MovieColumns.POPULARITY + " DESC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = {}
        )
        public static Uri buildMoviePopularityView() {
            return CONTENT_URI.buildUpon().appendEncodedPath("sort/popularity").build();
        }


        @InexactContentUri(
                name = "SORT_BY_RATING",
                path = MOVIE_PATH + "/sort/rating",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = {},
                defaultSort = MovieColumns.RATING + " DESC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = {}
        )
        public static Uri buildMovieByRatingView() {
            return CONTENT_URI.buildUpon().appendEncodedPath("sort/rating").build();
        }


        @InexactContentUri(
                name = "FILTER_BY_DATE",
                path = MOVIE_PATH + "/betweenDates/*",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = {},
                defaultSort = MovieColumns.RELEASE_DATE + " ASC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = {}
        )
        public static Uri buildMovieUriWithDates(String startDate, String endDate) {
            return CONTENT_URI.buildUpon()
                    .appendEncodedPath("betweenDates/query")
                    .appendQueryParameter("start_date", startDate)
                    .appendQueryParameter("end_date", endDate)
                    .build();
        }

        @InexactContentUri(
                name = "FILTER_BY_TITLE",
                path = MOVIE_PATH + "/f_name/*",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = {},
                defaultSort = MovieColumns.RELEASE_DATE + " DESC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 2
        )
        public static Uri buildMovieUriWithTitleFilter(String title) {
            return CONTENT_URI.buildUpon().appendPath("f_name").appendEncodedPath(title).build();
        }


        @NotifyInsert(paths = MOVIE_PATH)
        public static Uri[] onInsert(ContentValues values) {
            final long movieId = values.getAsLong(MovieColumns._ID);

            return new Uri[]{
                    buildMovieUri(movieId),
                    CONTENT_URI

            };
        }

        @NotifyDelete(paths = MOVIE_PATH + "/#")
        public static Uri[] onDelete(Context context, Uri uri) {


            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final long movieId = cursor.getLong(cursor.getColumnIndex(MovieColumns._ID));

            cursor.close();


            return new Uri[]{
                    buildMovieUri(movieId),
                    CONTENT_URI
            };
        }

        @NotifyBulkInsert(paths = MOVIE_PATH)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] contentValues, long[] ids) {
            return new Uri[]{
                    uri
            };
        }


        @NotifyUpdate(paths = MOVIE_PATH + "/#")
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            final long movieId = ContentUris.parseId(uri);
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    MovieColumns._ID}, null, null, null);


            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final long updatedId = cursor.getLong(cursor.getColumnIndex(MovieColumns._ID));

            cursor.close();
            Log.e("UpdateId", String.valueOf(updatedId));
            Log.e("NotifyUpdateUri", buildMovieUri(updatedId).toString());
            return new Uri[]{
                    buildMovieUri(updatedId)
            };


        }
    }
}
