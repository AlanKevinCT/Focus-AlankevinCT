package mx.unam.fc.focus_alan.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el ciclo de vida de las tareas sugeridas dentro de la aplicación.
 * Implementa las operaciones básicas de persistencia en memoria (CRUD).
 * @author <a href="mailto:monmm@ciencias.unam.mx" > Mónica Miranda Mijangos </a> - @monmm
 * @version 1.3, feb 2026
 */
public class SessionManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FocusMony.db";
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
     * TODO: Documentar.
     * @param context ...
     */
    public SessionManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * TODO: Documentar.
     * @param db ...
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
     * TODO: Documentar.
     * @param session ...
     */
    public void saveSession(Session session) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        db.close(); // Siempre cierra la conexión para evitar fugas de memoria
    }

    /**
     * TODO: Documentar.
     * @return ...
     */
    public List<Session> getAllSessions() {
        List<Session> sessionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultamos toda la tabla, ordenando por ID descendente (dejando la sesión más reciente primero)
        Cursor cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, COLUMN_ID + " DESC");

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
        cursor.close();
        db.close();
        return sessionList;
    }

    // TODO: realizar metodo(s) para filtrar las sesiones:
    //  + del dia de hoy.
    //  + de esta semana.

    /**
     * TODO: Documentar.
     * @param db ...
     * @param oldVersion ...
     * @param newVersion ...
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