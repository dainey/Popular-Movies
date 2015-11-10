package payment_app.mcs.com.ciniplexis.Interfaces.API;

import java.util.ArrayList;

import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.PageDataModel;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ogayle on 02/11/2015.
 */
public interface MovieHubService {


    @GET(MovieUtility.DISCOVER_MOVIE)
    Observable<PageDataModel> getMoviesSortBy(@Query("") String criteria, @Query("page") int page);

    @GET(MovieUtility.DISCOVER_MOVIE)
    Observable<PageDataModel>
    getMovieBetweenDates(@Query("primary_release_date.gte") String startDate, @Query("primary_release_date.lte") String endDate, @Query("page") int page);

    @GET(MovieUtility.DISCOVER_MOVIE)
    Observable<PageDataModel> getTrailers();

    @GET(MovieUtility.DISCOVER_MOVIE)
    Observable<PageDataModel> getReviews();
}
