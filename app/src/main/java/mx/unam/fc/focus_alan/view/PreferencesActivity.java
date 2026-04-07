package mx.unam.fc.focus_alan.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import mx.unam.fc.icat.focusmony.R;

/**
 * Actividad encargada de gestionar las preferencias del usuario.
 * Implementa un Listener para reaccionar a cambios en los ajustes (como el idioma).
 * TODO: Asegurarse de agregar la dependencia de AndroidX Preference en el build.gradle.
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.2, mar 2026 (esqueleto para alumnos)
 */
public class PreferencesActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // Inicialización de la Toolbar
        Toolbar toolbar = findViewById(R.id.preferences_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_preferences);
        }

        // Inicializamos las preferencias por defecto
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Reemplazamos el contenido de la actividad con el fragmento
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_content, new PreferencesFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registramos el listener para detectar cambios mientras la actividad es visible
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistramos para evitar fugas de memoria.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Callback que se dispara cuando el usuario cambia cualquier ajuste.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Caso Idioma
        if (key.equals(getString(R.string.lang_preference_key))) {
            String lang = sharedPreferences.getString(key, "es");
            applyLanguage(lang);
            recreate();
        }

        // Caso Tema (Oscuro / Claro / Sistema)
        if (key.equals(getString(R.string.theme_preference_key))) {
            String themeValue = sharedPreferences.getString(key, "system");
            applyTheme(themeValue);
        }
    }

    /**
     * TODO: Implementar este método para cambiar la configuración del idioma.
     */
    private void applyLanguage(String langCode) {
        // Configurar la baseContext con el nuevo Locale.
    }

    /**
     * Aplica el modo oscuro o claro según la preferencia del usuario.
     * @param themeValue Valores esperados: "light", "dark" o "system".
     */
    private void applyTheme(String themeValue) {
        switch (themeValue) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                // Sigue la configuración del sistema operativo
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}