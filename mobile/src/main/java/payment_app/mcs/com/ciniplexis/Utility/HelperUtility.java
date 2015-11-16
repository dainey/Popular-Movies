package payment_app.mcs.com.ciniplexis.Utility;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import payment_app.mcs.com.ciniplexis.ContentProvider.AutoMovieContentProvider;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.ReviewColumns;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.VideoColumns;
import payment_app.mcs.com.ciniplexis.Model.DataModels.BaseEntity;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.ReviewDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoDataModel;

/**
 * Created by ogayle on 28/10/2015.
 */
public class HelperUtility {

    /*
    * Shared Preference Constants
    * */
    public final static String MOVIE_PREF = "movie_settings";
    public final static String FILTER_URI_PREF = "filter_uri";
    public final static String PAGE_INDEX_PREF = "page_index";
    public final static String LAST_VISITED_URL_PREF = "last_visited_url";

    /*
    * API Constants
    * */

    public static final String YOUTUBE_WATCH = "http://www.youtube.com/watch?v=";

    public static final String API_KEY = "xxx-xxx-xxx";
    public static final String DISCOVER_MOVIE = "discover/movie";
    public static final String MOVIE_VIDEOS = "movie/{id}/videos";
    public static final String MOVIE_REVIEWS = "movie/{id}/reviews";
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String CRITERIA_POPULARITY = "popularity.desc";
    public static final String CRITERIA_RATING = "vote_average.desc";
    public static final String CRITERIA_DATE = "release_date.desc";

    public static final String MOVIE_URI = "URI";
    public static final String REQUEST_URL = "URL";

    public static SharedPreferences getMovieSharedPreference(Context myContext) {
        if (myContext == null)
            return null;

        return myContext.getSharedPreferences(MOVIE_PREF, Context.MODE_PRIVATE);

    }


    public MovieDataModel getMovieFromCursor(Cursor movieCursor) {
        MovieDataModel movie = new MovieDataModel();
        int index = movieCursor.getColumnIndex(MovieColumns._ID);
        movie.setId(movieCursor.getInt(index));

        index = movieCursor.getColumnIndex(MovieColumns.TITLE);
        movie.setTitle(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieColumns.PLOT);
        movie.setPlot(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieColumns.RATING);
        movie.setRating(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieColumns.RELEASE_DATE);
        movie.setReleaseDate(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieColumns.PRICE);
        movie.setPrice(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieColumns.IMAGE);
        movie.setImageUrl(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieColumns.BACKGROUND_IMAGE);
        movie.setMovieBackground(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieColumns.POPULARITY);
        movie.setPopularity(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieColumns.IS_PURCHASED);
        movie.setIsPurchased((movieCursor.getInt(index) != 0));

        index = movieCursor.getColumnIndex(MovieColumns.IS_FAVORITE);
        movie.setIsFavorite((movieCursor.getInt(index) != 0));

        index = movieCursor.getColumnIndex(MovieColumns._ID);
        movie.setId(movieCursor.getInt(index));

        return movie;
    }

    public ReviewDataModel getReviewFromCursor(Cursor cursor) {
        ReviewDataModel review = new ReviewDataModel();
        int index = cursor.getColumnIndex(ReviewColumns._ID);
        review.setId(cursor.getInt(index));

        index = cursor.getColumnIndex(ReviewColumns.USERNAME);
        review.setAuthor(cursor.getString(index));

        index = cursor.getColumnIndex(ReviewColumns.COMMENT);
        review.setContent(cursor.getString(index));

        index = cursor.getColumnIndex(ReviewColumns.COMMENT_DATE);
        review.setDate(cursor.getString(index));

        index = cursor.getColumnIndex(ReviewColumns._ID);
        review.setId(cursor.getInt(index));

        return review;
    }

