package payment_app.mcs.com.ciniplexis.Activities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import payment_app.mcs.com.ciniplexis.Fragments.MovieDetailsFragment;
import payment_app.mcs.com.ciniplexis.Interfaces.CallBacks.MovieCallback;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 25/10/2015.
 */
public class MovieDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState != null)
            return;


        Bundle bundle = new Bundle();
        bundle.putParcelable(MovieUtility.MOVIE_URI, getIntent().getData());

        FragmentManager fm = getSupportFragmentManager();
        MovieDetailsFragment ddf = new MovieDetailsFragment();//.getInstance(mdm);
        ddf.setArguments(bundle);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.details_container, ddf);
        ft.commit();


    }

}
