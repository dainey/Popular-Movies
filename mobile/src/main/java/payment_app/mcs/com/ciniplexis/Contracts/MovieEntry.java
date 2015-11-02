package payment_app.mcs.com.ciniplexis.Contracts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieEntry extends DataContracts implements BaseColumns {
    public static final Uri CONTENT_URI = getBaseUri().buildUpon().appendPath(MOVIE_PATH).build();
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    public static final String TABLE_NAME = "movie";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_BACKGROUND_IMAGE_URL = "background_image_url";
    public static final String COLUMN_AVERAGE_POPULARITY = "popularity";
    public static final String COLUMN_IS_PURCHASED = "isPurchased";

    public static Uri buildMovieUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildMovieBySortCriteria(String criteria) {
        return CONTENT_URI.buildUpon().appendPath("sort").appendEncodedPath(criteria).build();
    }

    public static Uri buildMovieUriWithTitleFilter(String title) {
        return CONTENT_URI.buildUpon().appendPath("f_name").appendEncodedPath(title).build();
    }


    public static Uri buildMoviePopularityView() {
        return CONTENT_URI.buildUpon().appendEncodedPath("sort/popularity").build();
    }

    public static Uri buildMovieByDateView() {
        return CONTENT_URI.buildUpon().appendEncodedPath("sort/date").build();
    }

    public static Uri buildMovieByRatingView() {
        return CONTENT_URI.buildUpon().appendEncodedPath("sort/rating").build();
    }

    public static Uri buildMovieUriWithDates(String startDate, String endDate) {

        return CONTENT_URI.buildUpon()
                .appendEncodedPath("betweenDates/query")
                .appendQueryParameter("start_date", startDate)
                .appendQueryParameter("end_date", endDate)
                .build();
    }


    public static String getMoviePath() {
        return MOVIE_PATH;
    }
}
