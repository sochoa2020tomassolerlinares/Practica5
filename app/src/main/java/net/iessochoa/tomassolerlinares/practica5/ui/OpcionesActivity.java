package net.iessochoa.tomassolerlinares.practica5.ui;


import android.app.Activity;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import net.iessochoa.tomassolerlinares.practica5.R;

/**
 * Clase donde se lanza el fragment de Opciones
 */
public class OpcionesActivity  extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.opciones);
    }

    public static class OpcionesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.opciones);
        }
    }

    public class SettingsActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new OpcionesFragment())
                    .commit();
        }
    }


}