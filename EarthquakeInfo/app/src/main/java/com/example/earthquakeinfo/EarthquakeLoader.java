package com.example.earthquakeinfo;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<EarthquakeData>> {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    //Tag for log messages
    //Query URL
    private String murl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        Log.i(LOG_TAG,"In EarthquakeLoader() constructor, storing url passed during instantiation");
        murl = url;
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"entered onStartLoading() method, forceLoad() will be called");
        forceLoad();
    }
    /**
     * This is on a background thread.
     */
    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     * <p>
     * Implementations should not deliver the result directly, but should return them
     * from this method, which will eventually end up calling {@link #deliverResult} on
     * the UI thread.  If implementations need to process the results on the UI thread
     * they may override {@link #deliverResult} and do so there.
     * <p>
     * To support cancellation, this method should periodically check the value of
     * {@link #isLoadInBackgroundCanceled} and terminate when it returns true.
     * Subclasses may also override {@link #cancelLoadInBackground} to interrupt the load
     * directly instead of polling {@link #isLoadInBackgroundCanceled}.
     * <p>
     * When the load is canceled, this method may either return normally or throw
     * {@link //OperationCanceledException}.  In either case, the {@link //Loader} will
     * call {@link #onCanceled} to perform post-cancellation cleanup and to dispose of the
     * result object, if any.
     *
     * @return The result of the load operation.
     * @throws //OperationCanceledException if the load is canceled during execution.
     * @see #isLoadInBackgroundCanceled
     * @see #cancelLoadInBackground
     * @see #onCanceled
     */
    @Nullable
    @Override
    public List<EarthquakeData> loadInBackground() {
        Log.i(LOG_TAG,"entered loadingInBackground() method");
        if (murl == null) {
            return null;
        }
        Log.i(LOG_TAG,"value of murl checked: not equal to null");

        //Perform the network request, parse the response, and extract
        //a list of earthquakes
        List<EarthquakeData> earthquakes = EarthquakeQueryUtils.fetchEarthquakeData(murl);
        Log.i(LOG_TAG,"list of eartquakes fetched using EarthquakeQueryUtils.fetchEarthquakeData(murl); now return");
        return earthquakes;
    }
}
