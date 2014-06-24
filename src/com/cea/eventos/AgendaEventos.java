package com.cea.eventos;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class AgendaEventos extends Application {
	private static final String RINGTONE_PREF = "prefRingtone";
	
    public static EventosDBAdapter dbAdapter;
    public static SharedPreferences sharedPref;
    public static MediaPlayer mMediaPlayer;
 
    @Override
    public void onCreate() {
        super.onCreate();
         
        PreferenceManager.setDefaultValues(this, R.xml.preferences_menu, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
         
        // Adaptador de eventos
 		dbAdapter = new EventosDBAdapter(this);
 		dbAdapter.abrir();
 		
 		// Reproductor de sonidos
 		mMediaPlayer = new MediaPlayer();
    }
     
    public static String getRingtone() {   	
        return sharedPref.getString(AgendaEventos.RINGTONE_PREF, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    }
}
