package payment_app.mcs.com.ciniplexis.Contracts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ogayle on 28/10/2015.
 */
public class ReviewEntry  extends DataContracts implements BaseColumns {

    public static final Uri CONTENT_URI = getBaseUri().buildUpon().appendPath(REVIEW_PATH).build();
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;
    public static final String TABLE_NAME = "review";
    public static final String USERNAME = "username";
    public static final String COLUMN_COMMENT_POST = "post";
    public static final String COLUMN_COMMENT_POST_DATE = "post_date";
    public static final String FK_COLUMN_MOVIE_ID = "movie_id";

    public static Uri buildReviewUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static String getReviewPath(){
        return REVIEW_PATH;
    }
}
