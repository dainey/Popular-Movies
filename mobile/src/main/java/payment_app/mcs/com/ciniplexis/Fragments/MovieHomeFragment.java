package payment_app.mcs.com.ciniplexis.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Features.adapters.MovieCursorRecyclerAdapter;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Service.MovieIntentService;
import payment_app.mcs.com.ciniplexis.Utility.MovieUtility;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public RecyclerView rvMovieContainer;

    private Toolbar toolbar;
    private String partialQuery;
    private static SharedPreferences movieSettings;
    private Uri filterUri;
    private static final int MOVIE_LOADER_ID = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragMovieListPage = inflater.inflate(R.layout.fragment_movie_list, container, false);
        setHasOptionsMenu(true);

        rvMovieContainer = (RecyclerView) fragMovieListPage.findViewById(R.id.movie_list);
        final StaggeredGridLayoutManager sGridLayout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sGridLayout.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvMovieContainer.setLayoutManager(sGridLayout);
        MovieCursorRecyclerAdapter mAdapter = new MovieCursorRecyclerAdapter(null, getActivity());
        rvMovieContainer.setAdapter(mAdapter);

        rvMovieContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] positions = sGridLayout.findLastCompletelyVisibleItemPositions(null);
                int count = (rvMovieContainer.getAdapter().getItemCount() - 10);

                for (int pos : positions) {
                    if (pos == count && partialQuery != null) {
                        fetchServerData(partialQuery, false);
                        return;
                    }
                }
            }


        });
        return fragMovieListPage;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_menu_filter, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            registerTextChangeListener(searchView);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String param1;
        String param2 = null;
        Calendar calendar;
        DateFormat dateFormat;

        boolean isSort = true;


        switch (item.getItemId()) {
            case R.id.popularity:
                partialQuery = MovieUtility.sortQuery.replace(MovieUtility.SORT_CRITERIA, MovieUtility.CRITERIA_POPULARITY);
                param1 = "popularity";
                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.popular)));
                break;
            case R.id.rating:
                partialQuery = MovieUtility.sortQuery.replace(MovieUtility.SORT_CRITERIA, MovieUtility.CRITERIA_RATING);
                param1 = "rating";
                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.rating)));
                break;
            case R.id.now_showing:
                calendar = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                param1 = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                param2 = dateFormat.format(calendar.getTime());
                partialQuery = MovieUtility.movieByDate.replace(MovieUtility.START_DATE, param1).replace(MovieUtility.END_DATE, param2);
                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.in_theatres)));
                isSort = false;
                break;

            case R.id.date:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                param1 = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, 60);
                param2 = dateFormat.format(calendar.getTime());
                partialQuery = MovieUtility.movieByDate.replace(MovieUtility.START_DATE, param1).replace(MovieUtility.END_DATE, param2);
                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.coming_soon)));
                isSort = false;
                break;
            default:
                return false;
        }
        fetchServerData(partialQuery, true);


        if (isSort)
            filterUri = MovieEntry.buildMovieBySortCriteria(param1);
        else
            filterUri = MovieEntry.buildMovieUriWithDates(param1, param2);

        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Filter Movies");
        menu.add(R.menu.movie_menu_filter, R.id.popularity, 0, "By Popularity");
        menu.add(R.menu.movie_menu_filter, R.id.rating, 1, "By Rating");
        menu.add(R.menu.movie_menu_filter, R.id.now_showing, 2, "In Theatres");
        menu.add(R.menu.movie_menu_filter, R.id.date, 3, "Coming Soon");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        movieSettings = MovieUtility.getMovieSharedPreference(getContext());
        String filterUriStr = movieSettings.getString(MovieUtility.FILTER_URI_PREF, null);

        if (filterUriStr != null)
            filterUri = Uri.parse(filterUriStr);

        if (filterUri == null)
            filterUri = MovieEntry.buildMoviePopularityView();


        //use cached data
        fetchServerData(resolveServerQuery(), true);

        if (getView() == null)
            return;

        final FloatingActionButton filterMovies = (FloatingActionButton) getView().findViewById(R.id.sort_movies);
        registerForContextMenu(filterMovies);
        filterMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().openContextMenu(filterMovies);
            }
        });


        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private String resolveServerQuery() {
        String filterUriStr = filterUri.toString();
        boolean isFilter = filterUriStr.contains(MovieUtility.START_DATE);


        if (isFilter) {
            toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.date)));
            return MovieUtility
                    .movieByDate
                    .replace(MovieUtility.START_DATE, filterUri.getQueryParameter(MovieUtility.START_DATE))
                    .replace(MovieUtility.END_DATE, filterUri.getQueryParameter(MovieUtility.END_DATE));
        } else {
            if (!filterUriStr.contains("sort")) {
                String query = filterUri.getLastPathSegment();

                if (query.equals(MovieEntry.buildMovieByRatingView().getLastPathSegment())) {
                    toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.rating)));
                    return MovieUtility.sortQuery.replace(MovieUtility.SORT_CRITERIA, MovieUtility.CRITERIA_RATING);
                }

                if (query.equals(MovieEntry.buildMovieByDateView().getLastPathSegment())) {
                    toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.date)));
                    return MovieUtility.sortQuery.replace(MovieUtility.SORT_CRITERIA, MovieUtility.CRITERIA_DATE);
                }

            }
        }
        //default
        toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.popular)));
        return MovieUtility.sortQuery.replace(MovieUtility.SORT_CRITERIA, MovieUtility.CRITERIA_POPULARITY);
    }

    private void fetchServerData(String query, boolean isFirstPage) {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), getResources().getString(R.string.net_unavailable), Toast.LENGTH_SHORT).show();
            return;
        }

        int page = 1;
        partialQuery = query;
        if (!isFirstPage)
            page = MovieUtility.getMovieSharedPreference(getContext()).getInt(MovieUtility.PAGE_INDEX_PREF, 0) + 1;

        String searchQuery = MovieUtility.baseQuery.replace(MovieUtility.DISCOVERY_PARAM, query) + "&page=" + page;

        String mostRecentUrl = MovieUtility.getMovieSharedPreference(getContext()).getString(MovieUtility.LAST_VISITED_URL_PREF, "");

        if (!searchQuery.equals(mostRecentUrl)) {
            Intent myMovieIntent = new Intent(getActivity(), MovieIntentService.class);
            myMovieIntent.putExtra(MovieUtility.REQUEST_URL, searchQuery);
            getActivity().startService(myMovieIntent);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityMangr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityMangr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void registerTextChangeListener(SearchView sv) {
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty())
                    return true;

                filterUri = MovieEntry.buildMovieUriWithTitleFilter(newText);
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MovieHomeFragment.this);

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {


        movieSettings = MovieUtility.getMovieSharedPreference(getContext());
        SharedPreferences.Editor movieSettingsEditor = movieSettings.edit();
        movieSettingsEditor.putString(MovieUtility.FILTER_URI_PREF, filterUri.toString());
        movieSettingsEditor.apply();

        return new CursorLoader(getActivity(), filterUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (rvMovieContainer != null)
            ((MovieCursorRecyclerAdapter) rvMovieContainer.getAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        if (rvMovieContainer != null)
            ((MovieCursorRecyclerAdapter) rvMovieContainer.getAdapter()).swapCursor(null);
    }
}
