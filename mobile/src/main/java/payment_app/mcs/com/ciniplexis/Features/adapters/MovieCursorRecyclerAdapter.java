package payment_app.mcs.com.ciniplexis.Features.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Interfaces.CallBacks.MovieDetailsCallBack;
import payment_app.mcs.com.ciniplexis.Utility.Constants;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.Model.ViewModels.MovieViewModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.HelperUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieCursorRecyclerAdapter extends CursorRecyclerAdapter<MovieViewModel> {


    private Activity _activity;
    private Drawable placeholder, favorite;
    private int ratingProgressColorId, selectedPosition = -1;
    private MovieDetailsCallBack defaultDetails = new MovieDetailsCallBack() {
        @Override
        public void displayDetails(Uri uri) {

        }
    };

    public MovieCursorRecyclerAdapter(Cursor cursor, Activity activity) {
        super(cursor);
        _activity = activity;

        int favoriteColorId = ContextCompat.getColor(activity, R.color.primary_red);
        ratingProgressColorId = ContextCompat.getColor(activity, R.color.primary_red);
        placeholder = ContextCompat.getDrawable(_activity, R.drawable.ic_video_camera_icon);
        favorite = ContextCompat.getDrawable(_activity, R.drawable.ic_action_favorite_dark);

        favorite.setColorFilter(favoriteColorId, PorterDuff.Mode.MULTIPLY);

        if (activity instanceof MovieDetailsCallBack) {
            defaultDetails = (MovieDetailsCallBack) activity;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(MovieViewModel holder, final Cursor cursor, final int position) {

        final MovieDataModel movie = new HelperUtility().getMovieFromCursor(cursor);
        holder.setTitle(movie.getTitle());
        holder.setRating((float) movie.getRating() / 2);
        holder.setRatingValue(String.format("(%.1f)", movie.getRating()));
        holder.setReleaseDate(movie.getReleaseDate());
        holder.setRatingColor(ratingProgressColorId);
        if (movie.isFavorite()) {
            holder.setFavourite(favorite);
        }
        else
        holder.setFavourite(null);


        Picasso.with(_activity)
                .load(Constants.BASE_IMAGE_URL + Constants.TABLET_SIZE_IMAGE + movie.getImageUrl())
                .fit()
                .placeholder(placeholder)
                .error(placeholder)
                .into(holder.getImageView());


        if (selectedPosition == position)
            ((CardView) holder.container).setCardBackgroundColor(ContextCompat.getColor(_activity, R.color.yellow_green_light));
        else
            ((CardView) holder.container).setCardBackgroundColor(ContextCompat.getColor(_activity, R.color.movie_card_bg));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri myUri = MovieEntry.buildMovieUri(movie.getId());
                defaultDetails.displayDetails(myUri);
                int prevSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(position);

                if (prevSelected != -1)
                    notifyItemChanged(prevSelected);
            }
        });
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public MovieViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_movie_item, parent, false);
        return new MovieViewModel(item);
    }


}
