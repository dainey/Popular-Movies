package payment_app.mcs.com.ciniplexis.Model;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import payment_app.mcs.com.ciniplexis.R;

/**
 * Created by ogayle on 26/10/2015.
 */
public class MovieViewModel extends RecyclerView.ViewHolder {

    protected TextView Title;
    protected TextView ReleaseDate;
    protected AppCompatRatingBar Rating;
    protected ImageView Image;
    public View container;


    public MovieViewModel(View movie) {
        super(movie);

        Title = (TextView) movie.findViewById(R.id.movie_title);
        ReleaseDate = (TextView) movie.findViewById(R.id.release_date);
        Rating = (AppCompatRatingBar) movie.findViewById(R.id.rating);
        Image = (ImageView) movie.findViewById(R.id.movie_image);
        container = movie;
    }


    public void setImage(Bitmap image) {
        Image.setImageBitmap(image);
    }

    public ImageView getImageView() {
        return Image;
    }

    public void setRating(float rating) {
        Rating.setRating(rating);
        Rating.setNumStars(5);
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate.setText(releaseDate);
    }

    public void setTitle(String title) {
        Title.setText(title);
    }

}
