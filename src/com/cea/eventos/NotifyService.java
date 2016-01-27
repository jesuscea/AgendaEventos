package com.cea.eventos;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


// Clase que invoca el servicio de alarmas y notificaciones
public class NotifyService extends IntentService {

	private static final String TAG = NotifyService.class.getName();
	private IntentFilter matcher;
    private AlarmManager mAlarm;

	public static final String ALL    = "ALL";
	public static final String CREATE = "CREATE";
	public static final String DELETE = "DELETE";
    public static final String CANCEL = "CANCEL";	

    // Constructor del servicio
    public NotifyService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(ALL);
        matcher.addAction(CREATE);
        matcher.addAction(DELETE);
        matcher.addAction(CANCEL);
    }
 
    
    // Metodo que se ejecuta al requerir un intent
    @Override
    protected void onHandleIntent(Intent intent) {
        String sAction = intent.getAction();
        long iID = intent.getLongExtra(GlobalValues.SERVICE_ID, -1);

        // Si coincide la accion se ejecuta en un metodo a parte (execute)
        if (matcher.matchAction(sAction)) {
            execute(sAction, iID);
        }
    }
    
    
    // Metodo que gestiona las alarmas almacenadas
    private void execute(String sAction, long iID) {
        mAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Cursor curAlarms;
        
        // Obtenemos todas las alarmas de la base de datos
        if (ALL.equals(sAction)) {
        	curAlarms =  AgendaEventos.dbAdapter.getPendingAlarms();
        } else {
        	curAlarms =  AgendaEventos.dbAdapter.getPendingAlarm(iID);
        }

        // Si tenemos alarmas pendientes la creamos
        if ( !DELETE.equals(sAction) && curAlarms.moveToFirst()) {
        	do {
	            Intent intent = new Intent(this, NotifyReceiver.class);

                boolean[] bWeekDays = new boolean[7];

	            long lID        = curAlarms.getLong(curAlarms.getColumnIndex(EventosDBAdapter.COL_ID));
	            String sTitulo  = curAlarms.getString(curAlarms.getColumnIndex(EventosDBAdapter.COL_TITLE));
	            String sDesc    = curAlarms.getString(curAlarms.getColumnIndex(EventosDBAdapter.COL_DESC));
	            int iTipoNotify = curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
                int bRepeat     = curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_REPEAT));
                bWeekDays[0]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_SUN)) != 0);
                bWeekDays[1]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_MON)) != 0);
                bWeekDays[2]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_TUE)) != 0);
                bWeekDays[3]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_WED)) != 0);
                bWeekDays[4]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_THU)) != 0);
                bWeekDays[5]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_FRI)) != 0);
                bWeekDays[6]    = (curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_SAT)) != 0);
	            
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_ID, lID);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_MSG, sTitulo);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_DESC, sDesc);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_TYPE, iTipoNotify);
                intent.putExtra(GlobalValues.BUNDLE_NOTIFY_REPEAT, bRepeat);
                intent.putExtra(GlobalValues.BUNDLE_NOTIFY_WEEKDAYS, bWeekDays);
	            
	            int iIDIntent = (int)lID;	             
	            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), iIDIntent, intent, PendingIntent.FLAG_UPDATE_CURRENT);

	            long time = curAlarms.getLong(curAlarms.getColumnIndex(EventosDBAdapter.COL_DATE));
                Date dumyDate = new Date(time);

	            if (CREATE.equals(sAction) || ALL.equals(sAction)) {

                    // Si hay que repetirlo, en principio, activamos la repeticion diaria
                    // Cuando el broadcast reciba la alarma comprobara con el vector de dias de la semana
                    // si hay que activar la alarma ese dia o no
                    if (bRepeat == 1) {
                        mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                    else {
                        mAlarm.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                    }
	            }
	            else if (CANCEL.equals(sAction)) {
                    mAlarm.cancel(pendingIntent);
	            	Log.e("Cancel Service", "Servicio cancelado");
	            }
        	}
        	while(curAlarms.moveToNext());
        }
        // Podemos cancelar cualquier evento (se borra primero de la BD)
        else if (DELETE.equals(sAction)) {
        	Intent intent = new Intent(this, NotifyReceiver.class);
                       
            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_ID, iID);            
            int iIDIntent = (int)iID;
             
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, iIDIntent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarm.cancel(pendingIntent);
            Log.e("Cancel Service", "Servicio cancelado y evento borrado");
        }

        curAlarms.close();
    }
}
