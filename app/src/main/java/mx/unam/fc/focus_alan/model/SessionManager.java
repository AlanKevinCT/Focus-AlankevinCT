package mx.unam.fc.focus_alan.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Clase que maneja el ciclo de vida de las tareas sugeridas dentro de la aplicación.
 * Implementa las operaciones básicas de persistencia en memoria (CRUD).
 * @author <a href="mailto:alan.kevin@ciencias.unam.mx" > Alan Kevin Cano Tenorio </a> - @AlanKevinCT
 * @version 1.4, abril 2026
 */
public class SessionManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FocusAlan.db";
    private static final int DATABASE_VERSION = 1;

    // Definición de los NOMBRES de las columnas de la tabla.
    public static final String TABLE_SESSIONS = "sessions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START = "startTime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_COMPLETED = "completed";


    /**
     * Constructor de la clase SessionManager.
     * Inicializa la conexión con la base de datos SQLite nativa.
     * @param context El contexto de la aplicación o actividad necesario para
     * localizar la ruta de la base de datos en el almacenamiento interno.
     */
    public SessionManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Se ejecuta cuando la base de datos es creada por primera vez.
     * Define la estructura de la tabla 'sessions' mediante una sentencia SQL.
     * @param db Instancia de la base de datos donde se ejecutará la creación.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SESSIONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_START + " TEXT, "
                + COLUMN_DURATION + " INTEGER, "
                + COLUMN_COMPLETED + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Guarda una sesión en la base de datos.
     * @param session la sesion a almacenar.
     */
    public void saveSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();

            // Mapeamos los atributos del objeto Session a las columnas de la DB
            values.put(COLUMN_TYPE, session.getType());
            values.put(COLUMN_DATE, session.getDate());
            values.put(COLUMN_START, session.getStartTime());
            values.put(COLUMN_DURATION, session.getDuration());

            // Convertimos el boolean 'completed' a un entero (1 o 0) para SQLite
            values.put(COLUMN_COMPLETED, session.isCompleted() ? 1 : 0);

            // Insertamos la fila
            db.insert(TABLE_SESSIONS, null, values);
            Log.d("DB_SUCCESS", "Sesión guardada correctamente.");
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al guardar sesión: " + e.getMessage());
        } finally {
            db.close(); // Siempre cierra la conexión para evitar fugas de memoria
        }
    }

    /**
     * Obtiene todas las sesiones guardadas en la base de datos.
     * @return sessionList lista que contiene todas las sesiones registradas en la base de datos.
     */
    public List<Session> getAllSessions() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
            cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, COLUMN_ID + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Session session = new Session();
                    // Extraemos los datos usando el índice de la columna
                    session.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                    session.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    session.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START)));
                    session.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));

                    // Convertimos el 1/0 de SQLite de vuelta a boolean
                    int completedInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED));
                    session.setCompleted(completedInt == 1);

                    sessionList.add(session);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", " Error al obtener las sesiones: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return sessionList;
    }


    /**
     * Obtiene las sesiones completadas el día de hoy.
     * @param todayDate Fecha actual.
     * @return sessionList Lista de sesiones filtradas por el día de hoy.
     */
    public List<Session> getSessionsByDate(String todayDate) {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Filtramos por la columna COLUMN_DATE
            String selection = COLUMN_DATE + " = ?"; // incluimos el signo ? para filtrar
            String[] selectionArgs = { todayDate };

            cursor = db.query(TABLE_SESSIONS, null, selection, selectionArgs, null, null, COLUMN_ID + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Session session = new Session();
                    session.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                    session.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    session.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START)));
                    session.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                    session.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                    sessionList.add(session);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener las sesiones de hoy: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return sessionList;
    }

    /**
     * Obtiene las sesiones de los últimos 7 días usando una consulta SQL directa.
     * @return sessionList Lista de sesiones filtradas por la última semana.
     */
    public List<Session> getWeeklySessions() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Preparamos el formato de fecha y el calendario
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", java.util.Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            // creamos los argumentos (los 7 días hacia atrás)
            String[] selectionArgs = new String[7];
            StringBuilder placeholders = new StringBuilder();

            for (int i = 0; i < 7; i++) {
                selectionArgs[i] = sdf.format(calendar.getTime());
                placeholders.append("?"); // Añadimos un marcador por cada día
                if (i < 6) placeholders.append(", ");
                calendar.add(Calendar.DAY_OF_YEAR, -1); // Retrocedemos un día en cada iteración
            }

            // WHERE date IN (?, ?, ?, ?, ?, ?, ?)
            String selection = COLUMN_DATE + " IN (" + placeholders.toString() + ")";
            cursor = db.query(TABLE_SESSIONS, null, selection, selectionArgs, null, null, COLUMN_ID + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Session session = new Session();
                    session.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                    session.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    session.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START)));
                    session.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
                    session.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                    sessionList.add(session);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al filtrar sesiones semanales: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return sessionList;
    }


    /**
     * Actualiza la versión de la base de datos.
     * @param db la base de datos a mejorar.
     * @param oldVersion versión anterior de la base de datos.
     * @param newVersion número nuevo de versión de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 1. Eliminamos la tabla si ya existe
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);

        // 2. Volvemos a crearla llamando al metodo onCreate
        onCreate(db);

        Log.d("SQLite", "Base de datos actualizada de la versión " + oldVersion + " a la " + newVersion);
    }
}