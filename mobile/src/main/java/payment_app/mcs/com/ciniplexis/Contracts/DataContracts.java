package payment_app.mcs.com.ciniplexis.Contracts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ogayle on 26/10/2015.
 */
public class DataContracts {

    protected static final String CONTENT_AUTHORITY = "payment_app.mcs.com.ciniplexis";
    protected static final Uri BASE_CONT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    protected static final String MOVIE_PATH = "movie";
    protected static final String REVIEW_PATH = "review";

    public static String getContentAuthority()
    {
        return CONTENT_AUTHORITY;
    }

    public static Uri getBaseUri(){
        return BASE_CONT_URI;
    }
}
