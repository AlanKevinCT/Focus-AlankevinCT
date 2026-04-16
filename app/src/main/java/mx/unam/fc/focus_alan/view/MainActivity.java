package mx.unam.fc.focus_alan.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mx.unam.fc.focus_alan.R;
import mx.unam.fc.focus_alan.model.Session;
import mx.unam.fc.focus_alan.data.SessionManager;

/**
 * Actividad principal que gestiona el ciclo de vida del temporizador Pomodoro.
 * Coordina la interfaz de usuario, los estados de la sesión y la persistencia en SQLite.
 * * @author Alan Kevin Cano Tenorio - @AlanKevinCT
 * @version 1.9, abril 2026
 */
public class MainActivity extends AppCompatActivity {

    /** Estados posibles del temporizador para controlar el flujo de la UI. */
    enum TimerState { IDLE, RUNNING, PAUSED }

    /** Modos de sesión según la técnica Pomodoro original. */
    enum SessionMode { FOCUS, BREAK, REST}

    // Constantes de configuración de tiempo.
    private static final long FOCUS_DURATION_MS   = 25 * 60 * 1000L;
    private static final long BREAK_DURATION_MS   = 5 * 60 * 1000L;
    private static final long REST_DURATION_MS    = 15 * 60 * 1000L;
    private static final int SESSIONS_BEFORE_REST = 4;

    // Componentes de la interfaz de usuario.
    private Toolbar toolbar;
    private ChipGroup chipGroupMode;
    private Chip chipFocus, chipBreak, chipRest;
    private TextView tvTimerDisplay, tvSessionStatus, tvSessionsCount, tvQuote;
    private MaterialButton btnStartStop;
    private ImageButton btnReset, btnSkip;
    private LinearLayout sessionDotsContainer;

    // Elementos de lógica y persistencia.
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.IDLE;
    private SessionMode currentMode = SessionMode.FOCUS;
    private long timeLeftMillis = FOCUS_DURATION_MS;
    private int focusSessionsCompleted = 0;

    private SessionManager sessionManager;
    private Session currentSession;

    /**
     * Inicializa la actividad, configura el diseño inmersivo y carga los servicios básicos.
     * @param savedInstanceState Estado de la instancia guardado (nulo en el primer inicio).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString(getString(R.string.lang_preference_key), "es");
        applyLanguage(lang);
        String theme = prefs.getString(getString(R.string.theme_preference_key), "system");
        applyTheme(theme);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bindViews();
        sessionManager = new SessionManager(this);
        setSupportActionBar(toolbar);
        setupClickListeners();
        updateTimerDisplay(timeLeftMillis);
        updateSessionDots();
        displayRandomQuote();
    }

    @Override
    protected void onResume() {
        super.onResume();

        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        String langEnPreferencias = prefs.getString(getString(R.string.lang_preference_key), "es");
        String themeEnPreferencias = prefs.getString(getString(R.string.theme_preference_key), "system");

        String langActual = getResources().getConfiguration().getLocales().get(0).getLanguage();

        if (!langEnPreferencias.equals(langActual)) {
            applyLanguage(langEnPreferencias);
            applyTheme(themeEnPreferencias);

            recreate();
        } else {
            applyTheme(themeEnPreferencias);
        }
    }

    /**
     * Infla el menú de opciones en la Toolbar superior.
     * @param menu Objeto menú donde se colocarán los ítems.
     * @return true para confirmar la visualización del menú.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Gestiona los eventos de clic en los ítems del menú (Historial y Configuración).
     * @param item El ítem del menú seleccionado.
     * @return true si el evento fue manejado correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, SessionHistoryActivity.class));
        } else if (id == R.id.action_preferences) {
            startActivity(new Intent(this, PreferencesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura el modo inmersivo cada vez que la ventana recupera el foco.
     * Bloquea la orientación en vertical y oculta barras del sistema.
     * @param hasFocus Indica si la ventana tiene el foco actual.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * Libera recursos y cancela el temporizador para evitar fugas de memoria (memory leaks).
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    /**
     * Vincula los componentes gráficos del XML con las referencias en Java.
     */
    private void bindViews() {
        toolbar = findViewById(R.id.tbMenu);
        chipGroupMode = findViewById(R.id.chipGroupMode);
        chipFocus = findViewById(R.id.chipFocus);
        chipBreak = findViewById(R.id.chipBreak);
        chipRest = findViewById(R.id.chipRest);
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartStop = findViewById(R.id.btnStartStop);
        sessionDotsContainer = findViewById(R.id.sessionDotsContainer);
        tvSessionStatus = findViewById(R.id.tvSessionStatus);
        tvSessionsCount = findViewById(R.id.tvTotalSessions);
        tvQuote = findViewById(R.id.tvQuote);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
    }

