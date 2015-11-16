package payment_app.mcs.com.ciniplexis.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import payment_app.mcs.com.ciniplexis.Contracts.MovieEntry;
import payment_app.mcs.com.ciniplexis.Features.adapters.MovieCursorRecyclerAdapter;
import payment_app.mcs.com.ciniplexis.Interfaces.DB.MovieColumns;
import payment_app.mcs.com.ciniplexis.R;
import payment_app.mcs.com.ciniplexis.Utility.HelperUtility;
import payment_app.mcs.com.ciniplexis.Utility.WebController;
import payment_app.mcs.com.ciniplexis.ContentProvider.AutoMovieContentProvider;

/**
 * Created by ogayle on 28/10/2015.
 */
public class MovieHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean loading = false;
    private int scrollPosition = -1;

    @Bind(R.id.movie_list)
    RecyclerView rvMovieContainer;


    Toolbar toolbar;

    private static SharedPreferences movieSettings;
    private Uri filterUri;
    private static final int MOVIE_LOADER_ID = 100;
    private static final String SCROLL_POSITION = "SCROLL_POS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragMovieListPage = inflater.inflate(R.layout.fragment_movie_list, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, fragMovieListPage);


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
                    if (!loading)
                        if (pos >= count && filterUri != null) {
                            resolveServerQuery(false);
                            loading = true;

                            TimerTask tTask = new TimerTask() {
                                @Override
                                public void run() {
                                    loading = false;
                                }
                            };
                            Timer t = new Timer("loadingTimeOut");
                            t.schedule(tTask, 25);

                            return;
                        }
                }
            }


        });
        return fragMovieListPage;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        String param1;
        String param2;
        Calendar calendar;
        DateFormat dateFormat;

        switch (item.getItemId()) {

            case R.id.favorite:
                filterUri = AutoMovieContentProvider.Movie.buildMovieFavoriteView();

                break;
            case R.id.popularity:
                filterUri = AutoMovieContentProvider.Movie.buildMoviePopularityView();

                break;
            case R.id.rating:
                filterUri = AutoMovieContentProvider.Movie.buildMovieByRatingView();

                break;
            case R.id.now_showing:
                calendar = Calendar.getInstance();

                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                param1 = dateFormat.format(calendar.getTime());

                calendar.add(Calendar.DAY_OF_YEAR, 7);
                param2 = dateFormat.format(calendar.getTime());

                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.in_theatres)));

                filterUri = AutoMovieContentProvider.Movie.buildMovieUriWithDates(param1, param2);
                break;

            case R.id.coming_soon:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar = Calendar.getInstance();

                calendar.add(Calendar.DAY_OF_YEAR, 7);
                param1 = dateFormat.format(calendar.getTime());

                calendar.add(Calendar.DAY_OF_YEAR, 60);
                param2 = dateFormat.format(calendar.getTime());

                toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.coming_soon)));

                filterUri = AutoMovieContentProvider.Movie.buildMovieUriWithDates(param1, param2);
                break;
            default:
                return false;
        }


        resolveServerQuery(true);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, scrollPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        movieSettings = HelperUtility.getMovieSharedPreference(getContext());
        String filterUriStr = movieSettings.getString(HelperUtility.FILTER_URI_PREF, null);

        if (filterUriStr != null)
            filterUri = Uri.parse(filterUriStr);

        if (filterUri == null)
            filterUri = MovieEntry.buildMoviePopularityView();


        //use cached data
        resolveServerQuery(true);

        if (getView() == null)
            return;

        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private void resolveServerQuery(boolean isFirstPage) {
        String filterUriStr = filterUri.toString();
        boolean isFilter = filterUriStr.contains(HelperUtility.START_DATE);
        WebController webController = new WebController(getContext());

        int page = 1;
        if (!isFirstPage)
            page = HelperUtility.getMovieSharedPreference(getContext()).getInt(HelperUtility.PAGE_INDEX_PREF, 0) + 1;


        if (isFilter) {
            ////// TODO: 03/11/2015 do date diff to choose appropriate title
            toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.date)));
            String start = filterUri.getQueryParameter(HelperUtility.START_DATE);
            String end = filterUri.getQueryParameter(HelperUtility.END_DATE);

            webController.getMostBetweenDates(start, end, page);

            return;
        } else {
            if (filterUriStr.contains("sort")) {
                String query = filterUri.getLastPathSegment();

                if (query.equals(MovieEntry.buildMovieByRatingView().getLastPathSegment())) {
                    toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.rating)));

                    webController.getMostTopRated(page);
                    return;
                }

                if (query.equals(MovieEntry.buildMovieByDateView().getLastPathSegment())) {
                    toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.date)));

                    webController.getMostRecent(page);
                    return;
                }

            }
        }
        //default
        toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.popular)));
        webController.getMostPopular(page);
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

                filterUri = AutoMovieContentProvider.Movie.buildMovieUriWithTitleFilter(newText);
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


        movieSettings = HelperUtility.getMovieSharedPreference(getContext());
        SharedPreferences.Editor movieSettingsEditor = movieSettings.edit();
        movieSettingsEditor.putString(HelperUtility.FILTER_URI_PREF, filterUri.toString());
        movieSettingsEditor.apply();

        String filterUriStr = filterUri.toString();
        boolean isDateFilter = filterUriStr.contains(HelperUtility.START_DATE);
        boolean isTitleFilter = filterUriStr.contains("f_name");
        String[] selectionArgs = null;
        String selection = null;

        if (isDateFilter) {
            toolbar.setTitle(String.format("%s : %s", getString(R.string.app_name), getString(R.string.date)));
            String start = filterUri.getQueryParameter(HelperUtility.START_DATE);
            String end = filterUri.getQueryParameter(HelperUtility.END_DATE);
            selectionArgs = new String[]{start, end};
            selection = MovieColumns.RELEASE_DATE + " BETWEEN ? AND ? ";
        }

        if (isTitleFilter) {
            String title = filterUri.getLastPathSegment();
            selectionArgs = new String[]{"%" + title, title + "%", "%" + title + "%"};
            selection = MovieColumns.TITLE + " like ? OR "
                    + MovieColumns.TITLE + " like ? OR "
                    + MovieColumns.TITLE + " like ? ";
        }


        return new CursorLoader(getActivity(), filterUri, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (rvMovieContainer != null) {
            MovieCursorRecyclerAdapter movieCursorAdapter = ((MovieCursorRecyclerAdapter) rvMovieContainer.getAdapter());
            movieCursorAdapter.swapCursor(data);

        }

        loading = false;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        if (rvMovieContainer != null)
            ((MovieCursorRecyclerAdapter) rvMovieContainer.getAdapter()).swapCursor(null);
    }
}
