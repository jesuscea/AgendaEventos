package com.cea.eventos;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;



// Clase que gestiona cuando se invoca un evento, comprueba si hay que notificar o sonar una alarma (o ambas)
public class NotifyReceiver extends BroadcastReceiver {

    private Context mContext;
	@Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        long iID   = intent.getLongExtra(GlobalValues.BUNDLE_NOTIFY_ID, 0);
        String sMsg = intent.getStringExtra(GlobalValues.BUNDLE_NOTIFY_MSG);
        String sDesc = intent.getStringExtra(GlobalValues.BUNDLE_NOTIFY_DESC);
        int iTipoNotify = intent.getIntExtra(GlobalValues.BUNDLE_NOTIFY_TYPE, 0);
        boolean bRepeat = (intent.getIntExtra(GlobalValues.BUNDLE_NOTIFY_REPEAT, 0) != 0);
        boolean[] bWeekDays = intent.getBooleanArrayExtra(GlobalValues.BUNDLE_NOTIFY_WEEKDAYS);
        Uri uriSound = Uri.parse(AgendaEventos.getRingtone());

        // Obtenemos el dia actual de la semana para ver si tenemos que notificar una repeticion
        Calendar calendar = Calendar.getInstance();
        int iCurrentDay = calendar.getTime().getDay();

        // Condiciones para que salte la notificacion:
        // 1 - Es la hora y minutos establecidos y no hay que repetirlo
        // 2 - Es la hora y minuos establecidos, hay que repetirlo y el dia a repetir es true
        if(!bRepeat || (bWeekDays.length == 7 && bWeekDays[iCurrentDay])) {
            // Si el tipo de notificacion incluye alarma, la hacemos sonar
            if (iTipoNotify == EventosDBAdapter.NOTIFY_ALARM || iTipoNotify == EventosDBAdapter.NOTIFY_ALARM_BAR) {
                playAlarm(uriSound);

                PowerManager pm = (PowerManager)mContext.getSystemService(mContext.POWER_SERVICE);
                PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "EventosActivity");
                wl.acquire();

                Intent intDialog = new Intent(context, DialogNotify.class);
                intDialog.putExtra(GlobalValues.BUNDLE_DIALOG_ID, iID);
                intDialog.putExtra(GlobalValues.BUNDLE_DIALOG_MSG, sMsg);
                intDialog.putExtra(GlobalValues.BUNDLE_DIALOG_DESC, sDesc);
                intDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intDialog);

                // Liberamos el wakelock
                wl.release();
            }

            // Si el tipo de notificacion incluye la barra de notificaciones, la creamos
            if (iTipoNotify >= EventosDBAdapter.NOTIFY_BAR) {
                // Obtenemos una referencia al servicio de notificaciones
                NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Configuramos la notificacion que va a aparecer en la barra
                int iIcono = R.drawable.ic_launcher;
                long lTime = System.currentTimeMillis();

                // Creamos la notificacion
                Notification notificacion = new Notification(iIcono, sMsg, lTime);

                // Configuramos el Intent
                // Obtenemos el contexto de la aplicacion
                Intent notIntent = new Intent(context, EventosActivity.class);
                PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notIntent, 0);

                // Incluimos la informacion de la notificacion
                notificacion.setLatestEventInfo(context, sMsg, sDesc, pendIntent);

                // AutoCancel: cuando se pulsa la notificacion desaparece
                notificacion.flags |= Notification.FLAG_AUTO_CANCEL;
                // Si no tenemos alarmas, hacemos sonar la notificacion
                if (iTipoNotify == EventosDBAdapter.NOTIFY_BAR) {
                    notificacion.defaults |= Notification.DEFAULT_SOUND;
                }

                // Mostramos la notificacion
                notManager.notify((int) iID, notificacion);
            }

            // Por ultimo, si tenemos la opcion marcada de vibracion, vibramos unos segundos
            boolean bVibrate = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_VIBRATE, false);
            if (bVibrate) {
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(GlobalValues.VIBRATE_INTERVAL);
            }

            boolean bAutoEnd = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_AUTO_END_EVENT, false);
            // Autotachamos el evento notificado si no hay que repetirla
            if (!bRepeat && bAutoEnd) {
                AgendaEventos.dbAdapter.setFin(iID, true);
            }
        }
    }


	// Reproduce un sonido de alarma
	private void playAlarm(Uri uriPlayer)
	{
        try {
            AgendaEventos.mRingTone = RingtoneManager.getRingtone(mContext, uriPlayer);
            AgendaEventos.mRingTone.play();
            Log.e("NotifyReceiver", "Reproduciendo sonido: " + uriPlayer.toString());
        } 
        catch (Exception e) {
        	Log.e("Error play alarm", "Error al reproducir la alarma");
        }
	}
}
