package mx.unam.fc.focus_alan.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import mx.unam.fc.focus_alan.R;

/**
 * Fragmento que carga las preferencias definidas en el archivo XML.
 * Se encarga de inflar la interfaz de usuario de los ajustes de forma automática.
 */
public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargamos las preferencias desde el recurso XML
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
