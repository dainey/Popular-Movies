package payment_app.mcs.com.ciniplexis.Utility;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import payment_app.mcs.com.ciniplexis.ContentProvider.AutoMovieContentProvider;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieUtility {

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


    public static final String API_KEY = "273abd1f83b363bf5c91521f9ad8b9c0";

    public static final String DISCOVER_MOVIE = "discover/movie";
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String CRITERIA_POPULARITY = "popularity.desc";
    public static final String CRITERIA_RATING = "vote_average.desc";
    public static final String CRITERIA_DATE = "release_date.desc";

    public static final String MOVIE_URI = "URI";
    public static final String REQUEST_URL = "URL";

    public static MovieDataModel getMovieDataFromCursor(Cursor movieCursor) {
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
        return movie;
    }

    public static long saveMovies(Context mContext, ArrayList<MovieDataModel> movies) {
        Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
                .query(AutoMovieContentProvider.Movie.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        long retVal = 0;

        if (movies == null) return retVal;
        if (movieCursor == null) return retVal;

        int count = movieCursor.getCount();
        movieCursor.close();


        if (count == 0) return addAllMovies(mContext, movies);
        else
            for (MovieDataModel movie : movies) {
                retVal = addMovie(mContext, movie);
            }


        return retVal;

    }

    private static int addAllMovies(Context mContext, ArrayList<MovieDataModel> movies) {
        int numOFRows = 0;
        if (movies != null) {
            ContentValues[] cvList = new ContentValues[movies.size()];
            int i = 0;
            for (MovieDataModel movie : movies) {

                cvList[i] = createMovieContentValues(movie);
                i++;
            }
            numOFRows = mContext.getContentResolver().bulkInsert(AutoMovieContentProvider.Movie.CONTENT_URI, cvList);
        }

        return numOFRows;
    }

    public static long addMovie(Context mContext, MovieDataModel movie) {
        ContentValues addCv = createMovieContentValues(movie);
        String[] selectionArgs = new String[1];
        String selection = MovieColumns._ID + "= ?";
        selectionArgs[0] = String.valueOf(movie.getId());

        Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
                .query(AutoMovieContentProvider.Movie.CONTENT_URI,
                        new String[]{MovieColumns._ID},
                        selection,
                        selectionArgs,
                        null);
        if (movieCursor == null) return -1;
        if (movieCursor.moveToFirst()) return 0;

        movieCursor.close();
        Uri addUri = mContext.getContentResolver().insert(AutoMovieContentProvider.Movie.CONTENT_URI, addCv);
        return ContentUris.parseId(addUri);
    }

    public static int updateMovie(Context mContext, MovieDataModel movie) {
        String[] selectionArgs = new String[1];
        String selection = MovieColumns._ID + "= ?";
        selectionArgs[0] = String.valueOf(movie.getId());
        ContentValues updateCv = createMovieContentValues(movie);

        return mContext.getContentResolver().update(AutoMovieContentProvider.Movie.CONTENT_URI, updateCv, selection, selectionArgs);
    }

    public static ContentValues createMovieContentValues(MovieDataModel movie) {
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

    public static SharedPreferences getMovieSharedPreference(Context myContext) {
        if (myContext == null)
            return null;

        return myContext.getSharedPreferences(MOVIE_PREF, Context.MODE_PRIVATE);

    }


    public static void parseMoviesFromJSONString(Context mContext, String jsonMovieList, String url) {
        final String PAGE = "page";
        final String RESULTS = "results";
        final String TITLE = "title";
        final String ID = "id";
        final String RATING = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String PLOT = "overview";
        final String PRICE = "vote_count";
        final String IMAGE_URL = "poster_path";
        final String POPULARITY = "popularity";
        final String BACKGROUND_IMAGE_URL = "backdrop_path";

        if (jsonMovieList == null)
            return;

        try {
            JSONObject movieList = new JSONObject(jsonMovieList);
            JSONArray pageResults = movieList.getJSONArray(RESULTS);
            int page = movieList.getInt(PAGE);

            ArrayList<MovieDataModel> movies = new ArrayList<>();

            for (int i = 0; i < pageResults.length(); i++) {
                MovieDataModel mDataModel = new MovieDataModel();
                JSONObject obj = pageResults.getJSONObject(i);
                mDataModel.setId(obj.getInt(ID));
                mDataModel.setTitle(obj.getString(TITLE));
                mDataModel.setRating(obj.getDouble(RATING));
                mDataModel.setReleaseDate(obj.getString(RELEASE_DATE));
                mDataModel.setPlot(obj.getString(PLOT));
                mDataModel.setPrice(obj.getDouble(PRICE));
                mDataModel.setImageUrl(obj.getString(IMAGE_URL));
                mDataModel.setMovieBackground(obj.getString(BACKGROUND_IMAGE_URL));
                mDataModel.setPopularity(obj.getDouble(POPULARITY));
                movies.add(mDataModel);
            }

            saveMovies(mContext, movies);


            Log.v("Response", jsonMovieList);

        } catch (JSONException je) {
            Log.e("doInB:JSon", je.getMessage());
        } catch (Exception e) {
            Log.e("doInB:MContentProvider", e.getMessage());
        }
    }
}
