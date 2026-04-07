package mx.unam.fc.focus_alan.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import mx.unam.fc.icat.focusmony.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargamos las preferencias desde el recurso XML
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
