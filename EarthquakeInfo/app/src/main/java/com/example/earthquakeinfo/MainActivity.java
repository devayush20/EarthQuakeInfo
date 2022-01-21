package com.example.earthquakeinfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG= MainActivity.class.getName();
    /**
     * URL to get the earthquake data from USGS dataset
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=3&limit=1000";
    //
    /**
     * Make the adapter variable a global variable
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create an ArrayList of earthquakes and obtain it from EarthquakeQueryUtils class
        //ArrayList<EarthquakeData> earthquakes = EarthquakeQueryUtils.extractFeatureFromJSON();
        //Now the above list can be removed as it obtained the list from hardcoded string

        //Find a reference to the listview in the layout
        ListView earthquakeListView = findViewById(R.id.list);
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
        // Start the AsyncTask to fetch the earthquake data
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
    }
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     *
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