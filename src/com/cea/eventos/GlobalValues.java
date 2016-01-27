package com.cea.eventos;


// Aquí vamos almacenando valores globales para la aplicación
public class GlobalValues {
	static public final int NUM_TABS = 4;
	
	static public final String BUNDLE_OP = "tipoOp";
	static public final String BUNDLE_ID = "idEvent";
	static public final String BUNDLE_TITLE = "titleEvent";
	static public final String BUNDLE_DESC  = "descEvent";
	static public final String BUNDLE_TIPO  = "tipoEvent";
	static public final String BUNDLE_DATE  = "dateEvent";
	static public final String BUNDLE_NOTIFY = "notifyEvent";
	static public final String BUNDLE_FIN    = "finEvent";
	static public final String BUNDLE_REPEAT = "repeatEvent";
	static public final String BUNDLE_WEEKDAYS = "weekDaysEvent";
	
	static public final String BUNDLE_DIALOG_ID = "dialogID";
	static public final String BUNDLE_DIALOG_MSG = "dialogMsg";
	static public final String BUNDLE_DIALOG_DESC = "dialogDesc";
	
	static public final String FORMAT_DATE = "dd MMM yyyy. HH:mm";
	static public final String FORMAT_MONTH = "MMMM yyyy";
	
	static public final String SERVICE_ID  = "idEvent";

    public static final String NO_VERSION      = "-1";
    public static final String PREF_VERSION    = "appVersion";
	public static final String PREF_TAB_TASKS  = "chkTasks";
	public static final String PREF_TAB_WORK   = "chkWork";
    public static final String PREF_TAB_SHOP   = "chkShop";
	public static final String PREF_TAB_ALARMS = "chkAlarms"; 
	public static final String PREF_VIBRATE    = "chkVibrate";
	public static final String PREF_AUTO_END_EVENT = "chkAutoEndEvent";
	public static final String PREF_TAB_TASKS_ORDER   = "btnTasks";
	public static final String PREF_TAB_WORK_ORDER    = "btnWork";
    public static final String PREF_TAB_SHOP_ORDER    = "btnShop";
	public static final String PREF_TAB_ALARMS_ORDER  = "btnAlarms";
	
	public static final String BUNDLE_NOTIFY_ID   = "idNotify";
	public static final String BUNDLE_NOTIFY_MSG  = "msgNotify";
	public static final String BUNDLE_NOTIFY_DESC = "descNotify";
	public static final String BUNDLE_NOTIFY_TYPE = "typeNotify";
	public static final String BUNDLE_NOTIFY_REPEAT = "repeatNotify";
	public static final String BUNDLE_NOTIFY_WEEKDAYS = "weeksDaysNotify";
	
	// Alarma de 10 segundos
	static public int ALARM_INTERVAL   = 10000;
	static public int VIBRATE_INTERVAL =  1000;
}
