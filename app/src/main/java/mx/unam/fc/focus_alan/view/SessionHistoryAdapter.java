package mx.unam.fc.focus_alan.view;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import mx.unam.fc.focus_alan.R;
import mx.unam.fc.focus_alan.model.Session;

/**
 * Adaptador para gestionar y reciclar las vistas del historial de sesiones.
 * Extiende de RecyclerView. Adapter parametrizado con nuestro ViewHolder específico
 * @author <a href="mailto:alan.kevin@ciencias.unam.mx" > Alan Kevin Cano Tenorio </a> - @AlanKevinCT
 * @version 1.3, abr 2026
 */
public class SessionHistoryAdapter extends RecyclerView.Adapter<SessionHistoryAdapter.SessionViewHolder> {

    /**
     * Clase interna que describe y mantiene las referencias a los widgets de cada ítem.
     * Actúa como un contenedor que evita llamadas repetitivas a findViewById.
     */
     static class SessionViewHolder extends RecyclerView.ViewHolder {
        // Referencias a los elementos gráficos definidos en history_session_entry.xml.
        private final TextView tvSessionType, tvSessionDate, tvSessionTime, tvSessionDuration;
        private final Chip chipStatus;

        /**
         * Constructor que recibe la vista inflada del ítem.
         * @param itemView La vista raíz del layout del elemento de la lista.
         */
        SessionViewHolder(View itemView) {
            super(itemView);
            // Vinculamos los componentes del layout con los atributos de la clase.
            tvSessionType = itemView.findViewById(R.id.tvSessionType);
            tvSessionDate = itemView.findViewById(R.id.tvSessionDate);
            tvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            tvSessionDuration = itemView.findViewById(R.id.tvSessionDuration);
            chipStatus = itemView.findViewById(R.id.chipSessionStatus);
        }
    }

    // Estructura de datos que contiene la información a mostrar (Dataset).
    private List<Session> DATASET;
    // Referencia a recursos para obtener colores y dimensiones dinámicamente.
    private final Resources RESOURCES;

    /**
     * Constructor del adaptador.
     * @param sessions Lista de objetos de tipo Session.
     * @param res Referencia a los recursos de la aplicación.
     */
    public SessionHistoryAdapter(List<Session> sessions, Resources res) {
        this.DATASET = sessions;
        this.RESOURCES = res;
    }

    /**
     * Metodo encargado de "inflar" (crear) el layout XML para cada entrada de la lista.
     * Se llama solo cuando el RecyclerView necesita crear un nuevo ViewHolder.
     */
    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Convertimos el XML history_session_entry en un objeto View.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_session_entry, parent, false);
        return new SessionViewHolder(view);
    }

    /**
     * Metodo encargado de vincular los datos del objeto Session con los widgets del ViewHolder.
     * Se ejecuta cada vez que un elemento entra en el rango visible de la pantalla.
     * @param holder El contenedor de las vistas (ViewHolder).
     * @param position La posición del elemento dentro del DATASET.
     */
    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        // Recuperamos el objeto de datos según la posición actual.
        Session session = DATASET.get(position);

        // Asignamos los valores del objeto a los TextViews del ViewHolder.
        holder.tvSessionType.setText(session.getType());
        holder.tvSessionDate.setText(session.getDate());
        holder.tvSessionTime.setText(session.getStartTime());
        // Concatenamos la unidad de tiempo (min) al valor numérico.
        int minutes = session.getDuration();
        String durationText = holder.itemView.getContext().getString(R.string.session_duration_format, minutes);
        holder.tvSessionDuration.setText(durationText);

        // Lógica de retroalimentación visual basada en el estado de la sesión.
        if (session.isCompleted()) {
            // Caso: Sesión terminada exitosamente.
            holder.chipStatus.setText(holder.itemView.getContext().getString(R.string.status_completed));            holder.chipStatus.setChipBackgroundColorResource(R.color.white);
            holder.chipStatus.setTextColor(RESOURCES.getColor(R.color.color_primary, null));
        } else {
            // Caso: Sesión interrumpida por el usuario o sistema.
            holder.chipStatus.setText(holder.itemView.getContext().getString(R.string.status_interrupted));            holder.chipStatus.setChipBackgroundColorResource(R.color.color_secondary);
            holder.chipStatus.setTextColor(RESOURCES.getColor(R.color.white, null));
        }
    }

    /**
     * Indica el tamaño total de la lista de datos.
     * @return Cantidad de elementos en el DATASET.
     */
    @Override
    public int getItemCount() {
        return DATASET.size();
    }

    // 2. Agrega este método al final de la clase (antes de la última llave)
    public void updateList(List<Session> newList) {
        this.DATASET = newList;
        notifyDataSetChanged();
    }
}
