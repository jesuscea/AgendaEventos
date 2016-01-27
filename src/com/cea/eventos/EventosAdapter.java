package com.cea.eventos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private static class EventViewHolder {
        public ImageView imgIcono;
        public CheckBox chkEvento;
        public TextView txtMonth;
        public TextView txtDesc;
        public TextView txtFecha;
    }

    /**
     * State of ListView item that has never been determined.
     */
    private static final int STATE_UNKNOWN = 0;

    /**
     * State of a ListView item that is sectioned. A sectioned item must
     * display the separator.
     */
    private static final int STATE_SECTIONED_CELL = 1;

    /**
     * State of a ListView item that is not sectioned and therefore does not
     * display the separator.
     */
    private static final int STATE_REGULAR_CELL = 2;
    private int[] mCellStates;

	Context context;
	private SimpleDateFormat format;
    String m_sCurrentMonth;

	// Constructor del adaptador usando el contexto de la aplicación
	EventosAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		
		this.context = context;
        Locale current = context.getResources().getConfiguration().locale;
        format = new SimpleDateFormat(GlobalValues.FORMAT_DATE, current);

        mCellStates = cursor == null ? null : new int[cursor.getCount()];
        m_sCurrentMonth = "";
	}


    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        mCellStates = cursor == null ? null : new int[cursor.getCount()];
    }

	
	// Método que se invoca la primera vez que se crea una fila
	@Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
		// Inflamos la fila con el layout fila_evento
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.fila_evento, parent, false);

        EventViewHolder holder = new EventViewHolder();

        holder.imgIcono = (ImageView)retView.findViewById(R.id.imgIcono);
        holder.chkEvento = (CheckBox)retView.findViewById(R.id.chkEvento);
        holder.txtDesc   = (TextView)retView.findViewById(R.id.txtDesc);
        holder.txtFecha  = (TextView)retView.findViewById(R.id.txtFecha);
        holder.txtMonth  = (TextView)retView.findViewById(R.id.txtMonth);

        retView.setTag(holder);

        return retView;
    } // end newView
 
	
	// Método que se invoca para recuperar una vista
    @Override
    public void bindView (View view, Context context, Cursor cursor) {

        final EventViewHolder holder = (EventViewHolder)view.getTag();

        // Getsión de cabeceras (separación por meses)
        /*
         * Separator
         */
        boolean bNeedSeparator = false;

        long lFecha;
        lFecha  = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));
        Calendar sDateTask = Calendar.getInstance();
        sDateTask.setTimeInMillis(lFecha);

        /* Gestión de las secciones (separadas por mes)
         */
        final int position = cursor.getPosition();

        format = new SimpleDateFormat(GlobalValues.FORMAT_MONTH);
        m_sCurrentMonth = format.format(sDateTask.getTime());
        holder.txtMonth.setText(m_sCurrentMonth);

        switch (mCellStates[position]) {
            case STATE_SECTIONED_CELL:
                bNeedSeparator = true;
                break;

            case STATE_REGULAR_CELL:
                bNeedSeparator = false;
                break;

            case STATE_UNKNOWN:
            default:
                // A separator is needed if it's the first itemview of the
                // ListView or if the group of the current cell is different
                // from the previous itemview.
                if (position == 0) {
                    bNeedSeparator = true;
                }
                else {
                    cursor.moveToPosition(position - 1);

                    lFecha = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));
                    sDateTask = Calendar.getInstance();
                    sDateTask.setTimeInMillis(lFecha);
                    m_sCurrentMonth = format.format(sDateTask.getTime());

                    if (m_sCurrentMonth.length() > 0&& holder.txtMonth.length() > 0 && !m_sCurrentMonth.equals(holder.txtMonth.getText().toString())) {
                        m_sCurrentMonth = holder.txtMonth.getText().toString();
                        bNeedSeparator = true;
                    }

                    cursor.moveToPosition(position);
                }

                // Cache the result
                mCellStates[position] = bNeedSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (bNeedSeparator) {
            holder.txtMonth.setText(m_sCurrentMonth);
            holder.txtMonth.setVisibility(View.VISIBLE);
        } else {
            holder.txtMonth.setVisibility(View.GONE);
        }
        // FIN Gestión de Secciones (meses)

        // Completamos el resto de campos
    	// Obtenemos una fila de la consulta y establecemos los datos
    	// de la fila de nuestro ListView   
    	int iTipo;
    	String sTitulo;
    	String sDesc;
    	String sFecha;
    	int iNotify;
    	boolean bFin;

    	// Cargamos los datos y comprobamos si hay que tacharlo
    	iTipo = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_TIPO));
    	if (iTipo == EventosDBAdapter.TASK) {
            holder.imgIcono.setImageResource(R.drawable.task_item);
    	}
    	else if (iTipo == EventosDBAdapter.WORK) {
            holder.imgIcono.setImageResource(R.drawable.work_item);
    	}
        else if (iTipo == EventosDBAdapter.SHOP) {
            holder.imgIcono.setImageResource(R.drawable.shopping_item);
        }
    	
    	sTitulo = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE));
    	sDesc   = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_DESC));
        lFecha  = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));
        sDateTask = Calendar.getInstance();
        sDateTask.setTimeInMillis(lFecha);
        format = new SimpleDateFormat(GlobalValues.FORMAT_DATE);
    	sFecha  = format.format(sDateTask.getTime());
    	iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
        bFin = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0);

        holder.chkEvento.setText(sTitulo);
        holder.chkEvento.setChecked(bFin);
        holder.txtDesc.setText(sDesc);
    	// Mostramos la fecha si el evento es notificable
    	if (iNotify > 0) {
    		// Formateamos la fecha
        	try {
    			Date dFecha = format.parse(sFecha);
    			sFecha = format.format(dFecha);
    		} catch (ParseException e) {
    			Log.e("ERROR", "Fallo al formatear la fecha", e);
    		}

            holder.txtFecha.setText(sFecha);
    	}
    	else {
            holder.txtFecha.setText("");
    	}
    	
    	// Si el checkbox está marcado tachamos el texto y el subtítulo
    	if (bFin) {
            holder.chkEvento.setPaintFlags(holder.chkEvento.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtDesc.setPaintFlags(holder.txtDesc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtFecha.setPaintFlags(holder.txtFecha.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    	}
    	else {
            holder.chkEvento.setPaintFlags(holder.chkEvento.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtDesc.setPaintFlags(holder.txtDesc.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtFecha.setPaintFlags(holder.txtFecha.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
    	}

    } // end bindView
}