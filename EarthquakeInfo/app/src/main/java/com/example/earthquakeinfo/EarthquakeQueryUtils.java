package com.example.earthquakeinfo;
//To parse the Earthquake JSON File

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class EarthquakeQueryUtils {
    
    private static final String LOG_TAG = EarthquakeQueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link EarthquakeQueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private EarthquakeQueryUtils() {

    }

    /*
      Return a list of earthquake objects that has been built up from
      parsing a JSON response.
     */
    /*Coverting the method signature to accept a String json to make it more
    reusable.*/

    public static ArrayList<EarthquakeData> extractFeatureFromJSON(String earthquakeJSON) {
        //If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        //Create an empty ArrayList that we can start adding earthquakes to

        ArrayList<EarthquakeData> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //First create the root GEOJSON object which parses the complete string which we obtained
            //from the api
            JSONObject baseJSONResponse = new JSONObject(earthquakeJSON);
            //We need the features array
            //Extract the JSONArray associated with the key called "features",
            //which represents a list of features (or earthquakes).
            JSONArray earthquakeArray = baseJSONResponse.getJSONArray("features");
            //obtain the magnitude, date and location for each earthquake in the earthquakeArray
            for (int i = 0; i < earthquakeArray.length(); i++) {
                //obtain the current earthquake object in the features earthquake array
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                //obtain the magnitude (before: string after:double)
                //String magnitude=properties.getString("mag");
                double magnitude = properties.getDouble("mag");
                //obtain the location
                String location = properties.getString("place");
                //String time=properties.getString("time");
                long time = properties.getLong("time");


                //Obtain the url for the webpage
                String url = properties.getString("url");
                //Now create a new EarthquakeData object and pass the properties obtained
                //EarthquakeData earthquake=new EarthquakeData(location,magnitude,dateToDisplay);
                EarthquakeData earthquake = new EarthquakeData(location, magnitude, time, url);
                //Add the object to the arraylist of earthquakes
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            //If an error is thrown when executing any of the above in the
            //catch the exception here, so the app doesn't crash. Print a log message
            //with the message from the exception.
            Log.e("EarthquakeQueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        //return the list of earthquakes
        return earthquakes;
    }

    /**
     * Returns the new URL object from the given string URL
     */
    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /*
      Make an HTTP request to the given URL and return
      a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (response code 200),
            //then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results. ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                //Closing the input stream could throw an IOException, which is why
                //the makeHttpRequest(URL url) method signature specifies that an IOException
                //could be thrown
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        //return the read data as a String
        return output.toString();
    }

    /**
     * Query the USGS dataset and return a list of
     * {@link EarthquakeData} objects.
     */
    public static List<EarthquakeData> fetchEarthquakeData(String requestURL) {
        /*For testing purpose of loading indicator use sleep()
        on this thread.
         */
        /*try
        {
            Thread.sleep(3000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }*/
        //Create URL object
        URL url = createURL(requestURL);
        //Perform HTTP request to the URL and receive a JSON back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request. ", e);
        }
        //Extract relevant fields from the JSON response and create a list of {@link EarthquakeData}s
        //Return the list of {@link Earthquake}s
        return extractFeatureFromJSON(jsonResponse);
    }
}
