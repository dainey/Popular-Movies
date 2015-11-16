package payment_app.mcs.com.ciniplexis.Service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieIntentService extends IntentService {

    public MovieIntentService() {
        super("MovieIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*String url = intent.getStringExtra(HelperUtility.REQUEST_URL);

        String jsonMovieList = new WebController().getRequest(url);
        HelperUtility.parseMoviesFromJSONString(getApplicationContext(), jsonMovieList, url);*/
    }


}
