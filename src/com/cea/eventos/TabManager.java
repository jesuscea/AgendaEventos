package com.cea.eventos;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Vector;


class Tab {
    String   sTabId;
    String   sTabName;
    Drawable iIcon;
    boolean bIsVisible;
    boolean bIsActive;
}

public class TabManager {

	Context m_context;
	int m_iNumTabs;

    Vector<Tab> m_vListTabs;
	
	// Constantes para crear las pestañas
	public static final short TAB_TASKS = 0;
	public static final short TAB_WORK  = 1;
    public static final short TAB_SHOP  = 2;
	public static final short TAB_ALARM = 3;
	
	// Ids para gestionar las pestañas
	public int TAB_ID_TASKS;
	public int TAB_ID_WORK;
    public int TAB_ID_SHOP;
	public int TAB_ID_ALARM;
	
	
	// Construye el contenedor de pestañas con las pestañas por defecto
	public TabManager(Context c) {
		m_context = c;
		m_iNumTabs = 0;
        m_vListTabs = new Vector<Tab>();
	}
	
	
	// Añade una pestaña nueva al contenedor de pestañas en función del id
	public void addTab(int _id) {

        Tab newTab = new Tab();

        newTab.bIsVisible = true;
        newTab.bIsActive = true;

		switch (_id) {
		case TAB_TASKS:
			
			// pestaña Tareas
            newTab.sTabId   = m_context.getResources().getString(R.string.tabTasksId);
            newTab.sTabName = m_context.getResources().getString(R.string.tabTasks);
            newTab.iIcon    = m_context.getResources().getDrawable(R.drawable.task_tab);

			TAB_ID_TASKS = m_iNumTabs;
			m_iNumTabs++;
			
			break;
		case TAB_WORK:
			
			// pestaña Trabajo
            newTab.sTabId   = m_context.getResources().getString(R.string.tabWorkId);
            newTab.sTabName = m_context.getResources().getString(R.string.tabWork);
            newTab.iIcon    = m_context.getResources().getDrawable(R.drawable.work_tab);

			TAB_ID_WORK = m_iNumTabs;
			m_iNumTabs++;
			
			break;
        case TAB_SHOP:

			// pestaña Trabajo
            newTab.sTabId   = m_context.getResources().getString(R.string.tabShopId);
            newTab.sTabName = m_context.getResources().getString(R.string.tabShop);
            newTab.iIcon    = m_context.getResources().getDrawable(R.drawable.shopping_tab);

			TAB_ID_SHOP = m_iNumTabs;
			m_iNumTabs++;

			break;
		case TAB_ALARM:
			
			// pestaña Tareas
            newTab.sTabId   = m_context.getResources().getString(R.string.tabAlarmId);
            newTab.sTabName = m_context.getResources().getString(R.string.tabAlarm);
            newTab.iIcon    = m_context.getResources().getDrawable(R.drawable.alarms_tab);

			TAB_ID_ALARM = m_iNumTabs;
			m_iNumTabs++;
			
			break;
		default:
			;
		}

        m_vListTabs.add(newTab);
	}


    // Devuelve el número de pestañas
    public int getNumTabs() {
        return this.m_iNumTabs;
    }

    // Devuelve el título de la pestaña
    public String getTabTittle(int _id) {
        try {
            return m_vListTabs.get(_id).sTabName;
        }
        catch (Exception e) {
            return "";
        }
    }
}
