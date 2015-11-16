package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ogayle on 12/11/2015.
 */
public class VideoDataModel extends BaseEntity implements Serializable {

    @SerializedName("iso_639_1")
    private String languageISO;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private String size;

    @SerializedName("type")
    private String type;

    private int movieId;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public String getId() {
        return (String)super.getId();
    }

    public String getKey() {
        return key;
    }

    public String getLanguageISO() {
        return languageISO;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLanguageISO(String languageISO) {
        this.languageISO = languageISO;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }
}
