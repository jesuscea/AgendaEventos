package com.cea.eventos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TabHost;


public class TabManager {

	TabHost m_tabs;
	Context m_context;
	
	// Constantes para crear las pestañas
	public final short TAB_TASKS = 0;
	public final short TAB_WORK  = 1;
	public final short TAB_ALARM = 2;
	
	// Ids para gestionar las pestañas
	public int TAB_ID_TASKS;
	public int TAB_ID_WORK;
	public int TAB_ID_ALARM;
	
	
	// Construye el contenedor de pestañas con las pestañas por defecto
	public TabManager(Context c, TabHost tabs) {
		m_tabs = tabs;
		m_context = c;

		// Grupo de pestañas por defecto
		// Preparamos su configuración
		if (tabs.getTabWidget() != null ) {
			this.clearTabManager();
		}
		else {
			m_tabs.setup();
		}		
	}
	
	
	// Añade una pestaña nueva al contenedor de pestañas en función del id
	public void addTab(short _id) {
		TabHost.TabSpec newTab;
		String   sTabId;
		String   sTabName;		
		Drawable iIcon;

		switch (_id) {
		case TAB_TASKS:
			
			// Pestaña Tareas
			sTabId   = m_context.getResources().getString(R.string.tabTasksId);
			sTabName = m_context.getResources().getString(R.string.tabTasks);
			iIcon    = m_context.getResources().getDrawable(R.drawable.task_tab);
			// Preparamos un objeto con referencia a la pestaña
			newTab = m_tabs.newTabSpec(sTabId);
			//Establecemos el contenido de la pestaña
			newTab.setContent(R.id.tabTasks);
			// Definimos la pestaña en el TabHost
			newTab.setIndicator(sTabName, iIcon);
			// Añadimos la pestaña al TabHost
			m_tabs.addTab(newTab);
			
			TAB_ID_TASKS = m_tabs.getTabWidget().getTabCount()-1;
			
			break;
		case TAB_WORK:
			
			// Pestaña Trabajo
			sTabId   = m_context.getResources().getString(R.string.tabWorkId);
			sTabName = m_context.getResources().getString(R.string.tabWork);
			iIcon    = m_context.getResources().getDrawable(R.drawable.work_tab);
			// Preparamos un objeto con referencia a la pestaña
			newTab = m_tabs.newTabSpec(sTabId);
			//Establecemos el contenido de la pestaña
			newTab.setContent(R.id.tabWork);
			// Definimos la pestaña en el TabHost
			newTab.setIndicator(sTabName, iIcon);
			// Añadimos la pestaña al TabHost
			m_tabs.addTab(newTab);
			
			TAB_ID_WORK = m_tabs.getTabWidget().getTabCount()-1;
			
			break;
		case TAB_ALARM:
			
			// Pestaña Tareas
			sTabId   = m_context.getResources().getString(R.string.tabAlarmId);
			sTabName = m_context.getResources().getString(R.string.tabAlarm);
			iIcon    = m_context.getResources().getDrawable(R.drawable.alarms_tab);
			// Preparamos un objeto con referencia a la pestaña
			newTab = m_tabs.newTabSpec(sTabId);
			//Establecemos el contenido de la pestaña
			newTab.setContent(R.id.tabAlarms);
			// Definimos la pestaña en el TabHost
			newTab.setIndicator(sTabName, iIcon);
			// Añadimos la pestaña al TabHost
			m_tabs.addTab(newTab);
			
			TAB_ID_ALARM = m_tabs.getTabWidget().getTabCount()-1;
			
			break;
			default:
				;
		}
	}
	
	
	// Oculta una pestaña del contenedor de pestañas
	public void hideTab(int _id) {
		if ( (_id == TAB_ID_TASKS || _id == TAB_ID_WORK || _id == TAB_ID_ALARM) &&
			 (m_tabs.getTabWidget().getChildAt(_id).getVisibility() != View.GONE) ) {
			m_tabs.getTabWidget().getChildAt(_id).setVisibility(View.GONE);
			// Buscamos una pestaña activa para establecerla
			setFirstVisibleTab();
		}
	}
	
	
	// Muestra una pestaña del contenedor de pestañas
	public void showTab(int _id) {
		if ( (_id == TAB_ID_TASKS || _id == TAB_ID_WORK || _id == TAB_ID_ALARM) &&
			 (m_tabs.getTabWidget().getChildAt(_id).getVisibility() != View.VISIBLE) ) {  
			m_tabs.getTabWidget().getChildAt(_id).setVisibility(View.VISIBLE);
			// Buscamos una pestaña activa para establecerla
			setFirstVisibleTab();
		}
	}
	
	
	// Muestra la pestaña indicada por el id
	public void setTab(int id) {
		m_tabs.setCurrentTab(id);
	}
	
	
	// Busca la primera pestaña visible y la habilita
	public void setFirstVisibleTab() {
		if (m_tabs.getTabWidget().getChildAt(TAB_ID_TASKS).getVisibility() == View.VISIBLE) {
			m_tabs.setCurrentTab(TAB_ID_TASKS);
		}
		else if (m_tabs.getTabWidget().getChildAt(TAB_ID_WORK).getVisibility() == View.VISIBLE) {
			m_tabs.setCurrentTab(TAB_ID_WORK);
		}
		else if (m_tabs.getTabWidget().getChildAt(TAB_ID_ALARM).getVisibility() == View.VISIBLE) {
			m_tabs.setCurrentTab(TAB_ID_ALARM);
		}
	}

	
	// Devuelve el número de pestañas visibles
	public int getTabCount() {
		return m_tabs.getTabWidget().getChildCount();
	}
	
	
	// Elimina todas las pestañas del contenedor
	public void clearTabManager() {
		m_tabs.setCurrentTab(0);
		m_tabs.getTabWidget().removeAllViews();
	}
}
