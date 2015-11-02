package payment_app.mcs.com.ciniplexis;

import android.test.AndroidTestCase;

/**
 * Created by ogayle on 28/10/2015.
 */
public class TestMovieServiceProvider extends AndroidTestCase {

   /* @TargetApi(11)
    public void testLoadMovie() {
        //clear all data and start a new
        getContext().getContentResolver().delete(DataContracts.MovieEntry.CONTENT_URI, null, null);

        MovieServiceProvider msp = new MovieServiceProvider(getContext());
        ArrayList<MovieDataModel> movies = new ArrayList<>();
        MovieDataModel sample = new MovieDataModel();
        sample.setId(1);
        sample.setPlot("test plot");
        sample.setPrice(12.52);
        sample.setImageUrl("img_url");
        sample.setRating(8.0);
        sample.setPopularity(80);
        sample.setTitle("test");
        sample.setReleaseDate("2015-08-28");
        sample.setIs3D(false);

        movies.add(sample);
        long val = msp.saveMovies(movies);

        assertFalse("Error: add Movie return invalid Id on Insert", val == -1);


            Cursor movieCursor = getContext().getContentResolver()
                    .query(DataContracts.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            if (movieCursor.moveToFirst()) {
                int index = movieCursor.getColumnIndex(DataContracts.MovieEntry._ID);
                assertEquals(movieCursor.getInt(index), sample.getId());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_TITLE);
                assertEquals(movieCursor.getString(index), sample.getTitle());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_PLOT);
                assertEquals(movieCursor.getString(index), sample.getPlot());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_RATING);
                assertEquals(movieCursor.getDouble(index), sample.getRating());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_RELEASE_DATE);
                assertEquals(movieCursor.getString(index), sample.getReleaseDate());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_PRICE);
                assertEquals(movieCursor.getDouble(index), sample.getPrice());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_IMAGE_URL);
                assertEquals(movieCursor.getString(index), sample.getImageUrl());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_AVERAGE_POPULARITY);
                assertEquals(movieCursor.getDouble(index), sample.getPopularity());

                index = movieCursor.getColumnIndex(DataContracts.MovieEntry.COLUMN_IS_PURCHASED);
                assertEquals(movieCursor.getInt(index), (sample.isPurchased()) ? 1 : 0);
            } else
                fail("Error: the cursor is empty");


            assertFalse("Error: Only one record should be present in th query", movieCursor.moveToNext());

            long res = msp.saveMovies(movies);

            assertTrue(res != -1);


        getContext().getContentResolver().delete(DataContracts.MovieEntry.CONTENT_URI, null, null);
        getContext().getContentResolver().acquireContentProviderClient(DataContracts.MovieEntry.CONTENT_URI)
                .getLocalContentProvider().shutdown();

    }


*/


}
