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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import payment_app.mcs.com.ciniplexis.Interfaces.API.MovieHubService;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.Model.DataModels.PageDataModel;
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
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(MovieUtility.CRITERIA_POPULARITY, page));

    }

    public void getMostTopRated(int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(MovieUtility.CRITERIA_RATING, page));
    }

    public void getMostRecent(int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMoviesSortBy(MovieUtility.CRITERIA_DATE, page));
    }

    public void getMostBetweenDates(String startDate, String endDate, int page) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mContext, R.string.net_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        subscribeToPageSubscriber(movieHubService.getMovieBetweenDates(startDate, endDate, page));
    }

    private void subscribeToPageSubscriber(Observable<PageDataModel> pageObserver) {
        pageObserver.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<PageDataModel>() {
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
                    public void onNext(PageDataModel pageModel) {
                        MovieUtility.saveMovies(mContext, pageModel.getResults());
                        SharedPreferences movieSetting = MovieUtility.getMovieSharedPreference(mContext);
                        SharedPreferences.Editor movieSettingEditor = movieSetting.edit();

                        movieSettingEditor.putInt(MovieUtility.PAGE_INDEX_PREF, pageModel.getPage());
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


                HttpUrl.Builder authorizedUrl = url.newBuilder().addQueryParameter("api_key", MovieUtility.API_KEY);
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
                .baseUrl(MovieUtility.BASE_URL)
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
