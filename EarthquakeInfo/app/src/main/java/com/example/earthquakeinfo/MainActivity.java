package com.example.earthquakeinfo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/*
Implementing the LoaderCallbacks in our activity is a little
more complex. First we need to say that
EarthquakeActivity implements the LoaderCallbacks
interface, along with a generic parameter
specifying what the loader will return
(in this case an Earthquake).

In EarthquakeActivity.java:
 */
//public class MainActivity extends AppCompatActivity {
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthquakeData>> {

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getName();
    /**
     * URL to get the earthquake data from USGS dataset
     */
    //private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=100";
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /**
     * Make the adapter variable a global variable
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter adapter;
    /*Make the ListView Variable as global as
      it is used at multiple steps.
     */
    ListView earthquakeListView;

    /*
     * Then we need to override the three methods
     * specified in the LoaderCallbacks interface.
     * We need onCreateLoader(), for when the
     * LoaderManager has determined that the loader
     * with our specified ID isn't running, so we
     * should create a new one.
     */

    /**
     * @param i
     * @param bundle
     * @deprecated
     */
    @Override
    public Loader<List<EarthquakeData>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude=sharedPrefs.getString(getString(R.string.settings_min_magnitude_key),
                                        getString(R.string.settings_min_magnitude_default));
        /*
        we need to look up the user’s preferred
        sort order when we build the URI for making
        the HTTP request. Read from SharedPreferences
        and check for the value associated with the key:
         getString(R.string.settings_order_by_key).
         When building the URI and appending query
         parameters, instead of hardcoding the “orderby”
         parameter to be “time”, we will use the user’s
          preference (stored in the orderBy variable).

        In EarthquakeActivity.java:
         */
        String orderBy=sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri=Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder=baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        //uriBuilder.appendQueryParameter("orderby", "time");



        //Create a new loader for the given URL
        Log.i(LOG_TAG, "entered onCreateLoader() method, will return new loader for given URL");
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    /*
    We need onLoadFinished(), where we'll do exactly
    what we did in onPostExecute(), and use the
    earthquake data to update
    our UI - by updating the dataset in the adapter.
     */
    @Override
    public void onLoadFinished(Loader<List<EarthquakeData>> loader, List<EarthquakeData> earthquakes) {
        //Clear the adapter of previous earthquake data
        adapter.clear();
        Log.i(LOG_TAG, "entered onLoadFinished() method, adapter.clear() invoked");
        //Hide the loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        //If there is a valid list of {@link Earthquake}s, then
        //add them to the adapter's data set.
        //This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        } else {
            TextView empty = findViewById(R.id.empty_message_view);
            empty.setText(R.string.empty_list_message);
        }

        Log.i(LOG_TAG, "checked (earthquakes != null && earthquakes.isEmpty()) and added earthquakes to adapter using addAll ");
    }

    /*
    And we need onLoaderReset(), we're we're being
     informed that the data from our loader is no
     longer valid. This isn't actually a case that's
     going to come up with our simple loader, but
     the correct thing to do is to remove all the
     earthquake data from our UI by clearing out the
     adapter’s data set.
     */
    @Override
    public void onLoaderReset(Loader<List<EarthquakeData>> loader) {
        //Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings)
        {
            Intent settingsIntent=new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "entered onCreate() and passed values to super.onCreate()");

        setContentView(R.layout.activity_main);
        //Set the empty view for the list, in case no results are available for the user
        earthquakeListView = findViewById(R.id.list);
        TextView emptyView = findViewById(R.id.empty_message_view);
        earthquakeListView.setEmptyView(emptyView);

        Log.i(LOG_TAG, "setcontentView() called");
        //Create an ArrayList of earthquakes and obtain it from EarthquakeQueryUtils class
        //ArrayList<EarthquakeData> earthquakes = EarthquakeQueryUtils.extractFeatureFromJSON();
        //Now the above list can be removed as it obtained the list from hardcoded string

        //Find a reference to the listview in the layout
        //ListView earthquakeListView = findViewById(R.id.list);
        //Create a new ArrayAdapter for earthquakes that takes an empty list of earthquakes as input
        adapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeData>());

        //Set the adapter on the ListView
        //so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        //Set the Listener for click event

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Find the current earthquake that was clicked on
                EarthquakeData currentEarthquake = adapter.getItem(position);

                //Convert the String URL into a URI object (to pass into the Intent Constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                //Create a new Intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                //Send the intent to launch the activity
                startActivity(websiteIntent);
            }
        });

        //----------------------------------------------------

        // Start the AsyncTask to fetch the earthquake data
        /*
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
        */
        //Get a reference to the Connectivity Manager to check state of network connectivity
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Get details on the currently active default data network
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


        /*Finally, to retrieve an earthquake,
        we need to get the loader manager and
        tell the loader manager to initialize the
        loader with the specified ID, the second
        argument allows us to pass a bundle of
        additional information, which we'll skip.
        The third argument is what object should
        receive the LoaderCallbacks (and therefore,
        the data when the load is complete!) -
        which will be this activity. This code goes
        inside the onCreate() method of the
        EarthquakeActivity, so that the loader can
        be initialized as soon as the app opens.
         */
            //Get a reference to the LoaderManager, in order to
            //interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "loaderManager object instantiated in onCreate()");

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
        else
        {
            //Otherwise display error
            //First, hide the loading indicator so error messages will be visible
            View loadingIndicator=findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            //Update empty state with no connection error message.
            emptyView.setText(R.string.no_internet_message);

        }
        Log.i(LOG_TAG, "loadManager.initLoader() called to initialise the loader");


    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     * <p>
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     * <p>
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long-running code
     * (like network activity), without interfering with the responsiveness of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeData>> {

        /**
         * @deprecated This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link EarthquakeData}s as the result
         */
        @Override
        protected List<EarthquakeData> doInBackground(String... urls) {
            //Don't perform the request if there are no URLs, or the first URL
            //is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return EarthquakeQueryUtils.fetchEarthquakeData(urls[0]);
        }


        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of earthquake data from a previous
         * query to USGS. Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<EarthquakeData> earthquakeData) {
            super.onPostExecute(earthquakeData);
            //Clear the adapter of previous earthquake data
            adapter.clear();
            //If there is a valid list of {@link Earthquake}s, then add them
            //to the adapter's data set. This will trigger the ListView to update.
            if (earthquakeData != null && !earthquakeData.isEmpty()) {
                adapter.addAll(earthquakeData);
            }
        }
    }

}