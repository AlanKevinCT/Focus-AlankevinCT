package mx.unam.fc.focus_alan.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mx.unam.fc.icat.focusmony.R;
import mx.unam.fc.icat.focusmony.model.Session;
import mx.unam.fc.icat.focusmony.model.SessionManager;

/**
 * Actividad que visualiza el historial cronológico de las sesiones de enfoque y descanso.
 * Se utiliza como práctica para el manejo de RecyclerView, adaptadores y filtrado de datos.
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.3, mar 2026 (esqueleto para alumnos)
 */
public class SessionHistoryActivity extends AppCompatActivity {

    // Componentes de la Interfaz de Usuario.
    private Toolbar toolbar;
    private TextView tvResultCount;
    private ConstraintLayout layoutEmpty;
    private RecyclerView recyclerView;

    // TODO: Declarar los componentes de filtrado (ChipGroup y Chips individuales).

    // Lógica y Datos.
    private SessionHistoryAdapter adapter;
    private SessionManager sessionManager;

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

        // TODO: Vincular Chips mediante findViewById y asignar IDs correspondientes.

        sessionManager = new SessionManager(this);
    }

    /**
     * Configuración del sistema de filtrado por temporalidad.
     */
    private void setupFilterLogic() {
        // TODO (Opcional): Implementar el funcionamiento del ChipGroup (filtrado).
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
        // TODO: Recuperar datos reales para el listado de sesiones.

        List<Session> sessions = sessionManager.getAllSessions();
        boolean isEmpty = (sessions == null || sessions.isEmpty());

        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        // TODO: Investigar cómo usar Plurals en strings.xml para manejar "1 sesión" vs "2 sesiones".
        String countText = getString(R.string.session_count, (sessions != null ? sessions.size() : 0));
        tvResultCount.setText(countText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}