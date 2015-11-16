package payment_app.mcs.com.ciniplexis.Interfaces.API;

import payment_app.mcs.com.ciniplexis.Model.DataModels.MoviePageDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.ReviewPageDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoCollection;
import payment_app.mcs.com.ciniplexis.Utility.HelperUtility;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ogayle on 02/11/2015.
 */
public interface MovieHubService {


    @GET(HelperUtility.DISCOVER_MOVIE)
    Observable<MoviePageDataModel> getMoviesSortBy(@Query("") String criteria, @Query("page") int page);

    @GET(HelperUtility.DISCOVER_MOVIE)
    Observable<MoviePageDataModel>
    getMovieBetweenDates(@Query("primary_release_date.gte") String startDate, @Query("primary_release_date.lte") String endDate, @Query("page") int page);

    @GET(HelperUtility.MOVIE_VIDEOS)
    Observable<VideoCollection> getTrailers(@Path("id") int movieId);

    @GET(HelperUtility.MOVIE_REVIEWS)
    Observable<ReviewPageDataModel> getReviews(@Path("id") int movieId);
}
