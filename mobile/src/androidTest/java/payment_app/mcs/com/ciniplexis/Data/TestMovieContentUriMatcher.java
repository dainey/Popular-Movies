package payment_app.mcs.com.ciniplexis.Data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import payment_app.mcs.com.ciniplexis.Contracts.ReviewEntry;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;

import static payment_app.mcs.com.ciniplexis.ContentProvider.MovieContentProvider.*;

/**
 * Created by ogayle on 27/10/2015.
 */
public class TestMovieContentUriMatcher extends AndroidTestCase {

    private static final String SORT_BY_POPULARITY_QUERY = "popularity";
    private static final String SORT_BY_DATE_QUERY = "date";
    private static final String SORT_BY_RATING_QUERY = "rating";
    private static final String START_DATE = "2015-10-31";
    private static final String END_DATE = "2015-11-07";
    private static final String TITLE = "spectre";

    private static final Uri TEST_MOVIE_DIR = MovieEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_DIR = ReviewEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_NOW_SHOWING = MovieEntry.buildMovieUriWithDates(START_DATE, END_DATE);
    private static final Uri TEST_MOVIE_BY_DATE_DIR = MovieEntry.buildMovieBySortCriteria(SORT_BY_DATE_QUERY);
    private static final Uri TEST_MOVIE_BY_RATING = MovieEntry.buildMovieBySortCriteria(SORT_BY_RATING_QUERY);
    private static final Uri TEST_MOVIE_BY_POPULARITY = MovieEntry.buildMovieBySortCriteria(SORT_BY_POPULARITY_QUERY);
    private static final Uri TEST_MOVIE_FILTER_BY_TITLE = MovieEntry.buildMovieUriWithTitleFilter(TITLE);
    private static final Uri TEST_MOVIE_BY_ID = MovieEntry.buildMovieUri(1);

    public void testUriMatcher() {
        UriMatcher myUriMatcher = buildUriMatcher();

        int i = myUriMatcher.match(TEST_MOVIE_NOW_SHOWING);
        Log.e("NowShowingM", String.valueOf(i));
        Log.e("NowUri", TEST_MOVIE_NOW_SHOWING.toString());


        assertEquals("Error: MOVIE URI is incorrect", MOVIE, myUriMatcher.match(TEST_MOVIE_DIR));
        Log.e("NowUri", TEST_MOVIE_DIR.toString());
        assertEquals("Error: REVIEW URI is incorrect", REVIEW, myUriMatcher.match(TEST_REVIEW_DIR));
        Log.e("NowUri", TEST_REVIEW_DIR.toString());
        assertEquals("Error: MOVIE BY ID is incorrect", MOVIE_BY_ID, myUriMatcher.match(TEST_MOVIE_BY_ID));
        Log.e("NowUri", TEST_MOVIE_BY_ID.toString());
        assertEquals("Error: REVIEWS BY MOVIE is incorrect", MOVIE_BY_ID, myUriMatcher.match(TEST_MOVIE_BY_ID));
        assertEquals("Error: MOVIE BY TITLE is incorrect", MOVIE_FILTER_TITLE, myUriMatcher.match(TEST_MOVIE_FILTER_BY_TITLE));
        Log.e("NowUri", TEST_MOVIE_FILTER_BY_TITLE.toString());
        assertEquals("Error: MOVIE NOW SHOWING URI is incorrect", MOVIE_BETWEEN_DATES, myUriMatcher.match(TEST_MOVIE_NOW_SHOWING));
        Log.e("NowUri", TEST_MOVIE_NOW_SHOWING.toString());
        assertEquals("Error: MOVIE BY DATE URI is incorrect", MOVIE_BY_DATE, myUriMatcher.match(TEST_MOVIE_BY_DATE_DIR));
        assertEquals("Error: MOVIE BY RATINGS URI is incorrect", MOVIE_BY_RATING, myUriMatcher.match(TEST_MOVIE_BY_RATING));
        assertEquals("Error: MOVIE BY POPULARITY URI is incorrect", MOVIE_BY_POPULARITY, myUriMatcher.match(TEST_MOVIE_BY_POPULARITY));

    }
}
