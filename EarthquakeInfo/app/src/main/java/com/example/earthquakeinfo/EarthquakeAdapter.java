package com.example.earthquakeinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeData> {
    private final String LOCATION_SEPARATOR=" of ";
    private String primaryLocation;
    private String locationOffset;
    private EarthquakeData currentItem;
    EarthquakeAdapter(Context context, ArrayList<EarthquakeData> data)
    {
        super(context,0,data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //check if the existing view is reused otherwise inflate new view
        View listItemView=convertView;
        if(listItemView==null)
        {
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_view,parent,false);
        }
        //Get the object located at this position in the list
        currentItem=getItem(position);
        //Find the TextView in the list_item.xml layout with the View ID magnitude_view
        //Set the magnitude
        TextView mag=(TextView) listItemView.findViewById(R.id.magnitude_view);
        double magnitude=currentItem.getMagnitudeofEarthquake();
        //Set the color of the circle
        setCircleColor(mag);
        //Set the text
        mag.setText(formatMagnitude(magnitude));
        //-------------------------------------//
        //To format and set the date
        //Set the location
        TextView loc1=(TextView) listItemView.findViewById(R.id.location_view1);//location part 1
        TextView loc2=(TextView) listItemView.findViewById(R.id.location_view2);//location part 2
        String locationString=currentItem.getLocationOfEarthquake();


        if(locationString.contains(LOCATION_SEPARATOR)) {
            String[] parts = locationString.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation=parts[1];
        }
        else
        {
            locationOffset=getContext().getString(R.string.default_offset);
            primaryLocation=locationString;
        }
        //Now set the text
        loc1.setText(locationOffset);
        loc2.setText(primaryLocation);
        //---------------------------------------//

        //Obtain time in milliseconds
        long time=currentItem.getTimeInMilliseconds();
        Date currentTime=new Date(time);
        //Set the date
        TextView date=(TextView) listItemView.findViewById(R.id.date_view);
        date.setText(formatDate(currentTime));
        //Set the time
        TextView time_text=(TextView) listItemView.findViewById(R.id.time_view);
        time_text.setText(formatTime(currentTime));
        //Return the whole LinearLayout containing 3 textviews
        return listItemView;
    }
    //to set the circle color
    private void setCircleColor(View mag)
    {
        //Set the proper background color on the magnitude circle.
        //Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle=(GradientDrawable)  mag.getBackground();
        //Get the appropriate background color based on the earthquake magnitude
        int magnitudeColor=getMagnitudeColor(currentItem.getMagnitudeofEarthquake());
        //Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

    }
    //To get the magnitude color using Switch case
    private int getMagnitudeColor(Double mag)
    {
        int resourceID;
        int magnitude=(int)Math.floor(mag);
        switch(magnitude)
        {
            case 0:
            case 1:
                resourceID=R.color.magnitude1;
                break;
            case 2:
                resourceID=R.color.magnitude2;
                break;
            case 3:
                resourceID=R.color.magnitude3;
                break;
            case 4:
                resourceID=R.color.magnitude4;
                break;
            case 5:
                resourceID=R.color.magnitude5;
                break;
            case 6:
                resourceID=R.color.magnitude6;
                break;
            case 7:
                resourceID=R.color.magnitude7;
                break;
            case 8:
                resourceID=R.color.magnitude8;
                break;
            case 9:
                resourceID=R.color.magnitude9;
                break;
            default:
                resourceID=R.color.magnitude10plus;
                break;
        }
        //return the color as integer
        return ContextCompat.getColor(getContext(),resourceID);
    }
    //to format magnitude
    private String formatMagnitude(double mag)
    {
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        return decimalFormat.format(mag);
    }
    //to format date
    private String formatDate(Date currentDate)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat("LLL dd,yyyy");
        return dateFormat.format(currentDate);
    }
    //to format time
    private String formatTime(Date currentTime)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat time=new SimpleDateFormat("hh:mm a");
        return time.format(currentTime);
    }



}
