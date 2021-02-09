package net.iessochoa.tomassolerlinares.practica5.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iessochoa.tomassolerlinares.practica5.R;

/**
 * Clase Fragment donde se visualiza el PreferenceScreen de opciones
 */
public class OpcionesFragment extends PreferenceFragment {
    private ListPreference mListPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Indicate here the XML resource you created above that holds the preferences
        addPreferencesFromResource(R.xml.opciones);
        setPreferencesFromResource(R.xml.opciones, rootKey);
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mListPreference = (ListPreference) getPreferenceManager().findPreference("preference_key");
        mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return true;
            }
        });

        return inflater.inflate(R.xml.opciones, container, false);
    }
}