    public VideoDataModel getVideoFromCursor(Cursor cursor) {
        VideoDataModel video = new VideoDataModel();
        int index = cursor.getColumnIndex(VideoColumns.KEY);
        video.setKey(cursor.getString(index));

        index = cursor.getColumnIndex(VideoColumns.NAME);
        video.setName(cursor.getString(index));

        index = cursor.getColumnIndex(VideoColumns.SITE);
        video.setSite(cursor.getString(index));

        index = cursor.getColumnIndex(VideoColumns.SIZE);
        video.setSize(cursor.getString(index));

        index = cursor.getColumnIndex(VideoColumns.TYPE);
        video.setType(cursor.getString(index));

        index = cursor.getColumnIndex(VideoColumns._ID);
        video.setId(cursor.getInt(index));

        return video;
    }

    /***
     * Operations Start: Movie | Video | Review
     */

    public long saveToDatabase(Context mContext, ArrayList list) {
        Uri dataUri = AutoMovieContentProvider.Movie.CONTENT_URI;


        Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
                .query(dataUri,
                        null,
                        null,
                        null,
                        null);
        long retVal = 0;

        if (list == null) return retVal;
        if (movieCursor == null) return retVal;

        int count = movieCursor.getCount();
        movieCursor.close();


        if (count == 0) return addAllToDatabase(mContext, list);
        else
            for (Object entity : list) {
                retVal = addEntityToDatabase(mContext, entity);
            }

        return retVal;
    }

    private int addAllToDatabase(Context mContext, ArrayList list) {
        int numOFRows = 0;
        if (list == null) return numOFRows;

        ContentValues[] cvList = new ContentValues[list.size()];
        Uri dataContentUri = null;
        int i = 0;
        for (Object entity : list) {

            if (entity instanceof MovieDataModel) {
                cvList[i] = createMovieContentValue((MovieDataModel) entity);
                dataContentUri = AutoMovieContentProvider.Movie.CONTENT_URI;
            }

            if (entity instanceof ReviewDataModel) {
                cvList[i] = createReviewContentValue((ReviewDataModel) entity);
                dataContentUri = AutoMovieContentProvider.Review.CONTENT_URI;
            }

            if (entity instanceof VideoDataModel) {
                cvList[i] = createVideoContentValue((VideoDataModel) entity);
                dataContentUri = AutoMovieContentProvider.Video.CONTENT_URI;
            }
            i++;
        }

        if (dataContentUri == null) return numOFRows;
        numOFRows = mContext.getContentResolver().bulkInsert(dataContentUri, cvList);


        return numOFRows;
    }

