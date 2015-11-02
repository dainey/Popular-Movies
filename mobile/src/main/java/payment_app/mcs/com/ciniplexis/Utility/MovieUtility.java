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

import payment_app.mcs.com.ciniplexis.Contracts.DataContracts;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Model.MovieDataModel;

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
    public static final String SORT_CRITERIA = "sort_criteria";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String CRITERIA_POPULARITY = "popularity.desc";
    public static final String CRITERIA_RATING = "vote_average.desc";
    public static final String CRITERIA_DATE = "release_date.desc";
    public static String sortQuery = "sort_by=" + SORT_CRITERIA;

    public static final String MOVIE_URI = "URI";
    public static final String REQUEST_URL = "URL";
    private static final String API_KEY = "xxx-xxx-xxx";

    public static final String DISCOVERY_PARAM = "discovery_parameter";
    public static String baseQuery = "http://api.themoviedb.org/3/discover/movie?" + DISCOVERY_PARAM + "&api_key=" + API_KEY;
    public static String movieByDate = "primary_release_date.gte=" + START_DATE + "&primary_release_date.lte=" + END_DATE;

    public static MovieDataModel getMovieDataFromCursor(Cursor movieCursor) {
        MovieDataModel movie = new MovieDataModel();
        int index = movieCursor.getColumnIndex(MovieEntry._ID);
        movie.setId(movieCursor.getInt(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        movie.setTitle(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_PLOT);
        movie.setPlot(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_RATING);
        movie.setRating(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
        movie.setReleaseDate(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_PRICE);
        movie.setPrice(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL);
        movie.setImageUrl(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_BACKGROUND_IMAGE_URL);
        movie.setMovieBackground(movieCursor.getString(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_AVERAGE_POPULARITY);
        movie.setPopularity(movieCursor.getDouble(index));

        index = movieCursor.getColumnIndex(MovieEntry.COLUMN_IS_PURCHASED);
        movie.setIsPurchased((movieCursor.getInt(index) != 0));
        return movie;
    }

    public static long saveMovies(Context mContext, ArrayList<MovieDataModel> movies) {
        Cursor movieCursor = mContext.getApplicationContext().getContentResolver()
                .query(MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        long retVal = 0;

        if (movieCursor != null) {
            if (movieCursor.getCount() == 0)
                retVal = addAllMovies(mContext, movies);
            else {
                movieCursor.close();
                if (movies != null) {
                    for (MovieDataModel movie : movies) {

                        String[] selectionArgs = new String[1];
                        String selection = MovieEntry._ID + "= ?";
                        selectionArgs[0] = String.valueOf(movie.getId());

                        movieCursor = mContext.getApplicationContext().getContentResolver()
                                .query(MovieEntry.CONTENT_URI,
                                        new String[]{MovieEntry._ID},
                                        selection,
                                        selectionArgs,
                                        null);
                        if (movieCursor != null) {
                            if (!movieCursor.moveToFirst())
                                retVal = addMovie(mContext, movie);
                            else
                                retVal = updateMovie(mContext, movie);

                            movieCursor.close();
                        }
                    }
                }

            }


        }
        return retVal;

    }

    public static int addAllMovies(Context mContext, ArrayList<MovieDataModel> movies) {
        int numOFRows = 0;
        if (movies != null) {
            ContentValues[] cvList = new ContentValues[movies.size()];
            int i = 0;
            for (MovieDataModel movie : movies) {
                cvList[i] = createMovieContentValues(movie);
                i++;
            }
            numOFRows = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvList);
        }

        return numOFRows;
    }

    public static long addMovie(Context mContext, MovieDataModel movie) {
        ContentValues addCv = createMovieContentValues(movie);
        Uri addUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, addCv);

        return ContentUris.parseId(addUri);
    }

    public static int updateMovie(Context mContext, MovieDataModel movie) {
        String[] selectionArgs = new String[1];
        String selection = MovieEntry._ID + "= ?";
        selectionArgs[0] = String.valueOf(movie.getId());
        ContentValues updateCv = createMovieContentValues(movie);

        return mContext.getContentResolver().update(MovieEntry.CONTENT_URI, updateCv, selection, selectionArgs);
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
            SharedPreferences movieSetting = getMovieSharedPreference(mContext);
            SharedPreferences.Editor movieSettingEditor = movieSetting.edit();

            movieSettingEditor.putString(LAST_VISITED_URL_PREF, url);
            movieSettingEditor.putInt(PAGE_INDEX_PREF, page);
            movieSettingEditor.apply();

            Log.v("Response", jsonMovieList);

        } catch (JSONException je) {
            Log.e("doInB:JSon", je.getMessage());
        } catch (Exception e) {
            Log.e("doInB:MContentProvider", e.getMessage());
        }
    }

    public static ContentValues createMovieContentValues(MovieDataModel movie) {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry._ID, movie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MovieEntry.COLUMN_PLOT, movie.getPlot());
        cv.put(MovieEntry.COLUMN_RATING, movie.getRating());
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MovieEntry.COLUMN_PRICE, movie.getPrice());
        cv.put(MovieEntry.COLUMN_IMAGE_URL, movie.getImageUrl());
        cv.put(MovieEntry.COLUMN_BACKGROUND_IMAGE_URL, movie.getMovieBackground());
        cv.put(MovieEntry.COLUMN_AVERAGE_POPULARITY, movie.getPopularity());
        cv.put(MovieEntry.COLUMN_IS_PURCHASED, movie.isPurchased());

        return cv;
    }

    public static SharedPreferences getMovieSharedPreference(Context myContext) {
        if (myContext == null)
            return null;

        return myContext.getSharedPreferences(MOVIE_PREF, Context.MODE_PRIVATE);

    }


}
