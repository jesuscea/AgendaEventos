package com.cea.eventos;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.OnTabChangeListener;

// Actividad principal de la aplicación donde se muestran el conjunto de eventos organizados por tipo
public class EventosActivity extends Activity {
	
	static private final int NEW_EVENT = 10;
	static private final int EDIT_EVENT = 20;
	
	TabHost tabHost;
	TabManager m_tabs;
	EventosAdapter tasksAdapter;
	EventosAdapter workAdapter;
	EventosAdapter alarmsAdapter;
	
	ListView listTasks;
	ListView listWork;
	ListView listAlarms;
	
	boolean m_bTabTasks  = true;
	boolean m_bTabWork   = true;
	boolean m_bTabAlarms = true;
	
	int iTabTasksOrder;
	int iTabWorkOrder;
	int iTabAlarmsOrder;
	
	// Creación de la actividad
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);		
		
		// Comenzamos el servicio por si estaba parado
		startService();
		
	} // end onCreate
	

	// Método que se invoca cuando se restaura la actividad
	// Recreamos las pestañas al salir del menú de opciones
	@Override
	public void onResume()
	{
		super.onResume();
		this.setContentView(R.layout.main);		
		// Obtenemos la referencia al componente TabHost
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
				
		listTasks  = (ListView)findViewById(R.id.listTasks);
		listWork   = (ListView)findViewById(R.id.listWork);
		listAlarms = (ListView)findViewById(R.id.listAlarms);
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
	
		// Gestionamos las pestañas visibles en el evento onResume()			
		listTasks.setEmptyView(emptyText);
		listWork.setEmptyView(emptyText);
		listAlarms.setEmptyView(emptyText);
		
		// Declaramos el adaptador de tipo cursor para que el autocompletado lo obtenga desde la BD
		tasksAdapter  = new EventosAdapter(this, AgendaEventos.dbAdapter.getTasks());
		workAdapter   = new EventosAdapter(this, AgendaEventos.dbAdapter.getWork());
		alarmsAdapter = new EventosAdapter(this, AgendaEventos.dbAdapter.getAlarms());
		
		listTasks.setAdapter(tasksAdapter);
		listWork.setAdapter(workAdapter);
		listAlarms.setAdapter(alarmsAdapter);
		
		// Registramos el menú contextual para cada lista, para mostrar la opción de eliminar un evento
		registerForContextMenu(listTasks);
		registerForContextMenu(listWork);
		registerForContextMenu(listAlarms);		
		
		// Definimos el evento OnTabChanged (cuando el usuario ha cambiado de pestaña)
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				updateListTab(tabHost.getCurrentTab());
			}
		}); // end setOnTabChangedListener
		
		// Capturamos el evento Click de la lista de eventos de tipo Tareas
		listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {		
				if (tabHost.getCurrentTab() == m_tabs.TAB_ID_TASKS)	{
					setFinListAdapter(tasksAdapter, position);
				}
			}
		}); // end setOnItemClickListener de listTasks
		
		// Capturamos el evento Click de la lista de eventos de tipo Trabajo
		listWork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {				
				if (tabHost.getCurrentTab() == m_tabs.TAB_ID_WORK) {
					setFinListAdapter(workAdapter, position);
				}
			}
		}); // end setOnItemClickListener de listWork
		
		// Capturamos el evento Click de la lista de eventos que se notifican
		listAlarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {				
				if (tabHost.getCurrentTab() == m_tabs.TAB_ID_ALARM) {
					setFinListAdapter(alarmsAdapter, position);
				}
			}
		}); // end setOnItemClickListener de listAlarms
		
		int iTabUpdate = setTabOrder();
		
		m_bTabTasks  = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_TASKS, true);
		m_bTabWork   = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_WORK, true);
		m_bTabAlarms = AgendaEventos.sharedPref.getBoolean(GlobalValues.PREF_TAB_ALARMS, true);

		// Comprobamos si hay que ocultar alguna pestaña
		if (!m_bTabTasks) m_tabs.hideTab(m_tabs.TAB_ID_TASKS);
		else m_tabs.showTab(m_tabs.TAB_ID_TASKS);
		
		if (!m_bTabWork) m_tabs.hideTab(m_tabs.TAB_ID_WORK);
		else m_tabs.showTab(m_tabs.TAB_ID_WORK);
		
		if (!m_bTabAlarms) m_tabs.hideTab(m_tabs.TAB_ID_ALARM);
		else m_tabs.showTab(m_tabs.TAB_ID_ALARM);
		
		m_tabs.setTab(iTabUpdate);		
	}
	
	// Menú de opciones de la actividad (Nuevo evento y Preferencias)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Definimos el menú inflando el fichero XML con su diseño
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_events, menu);
		
		return true;
	}
	
	
	@Override
	// Pulsación de una opción del menú
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Nuevo evento
		case R.id.menuNew:
		{
			Intent intent = new Intent(this, EditEventoActivity.class);
			// Indicamos que se trata de una operación de nuevo evento
			intent.putExtra(GlobalValues.BUNDLE_OP, true);

			this.startActivityForResult(intent, NEW_EVENT);
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
			builder.setTitle(getResources().getString(R.string.app_name));
			builder.setMessage(getResources().getString(R.string.aboutApp));
			builder.setPositiveButton(getResources().getString(R.string.dialogBtnCerrar), 
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int boton) {
						// Cerrar diálogo						
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
	
	
	// Menú contextual de cada lista que permite eliminar un evento
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//Inflador del menú contextual
		MenuInflater inflater = getMenuInflater();
		// Si el componente que vamos a dibujar es la etiqueta usamos el fichero XML correspondiente
		if (v.getId() == R.id.listTasks || v.getId() == R.id.listWork || v.getId() == R.id.listAlarms) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			// Definimos la cabecera del menú contextual
			Cursor cursor = (Cursor)((ListView)v).getAdapter().getItem(info.position);
			menu.setHeaderTitle( cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE)));
			// Inflamos el menú contextual
			inflater.inflate(R.menu.context_events, menu);
			
		} // end if
	} // end onCreateContextMenu
	
	
	// Acciones del menú contextual
	// En este caso, eliminar un evento de la lista seleccionada
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		// Editar un evento TO-DO
		case R.id.contextEditar:
		{
			long iID = info.id;
			String sTitulo;
			String sDesc;
			int iTipo;
			int iNotify;
			long lFecha;
			boolean bFin;
			
			// Obtenemos los datos del evento
			Cursor cursor = AgendaEventos.dbAdapter.getEvent(iID);
			Intent intent = new Intent(this, EditEventoActivity.class);
			
			sTitulo = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_TITLE));
			sDesc   = cursor.getString(cursor.getColumnIndex(EventosDBAdapter.COL_DESC));
			iTipo   = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_TIPO));
			iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
			lFecha  = cursor.getLong(cursor.getColumnIndex(EventosDBAdapter.COL_DATE));
			bFin    = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0);
			
			// Indicamos que se trata de una operación de edición de evento
			intent.putExtra(GlobalValues.BUNDLE_OP, false);
			intent.putExtra(GlobalValues.BUNDLE_ID, iID);
			intent.putExtra(GlobalValues.BUNDLE_TITLE, sTitulo);
			intent.putExtra(GlobalValues.BUNDLE_DESC, sDesc);
			intent.putExtra(GlobalValues.BUNDLE_TIPO, iTipo);
			intent.putExtra(GlobalValues.BUNDLE_NOTIFY, iNotify);
			intent.putExtra(GlobalValues.BUNDLE_DATE, lFecha);
			intent.putExtra(GlobalValues.BUNDLE_FIN, bFin);

			this.startActivityForResult(intent, EDIT_EVENT);
			
			return true;
		}
		// Eliminar evento
		case R.id.contextEliminar:
		{
			// El id lo obtenemos del info directamente
			long iID = info.id;
			// Cancelamos el servicio correspondiente a dicha alarma y lo eliminamos de la BD
			delService(iID);
			AgendaEventos.dbAdapter.delEvent(iID);
			
			// Refrescamos la pestaña visible
			updateListTab(tabHost.getCurrentTab());

			// Notificamos que el borrado se ha realizado
			Toast.makeText(this, getResources().getString(R.string.msgDelete), Toast.LENGTH_SHORT).show();
			
			return true;
		}
		default:
			return super.onContextItemSelected(item);
			
		} // end switch
	} // end onContextItemSelected

	
	// Método que refresca la lista de la pestaña actual
	public void updateListTab(int iPosTab) {
		if (iPosTab == m_tabs.TAB_ID_TASKS)	{
			tasksAdapter.getCursor().requery();
			tasksAdapter.notifyDataSetChanged();			
		}
		else if (iPosTab == m_tabs.TAB_ID_WORK)	{
			workAdapter.getCursor().requery();
			workAdapter.notifyDataSetChanged();			
		}
		else if (iPosTab == m_tabs.TAB_ID_ALARM) {
			alarmsAdapter.getCursor().requery();
			alarmsAdapter.notifyDataSetChanged();			
		} // end if
	} // end updateListTab
	
	
	// Método que establece el campo "finalizado" de la lista actual
	private boolean setFinListAdapter(EventosAdapter listAdapter, int iPos) {
		boolean bOk = false;
		
		Cursor cursor = (Cursor)listAdapter.getItem(iPos);
		
		// Actualizamos a finalizado el evento seleccionado
		int iID  = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_ID));
		int iNotify = cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_NOTIFY));
		boolean bFin = (cursor.getInt(cursor.getColumnIndex(EventosDBAdapter.COL_FIN)) != 0);
		
		// Actualizamos el campo "finalizado" a true
		if (iNotify == EventosDBAdapter.NO_NOTIFY) {
			bFin = !bFin;
		} else {
			bFin = true;
		}
		bOk = AgendaEventos.dbAdapter.setFin(iID, bFin);					
		listAdapter.getCursor().requery();
		listAdapter.notifyDataSetChanged();
		
		// Si se desactiva el evento, cancelamos el servicio correspondiente
		if (bFin == true) {
			stopService(iID);
		}
		
		return bOk;
	} // end setFinListAdapter
	
	
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
	    	Date dFecha    = new Date();
	    	dFecha.setTime(data.getLongExtra(GlobalValues.BUNDLE_DATE, 0));
	    	int iNotify    = data.getIntExtra(GlobalValues.BUNDLE_NOTIFY, 0);
	    	boolean bFin   = data.getBooleanExtra(GlobalValues.BUNDLE_FIN, false);
			
			// Si la respuesta proviene de crear un nuevo evento, cogemos los datos y 
			// los insertamos en la base de datos
			if (requestCode == NEW_EVENT) {
				iID = AgendaEventos.dbAdapter.createEvent(sTitulo, sDesc, iTipo, dFecha, iNotify, bFin); 
				if(iID != -1) {
					this.updateListTab(tabHost.getCurrentTab());
					createtService(iID);
					Toast.makeText(this, getResources().getString(R.string.msgCreate), Toast.LENGTH_SHORT).show();
				}
			}
			// Actualizamos el evento
			else if (requestCode == EDIT_EVENT) {
				if(AgendaEventos.dbAdapter.updateEvent(iID, sTitulo, sDesc, iTipo, dFecha, iNotify, bFin)) {
					this.updateListTab(tabHost.getCurrentTab());
					createtService(iID);
					Toast.makeText(this, getResources().getString(R.string.msgUpdate), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	
	// Crea las pestañas en el orden establecido
	private int setTabOrder() {
		int iTabUpdate = 0;
		
		// Establecemos el orden de las pestañas
		iTabTasksOrder  = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_TASKS_ORDER, 0);
		iTabWorkOrder   = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_WORK_ORDER, 1);
		iTabAlarmsOrder = AgendaEventos.sharedPref.getInt(GlobalValues.PREF_TAB_ALARMS_ORDER, 2);
		
		// Obtenemos la referencia al componente TabHost
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		m_tabs = new TabManager(this, tabHost);
		
		for (int i = 0; i < GlobalValues.NUM_TABS; i++) {
			if (i == iTabTasksOrder) {
				m_tabs.addTab(m_tabs.TAB_TASKS);
				if (i == 0) iTabUpdate = m_tabs.TAB_ID_TASKS;
			}
			else if (i == iTabWorkOrder) {
				m_tabs.addTab(m_tabs.TAB_WORK);
				if (i == 0) iTabUpdate = m_tabs.TAB_ID_WORK;
			}
			else if (i == iTabAlarmsOrder) {
				m_tabs.addTab(m_tabs.TAB_ALARM);
				if (i == 0) iTabUpdate = m_tabs.TAB_ID_ALARM;
			}
		}
		
		return iTabUpdate;
	}
	
	
	// Comienza el servicio de notificación de eventos
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
	private void stopService(long iID) {
		Intent iCancel = new Intent(this, NotifyService.class);
		iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
		iCancel.setAction(NotifyService.CANCEL);
        this.startService(iCancel);
	}
	
	// Detiene el servicio de notificación del evento correspondiente
	// y elimina el evento de la BD
	private void delService(long iID) {
		Intent iCancel = new Intent(this, NotifyService.class);
		iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
		iCancel.setAction(NotifyService.DELETE);
        this.startService(iCancel);
	}
}

