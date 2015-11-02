package payment_app.mcs.com.ciniplexis.Features.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Interfaces.MovieDetailsCallBack;
import payment_app.mcs.com.ciniplexis.Utility.Constants;
import payment_app.mcs.com.ciniplexis.Model.MovieDataModel;
import payment_app.mcs.com.ciniplexis.Model.MovieViewModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieCursorRecyclerAdapter extends CursorRecyclerAdapter<MovieViewModel> {


    private Activity _activity;
    private Drawable placeholder;
    private MovieDetailsCallBack defaultDetails = new MovieDetailsCallBack() {
        @Override
        public void displayDetails(Uri uri) {

        }
    };

    public MovieCursorRecyclerAdapter(Cursor cursor, Activity activity) {
        super(cursor);
        _activity = activity;
        placeholder = ContextCompat.getDrawable(_activity, R.drawable.ic_video_camera_icon);
        if (activity instanceof MovieDetailsCallBack) {
            defaultDetails = (MovieDetailsCallBack) activity;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public void onBindViewHolder(MovieViewModel holder, final Cursor cursor) {

        final MovieDataModel movie = MovieUtility.getMovieDataFromCursor(cursor);

        //String plot = (movie.getPlot()==null)?"No plot"
        //holder.setPlot();
        holder.setTitle(movie.getTitle());
        holder.setRating((float) movie.getRating() / 2);
        holder.setReleaseDate(movie.getReleaseDate());

        Picasso.with(_activity)
                .load(Constants.BASE_IMAGE_URL + Constants.TABLET_SIZE_IMAGE + movie.getImageUrl())
                .fit()
                .placeholder(placeholder)
                .error(placeholder)
                .into(holder.getImageView());


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri myUri = MovieEntry.buildMovieUri(movie.getId());
                defaultDetails.displayDetails(myUri);
            }
        });
    }

    @Override
    public MovieViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_movie_item, parent, false);
        return new MovieViewModel(item);
    }


}