    /**
     * Asigna los escuchas de eventos (listeners) a los botones y al grupo de chips.
     */
    private void setupClickListeners() {
        btnStartStop.setOnClickListener(v -> {
            if (timerState == TimerState.RUNNING) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());
        btnSkip.setOnClickListener(v -> skipToNextSession());

        chipGroupMode.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty() && timerState == TimerState.IDLE) {
                int id = checkedIds.get(0);
                if (id == R.id.chipFocus) currentMode = SessionMode.FOCUS;
                else if (id == R.id.chipBreak) currentMode = SessionMode.BREAK;
                else if (id == R.id.chipRest) currentMode = SessionMode.REST;
                resetModeTime();
            }
        });
    }

    /**
     * Inicia el flujo del cronómetro y genera una nueva instancia de sesión para persistencia.
     */
    private void startTimer() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timerState = TimerState.RUNNING;
        btnStartStop.setText(R.string.btn_pause);

        currentSession = new Session();
        currentSession.setType(getModeName());
        currentSession.setDuration(currentMode == SessionMode.FOCUS ? 25 : currentMode == SessionMode.BREAK ? 5 : 15);
        currentSession.setDate(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date()));
        currentSession.setStartTime(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));

        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateTimerDisplay(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                onSessionFinished();
            }
        }.start();
    }

    /**
     * Detiene el temporizador y actualiza el estado de la UI a pausa.
     */
    private void pauseTimer() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (countDownTimer != null) countDownTimer.cancel();
        timerState = TimerState.PAUSED;
        btnStartStop.setText(R.string.btn_resume);
    }

    /**
     * Ejecuta la lógica de finalización de sesión, guarda en la base de datos
     * y gestiona la transición al siguiente modo Pomodoro.
     */
    private void onSessionFinished() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timerState = TimerState.IDLE;

        if (currentMode == SessionMode.FOCUS) {
            focusSessionsCompleted++;
            currentMode = (focusSessionsCompleted >= SESSIONS_BEFORE_REST) ? SessionMode.REST : SessionMode.BREAK;
        } else if (currentMode == SessionMode.REST) {
            focusSessionsCompleted = 0;
            currentMode = SessionMode.FOCUS;
            updateSessionDots();
        }
        else {
            currentMode = SessionMode.FOCUS;
        }
        currentSession.setCompleted(true);
        new Thread(() -> {
            try {
                sessionManager.saveSession(currentSession);
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.session_saved, Toast.LENGTH_SHORT).show();
                    updateSessionDots();
                });

            } catch (Exception e) {
                Log.e("FocusAlan", "Error al persistir la sesión", e);
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.error_db_save, Toast.LENGTH_LONG).show()
                );
            }
        }).start();
        runOnUiThread(() -> {
            updateSessionDots();
        });
        notifyUser();
        resetModeTime();
        btnStartStop.setText(R.string.btn_start);
    }

    /**
     * Actualiza el contenedor de indicadores visuales (puntos) según el progreso de la ronda.
     */
    private void updateSessionDots() {
        if (sessionDotsContainer == null) return;
        sessionDotsContainer.removeAllViews();
        int currentRound = (focusSessionsCompleted == 0) ? 0 : focusSessionsCompleted;

        for (int i = 1; i <= 4; i++) {
            View dot = new View(this);
            int dotSize = (int) (12 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            if (i <= currentRound) {
                dot.setBackgroundResource(R.drawable.dot_session_completed);
            } else {
                dot.setBackgroundResource(android.R.drawable.presence_invisible);
                dot.setAlpha(0.3f);
            }
            sessionDotsContainer.addView(dot);
        }
        if (tvSessionsCount != null) {
            tvSessionsCount.setText(getString(R.string.sessionsCount, focusSessionsCompleted));
        }
    }

    /**
     * Reasigna el tiempo base según el modo seleccionado (Focus, Break o Rest).
     */
    private void resetModeTime() {
        switch (currentMode) {
            case FOCUS: timeLeftMillis = FOCUS_DURATION_MS; break;
            case BREAK: timeLeftMillis = BREAK_DURATION_MS; break;
            case REST:  timeLeftMillis = REST_DURATION_MS; break;
        }
        updateTimerDisplay(timeLeftMillis);
    }

    /**
     * Cancela físicamente la ejecución del CountDownTimer.
     */
    private void cancelTimer() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * Reinicia el cronómetro al tiempo inicial del modo actual de forma manual.
     */
    private void resetTimer() {
        cancelTimer();
        timerState = TimerState.IDLE;
        resetModeTime();
        btnStartStop.setText(R.string.btn_start);
    }

    /**
     * Salta la sesión actual. Si está corriendo, se guarda como interrumpida en el historial.
     */
    private void skipToNextSession() {
        if (timerState == TimerState.RUNNING) {
            currentSession.setCompleted(false);
            sessionManager.saveSession(currentSession);
            cancelTimer();
        }
        handleSkipLogic();
    }

    /**
     * Lógica de transición de estados cuando el usuario decide saltar una sesión.
     */
    private void handleSkipLogic() {
        if (currentMode == SessionMode.FOCUS) {
            focusSessionsCompleted++;
            currentMode = (focusSessionsCompleted % 4 == 0) ? SessionMode.REST : SessionMode.BREAK;
        } else if (currentMode == SessionMode.REST) {
            focusSessionsCompleted = 0;
            currentMode = SessionMode.FOCUS;
        } else {
            currentMode = SessionMode.FOCUS;
        }
        timerState = TimerState.IDLE;
        resetModeTime();
        btnStartStop.setText(R.string.btn_start);
        updateSessionDots();
    }

    /**
     * Actualiza el cronómetro visual y la etiqueta de estado en la pantalla.
     * @param millis Tiempo restante en milisegundos.
     */
    private void updateTimerDisplay(long millis) {
        selectChipForMode(currentMode);
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        tvTimerDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        if (tvSessionStatus != null) tvSessionStatus.setText(getModeName());
    }

    /**
     * Selecciona y resalta el Chip correspondiente al modo de sesión activo.
     * @param mode Modo de sesión actual.
     */
    private void selectChipForMode(SessionMode mode) {
        int chipId = (mode == SessionMode.BREAK) ? R.id.chipBreak :
                (mode == SessionMode.REST) ? R.id.chipRest : R.id.chipFocus;
        chipGroupMode.check(chipId);
        highlightChip(findViewById(chipId));
    }

    /**
     * Aplica un borde de resaltado al Chip activo y lo quita de los demás.
     * @param activeChip El componente Chip que debe ser resaltado.
     */
    private void highlightChip(Chip activeChip) {
        if (activeChip == null) return;
        float density = getResources().getDisplayMetrics().density;
        Chip[] allChips = {chipFocus, chipBreak, chipRest};
        for (Chip chip : allChips) chip.setChipStrokeWidth(0);

        activeChip.setChipStrokeWidth(2 * density);
        activeChip.setChipStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.color_border_accent)));
    }

    /**
     * Activa una vibración corta y muestra un mensaje para notificar al usuario el fin de la sesión.
     */
    private void notifyUser() {
        Toast.makeText(this, getString(R.string.toast_session_finished), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    /**
     * Obtiene el nombre legible del modo de sesión actual.
     * @return Cadena de texto con el nombre del modo.
     */
    private String getModeName() {
        return (currentMode == SessionMode.FOCUS) ? getString(R.string.mode_focus) :
                (currentMode == SessionMode.BREAK) ? getString(R.string.mode_break) : getString(R.string.mode_long_break);
    }

    private void displayRandomQuote() {
        if (tvQuote != null) {
            // Usamos la frase que definiste en strings.xml
            tvQuote.setText(R.string.quote);
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
}