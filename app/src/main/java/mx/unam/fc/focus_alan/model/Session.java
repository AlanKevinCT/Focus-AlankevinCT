package mx.unam.fc.focus_alan.model;

public class Session {
    private String type;      // Tipo de la sesion: Enfoque o Descanso
    private String date;      // Formato: EEE, dd MMM yyyy
    private String startTime; // Formato: hh:mm
    private int duration;     // Duracion de la sesion: 25, 5 o 15 min
    private boolean completed; // La sesion fue Completada o Interrumpida

    public Session() {}

    // Constructor, Getters y Setters...

    public Session(String type, String date, String startTime, int duration, boolean completed) {
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.completed = completed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
