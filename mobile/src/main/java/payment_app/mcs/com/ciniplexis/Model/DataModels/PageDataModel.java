package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ogayle on 03/11/2015.
 */
public class PageDataModel {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private ArrayList<MovieDataModel> results;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<MovieDataModel> getResults() {
        return results;
    }

    public void setResults(ArrayList<MovieDataModel> results) {
        this.results = results;
    }
}
