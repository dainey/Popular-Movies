package payment_app.mcs.com.ciniplexis.Model.DataModels;

import android.graphics.Bitmap;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ogayle on 26/10/2015.
 */
public class MovieDataModel implements Serializable {
    @SerializedName("id")
    private int Id;

    @SerializedName("title")
    private String title;

    @SerializedName("vote_average")
    private double rating;

    @SerializedName("poster_path")
    private String imageUrl;

    @SerializedName("backdrop_path")
    private String movieBackground;

    @SerializedName("overview")
    private String plot;

    @SerializedName("release_date")
    private String releaseDate;

    private boolean is3D;

    @SerializedName("vote_count")
    private double price;

    @SerializedName("popularity")
    private double popularity;

    private boolean isPurchased;

    private boolean isFavorite;

    public boolean isPurchased() {
        return isPurchased;
    }

    public String getMovieBackground() {
        return movieBackground;
    }

    public void setMovieBackground(String movieBackground) {
        this.movieBackground = movieBackground;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setIsPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean is3D() {
        return is3D;
    }

    public void setIs3D(boolean is3D) {
        this.is3D = is3D;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
}
