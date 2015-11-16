package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ogayle on 12/11/2015.
 */
public class ReviewDataModel extends BaseEntity implements Serializable {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    private int movieId;

    private String date;

    @Override
    public String getId() {
        return (String)super.getId();
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }


    public String getUrl() {
        return url;
    }
}
