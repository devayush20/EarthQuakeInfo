package com.example.earthquakeinfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }
    public static class EarthquakePreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener
    {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
            //super.onCreate(savedInstanceState);
            setPreferencesFromResource(R.xml.settings_main,rootKey);
            /*
            However, we still need to update the preference
            summary when the settings activity is launched.
            Given the key of a preference, we can use
            PreferenceFragment's findPreference() method to
            get the Preference object, and setup the
            preference using a helper method called
            bindPreferenceSummaryToValue().

            In EarthquakePreferenceFragment:
             */
            Preference minMagnitude=findPreference(getString(R.string.settings_min_magnitude_key));
            assert minMagnitude != null;
            bindPreferenceSummaryToValue(minMagnitude);

            /*
            Finally, we'll add additional logic
            in the EarthquakePreferenceFragment
            so that it is aware of the new
            ListPreference, similar to what we did
            for the EditTextPreference. In the
            onCreate() method of the fragment,
            find the “order by” Preference object
            according to its key. Then call the
            bindPreferenceSummaryToValue() helper
            method on this Preference object, which
            will set this fragment as the
            OnPreferenceChangeListener and update the
            summary so that it displays
            the current value stored in SharedPreferences.

                In SettingsActivity.java, in EarthquakePreferenceFragment class:
             */
            Preference orderBy=findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
            /*
            Since this is the first ListPreference
            that the EarthquakePreferenceFragment
            is encountering, update the
            onPreferenceChange() method in
            EarthquakePreferenceFragment to properly
            update the summary of a ListPreference
            (using the label, instead of the key).
             */
        }

        /**
         * Called when a preference has been changed by the user. This is called before the state
         * of the preference is about to be updated and before the state is persisted.
         *
         * @param preference The changed preference
         * @param newValue   The new value of the preference
         * @return {@code true} to update the state of the preference with the new value
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue=newValue.toString();
            preference.setSummary(stringValue);

            if(preference instanceof ListPreference)
            {
                ListPreference listPreference=(ListPreference)  preference;
                int prefIndex=listPreference.findIndexOfValue(stringValue);
                if(prefIndex>=0)
                {
                    CharSequence[] labels=listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else
            {
                preference.setSummary(stringValue);
            }
            return true;
        }
        /*
        Now we need to define the bindPreferenceSummaryToValue()
        helper method to set the current EarthquakePreferenceFragment
        instance as the listener on each preference. We also
        read the current value of the preference stored
        in the SharedPreferences on the device, and
        display that in the preference summary
        (so that the user can see the current value of
        the preference).
         */
        private void bindPreferenceSummaryToValue(Preference preference)
        {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString=preferences.getString(preference.getKey(),"");
            onPreferenceChange(preference,preferenceString);
        }
    }
}
