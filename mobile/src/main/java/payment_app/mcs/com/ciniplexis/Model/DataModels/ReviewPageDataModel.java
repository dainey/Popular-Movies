package payment_app.mcs.com.ciniplexis.Model.DataModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ogayle on 12/11/2015.
 */
public class ReviewPageDataModel {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private ArrayList<ReviewDataModel> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;


    public int getPage() {
        return page;
    }

    public ArrayList<ReviewDataModel> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
