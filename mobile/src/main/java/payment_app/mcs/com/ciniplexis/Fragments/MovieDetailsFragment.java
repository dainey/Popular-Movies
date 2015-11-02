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
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Utility.Constants;
import payment_app.mcs.com.ciniplexis.Model.MovieDataModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

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
        return inflater.inflate(R.layout.fragment_movie_details, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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


        MovieDataModel movie = MovieUtility.getMovieDataFromCursor(data);


        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle(movie.getTitle());
        ((TextView) getView().findViewById(R.id.movie_title)).setText(movie.getTitle());
        ((TextView) getView().findViewById(R.id.release_date)).setText(movie.getReleaseDate());
        ((TextView) getView().findViewById(R.id.plot)).setText(movie.getPlot());

        ((AppCompatRatingBar) getView().findViewById(R.id.rating)).setRating((float) movie.getRating() / 2);
        ((TextView) getView().findViewById(R.id.rating_val)).setText(String.format(" (%.1f) ",movie.getRating()));
        ((TextView) getView().findViewById(R.id.price)).setText(String.format("$%.2f", movie.getPrice()));


        Drawable placeholder = ContextCompat.getDrawable(getActivity(), R.drawable.ic_video_camera_icon);

        Picasso.with(getContext())
                .load(Constants.BASE_IMAGE_URL + Constants.LARGE_TABLET_SIZE_IMAGE + movie.getImageUrl())
                .placeholder(placeholder)
                .error(placeholder)
                .into((ImageView) getView().findViewById(R.id.movie_image));


        final FloatingActionButton purchaseBtn = ((FloatingActionButton) getView().findViewById(R.id.buy_btn));
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo use database to determine if purchased
                String buyTicketStr = getResources().getString(R.string.buy_tickets);
                String refundTicketsStr = getResources().getString(R.string.ref_tickets);
                int btnColor;
                boolean isPurchased = (purchaseBtn.getLabelText().equals(buyTicketStr));
                purchaseBtn
                        .setLabelText(isPurchased ? refundTicketsStr : buyTicketStr);

                Dialog ticketAlert = new Dialog(getContext());

                if (isPurchased) {
                    ticketAlert.setTitle("Ticket Purchase");
                    btnColor = Color.RED;

                } else {
                    ticketAlert.setTitle("Ticket Purchase");
                    btnColor = Color.GREEN;

                }
                purchaseBtn.setColorNormal(btnColor);
                ticketAlert.show();
            }
        });


        final FloatingActionButton addFavBtn = ((FloatingActionButton) getView().findViewById(R.id.mark_fav_btn));
        addFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo use database to determine fav
                String addToFavStr = getResources().getString(R.string.add_fav);
                String remFromFavStr = getResources().getString(R.string.rem_fav);
                int btnColor;
                boolean isInFavorites = (addFavBtn.getLabelText().equals(addToFavStr));
                addFavBtn
                        .setLabelText(isInFavorites ? remFromFavStr : addToFavStr);


                if (isInFavorites) {
                    btnColor = Color.RED;
                    Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    btnColor = Color.GREEN;
                    Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }

                addFavBtn.setColorNormal(btnColor);
            }
        });

    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
