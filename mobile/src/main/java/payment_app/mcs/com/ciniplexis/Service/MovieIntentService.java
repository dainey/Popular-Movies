package payment_app.mcs.com.ciniplexis.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;
import payment_app.mcs.com.ciniplexis.Utility.WebController;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieIntentService extends IntentService {

    public MovieIntentService() {
        super("MovieIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*String url = intent.getStringExtra(MovieUtility.REQUEST_URL);

        String jsonMovieList = new WebController().getRequest(url);
        MovieUtility.parseMoviesFromJSONString(getApplicationContext(), jsonMovieList, url);*/
    }


}
