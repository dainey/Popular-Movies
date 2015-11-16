package payment_app.mcs.com.ciniplexis.Fragments;


import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import payment_app.mcs.com.ciniplexis.ContentProvider.AutoMovieContentProvider;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.Model.DataModels.ReviewDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoDataModel;
import payment_app.mcs.com.ciniplexis.Utility.Constants;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.HelperUtility;
import payment_app.mcs.com.ciniplexis.Utility.WebController;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieDataModel movie;

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

    @Bind(R.id.trailer_content)
    LinearLayout trailer_content;

    @Bind(R.id.comment_content)
    LinearLayout comment_content;

    private static final int MOVIE_LOADER = 101;
    private static final int REVIEW_LOADER = 102;
    private static final int VIDEO_LOADER = 103;
    private Uri mUri;
    private static String[] MOVIE_COLUMNS = {
            MovieColumns._ID,
            MovieColumns.PLOT,
            MovieColumns.TITLE,
            MovieColumns.IMAGE,
            MovieColumns.POPULARITY,
            MovieColumns.IS_PURCHASED,
            MovieColumns.RATING,
            MovieColumns.BACKGROUND_IMAGE,
            MovieColumns.RELEASE_DATE,
            MovieColumns.PRICE,
            MovieColumns.IS_FAVORITE,
    };

    public static MovieDetailsFragment getInstance(MovieDataModel _movie) {
        MovieDetailsFragment detailsFrag = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", _movie);
        detailsFrag.setArguments(args);
        return detailsFrag;
    }


    private void resolveMovieDetails(Cursor data) {
        if (!data.moveToFirst()) return;


        movie = new HelperUtility().getMovieFromCursor(data);
        WebController controller = new WebController(getActivity());

        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(shareMovieInfo(movie));

        controller.getReviews(movie.getId());
        controller.getVideos(movie.getId());

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

        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
    }

    private void resolveVideoDetails(Cursor data) {
        if (!data.moveToFirst()) return;

        trailer_content.removeAllViews();
        LayoutInflater inflater = getLayoutInflater(null);

        do {
            View trailerView = inflater.inflate(R.layout.fragment_trailer_item, trailer_content, false);
            trailer_content.addView(trailerView);
            final VideoDataModel video = new HelperUtility().getVideoFromCursor(data);

            ((TextView) trailerView.findViewById(R.id.video_name)).setText(video.getName());
            ((TextView) trailerView.findViewById(R.id.video_description)).setText(String.format("%sp %s", video.getSize(), video.getType()));
            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(shareMovieTrailer(video.getKey()));
                    //Toast.makeText(getContext(), "Start video via intent with youtube " + video.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        } while (data.moveToNext());

    }

    private void resolveReviewDetails(Cursor data) {
        if (!data.moveToFirst()) return;

        comment_content.removeAllViews();
        LayoutInflater inflater = getLayoutInflater(null);

        do {
            View reviewView = inflater.inflate(R.layout.fragment_review_item, comment_content, false);
            comment_content.addView(reviewView);
            final ReviewDataModel review = new HelperUtility().getReviewFromCursor(data);
            ((TextView) reviewView.findViewById(R.id.author)).setText(review.getAuthor());
            ((TextView) reviewView.findViewById(R.id.date)).setText(review.getDate());
            ((TextView) reviewView.findViewById(R.id.comment)).setText(String.format(" %s", review.getContent()));

        } while (data.moveToNext());

    }

    private Intent shareMovieTrailer(String key) {
        String youtubeUrl = "http://www.youtube.com/watch?v=";
        Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl + key));

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        return shareIntent;

    }

    private Intent shareMovieInfo(MovieDataModel movie) {
        if (movie == null) return null;

        String movieInfo = String.format("#CiniplexMovies\n#%s\nReleased: %s\nRating: %.1f \nTicket Price: $%.2f",
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getRating(),
                movie.getPrice());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, movieInfo);
        return shareIntent;
    }

    private void updateFavorite() {
        String addToFavStr = getResources().getString(R.string.add_fav);
        String remFromFavStr = getResources().getString(R.string.rem_fav);
        int btnColor;
        addFavBtn.setLabelText(movie.isFavorite() ? remFromFavStr : addToFavStr);

        if (movie.isFavorite()) {
            btnColor = ContextCompat.getColor(getContext(), R.color.accent_green);

        } else {
            btnColor = ContextCompat.getColor(getContext(), R.color.primary_red);
        }

        addFavBtn.setColorNormal(btnColor);
    }

    @OnClick(R.id.buy_btn)
    public void updatePurchase(View view) {

        if (movie == null) return;
        final Dialog ticketAlert = new Dialog(getContext());
        ticketAlert.setTitle("Ticket Purchase");
        ticketAlert.setContentView(R.layout.fragment_ticket_purchase);


        final NumberPicker numberPicker = (NumberPicker) ticketAlert.findViewById(R.id.number_of_ticket);
        final TextView totalCost = (TextView) ticketAlert.findViewById(R.id.total_cost);
        FloatingActionButton acceptUpdateBtn = (FloatingActionButton) ticketAlert.findViewById(R.id.accept_ticket_update);
        FloatingActionButton cancelUpdateBtn = (FloatingActionButton) ticketAlert.findViewById(R.id.cancel_ticket_update);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(25);
        numberPicker.setValue(0);
        numberPicker.setWrapSelectorWheel(false);
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

                new HelperUtility().updateEntity(getContext(), movie);
                ticketAlert.dismiss();
                Snackbar.make(purchaseBtn, (numberPicker.getValue() > 0) ? "Total Tickets Purchased : " + numberPicker.getValue() : "No  Tickets Purchased", Snackbar.LENGTH_SHORT)
                        .show();

            }
        });

        ticketAlert.show();

    }

    @OnClick(R.id.mark_fav_btn)
    public void updateFavorite(View view) {
        if (movie == null) return;
        String msg;

        if (movie.isFavorite()) {
            movie.setIsFavorite(false);
            msg = "Removed from Favorites";

        } else {
            movie.setIsFavorite(true);
            msg = "Added to Favorites";
        }

        new HelperUtility().updateEntity(getContext(), movie);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private ShareActionProvider mShareActionProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        setHasOptionsMenu(true);
        if (args != null) {
            mUri = args.getParcelable(HelperUtility.MOVIE_URI);
        }
        View movieDetails = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, movieDetails);
        return movieDetails;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.findItem(R.id.share) == null)
            inflater.inflate(R.menu.movie_details_menu, menu);

        MenuItem item = menu.findItem(R.id.share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        if (movie != null)
            mShareActionProvider.setShareIntent(shareMovieInfo(movie));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri entityUri = null;
        String[] entityColumns = null;
        switch (id) {
            case MOVIE_LOADER:
                if (mUri == null) return null;
                entityUri = mUri;
                entityColumns = MOVIE_COLUMNS;

                break;

            case VIDEO_LOADER:
                if (movie == null) return null;
                entityUri = AutoMovieContentProvider.Video.buildMovieVideoUri(movie.getId());
                break;

            case REVIEW_LOADER:
                if (movie == null) return null;
                entityUri = AutoMovieContentProvider.Review.buildMovieReviewUri(movie.getId());
                break;
        }


        return new CursorLoader(getActivity(),
                entityUri,
                entityColumns,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if (data == null)
            return;

        if (getView() == null)
            return;


        switch (loader.getId()) {
            case MOVIE_LOADER:
                resolveMovieDetails(data);
                break;

            case VIDEO_LOADER:
                resolveVideoDetails(data);
                break;

            case REVIEW_LOADER:
                resolveReviewDetails(data);
                break;
        }


    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
