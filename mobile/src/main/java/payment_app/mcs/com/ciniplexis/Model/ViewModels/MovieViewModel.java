package payment_app.mcs.com.ciniplexis.Model.ViewModels;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import payment_app.mcs.com.ciniplexis.R;

/**
 * Created by ogayle on 26/10/2015.
 */
public class MovieViewModel extends RecyclerView.ViewHolder {

    @Bind(R.id.movie_title)
    protected TextView Title;

    @Bind(R.id.release_date)
    protected TextView ReleaseDate;

    @Bind(R.id.rating)
    protected AppCompatRatingBar Rating;

    @Bind(R.id.movie_image)
    protected ImageView Image;

    @Bind(R.id.fav)
    protected TextView favourite;

    @Bind(R.id.rating_val)
    protected TextView ratingValue;

    public View container;


    public MovieViewModel(View movie) {
        super(movie);
        ButterKnife.bind(this, movie);
        container = movie;
    }

    public void setFavourite(Drawable favouriteDrawable) {
        this.favourite.setCompoundDrawables(favouriteDrawable, null, null, null);
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue.setText(ratingValue);
    }

    public void setImage(Bitmap image) {
        Image.setImageBitmap(image);
    }

    public ImageView getImageView() {
        return Image;
    }

    public void setRating(float rating) {
        Rating.setRating(1);
        Rating.setNumStars(1);
    }

    public void setRatingColor(int colorId){
        LayerDrawable starLayer = (LayerDrawable)Rating.getProgressDrawable();
        starLayer.setColorFilter(colorId, PorterDuff.Mode.SRC_ATOP);
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate.setText(releaseDate);
    }

    public void setTitle(String title) {
        Title.setText(title);
    }

}
