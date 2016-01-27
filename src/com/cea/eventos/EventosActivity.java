package com.cea.eventos;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import tabs.SlidingTabLayout;

// Actividad principal de la aplicación donde se muestran el conjunto de eventos organizados por tipo
public class EventosActivity extends ActionBarActivity {
	
	static private final int NEW_EVENT = 10;
	static private final int EDIT_EVENT = 20;

    ViewPager mPager;

    SlidingTabLayout m_Tabs;
    TabsAdapter m_tabsAdapter;

    boolean m_bShowAllTabs;
	boolean m_bTabTasks  = true;
	boolean m_bTabWork   = true;
    boolean m_bTabShop   = true;
	boolean m_bTabAlarms = true;
	
	int iTabTasksOrder;
	int iTabWorkOrder;
    int iTabShopOrder;
	int iTabAlarmsOrder;
	
	// Creación de la actividad
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

        // Leemos la versión de la aplicación
        m_bShowAllTabs = false;
        String sCurrentVersion = getResources().getString(R.string.app_version);
        String sVersion = AgendaEventos.sharedPref.getString(GlobalValues.PREF_VERSION, GlobalValues.NO_VERSION);
        if (sVersion.equals(GlobalValues.NO_VERSION) || !sVersion.equals(sCurrentVersion)) {
            m_bShowAllTabs = true;
        }

        SharedPreferences.Editor mSharedEditor = AgendaEventos.sharedPref.edit();
        mSharedEditor.putString(GlobalValues.PREF_VERSION, sCurrentVersion);
        mSharedEditor.apply();

		initToolBar();

        ImageButton fabImageButton = (ImageButton) findViewById(R.id.fab_new_event);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

