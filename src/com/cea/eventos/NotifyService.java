package com.cea.eventos;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;


// Clase que invoca el servicio de alarmas y notificaciones
public class NotifyService extends IntentService {

	private static final String TAG = NotifyService.class.getName();
	private IntentFilter matcher;
	
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
 
    
    // Método que se ejecuta al requerir un intent
    @Override
    protected void onHandleIntent(Intent intent) {
        String sAction = intent.getAction();
        long iID = intent.getLongExtra(GlobalValues.SERVICE_ID, -1);

        // Si coincide la acción se ejecuta en un método a parte (execute)
        if (matcher.matchAction(sAction)) {
            execute(sAction, iID);
        }
    }
    
    
    // Método que gestiona las alarmas almacenadas
    private void execute(String sAction, long iID) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
	                        
	            long lID        = curAlarms.getLong(curAlarms.getColumnIndex(EventosDBAdapter.COL_ID));
	            String sTitulo  = curAlarms.getString(curAlarms.getColumnIndex(EventosDBAdapter.COL_TITLE));
	            String sDesc    = curAlarms.getString(curAlarms.getColumnIndex(EventosDBAdapter.COL_DESC));
	            int iTipoNotify = curAlarms.getInt(curAlarms.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
	            
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_ID, lID);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_MSG, sTitulo);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_DESC, sDesc);
	            intent.putExtra(GlobalValues.BUNDLE_NOTIFY_TYPE, iTipoNotify);
	            
	            int iIDIntent = (int)lID;	             
	            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), iIDIntent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	     
	            long time = curAlarms.getLong(curAlarms.getColumnIndex(EventosDBAdapter.COL_DATE));
	            if (CREATE.equals(sAction) || ALL.equals(sAction)) {
	                am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);                 
	            } 
	            else if (CANCEL.equals(sAction)) {
	            	am.cancel(pendingIntent);
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
            am.cancel(pendingIntent);
            Log.e("Cancel Service", "Servicio cancelado y evento borrado");
        }

        curAlarms.close();
    }
}
