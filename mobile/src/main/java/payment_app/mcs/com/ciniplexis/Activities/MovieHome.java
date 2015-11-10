package payment_app.mcs.com.ciniplexis.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import payment_app.mcs.com.ciniplexis.Interfaces.CallBacks.MovieCallback;
import payment_app.mcs.com.ciniplexis.Interfaces.CallBacks.MovieDetailsCallBack;
import payment_app.mcs.com.ciniplexis.Model.DataModels.MovieDataModel;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 25/10/2015.
 */
public class MovieHome extends AppCompatActivity
        implements
        MovieDetailsCallBack,
        NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_purchases:
                Toast.makeText(getApplicationContext(), "Show Purchased and Reserved Tickets ", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_favorites:
                Toast.makeText(getApplicationContext(), "Show &amp; Share Favorite Movies", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_special:
                Toast.makeText(getApplicationContext(), "Show Special Feature events etc", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_location:
                Toast.makeText(getApplicationContext(), "Show Theatre Locations and their prices", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_nt:
                Toast.makeText(getApplicationContext(), "Show Theatre Opera Listing", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_concession:
                Toast.makeText(getApplicationContext(), "Order what is available at concession stands", Toast.LENGTH_LONG).show();
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void displayDetails(Uri dataUri) {
        Intent movieDetailsIntent = new Intent(this, MovieDetails.class);
        movieDetailsIntent.setData(dataUri);
        startActivity(movieDetailsIntent);

    }
}
