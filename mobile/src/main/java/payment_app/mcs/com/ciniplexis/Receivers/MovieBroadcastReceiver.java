package payment_app.mcs.com.ciniplexis.Receivers;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import payment_app.mcs.com.ciniplexis.Service.MovieIntentService;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("BROADCAST", "alarm broadcast");
        Intent myMovieIntent = new Intent(context, MovieIntentService.class);
        myMovieIntent.putExtra(MovieUtility.REQUEST_URL, intent.getStringExtra(MovieUtility.REQUEST_URL));
        context.startService(myMovieIntent);
    }
}
