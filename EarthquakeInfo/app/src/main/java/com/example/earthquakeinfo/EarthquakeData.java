package com.example.earthquakeinfo;

public class EarthquakeData {
    private final double magnitudeOfEarthquake;
    private final String locationOfEarthquake;
    private final long timeInMilliseconds;
    private final String url;

    public String getUrl() {
        return url;
    }


    EarthquakeData(String l, double m, long t,String url) {
        magnitudeOfEarthquake = m;
        locationOfEarthquake = l;
        timeInMilliseconds = t;
        this.url=url;
    }

    public double getMagnitudeofEarthquake() {
        return magnitudeOfEarthquake;
    }


    public String getLocationOfEarthquake() {
        return locationOfEarthquake;
    }


    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }


}
