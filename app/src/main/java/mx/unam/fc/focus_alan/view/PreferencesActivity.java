package mx.unam.fc.focus_alan.view;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.Locale;

import mx.unam.fc.focus_alan.R;

/**
 * Actividad encargada de gestionar las preferencias del usuario.
 * Implementa un Listener para reaccionar a cambios en los ajustes (como el idioma).
 * @author <a href="mailto:alan.kevin@ciencias.unam.mx" > Alan Kevin Cano Tenorio </a> - @AlanKevinCT
 * @version 1.3, abril 2026
 */
public class PreferencesActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Aplicar Tema
        String theme = prefs.getString(getString(R.string.theme_preference_key), "system");
        applyTheme(theme);

        // Aplicar Idioma
        String lang = prefs.getString(getString(R.string.lang_preference_key), "es");
        applyLanguage(lang);
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
            recreate();
        }
    }

    /**
     * Aplica el idioma de la aplicación según la preferencia del usuario.
     * @param langCode Código del idioma (es, en, etc.)
     */
    private void applyLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        res.updateConfiguration(conf, dm);
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