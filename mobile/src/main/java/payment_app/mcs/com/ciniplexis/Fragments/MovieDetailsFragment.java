package payment_app.mcs.com.ciniplexis.Fragments;


import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Interfaces.CallBacks.MovieCallback;
import payment_app.mcs.com.ciniplexis.Utility.Constants;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieDataModel movie;

  /*  private MovieCallback movieCallback = new MovieCallback() {
        @Override
        public void updateFavorites(int movieId, boolean isFavorite) {

        }

        @Override
        public void updateMovieTicket(int movieId, int amount, double cost) {

        }
    };*/

    Toolbar toolbar;

    @Bind(R.id.movie_image)
    ImageView poster;

    @Bind(R.id.movie_title)
    TextView title;

    @Bind(R.id.release_date)
    TextView releaseDate;

    @Bind(R.id.plot)
    TextView plot;

    @Bind(R.id.rating)
    AppCompatRatingBar ratingBar;

    @Bind(R.id.rating_val)
    TextView ratingVal;

    @Bind(R.id.price)
    TextView price;

    @Bind(R.id.buy_btn)
    FloatingActionButton purchaseBtn;

    @Bind(R.id.mark_fav_btn)
    FloatingActionButton addFavBtn;

    private static final int MOVIE_DETAILS_LOADER = 101;
    private Uri mUri;
    private static String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_PLOT,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_IMAGE_URL,
            MovieEntry.COLUMN_AVERAGE_POPULARITY,
            MovieEntry.COLUMN_IS_PURCHASED,
            MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_BACKGROUND_IMAGE_URL,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_PRICE
    };

    public static MovieDetailsFragment getInstance(MovieDataModel _movie) {
        MovieDetailsFragment detailsFrag = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", _movie);
        detailsFrag.setArguments(args);
        return detailsFrag;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            mUri = args.getParcelable(MovieUtility.MOVIE_URI);
        }
        View movieDetails = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, movieDetails);
        return movieDetails;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

      /*  if (getActivity() instanceof MovieCallback)
            movieCallback = (MovieCallback) getActivity();
*/
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri == null)
            return null;

        return new CursorLoader(getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if (data == null || !data.moveToFirst())
            return;

        if (getView() == null)
            return;


        movie = MovieUtility.getMovieDataFromCursor(data);

        toolbar.setTitle(movie.getTitle());
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        plot.setText(movie.getPlot());

        ratingBar.setRating((float) movie.getRating() / 2);
        ratingVal.setText(String.format(" (%.1f) ", movie.getRating()));
        price.setText(String.format("$%.2f", movie.getPrice()));


        Drawable placeholder = ContextCompat.getDrawable(getActivity(), R.drawable.ic_video_camera_icon);

        Picasso.with(getContext())
                .load(Constants.BASE_IMAGE_URL + Constants.LARGE_TABLET_SIZE_IMAGE + movie.getImageUrl())
                .placeholder(placeholder)
                .error(placeholder)
                .into(poster);

        updateFavorite();


    }


    private void updateFavorite() {
        String addToFavStr = getResources().getString(R.string.add_fav);
        String remFromFavStr = getResources().getString(R.string.rem_fav);
        int btnColor;
        addFavBtn.setLabelText(movie.isFavorite() ? remFromFavStr : addToFavStr);

        if (movie.isFavorite()) {
            btnColor = Color.RED;
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        } else {
            btnColor = Color.GREEN;
            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        }

        addFavBtn.setColorNormal(btnColor);
    }

    @OnClick(R.id.buy_btn)
    public void updatePurchase(View view) {

        final Dialog ticketAlert = new Dialog(getContext());
        ticketAlert.setTitle("Ticket Purchase");
        ticketAlert.setContentView(R.layout.fragment_ticket_purchase);


        final NumberPicker numberPicker = (NumberPicker) ticketAlert.findViewById(R.id.number_of_ticket);
        final TextView totalCost = (TextView) ticketAlert.findViewById(R.id.total_cost);
        FloatingActionButton acceptUpdateBtn = (FloatingActionButton) ticketAlert.findViewById(R.id.accept_ticket_update);
        FloatingActionButton cancelUpdateBtn = (FloatingActionButton) ticketAlert.findViewById(R.id.cancel_ticket_update);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                totalCost.setText(String.format("$%.2f", (movie.getPrice() * newVal)));
            }
        });
        cancelUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketAlert.dismiss();
            }
        });

        acceptUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberPicker.getValue() > 0)
                    movie.setIsPurchased(true);
                else
                    movie.setIsPurchased(false);

                MovieUtility.updateMovie(getContext(), movie);
                //movieCallback.updateMovieTicket(movie.getId(), numberPicker.getValue(), (movie.getPrice() * numberPicker.getValue()));
            }
        });

        ticketAlert.show();

    }

    @OnClick(R.id.mark_fav_btn)
    public void updateFavorite(View view) {
        if (movie == null) return;

        if (movie.isFavorite())
            movie.setIsFavorite(false);
            // movieCallback.updateFavorites(movie.getId(), false);
        else
            movie.setIsFavorite(true);
        // movieCallback.updateFavorites(movie.getId(), true);
        MovieUtility.updateMovie(getContext(), movie);
        //getLoaderManager().restartLoader(MOVIE_DETAILS_LOADER, null, this);
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
