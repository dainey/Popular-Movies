package payment_app.mcs.com.ciniplexis.ContentProvider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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

    private static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

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
                pathSegment = 1

        )
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        /*     @InexactContentUri(
                     name = "SORT_BY",
                     path = MOVIE_PATH + "/sort/*",
                     type = CONTENT_DIR_TYPE + MOVIE_PATH,
                     allowDelete = false,
                     allowInsert = false,
                     allowUpdate = false,
                     whereColumn = {MovieColumns.POPULARITY },
                     pathSegment = 2
             )*/
        public static Uri sortByCriteria(String criteria) {
            return CONTENT_URI.buildUpon().appendPath("sort").appendEncodedPath(criteria).build();
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
                pathSegment = 3
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
                pathSegment = 4
        )
        public static Uri buildMovieByRatingView() {
            return CONTENT_URI.buildUpon().appendEncodedPath("sort/rating").build();
        }


        @InexactContentUri(
                name = "FILTER_BY_DATE",
                path = MOVIE_PATH + "/betweenDates/*",
                type = CONTENT_DIR_TYPE + MOVIE_PATH,
                whereColumn = MovieColumns.RELEASE_DATE,
                where = MovieColumns.RELEASE_DATE + " BETWEEN ? AND ? ",
                defaultSort = MovieColumns.RELEASE_DATE + " ASC ",
                allowDelete = false,
                allowInsert = false,
                allowUpdate = false,
                pathSegment = 5
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
                pathSegment = 6
        )
        public static Uri buildMovieUriWithTitleFilter(String title) {
            return CONTENT_URI.buildUpon().appendPath("f_name").appendEncodedPath(title).build();
        }


        @NotifyInsert(paths = MOVIE_PATH)
        public static Uri[] onInsert(ContentValues values) {
            final long movieId = values.getAsLong(MovieColumns._ID);

            return new Uri[]{buildMovieUri(movieId)};
        }

        @NotifyDelete(paths = MOVIE_PATH + "/#")
        public static Uri[] onDelete(Context context, Uri uri) {


            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor == null) return null;

            if (!cursor.moveToFirst()) return null;

            final long movieId = cursor.getLong(cursor.getColumnIndex(MovieColumns._ID));

            cursor.close();


            return new Uri[]{buildMovieUri(movieId)};
        }

        @NotifyBulkInsert(paths = MOVIE_PATH)
        public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] contentValues, long[] ids) {
            return new Uri[]{
                    uri,
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

            return new Uri[]{
                    buildMovieUri(updatedId)
            };


        }
    }
}
