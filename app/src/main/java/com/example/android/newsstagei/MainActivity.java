package com.example.android.newsstagei;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for news data from Guardian
     */
    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?api-key=7005911c-53c2-426c-a348-329f1bbde801&order-by=newest";

    /**
     * Constant value for the news loader ID.
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * Adapter for the list of News
     */
    private NewsAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the empty view;
        mEmptyStateTextView = findViewById(R.id.empty_view);

        GridLayoutManager glmanager = new GridLayoutManager(this, 2);
        glmanager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 3 == 0 || position % 3 == 1) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(glmanager);

        // Create a new adapter that takes an empty list of newsListExtracted as input
        mAdapter = new NewsAdapter(null);

        // Set the adapter on the view
        // so the list can be populated in the user interface
        recyclerView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String pageSizePref = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));

        String searchQueryPref = sharedPrefs.getString(
                getString(R.string.settings_search_key),
                "");

        String sectionPref = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                "all"
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if (sectionPref.equals("all")) {

            // Append query parameter and its value.
            uriBuilder.appendQueryParameter("show-fields", "thumbnail");
            uriBuilder.appendQueryParameter("show-tags", "contributor");
            uriBuilder.appendQueryParameter("page-size", pageSizePref);
            uriBuilder.appendQueryParameter("q", searchQueryPref);

        } else {

            // Append query parameter and its value.
            uriBuilder.appendQueryParameter("show-fields", "thumbnail");
            uriBuilder.appendQueryParameter("show-tags", "contributor");
            uriBuilder.appendQueryParameter("page-size", pageSizePref);
            uriBuilder.appendQueryParameter("q", searchQueryPref);
            uriBuilder.appendQueryParameter("section", sectionPref);
        }

        // Return the completed uri, for example:
        // http://content.guardianapis.com/search?api-key=7005911c-53c2-426c-a348-329f1bbde801&order-by=newest&show-fields=thumbnail&show-tags=contributor&page-size=36&q=&section=culture

        Log.i("URI NOW IS: ", uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> mNewsListExtracted) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.notifyDataSetChanged();

        mEmptyStateTextView.setText(R.string.no_news);

        if (mNewsListExtracted != null && !mNewsListExtracted.isEmpty()) {

            mAdapter.setNewsList(mNewsListExtracted);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter = null;
    }

    // Settings screen set up starts here:

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        // To determine which item was selected and what action to take, call getItemId, which returns the unique ID for the menu item:
        int id = item.getItemId();
        // Match the ID against known menu items to perform the appropriate action:
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

