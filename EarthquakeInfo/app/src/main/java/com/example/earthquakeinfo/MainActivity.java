package com.example.earthquakeinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create a fake list of earthquake locations.
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*For Placeholder fake data only
        ArrayList<EarthquakeData> earthquakes=new ArrayList<>();
        earthquakes.add(new EarthquakeData("San Francisco","1.0","1 Jan, 2018"));
        earthquakes.add(new EarthquakeData("London","2.0","2 Jan, 2019"));
        earthquakes.add(new EarthquakeData("Tokyo","3.0","3 Jan, 2020"));
        earthquakes.add(new EarthquakeData("Mexico City","4.0","6 Feb, 2010"));
        earthquakes.add(new EarthquakeData("Moscow","6.7","16 Sep, 2009"));
        earthquakes.add(new EarthquakeData("Rio de Janeiro","5.8","28 Oct, 2015"));
        earthquakes.add(new EarthquakeData("Paris","3.4","24 March, 2014"));*/

        //Create an ArrayList of earthquakes and obtain it from EarthquakeQueryUtils class
        ArrayList<EarthquakeData> earthquakes = EarthquakeQueryUtils.extractEarthquakes();
        //Find a reference to the listview in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        //Create a new ArrayAdapter for earthquakes
        EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);

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

    }
}