		// Comenzamos el servicio por si estaba parado
		startService();
		
	} // end onCreate


    // Crea las pestañas e inicializa cada una de ellas
    void initTabManager() {

        m_tabsAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(m_tabsAdapter);
        m_Tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        m_Tabs.setViewPager(mPager);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        m_Tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_default_selected_indicator_color);
            }
        });
    }


	// Clase adaptadora para crear una pestaña
    class TabsAdapter extends FragmentPagerAdapter {

        TabManager m_TabManager;

        EventosAdapter m_TasksAdapter;
        EventosAdapter m_WorkAdapter;
        EventosAdapter m_ShopAdapter;
        EventosAdapter m_AlarmsAdapter;

        ListEventosActivity m_fragmentTab;

        public TabsAdapter(Context c, FragmentManager fm) {
            super(fm);

            // Limpiamos el fragment manager previo (si existiera)
            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }

            m_TabManager = new TabManager(c);
            this.setTabOrder();

            // Declaramos el adaptador de tipo cursor para que el autocompletado lo obtenga desde la BD
            m_TasksAdapter  = new EventosAdapter(c, AgendaEventos.dbAdapter.getTasks());
            m_WorkAdapter   = new EventosAdapter(c, AgendaEventos.dbAdapter.getWork());
            m_ShopAdapter   = new EventosAdapter(c, AgendaEventos.dbAdapter.getShopping());
            m_AlarmsAdapter = new EventosAdapter(c, AgendaEventos.dbAdapter.getAlarms());
        }

        @Override
        public Fragment getItem(int position) {
            m_fragmentTab = ListEventosActivity.getInstance(position);

            if (position == m_TabManager.TAB_ID_TASKS) {
                m_TasksAdapter.getCursor().requery();
                m_fragmentTab.setAdapterList(m_TasksAdapter);
                m_TasksAdapter.notifyDataSetChanged();
            }
            else if (position == m_TabManager.TAB_ID_WORK) {
                m_WorkAdapter.getCursor().requery();
                m_fragmentTab.setAdapterList(m_WorkAdapter);
                m_TasksAdapter.notifyDataSetChanged();
            }
            else if (position == m_TabManager.TAB_ID_SHOP) {
                m_ShopAdapter.getCursor().requery();
                m_fragmentTab.setAdapterList(m_ShopAdapter);
                m_TasksAdapter.notifyDataSetChanged();
            }
            else if (position == m_TabManager.TAB_ID_ALARM) {
                m_AlarmsAdapter.getCursor().requery();
                m_fragmentTab.setAdapterList(m_AlarmsAdapter);
                m_TasksAdapter.notifyDataSetChanged();
            }

            return m_fragmentTab;
        }

        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return m_TabManager.getTabTittle(position);
        }

        @Override
        public int getCount() {
            return m_TabManager.getNumTabs();
        }


        // Devuelve el id de la pestaña "Tareas"
        public int getTaskId() {
            return m_TabManager.TAB_ID_TASKS;
        }

        // Devuelve el id de la pestaña "Trabajo"
        public int getWorkId() {
            return m_TabManager.TAB_ID_WORK;
        }

        // Devuelve el id de la pestaña "Compras"
        public int getShopId() {
            return m_TabManager.TAB_ID_SHOP;
        }

        // Crea las pestañas en el orden establecido
        private void setTabOrder() {

        	// Establecemos el orden de las pestañas
            iTabTasksOrder  = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_TASKS_ORDER, 0);
            iTabWorkOrder   = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_WORK_ORDER, 1);
            iTabShopOrder   = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_SHOP_ORDER, 2);
            iTabAlarmsOrder = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_ALARMS_ORDER, 3);

            m_bTabTasks     = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_TASKS, true);
            m_bTabWork      = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_WORK, true);
            m_bTabShop      = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_SHOP, true);
            m_bTabAlarms    = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_ALARMS, true);

            for (int i = 0; i < GlobalValues.NUM_TABS; i++) {
                if (i == iTabTasksOrder && m_bTabTasks) {
                    m_TabManager.addTab(TabManager.TAB_TASKS);
                }
                else if (i == iTabWorkOrder && m_bTabWork)  {
                    m_TabManager.addTab(TabManager.TAB_WORK);
                }
                else if (i == iTabShopOrder && m_bTabShop) {
                    m_TabManager.addTab(TabManager.TAB_SHOP);
                }
                else if (i == iTabAlarmsOrder && m_bTabAlarms) {
                    m_TabManager.addTab(TabManager.TAB_ALARM);
                }
            }
        }

        // Refresca la lista de eventos
        public void refresh() {
            try {
                m_TasksAdapter.getCursor().requery();
                m_WorkAdapter.getCursor().requery();
                m_ShopAdapter.getCursor().requery();
                m_AlarmsAdapter.getCursor().requery();

                m_TasksAdapter.notifyDataSetChanged();
                m_WorkAdapter.notifyDataSetChanged();
                m_ShopAdapter.notifyDataSetChanged();
                m_AlarmsAdapter.notifyDataSetChanged();
            }
            catch (Exception ignored) {}
        }
    }
	

	// Método que se invoca cuando se restaura la actividad
	// Recreamos las pestañas al salir del menú de opciones
	@Override
	public void onResume() {
		super.onResume();
		supportInvalidateOptionsMenu();
        initTabManager();
	}

	// Initialize main toolbar
	private void initToolBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.agenda_toolbar);
		if (mToolbar != null) {
			setSupportActionBar(mToolbar);
            try {
                getSupportActionBar().setIcon(R.drawable.ic_launcher);
            }
            catch (Exception ignored) {}
		}
	}

    // Invoca la actividad "Nuevo_Editar_Evento"
    private void addEvent() {
        Intent intent = new Intent(this, EditEventoActivity.class);
        // Indicamos que se trata de una operación de nuevo evento
        intent.putExtra(GlobalValues.BUNDLE_OP, true);

        this.startActivityForResult(intent, NEW_EVENT);
    }
	
	// Menú de opciones de la actividad (Nuevo evento y Preferencias)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Definimos el menú inflando el fichero XML con su diseño
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_events, menu);

		return true;
	}

    // Menú contextual de cada lista que permite eliminar un evento
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //Inflador del menú contextual
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        // Definimos la cabecera del men� contextual
        Cursor cursor = (Cursor)((ListView)v).getAdapter().getItem(info.position);
        menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE)));
        // Inflamos el menu contextual
        inflater.inflate(R.menu.context_events, menu);

        // Si la tarea está finalizada, quitamos el elemento "Finalizar evento"
        if (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0) {
            menu.getItem(0).setVisible(false);
        }

    } // end onCreateContextMenu


	@Override
	// Pulsación de una opción del menú
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Nuevo evento
		case R.id.menuNew:
		{
			addEvent();
			return true;
		}
		// Menú de preferencias
		case R.id.menuOptions:
		{
			startActivity(new Intent(EventosActivity.this, PreferenciasActivity.class));
			return true;
		}
		// Menú de información de la aplicación
		case R.id.menuAbout:
		{
			// Mostramos la información de la aplicación en una ventana de diálogo
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.app_version));
			builder.setMessage(getResources().getString(R.string.aboutApp));
			builder.setPositiveButton(getResources().getString(R.string.dialogBtnCerrar),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int boton) {
                            // Cerrar dialogo
                        }
                    }
            );
			builder.show();

			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	} // end onOptionsItemSelected
	
	// Acciones del menu contextual
	// En este caso, eliminar un evento de la lista seleccionada
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
            // Finalizar un evento
            case R.id.contextFin: {
                setFinListAdapter(info.id);
                return true;
            }
            // Editar un evento
            case R.id.contextEditar: {
                long iID = info.id;
                String sTitulo;
                String sDesc;
                int iTipo;
                int iNotify;
                long lFecha;
                int bFin;
                int bRepeat;
                boolean[] bWeekDays;

                bWeekDays = new boolean[7];
                // Obtenemos los datos del evento
                Cursor cursor = AgendaEventos.dbAdapter.getEvent(iID);
                Intent intent = new Intent(this, EditEventoActivity.class);

                sTitulo = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE));
                sDesc   = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_DESC));
                iTipo   = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_TIPO));
                iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
                lFecha  = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));
                bFin    = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN));
                bRepeat = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_REPEAT));
                bWeekDays[0] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_SUN)) != 0);
                bWeekDays[1] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_MON)) != 0);
                bWeekDays[2] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_TUE)) != 0);
                bWeekDays[3] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_WED)) != 0);
                bWeekDays[4] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_THU)) != 0);
                bWeekDays[5] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FRI)) != 0);
                bWeekDays[6] = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_SAT)) != 0);

                // Indicamos que se trata de una operación de edición de evento
                intent.putExtra(GlobalValues.BUNDLE_OP, false);
                intent.putExtra(GlobalValues.BUNDLE_ID, iID);
                intent.putExtra(GlobalValues.BUNDLE_TITLE, sTitulo);
                intent.putExtra(GlobalValues.BUNDLE_DESC, sDesc);
                intent.putExtra(GlobalValues.BUNDLE_TIPO, iTipo);
                intent.putExtra(GlobalValues.BUNDLE_NOTIFY, iNotify);
                intent.putExtra(GlobalValues.BUNDLE_DATE, lFecha);
                intent.putExtra(GlobalValues.BUNDLE_FIN, bFin);
                intent.putExtra(GlobalValues.BUNDLE_REPEAT, bRepeat);
                intent.putExtra(GlobalValues.BUNDLE_WEEKDAYS, bWeekDays);

                this.startActivityForResult(intent, EDIT_EVENT);

                return true;
            }
            // Eliminar evento
            case R.id.contextEliminar: {
                // El id lo obtenemos del info directamente
                long iID = info.id;
                // Cancelamos el servicio correspondiente a dicha alarma y lo eliminamos de la BD
                delService(iID);
                AgendaEventos.dbAdapter.delEvent(iID);

                // Refrescamos la pestaña visible
                m_tabsAdapter.refresh();

                // Notificamos que el borrado se ha realizado
                Toast.makeText(this, getResources().getString(R.string.msgDelete), Toast.LENGTH_SHORT).show();

                return true;
            }
		default:
			return super.onContextItemSelected(item);
			
		} // end switch
	} // end onContextItemSelected


	// Método que recibe la respuesta de la actividad de edición de evento
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Si el código no coincide con ninguno no tratamos la información
		if (requestCode != NEW_EVENT && requestCode != EDIT_EVENT) return;
		
		if (resultCode == RESULT_OK) {
			long iID       = data.getLongExtra(GlobalValues.BUNDLE_ID, -1);
			String sTitulo = data.getStringExtra(GlobalValues.BUNDLE_TITLE);
	    	String sDesc   = data.getStringExtra(GlobalValues.BUNDLE_DESC);
	    	int iTipo 	   = data.getIntExtra(GlobalValues.BUNDLE_TIPO, 0);
            long iDate     = data.getLongExtra(GlobalValues.BUNDLE_DATE, 0);
	    	Calendar dFecha = Calendar.getInstance();
            dFecha.setTimeInMillis(iDate);
	    	int iNotify    = data.getIntExtra(GlobalValues.BUNDLE_NOTIFY, 0);
	    	boolean bFin   = data.getBooleanExtra(GlobalValues.BUNDLE_FIN, false);
            boolean bRepeat = data.getBooleanExtra(GlobalValues.BUNDLE_REPEAT, false);
            boolean[] bWeekDays = data.getBooleanArrayExtra(GlobalValues.BUNDLE_WEEKDAYS);

			// Si la respuesta proviene de crear un nuevo evento, cogemos los datos y 
			// los insertamos en la base de datos
			if (requestCode == NEW_EVENT) {
				iID = AgendaEventos.dbAdapter.createEvent(sTitulo, sDesc, iTipo, dFecha, iNotify, bFin, bRepeat, bWeekDays);
				if(iID != -1) {
					Toast.makeText(this, getResources().getString(R.string.msgCreate), Toast.LENGTH_SHORT).show();
				}
			}
			// Actualizamos el evento
			else if (requestCode == EDIT_EVENT) {
				if(AgendaEventos.dbAdapter.updateEvent(iID, sTitulo, sDesc, iTipo, dFecha, iNotify, bFin, bRepeat, bWeekDays)) {
					Toast.makeText(this, getResources().getString(R.string.msgUpdate), Toast.LENGTH_SHORT).show();
				}
			}

            if (iNotify > 0) {
                createtService(iID);
            }

            // Activamos la pestaña correspondiente a la tarea creada/editada
            if (iTipo == EventosDBAdapter.TASK) {
                mPager.setCurrentItem(m_tabsAdapter.getTaskId());
            }
            else if (iTipo == EventosDBAdapter.WORK) {
                mPager.setCurrentItem(m_tabsAdapter.getWorkId());
            }
            else if (iTipo == EventosDBAdapter.SHOP) {
                mPager.setCurrentItem(m_tabsAdapter.getShopId());
            }

            m_tabsAdapter.refresh();
		}
	}


    // Metodo que establece el campo "finalizado" de la lista actual
    private boolean setFinListAdapter(long iID) {
        boolean bOk;

        Cursor cursor = AgendaEventos.dbAdapter.getEvent(iID);

        // Actualizamos a finalizado el evento seleccionado
        int iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
        boolean bFin = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0);

        // Actualizamos el campo "finalizado" a true
        if (iNotify == EventosDBAdapter.NO_NOTIFY) {
            bFin = !bFin;
        } else {
            bFin = true;
        }
        bOk = AgendaEventos.dbAdapter.setFin(iID, bFin);
        m_tabsAdapter.refresh();

        // Si se desactiva el evento, cancelamos el servicio correspondiente
        if (bFin) {
            stopService(iID);
        }

        return bOk;
    } // end setFinListAdapter
	
	
	// Inicia el servicio de notificación de eventos
	private void startService() {
		Intent service = new Intent(this, NotifyService.class);
        service.setAction(NotifyService.ALL);
        this.startService(service);
	}


	// Crea un servicio de notificación de eventos
	private void createtService(long iID) {
		Intent service = new Intent(this, NotifyService.class);
		service.putExtra(GlobalValues.SERVICE_ID, iID);
        service.setAction(NotifyService.CREATE);
        this.startService(service);
	}


	// Detiene el servicio de notificación del evento correspondiente
	// y elimina el evento de la BD
	private void delService(long iID) {
		Intent iCancel = new Intent(this, NotifyService.class);
		iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
		iCancel.setAction(NotifyService.DELETE);
        this.startService(iCancel);
	}

    // Detiene el servicio de notificacion del evento correspondiente
    private void stopService(long iID) {
        Intent iCancel = new Intent(this, NotifyService.class);
        iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
        iCancel.setAction(NotifyService.CANCEL);
        this.startService(iCancel);
    }
}

