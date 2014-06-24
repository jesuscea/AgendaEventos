package com.cea.eventos;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;


// Clase que gestiona cuando se invoca un evento, comprueba si hay que notificar o sonar una alarma (o ambas)
public class NotifyReceiver extends BroadcastReceiver {
	
	@Override
    public void onReceive(Context context, Intent intent) {
	
        long iID   = intent.getLongExtra(GlobalValues.BUNDLE_NOTIFY_ID, 0);
        String sMsg = intent.getStringExtra(GlobalValues.BUNDLE_NOTIFY_MSG);
        String sDesc = intent.getStringExtra(GlobalValues.BUNDLE_NOTIFY_DESC);
        int iTipoNotify = intent.getIntExtra(GlobalValues.BUNDLE_NOTIFY_TYPE, 0);
        Uri uriSound = Uri.parse(AgendaEventos.getRingtone());
        
        // Si el tipo de notificación incluye alarma, la hacemos sonar
        if (iTipoNotify == EventosDBAdapter.NOTIFY_ALARM || iTipoNotify == EventosDBAdapter.NOTIFY_ALARM_BAR) {
        	playAlarm(context, uriSound);
        	
        	Intent intDialog = new Intent(context, DialogNotify.class);
        	intDialog.putExtra(GlobalValues.BUNDLE_DIALOG_ID, iID);
        	intDialog.putExtra(GlobalValues.BUNDLE_DIALOG_MSG, sMsg);
        	intDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            context.startActivity(intDialog);
        }
        
        // Si el tipo de notificación incluye la barra de notificaciones, la creamos
        if (iTipoNotify >= EventosDBAdapter.NOTIFY_BAR) {
	        // Obtenemos una referencia al servicio de notificaciones
	        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        
	        // Configuramos la notificación que va a aparecer en la barra
	        int iIcono = R.drawable.ic_launcher;
	        long lTime = System.currentTimeMillis();
	
	        // Creamos la notificación
	        Notification notificacion = new Notification(iIcono, sMsg, lTime);
	        
	        // Configuramos el Intent
	        // Obtenemos el contexto de la aplicación
	        Intent notIntent = new Intent(context,	EventosActivity.class);
	        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notIntent, 0);
	        
	        // Incluimos la información de la notificación
	        notificacion.setLatestEventInfo(context, sMsg, sDesc, pendIntent);
	        
	        // AutoCancel: cuando se pulsa la notificación desaparece
	        notificacion.flags |= Notification.FLAG_AUTO_CANCEL;
	        // Si no tenemos alarmas, hacemos sonar la notificación
	        if (iTipoNotify == EventosDBAdapter.NOTIFY_BAR)
	        {
	        	notificacion.defaults |= Notification.DEFAULT_SOUND;
	        }

	        // Mostramos la notificación
	        notManager.notify((int)iID, notificacion);
        }
        
        // Por último, si tenemos la opción marcada de vibración, vibramos unos segundos
        boolean bVibrate = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_VIBRATE, false);
        if (bVibrate == true) {
        	Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(GlobalValues.VIBRATE_INTERVAL);
        }
                
        // Autotachamos el evento notificado
        AgendaEventos.dbAdapter.setFin(iID, true);
    }


	// Reproduce un sonido de alarma
	private void playAlarm(Context context, Uri uriPlayer)
	{
        try {
            AgendaEventos.mMediaPlayer.setDataSource(context, uriPlayer);
            final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            	AgendaEventos.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            	AgendaEventos.mMediaPlayer.prepare();
            	AgendaEventos.mMediaPlayer.start();
            }
        } 
        catch (IOException e) {
        	Log.e("Error play alarm", "Error al reproducir la alarma");
        }
	}
}
