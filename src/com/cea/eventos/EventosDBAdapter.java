package com.cea.eventos;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class EventosDBAdapter {

	public static int NO_NOTIFY        = 0; // No notificar el evento
	public static int NOTIFY_ALARM     = 1; // Notificar son una alarma (vibrará o no en función de las preferencias)
	public static int NOTIFY_BAR       = 2; // Notificar con notificación
	public static int NOTIFY_ALARM_BAR = 3; // Notificar con alarma y notificación
	
	public static int TASK = 0;
	public static int WORK = 1;
	
	// Campos de la BD
	public static final String COL_ID     = "_id";
	public static final String COL_TITLE  = "titulo";
	public static final String COL_DESC   = "descripcion";
	public static final String COL_TIPO   = "tipo";
	public static final String COL_DATE   = "fecha";
	public static final String COL_NOTIFY = "notificar";
	public static final String COL_FIN    = "finalizado";
	
	private static final String TABLE_EVENTS = "eventos";
	private Context context;
	private SQLiteDatabase database;
	private EventosSqliteHelper dbHelper;
		
	
	// Constructor
	public EventosDBAdapter(Context context) {
		this.context = context;
	}
	
	
	// Método que abre la BD
	public EventosDBAdapter abrir() throws SQLException {
		// Abrimos la base de datos en modo lectura
		dbHelper = new EventosSqliteHelper(context);
		database = dbHelper.getReadableDatabase();
		
		return this;
	}
	
	
	// Método para cerrar la BD
	public void cerrar() {
		dbHelper.close();
	}
	
	
	// Devuelve un evento, según su ID
	public Cursor getEvent(long iID) throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE _id = $id
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_ID + "=" + iID, null, null, null, COL_DATE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	// Devuelve todos los eventos de tipo Tarea
	public Cursor getTasks() throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE tipo = 0 ORDER BY fecha ASC'
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_TIPO + "=" + TASK, null, null, null, COL_DATE + " ASC", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	// Devuelve todos los eventos de tipo Trabajo
	public Cursor getWork() throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE tipo = 1 ORDER BY fecha ASC'
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_TIPO + "=" + WORK, null, null, null, COL_DATE + " ASC", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	// Devuelve todos los eventos que se deben notificar
	public Cursor getAlarms() throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE tipo = 2 ORDER BY fecha ASC'
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_NOTIFY + "!=" + NO_NOTIFY, null, null, null, COL_DATE + " ASC", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	// Devuelve todos los eventos que se deben notificar
	public Cursor getPendingAlarms() throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		Date dCurrent = new Date();
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE notificar != 0 AND fecha > datetime() ORDER BY fecha ASC'
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_NOTIFY + " != " + NO_NOTIFY + " AND " 
										+ COL_DATE + " >= " + dCurrent.getTime() + " AND " + COL_FIN + " = 0",
										null, null, null, COL_DATE + " ASC", null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	// Devuelve todos los eventos que se deben notificar
	public Cursor getPendingAlarm(long lID) throws SQLException {
		String[] sCampos = new String[] {COL_ID, COL_TITLE, COL_DESC, COL_TIPO, COL_DATE, COL_NOTIFY, COL_FIN};
		Date dCurrent = new Date();
		
		// La sentencia SQL sería del tipo 'SELECT * FROM eventos WHERE notificar != 0 AND fecha > datetime() ORDER BY fecha ASC'
		Cursor mCursor = database.query(TABLE_EVENTS, sCampos, COL_ID + " = " + lID + " AND " + COL_NOTIFY + " != " 
										+ NO_NOTIFY + " AND " + COL_DATE + " >= " + dCurrent.getTime(),
										null, null, null, COL_DATE + " ASC", null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	// Método que inserta un evento nuevo. Devuelve el id del registro nuevo si se ha dado de alta
	// correctamente o -1 si no.
	public long createEvent(String sTitulo, String sDescripcion, int iTipo, Date dFecha, int iNotify, boolean bFin) {
		// Usamos un argumento variable para añadir el registro
		ContentValues initialValues = crearContentValues(sTitulo, sDescripcion, iTipo, dFecha, iNotify, bFin);
		// Usamos la función insert del SQLiteDatabase
		return database.insert(TABLE_EVENTS, null, initialValues);
	}


	// Método que actualiza un evento
	public boolean updateEvent(long iID, String sTitulo, String sDescripcion, int iTipo, Date dFecha, int iNotify, boolean bFin) {
		// Usamos un argumento variable para modificar el registro
		ContentValues updateValues = crearContentValues(sTitulo, sDescripcion, iTipo, dFecha, iNotify, bFin);
		// Usamos la función update del SQLiteDatabase
		return database.update(TABLE_EVENTS, updateValues, COL_ID + "=" + iID, null) > 0;
	}
	
	
	// Actualiza el campo "finalizado" el evento con el id indicado
	public boolean setFin(long iID, boolean bFin) {
		// Usamos un argumento variable para modificar el registro
		ContentValues updateValues = crearContentValues(bFin);
		// Usamos la función update del SQLiteDatabase
		return database.update(TABLE_EVENTS, updateValues, COL_ID + "=" + iID, null) > 0;
	}
	
	
	// Borra un evento según el ID
	public boolean delEvent(long iID) throws SQLException {
		return database.delete(TABLE_EVENTS, COL_ID + "=" + iID, null) > 0;
	}
	
	
	// Método que crea un objeto ContentValues con los parámetros indicados
	private ContentValues crearContentValues(String sTitulo, String sDescripcion, int iTipo, Date dFecha, int iNotify, boolean bFin) {
		ContentValues values = new ContentValues();
		values.put(COL_TITLE, sTitulo);
		values.put(COL_DESC, sDescripcion);
		values.put(COL_TIPO, iTipo);
		values.put(COL_DATE, dFecha.getTime());
		values.put(COL_NOTIFY, iNotify);
		values.put(COL_FIN, bFin);
		return values;
	}
	
	
	// Método que crea un objeto ContentValues con los parámetros indicados
	private ContentValues crearContentValues(boolean bFin) {
		ContentValues values = new ContentValues();
		values.put(COL_FIN, bFin);
		return values;
	}
}
