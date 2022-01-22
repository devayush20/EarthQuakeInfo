package com.example.earthquakeinfo;

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

    /**
     * URL to get the earthquake data from USGS dataset
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
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


    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeData>> {
        public EarthquakeAsyncTask() {
        }

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
         * @param earthquakeData
         * @deprecated
         */
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