    public long addEntityToDatabase(Context mContext, Object entity) {
        ContentValues addCv = null;
        String[] selectionArgs = new String[1];
        String selection = BaseColumns._ID + "= ?";
        Uri dataContentUri = null;

        if (entity instanceof MovieDataModel) {
            addCv = createMovieContentValue((MovieDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Movie.CONTENT_URI;
        }

        if (entity instanceof ReviewDataModel) {
            addCv = createReviewContentValue((ReviewDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Review.CONTENT_URI;
        }

        if (entity instanceof VideoDataModel) {
            addCv = createVideoContentValue((VideoDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Video.CONTENT_URI;
        }

        if (dataContentUri == null || addCv == null) return 0;

        selectionArgs[0] = addCv.getAsString(BaseColumns._ID);


        Cursor cursor = mContext.getApplicationContext().getContentResolver()
                .query(dataContentUri,
                        new String[]{BaseColumns._ID},
                        selection,
                        selectionArgs,
                        null);
        if (cursor == null) return -1;
        if (cursor.moveToFirst()) return 0;

        cursor.close();

        Uri addUri = mContext.getContentResolver().insert(dataContentUri, addCv);
        return ContentUris.parseId(addUri);
    }

    public int updateEntity(Context mContext, Object entity) {
        String[] selectionArgs = new String[1];
        String selection = BaseColumns._ID + "= ?";

        ContentValues updateCv = null;
        Uri dataContentUri = null;


        if (entity instanceof MovieDataModel) {
            updateCv = createMovieContentValue((MovieDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Movie.CONTENT_URI;
        }

        if (entity instanceof ReviewDataModel) {
            updateCv = createReviewContentValue((ReviewDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Review.CONTENT_URI;
        }

        if (entity instanceof VideoDataModel) {
            updateCv = createVideoContentValue((VideoDataModel) entity);
            dataContentUri = AutoMovieContentProvider.Video.CONTENT_URI;
        }

        if (dataContentUri == null || updateCv == null) return 0;

        selectionArgs[0] = updateCv.getAsString(BaseColumns._ID);

        Log.i("Updating", updateCv.toString());
        Log.i("UpdateUri", dataContentUri.toString());

        return mContext.getContentResolver().update(dataContentUri, updateCv, selection, selectionArgs);
    }


    /****
     * Section Start: Content Values
     * Create Review | Video | Movie - Content Value
     */

    public ContentValues createMovieContentValue(MovieDataModel movie) {
        ContentValues cv = new ContentValues();
        cv.put(MovieColumns._ID, movie.getId());
        cv.put(MovieColumns.TITLE, movie.getTitle());
        cv.put(MovieColumns.PLOT, movie.getPlot());
        cv.put(MovieColumns.RATING, movie.getRating());
        cv.put(MovieColumns.RELEASE_DATE, movie.getReleaseDate());
        cv.put(MovieColumns.PRICE, movie.getPrice());
        cv.put(MovieColumns.IMAGE, movie.getImageUrl());
        cv.put(MovieColumns.BACKGROUND_IMAGE, movie.getMovieBackground());
        cv.put(MovieColumns.POPULARITY, movie.getPopularity());
        cv.put(MovieColumns.IS_PURCHASED, movie.isPurchased());
        cv.put(MovieColumns.IS_FAVORITE, movie.isFavorite());

        return cv;
    }

    public ContentValues createReviewContentValue(ReviewDataModel review) {
        ContentValues cv = new ContentValues();
        cv.put(ReviewColumns._ID, review.getId());
        cv.put(ReviewColumns.COMMENT, review.getContent());
        cv.put(ReviewColumns.COMMENT_DATE, review.getDate());
        cv.put(ReviewColumns.USERNAME, review.getAuthor());
        cv.put(ReviewColumns.COMMENT_URL, review.getUrl());
        cv.put(ReviewColumns.FK_MOVIE_ID, review.getMovieId());

        return cv;
    }

    public ContentValues createVideoContentValue(VideoDataModel video) {
        ContentValues cv = new ContentValues();
        cv.put(VideoColumns._ID, video.getId());
        cv.put(VideoColumns.KEY, video.getKey());
        cv.put(VideoColumns.ISO_LANGUAGE, video.getLanguageISO());
        cv.put(VideoColumns.NAME, video.getName());
        cv.put(VideoColumns.SITE, video.getSite());
        cv.put(VideoColumns.SIZE, video.getSize());
        cv.put(VideoColumns.TYPE, video.getType());
        cv.put(VideoColumns.FK_MOVIE_ID, video.getMovieId());

        return cv;
    }

    /**
     * Section End: Content Values
     */


}


/**
 * public static void parseMoviesFromJSONString(Context mContext, String jsonMovieList, String url) {
 * final String PAGE = "page";
 * final String RESULTS = "results";
 * final String TITLE = "title";
 * final String ID = "id";
 * final String RATING = "vote_average";
 * final String RELEASE_DATE = "release_date";
 * final String PLOT = "overview";
 * final String PRICE = "vote_count";
 * final String IMAGE_URL = "poster_path";
 * final String POPULARITY = "popularity";
 * final String BACKGROUND_IMAGE_URL = "backdrop_path";
 * <p/>
 * if (jsonMovieList == null)
 * return;
 * <p/>
 * try {
 * JSONObject movieList = new JSONObject(jsonMovieList);
 * JSONArray pageResults = movieList.getJSONArray(RESULTS);
 * int page = movieList.getInt(PAGE);
 * <p/>
 * ArrayList<MovieDataModel> movies = new ArrayList<>();
 * <p/>
 * for (int i = 0; i < pageResults.length(); i++) {
 * MovieDataModel mDataModel = new MovieDataModel();
 * JSONObject obj = pageResults.getJSONObject(i);
 * mDataModel.setId(obj.getInt(ID));
 * mDataModel.setTitle(obj.getString(TITLE));
 * mDataModel.setRating(obj.getDouble(RATING));
 * mDataModel.setReleaseDate(obj.getString(RELEASE_DATE));
 * mDataModel.setPlot(obj.getString(PLOT));
 * mDataModel.setPrice(obj.getDouble(PRICE));
 * mDataModel.setImageUrl(obj.getString(IMAGE_URL));
 * mDataModel.setMovieBackground(obj.getString(BACKGROUND_IMAGE_URL));
 * mDataModel.setPopularity(obj.getDouble(POPULARITY));
 * movies.add(mDataModel);
 * }
 * <p/>
 * saveMovies(mContext, movies);
 * <p/>
 * <p/>
 * Log.v("Response", jsonMovieList);
 * <p/>
 * } catch (JSONException je) {
 * Log.e("doInB:JSon", je.getMessage());
 * } catch (Exception e) {
 * Log.e("doInB:MContentProvider", e.getMessage());
 * }
 * }
 * public static long saveMovies(Context mContext, ArrayList<MovieDataModel> movies) {
 * Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
 * .query(AutoMovieContentProvider.Movie.CONTENT_URI,
 * null,
 * null,
 * null,
 * null);
 * long retVal = 0;
 * <p/>
 * if (movies == null) return retVal;
 * if (movieCursor == null) return retVal;
 * <p/>
 * int count = movieCursor.getCount();
 * movieCursor.close();
 * <p/>
 * <p/>
 * if (count == 0) return addAllMovies(mContext, movies);
 * else
 * for (MovieDataModel movie : movies) {
 * retVal = addMovie(mContext, movie);
 * }
 * <p/>
 * <p/>
 * return retVal;
 * <p/>
 * }
 * <p/>
 * private static int addAllMovies(Context mContext, ArrayList<MovieDataModel> movies) {
 * int numOFRows = 0;
 * if (movies != null) {
 * ContentValues[] cvList = new ContentValues[movies.size()];
 * int i = 0;
 * for (MovieDataModel movie : movies) {
 * <p/>
 * cvList[i] = createMovieContentValue(movie);
 * i++;
 * }
 * numOFRows = mContext.getContentResolver().bulkInsert(AutoMovieContentProvider.Movie.CONTENT_URI, cvList);
 * }
 * <p/>
 * return numOFRows;
 * }
 * <p/>
 * public static long addMovie(Context mContext, MovieDataModel movie) {
 * ContentValues addCv = createMovieContentValue(movie);
 * String[] selectionArgs = new String[1];
 * String selection = MovieColumns._ID + "= ?";
 * selectionArgs[0] = String.valueOf(movie.getId());
 * <p/>
 * Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
 * .query(AutoMovieContentProvider.Movie.CONTENT_URI,
 * new String[]{MovieColumns._ID},
 * selection,
 * selectionArgs,
 * null);
 * if (movieCursor == null) return -1;
 * if (movieCursor.moveToFirst()) return 0;
 * <p/>
 * movieCursor.close();
 * Uri addUri = mContext.getContentResolver().insert(AutoMovieContentProvider.Movie.CONTENT_URI, addCv);
 * return ContentUris.parseId(addUri);
 * }
 * <p/>
 * public static int updateMovie(Context mContext, MovieDataModel movie) {
 * String[] selectionArgs = new String[1];
 * String selection = MovieColumns._ID + "= ?";
 * selectionArgs[0] = String.valueOf(movie.getId());
 * ContentValues updateCv = createMovieContentValue(movie);
 * <p/>
 * return mContext.getContentResolver().update(AutoMovieContentProvider.Movie.CONTENT_URI, updateCv, selection, selectionArgs);
 * }
 */