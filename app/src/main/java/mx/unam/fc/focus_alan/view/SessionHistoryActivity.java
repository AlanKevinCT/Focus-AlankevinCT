package mx.unam.fc.focus_alan.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mx.unam.fc.focus_alan.R;
import mx.unam.fc.focus_alan.model.Session;
import mx.unam.fc.focus_alan.model.SessionManager;

/**
 * Actividad que visualiza el historial cronológico de las sesiones de enfoque y descanso.
 * Se utiliza como práctica para el manejo de RecyclerView, adaptadores y filtrado de datos.
 * @author <a href="mailto:alan.kevin@ciencias.unam.mx" > Alan Kevin Cano Tenorio </a> - @AlanKevinCT
 * @version 1.4, abr 2026
 */
public class SessionHistoryActivity extends AppCompatActivity {

    // Componentes de la Interfaz de Usuario.
    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;

    // Componentes de filtrado
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipToday, chipWeek;
    // Lógica y Datos.
    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

    // Estado del filtro actual: 0=Todos, 1=Hoy, 2=Semana
    private int currentFilter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_session);

        bindViews();
        setupToolbar();
        setupRecyclerView();
        setupFilterLogic();
        updateHistoryDisplay();
    }

    /**
     * Vincula las variables con los componentes del XML.
     */
    private void bindViews() {
        toolbar = findViewById(R.id.history_toolbar);
        tvResultCount = findViewById(R.id.tvResultCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewHistory);

        sessionManager = new SessionManager(this);
        chipGroupFilters = findViewById(R.id.chipGroupFilter);
        chipAll = findViewById(R.id.chipFilterAll);
        chipToday = findViewById(R.id.chipFilterToday);
        chipWeek = findViewById(R.id.chipFilterWeek);
    }

    /**
     * Configuración del sistema de filtrado por temporalidad.
     */
    private void setupFilterLogic() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                currentFilter = 0;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipFilterAll) currentFilter = 0;
                else if (checkedId == R.id.chipFilterToday) currentFilter = 1;
                else if (checkedId == R.id.chipFilterWeek) currentFilter = 2;
            }
            updateHistoryDisplay();
        });
    }

    /**
     * Configura la Toolbar como ActionBar de la actividad.
     * Habilita el botón de retroceso (Up Navigation) y asigna el título
     * desde los recursos de cadena para soporte multi-idioma.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_history);
        }
    }

    /**
     * Inicializa el RecyclerView con su LayoutManager y Adaptador.
     * Vincula la lista de sesiones obtenida del SessionManager con la
     * interfaz visual mediante el SessionHistoryAdapter.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtenemos los datos iniciales.
        List<Session> history = sessionManager.getAllSessions();

        // Inicializamos el adaptador.
        adapter = new SessionHistoryAdapter(history, getResources());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Gestiona la visibilidad de la UI y actualiza el contador.
     */
    private void updateHistoryDisplay() {
        List<Session> sessions;
        switch (currentFilter) {
            case 1: // Hoy
                String today = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(new Date());
                sessions = sessionManager.getSessionsByDate(today);
                break;
            case 2: // Semana
                sessions = sessionManager.getWeeklySessions();
                break;
            default: // Todos
                sessions = sessionManager.getAllSessions();
                break;
        }

        if (adapter != null && sessions != null) {
            adapter.updateList(sessions);
        }

        boolean isEmpty = (sessions == null || sessions.isEmpty());
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        // Cambia getString por getQuantityString y apunta a R.plurals
        int count = (sessions != null ? sessions.size() : 0);
        String countText = getResources().getQuantityString(R.plurals.session_count, count, count);
        tvResultCount.setText(countText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}