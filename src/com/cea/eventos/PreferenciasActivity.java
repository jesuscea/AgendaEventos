package com.cea.eventos;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;


// Clase que gestiona las preferencias de la aplicaci�n
public class PreferenciasActivity  extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedEditor;
	
	Preference btnTasks;
	Preference btnWork;
	Preference btnAlarms;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_menu);
        
        mSharedPrefs  = this.getPreferenceScreen().getSharedPreferences();         
        
        btnTasks  = findPreference(GlobalValues.PREF_TAB_TASKS_ORDER);
        btnWork   = findPreference(GlobalValues.PREF_TAB_WORK_ORDER);
        btnAlarms = findPreference(GlobalValues.PREF_TAB_ALARMS_ORDER);
        
        btnTasks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
                int iOrder = btnTasks.getOrder();
                int iNewOrder = iOrder + 1;
                
                if (iNewOrder < GlobalValues.NUM_TABS) {
                	// Buscamos la pesta�a coincidente para reemplazarla
                	if (iNewOrder == btnWork.getOrder()) {
                		btnWork.setOrder(iOrder);
                	}
                	else if (iNewOrder == btnAlarms.getOrder()) {
                		btnAlarms.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuraci�n
                	btnTasks.setOrder(iNewOrder);
                    writeOrders();
                }                
                
                return true;
            }
        });
        
        btnWork.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	int iOrder = btnWork.getOrder();
                int iNewOrder = iOrder + 1;
                
                if (iNewOrder < GlobalValues.NUM_TABS) {
                	// Buscamos la pesta�a coincidente para reemplazarla
                	if (iNewOrder == btnTasks.getOrder()) {
                		btnTasks.setOrder(iOrder);
                	}
                	else if (iNewOrder == btnAlarms.getOrder()) {
                		btnAlarms.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuraci�n
                	btnWork.setOrder(iNewOrder);   
                    writeOrders();
                }                
                
                return true;
            }
        });
        
        btnAlarms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	int iOrder = btnAlarms.getOrder();
                int iNewOrder = iOrder + 1;
                
                if (iNewOrder < GlobalValues.NUM_TABS) {
                	// Buscamos la pesta�a coincidente para reemplazarla
                	if (iNewOrder == btnTasks.getOrder()) {
                		btnTasks.setOrder(iOrder);
                	}
                	else if (iNewOrder == btnWork.getOrder()) {
                		btnWork.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuraci�n
                	btnAlarms.setOrder(iNewOrder);     
                    writeOrders();
                }                
                
                return true;
            }
        });
    }
	
	
	@Override
    protected void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
        // Comprobamos si s�lo hay una pesta�a activa para deshabilitarla
        checkTabs();
        checkTabOrder();
    }
 
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }   
 
    // M�todo que se invoca al detectar que se han modificado las preferencias
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	// Comprobamos si s�lo queda una pesta�a disponible, en cuyo caso, deshabilitamos la opci�n de poder marcarla
    	// para que la aplicaci�n tenga, al menos, una pesta�a visible (no puede quedarse sin pesta�as)
    	checkTabs();
    	
    	// Actualizamos las opciones, estableciendo el tono de aviso
        updatePreference(key);
    }   
    
    
    // M�todo que comprueba las pesta�as activadas y, en caso de que s�lo quede una, deshabilitarla para no poder desmarcarla
    private void checkTabs() {
    	Preference prefTasks;
    	Preference prefWork;
    	Preference prefAlarms;
    	
    	prefTasks  = findPreference(GlobalValues.PREF_TAB_TASKS);
    	prefWork   = findPreference(GlobalValues.PREF_TAB_WORK);
    	prefAlarms = findPreference(GlobalValues.PREF_TAB_ALARMS);
    	
    	boolean bTabTasks  = mSharedPrefs.getBoolean(prefTasks.getKey(), false);
    	boolean bTabWork   = mSharedPrefs.getBoolean(prefWork.getKey(), false);
    	boolean bTabAlarms = mSharedPrefs.getBoolean(prefAlarms.getKey(), false);
    	
    	// S�lo queda disponible la pesta�a de Tareas
    	if ( (bTabTasks && !bTabWork && !bTabAlarms) ) {
    		prefTasks.setEnabled(false);
    	}
    	// S�lo queda disponible la pesta�a de Trabajo
    	else if ( (!bTabTasks && bTabWork && !bTabAlarms) ) {   		
    		prefWork.setEnabled(false);
    	}
    	// S�lo queda disponible la pesta�a de Alarmas
    	else if ( (!bTabTasks && !bTabWork && bTabAlarms) ) {  	
    		prefAlarms.setEnabled(false);
    	}
    	else {
    		prefTasks.setEnabled(true);
    		prefWork.setEnabled(true);
    		prefAlarms.setEnabled(true);
    	}
    }    
    
    
    // Lee el orden de las pesta�as del fichero de configuraci�n y establece el orden
    public void checkTabOrder() {
    	btnTasks.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_TASKS_ORDER, 0));
    	btnWork.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_WORK_ORDER, 1));
    	btnAlarms.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_ALARMS_ORDER, 2));
    }
    
    
    // Guarda el orden de las pesta�as en el fichero de configuraci�n
    public void writeOrders() {
    	mSharedEditor = mSharedPrefs.edit();
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_TASKS_ORDER, btnTasks.getOrder());
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_WORK_ORDER, btnWork.getOrder());
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_ALARMS_ORDER, btnAlarms.getOrder());
    	mSharedEditor.commit();
    }
    
    
    // M�todo que establece las nuevas preferencias
    private void updatePreference(String key) {
        Preference pref = findPreference(key);
     
        // Asignamos el tono de alarma         
        if (pref instanceof RingtonePreference) {
            Uri ringtoneUri = Uri.parse(AgendaEventos.getRingtone());
            Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            if (ringtone != null) pref.setSummary(ringtone.getTitle(this));
        }      
    }
}
