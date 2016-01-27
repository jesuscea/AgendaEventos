package com.cea.eventos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventosSqliteHelper extends SQLiteOpenHelper {
	// Definimos el nombre y la version de la BD
	private static final String BD_NOMBRE = "bdeventos.db";
    private static final int BD_VERSION = 2;    // Nueva version para repetir alarmas
	
	// Setencia SQL que usaremos para ejecutar las consultas y operaciones
	private static String strSQL = "";
	private static final String strSQL_Borrar = "DROP TABLE IF EXISTS eventos";
	
	private Context context;
	
	// Constructor de la clase
	public EventosSqliteHelper (Context context) {
		super(context, BD_NOMBRE, null, BD_VERSION);
		this.context = context;
	}
	
	// Metodo iniciado por Android si no existe la BD
	@Override
	public void onCreate(SQLiteDatabase database) {
		// Creamos la estructura de la BD y rellenamos los datos
		// Todo ello, a partir del script sql adjunto 
		// en la carpeta res/raw/bdeventos.sql
		try {
			BufferedReader fIn = new BufferedReader(new InputStreamReader (context.getResources().openRawResource(R.raw.dbeventos)));
			while( (strSQL = fIn.readLine()) != null ) {
				database.execSQL(strSQL);
			}
			fIn.close();
		}
		catch(FileNotFoundException e) {
			Log.e("Nombres", "Error al abrir el fichero de BD");
		}
		catch (IOException e) {
			Log.e("Nombres", "Error al leer el fichero de BD");
		}
	}
	
	
	// Metodo iniciado por Android cuando hay un cambio de version
	@Override
	public void onUpgrade(SQLiteDatabase database, int iOldVersion, int iNewVersion) {
		// En nuestro caso, eliminamos la BD y volvemos a crearla
		//database.execSQL(strSQL_Borrar);
		//onCreate(database);

		// Updating the database with raw file 'dbconnections_update.db'
		try {
			BufferedReader fIn = new BufferedReader(new InputStreamReader(context.getResources()
					.openRawResource(R.raw.dbeventos_update)));
			String strSQL;
			while ((strSQL = fIn.readLine()) != null) {
				database.execSQL(strSQL);
			}
			fIn.close();
		}
		catch (FileNotFoundException e) {
			Log.e("AgendaEventos", "dbeventos_update ERROR: Error opening the BD");
		}
		catch (IOException e) {
			Log.e("AgendaEventos", "dbeventos_update ERROR: Error reading the BD");
		}
	}
}
