package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ogayle on 12/11/2015.
 */
public class VideoCollection {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private ArrayList<VideoDataModel> results;

    public int getId() {
        return id;
    }

    public ArrayList<VideoDataModel> getResults() {
        return results;
    }
}
