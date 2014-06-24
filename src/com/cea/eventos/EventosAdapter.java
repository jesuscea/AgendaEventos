package com.cea.eventos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


// Definimos el adaptador personalizado para listar los eventos
public class EventosAdapter extends CursorAdapter {
	
	Context context;
	private SimpleDateFormat format;

	// Constructor del adaptador usando el contexto de la aplicación
	EventosAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		this.context = context;
		format = new SimpleDateFormat(GlobalValues.FORMAT_DATE, new Locale("es","ES"));
	}
	
	
	// Método que se invoca la primera vez que se crea una fila
	@Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
		// Inflamos la fila con el layout fila_evento
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.fila_evento, parent, false);
 
        return retView;
    } // end newView
 
	
	// Método que se invoca para recuperar una vista
    @Override
    public void bindView (View view, Context context, Cursor cursor) {
    	// Obtenemos una fila de la consulta y establecemos los datos
    	// de la fila de nuestro ListView   
    	int iTipo = -1;
    	String sTitulo;
    	String sDesc;
    	long lFecha;
    	String sFecha;
    	int iNotify = 0;
    	boolean bFin = false;
    
    	ImageView imgIcono = (ImageView)view.findViewById(R.id.imgIcono);
    	CheckBox chkEvento = (CheckBox)view.findViewById(R.id.chkEvento);
    	TextView txtDesc   = (TextView)view.findViewById(R.id.txtDesc);
    	TextView txtFecha  = (TextView)view.findViewById(R.id.txtFecha);
    	
    	// Cargamos los datos y comprobamos si hay que tacharlo
    	iTipo = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_TIPO));
    	if (iTipo == EventosDBAdapter.TASK) {
    		imgIcono.setImageResource(R.drawable.task_item);
    	}
    	else if (iTipo == EventosDBAdapter.WORK) {
    		imgIcono.setImageResource(R.drawable.work_item);
    	}
    	
    	sTitulo = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE));
    	sDesc   = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_DESC));
    	lFecha  = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));    	
    	sFecha  = format.format(new Date(lFecha));
    	iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
    	bFin    = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0);
    	
    	chkEvento.setText(sTitulo);
    	chkEvento.setChecked(bFin);
    	txtDesc.setText(sDesc);
    	// Mostramos la fecha si el evento es notificable
    	if (iNotify > 0) {
    		// Formateamos la fecha
        	try {
        		SimpleDateFormat format = new SimpleDateFormat(GlobalValues.FORMAT_DATE, new Locale("es","ES"));
    			Date dFecha = format.parse(sFecha);
    			sFecha = format.format(dFecha);
    		} catch (ParseException e) {
    			Log.e("ERROR", "Fallo al formatear la fecha", e);
    		}
        	
        	txtFecha.setText(sFecha);
    	}
    	else {
    		txtFecha.setText("");
    	}
    	
    	// Si el checkbox está marcado tachamos el texto y el subtítulo
    	if (bFin == true) {
    		chkEvento.setPaintFlags(chkEvento.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    		txtDesc.setPaintFlags(txtDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    		txtFecha.setPaintFlags(txtFecha.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    	}
    	else {
    		chkEvento.setPaintFlags(chkEvento.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
    		txtDesc.setPaintFlags(txtDesc.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
    		txtFecha.setPaintFlags(txtFecha.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
    	}
    	
    } // end bindView		
}