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


// Clase que gestiona las preferencias de la aplicación
public class PreferenciasActivity  extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedEditor;
	
	Preference btnTasks;
	Preference btnWork;
    Preference btnShop;
	Preference btnAlarms;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_menu);
        
        mSharedPrefs  = this.getPreferenceScreen().getSharedPreferences();         
        
        btnTasks  = findPreference(GlobalValues.PREF_TAB_TASKS_ORDER);
        btnWork   = findPreference(GlobalValues.PREF_TAB_WORK_ORDER);
        btnShop   = findPreference(GlobalValues.PREF_TAB_SHOP_ORDER);
        btnAlarms = findPreference(GlobalValues.PREF_TAB_ALARMS_ORDER);
        
        btnTasks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
                int iOrder = btnTasks.getOrder();
                int iNewOrder = iOrder + 1;
                
                if (iNewOrder < GlobalValues.NUM_TABS) {
                	// Buscamos la pestaña coincidente para reemplazarla
                	if (iNewOrder == btnWork.getOrder()) {
                		btnWork.setOrder(iOrder);
                	}
                    else if (iNewOrder == btnShop.getOrder()) {
                        btnShop.setOrder(iOrder);
                    }
                	else if (iNewOrder == btnAlarms.getOrder()) {
                		btnAlarms.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuración
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
                	// Buscamos la pestaña coincidente para reemplazarla
                	if (iNewOrder == btnTasks.getOrder()) {
                		btnTasks.setOrder(iOrder);
                	}
                    else if (iNewOrder == btnShop.getOrder()) {
                        btnShop.setOrder(iOrder);
                    }
                	else if (iNewOrder == btnAlarms.getOrder()) {
                		btnAlarms.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuración
                	btnWork.setOrder(iNewOrder);   
                    writeOrders();
                }                
                
                return true;
            }
        });

        btnShop.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                int iOrder = btnShop.getOrder();
                int iNewOrder = iOrder + 1;

                if (iNewOrder < GlobalValues.NUM_TABS) {
                    // Buscamos la pestaña coincidente para reemplazarla
                    if (iNewOrder == btnTasks.getOrder()) {
                        btnTasks.setOrder(iOrder);
                    }
                    else if (iNewOrder == btnWork.getOrder()) {
                        btnWork.setOrder(iOrder);
                    }
                    else if (iNewOrder == btnAlarms.getOrder()) {
                        btnAlarms.setOrder(iOrder);
                    }
                    // Actualizamos el orden y escribimos el fichero de configuración
                    btnShop.setOrder(iNewOrder);
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
                	// Buscamos la pestaña coincidente para reemplazarla
                	if (iNewOrder == btnTasks.getOrder()) {
                		btnTasks.setOrder(iOrder);
                	}
                    else if (iNewOrder == btnShop.getOrder()) {
                        btnShop.setOrder(iOrder);
                    }
                	else if (iNewOrder == btnWork.getOrder()) {
                		btnWork.setOrder(iOrder);
                	}
                	// Actualizamos el orden y escribimos el fichero de configuración
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
        
        // Comprobamos si sólo hay una pestaña activa para deshabilitarla
        checkTabs();
        checkTabOrder();
    }
 
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }   
 
    // Metodo que se invoca al detectar que se han modificado las preferencias
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	// Comprobamos si sólo queda una pestaña disponible, en cuyo caso, deshabilitamos la opción de poder marcarla
    	// para que la aplicación tenga, al menos, una pestaña visible (no puede quedarse sin pestañas)
    	checkTabs();
    	
    	// Actualizamos las opciones, estableciendo el tono de aviso
        updatePreference(key);
    }   
    
    
    // Método que comprueba las pestañas activadas y, en caso de que sólo quede una, deshabilitarla para no poder desmarcarla
    private void checkTabs() {
    	Preference prefTasks;
    	Preference prefWork;
        Preference prefShop;
    	Preference prefAlarms;
    	
    	prefTasks  = findPreference(GlobalValues.PREF_TAB_TASKS);
    	prefWork   = findPreference(GlobalValues.PREF_TAB_WORK);
        prefShop   = findPreference(GlobalValues.PREF_TAB_SHOP);
    	prefAlarms = findPreference(GlobalValues.PREF_TAB_ALARMS);
    	
    	boolean bTabTasks  = mSharedPrefs.getBoolean(prefTasks.getKey(), false);
    	boolean bTabWork   = mSharedPrefs.getBoolean(prefWork.getKey(), false);
        boolean bTabShop   = mSharedPrefs.getBoolean(prefShop.getKey(), false);
    	boolean bTabAlarms = mSharedPrefs.getBoolean(prefAlarms.getKey(), false);
    	
    	// Sólo queda disponible la pestaña de Tareas
    	if ( (bTabTasks && !bTabWork && !bTabShop && !bTabAlarms) ) {
    		prefTasks.setEnabled(false);
    	}
    	// Sólo queda disponible la pestaña de Trabajo
    	else if ( (!bTabTasks && bTabWork && !bTabShop && !bTabAlarms) ) {
    		prefWork.setEnabled(false);
    	}
        // Solo queda disponible la pestaña Compras
        else if ( (!bTabTasks && !bTabWork && bTabShop && !bTabAlarms) ) {
            prefWork.setEnabled(false);
        }
    	// Sólo queda disponible la pestaña de Alarmas
    	else if ( (!bTabTasks && !bTabWork && !bTabShop && bTabAlarms) ) {
    		prefAlarms.setEnabled(false);
    	}
    	else {
    		prefTasks.setEnabled(true);
    		prefWork.setEnabled(true);
            prefShop.setEnabled(true);
    		prefAlarms.setEnabled(true);
    	}
    }    
    
    
    // Lee el orden de las pestañas del fichero de configuración y establece el orden
    public void checkTabOrder() {
    	btnTasks.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_TASKS_ORDER, 0));
    	btnWork.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_WORK_ORDER, 1));
        btnShop.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_SHOP_ORDER, 2));
    	btnAlarms.setOrder(mSharedPrefs.getInt(GlobalValues.PREF_TAB_ALARMS_ORDER, 3));
    }
    
    
    // Guarda el orden de las pestañas en el fichero de configuracion
    public void writeOrders() {
    	mSharedEditor = mSharedPrefs.edit();
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_TASKS_ORDER, btnTasks.getOrder());
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_WORK_ORDER, btnWork.getOrder());
        mSharedEditor.putInt(GlobalValues.PREF_TAB_SHOP_ORDER, btnShop.getOrder());
    	mSharedEditor.putInt(GlobalValues.PREF_TAB_ALARMS_ORDER, btnAlarms.getOrder());
    	mSharedEditor.commit();
    }
    
    
    // Metodo que establece las nuevas preferencias
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
