package payment_app.mcs.com.ciniplexis.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import payment_app.mcs.com.ciniplexis.Interfaces.API.MovieHubService;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MoviePageDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.ReviewDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.ReviewPageDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoCollection;
import payment_app.mcs.com.ciniplexis.Model.DataModels.VideoDataModel;
import payment_app.mcs.com.ciniplexis.R;
import retrofit.CallAdapter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by ogayle on 28/10/2015.
 */
public class WebController {

    private Context mContext;
    private MovieHubService movieHubService;


    public WebController(Context appContext) {
        mContext = appContext;
        movieHubService = buildRetrofitController().create(MovieHubService.class);
    }

    public void getMostPopular(int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(HelperUtility.CRITERIA_POPULARITY, page));

    }

    public void getMostTopRated(int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(HelperUtility.CRITERIA_RATING, page));
    }

    public void getMostRecent(int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(HelperUtility.CRITERIA_DATE, page));
    }

    public void getMostBetweenDates(String startDate, String endDate, int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMovieBetweenDates(startDate, endDate, page));
    }

    public void getVideos(final int movieId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        movieHubService.getTrailers(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<VideoCollection>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mContext instanceof Activity) {
                            final Activity activity = (Activity) mContext;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, R.string.net_data_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onNext(VideoCollection videoCollection) {
                        for (VideoDataModel video : videoCollection.getResults()) {
                            video.setMovieId(movieId);
                        }
                        new HelperUtility().saveToDatabase(mContext, videoCollection.getResults());
                    }
                });
    }

    public void getReviews(final int movieId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        movieHubService.getReviews(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<ReviewPageDataModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mContext instanceof Activity) {
                            final Activity activity = (Activity) mContext;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, R.string.net_data_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onNext(ReviewPageDataModel reviewPage) {
                        for (ReviewDataModel reviews : reviewPage.getResults()) {
                            reviews.setMovieId(movieId);
                        }

                        ///todo add date since Api doesnt provide any for comments
                        new HelperUtility().saveToDatabase(mContext, reviewPage.getResults());
                    }
                });
    }

    private void subscribeToPageSubscriber(Observable<MoviePageDataModel> pageObserver) {
        pageObserver.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<MoviePageDataModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mContext instanceof Activity) {
                            final Activity activity = (Activity) mContext;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, R.string.net_data_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }

                    @Override
                    public void onNext(MoviePageDataModel pageModel) {
                        new HelperUtility().saveToDatabase(mContext, pageModel.getResults());
                        SharedPreferences movieSetting = HelperUtility.getMovieSharedPreference(mContext);
                        SharedPreferences.Editor movieSettingEditor = movieSetting.edit();

                        movieSettingEditor.putInt(HelperUtility.PAGE_INDEX_PREF, pageModel.getPage());
                        movieSettingEditor.apply();
                    }
                });
    }

    private Retrofit buildRetrofitController() {
        final OkHttpClient client = new OkHttpClient();

        Interceptor requestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request clientRequest = chain
                        .request();
                HttpUrl url = clientRequest.httpUrl();


                HttpUrl.Builder authorizedUrl = url.newBuilder().addQueryParameter("api_key", HelperUtility.API_KEY);
                Request interceptRequest = clientRequest
                        .newBuilder()
                        .url(authorizedUrl.build())
                        .method(clientRequest.method(), clientRequest.body())
                        .build();


                Log.e("URL", interceptRequest.urlString());
                return chain.proceed(interceptRequest);

            }
        };

        client.interceptors().add(requestInterceptor);

        CallAdapter.Factory factory = RxJavaCallAdapterFactory.create();

        return new Retrofit
                .Builder()
                .baseUrl(HelperUtility.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(factory)
                .client(client)
                .build();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityMangr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityMangr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
