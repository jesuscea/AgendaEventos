package com.cea.eventos;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// Actividad para crear/editar eventos
public class EditEventoActivity extends Activity {

	static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    static final int FIRST_YEAR = 1900;
    static private boolean bOpNuevo;
    
    private Intent intent;
    private Button btnDate;
    private Button btnTime;
    private Button btnSaveTop;
    private Button btnSave;
    private EditText edTitulo;
    private EditText edDesc;
    private RadioButton radioTask;
    private RadioButton radioWork;
    private CheckBox chkNotify;
    private CheckBox chkAlarm;
    private CheckBox chkNotifyBar;
    private TextView edDateTimeSelected;
    private Date m_dDateTime;
    private TableLayout lyNotifyData;
    private SimpleDateFormat format;
    
    private long m_iID;
    private String m_sTitulo;
    private String m_sDesc;
    private int m_iTipo;
    private int m_iNotify;
    private Date m_dFecha;
    private boolean m_bFin;    
           
	// Creación de la actividad
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit_evento);
		
		format = new SimpleDateFormat(GlobalValues.FORMAT_DATE, new Locale("es","ES"));
		intent = this.getIntent();
	
		btnDate = (Button)this.findViewById(R.id.btnDate);
		btnTime = (Button)this.findViewById(R.id.btnTime);
		btnSaveTop = (Button)this.findViewById(R.id.btnSaveTop);
		btnSave = (Button)this.findViewById(R.id.btnSave);
		
		lyNotifyData = (TableLayout)this.findViewById(R.id.layoutNotifyData);
		chkNotify = (CheckBox)this.findViewById(R.id.chkNotify);
		chkAlarm = (CheckBox)this.findViewById(R.id.chkAlarm);
		chkNotifyBar = (CheckBox)this.findViewById(R.id.chkNotifyBar);
		
		radioTask = (RadioButton)this.findViewById(R.id.rdTask);
		radioWork = (RadioButton)this.findViewById(R.id.rdWork);
		
		edTitulo = (EditText)this.findViewById(R.id.edTitulo);
		edDesc   = (EditText)this.findViewById(R.id.edDescripcion);
		edDateTimeSelected = (TextView)this.findViewById(R.id.edDateTimeSelected);
		
		// Por defecto, ocultamos todo lo relacionado con las notificaciones
		// Se mostrará si se marca la opción de "Notificar"
		// Ocultamos el botón guardar y mostramos el de arriba
		btnSaveTop.setVisibility(View.VISIBLE);
		btnSave.setVisibility(View.GONE);
		lyNotifyData.setVisibility(View.INVISIBLE);
		
		// Evento Check del CheckBox de notificar
		CheckBox.OnCheckedChangeListener CBNotifyListener = new CheckBox.OnCheckedChangeListener() {
        	@Override
        	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        		if (isChecked) {
        			// Ocultamos el botón guardar y mostramos el de abajo
        			btnSaveTop.setVisibility(View.GONE);
        			btnSave.setVisibility(View.VISIBLE);
        			lyNotifyData.setVisibility(View.VISIBLE);
        			// Por defectos, activamos la notificación de alarma
        			chkAlarm.setChecked(true);
        		}
        		else {
        			lyNotifyData.setVisibility(View.INVISIBLE);
        			chkAlarm.setChecked(false);
        			// Ocultamos el botón guardar y mostramos el de arriba
        			btnSaveTop.setVisibility(View.VISIBLE);
        			btnSave.setVisibility(View.GONE);
        		}
        	}
        };
        chkNotify.setOnCheckedChangeListener(CBNotifyListener);
		
		// Evento OnClick del botón de selección de fecha
		btnDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Show the DatePickerDialog
				showDialog(DATE_DIALOG_ID);
			}
		});
        
		// Evento OnClick del botón de selección de hora
		btnTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Show the TimePickerDialog
				showDialog(TIME_DIALOG_ID);
			}
		});	
		
		
		// Obtenemos los valores del Bundle (tipo de operacion: Nuevo/Editar, la Base de datos y el ID del evento (si existe)
		bOpNuevo = intent.getBooleanExtra(GlobalValues.BUNDLE_OP, false);//
		m_iID = -1;
		m_dDateTime = new Date();
		m_dDateTime.setMinutes(m_dDateTime.getMinutes() + 1);
		edDateTimeSelected.setText(format.format(m_dDateTime));
		if (bOpNuevo == false) {
			// Cargamos los datos
			loadDataEvent();
		}
	}

	// Declaramos un objeto listener del evento click del Diálogo de fecha
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// Evento de Fecha seleccionada
		public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
			m_dDateTime.setYear(yearSelected - FIRST_YEAR);
			m_dDateTime.setMonth(monthOfYear);
			m_dDateTime.setDate(dayOfMonth);
			// Actualizamos el cuadro de texto indicando la fecha que se ha seleccionado
			edDateTimeSelected.setText(format.format(m_dDateTime));
		}
	};

	// Declaramos un objeto listener del evento click del Diálogo de hora                 
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		// Evento de Hora seleccionada
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			m_dDateTime.setHours(hourOfDay);
			m_dDateTime.setMinutes(min);
			// Actualizamos el cuadro de texto indicando la hora que se ha seleccionado
			edDateTimeSelected.setText(format.format(m_dDateTime));
		}
	};
	
	// Método que se invocará al llamar al método showDialog(), en función del ID que se pase
    @Override
    protected Dialog onCreateDialog(int id) {
    	// Asignamos el dia y la hora actual del sistema
        final Calendar calendar = Calendar.getInstance();
        
        int iYear   = calendar.get(Calendar.YEAR);
        int iMonth  = calendar.get(Calendar.MONTH);
        int iDay    = calendar.get(Calendar.DAY_OF_MONTH);
        int iHour   = calendar.get(Calendar.HOUR_OF_DAY);
        int iMinute = calendar.get(Calendar.MINUTE) + 1;
             
    	switch (id) {
    	case DATE_DIALOG_ID:
    		// Mostramos el diálogo de selección de fecha con la fecha indicada por defecto 
    		return new DatePickerDialog(this, mDateSetListener, iYear, iMonth, iDay);
    		// Mostramos el diálogo de selección de hora con la hora indicada por defecto 
    	case TIME_DIALOG_ID:
    		return new TimePickerDialog(this, mTimeSetListener, iHour, iMinute, true);
        }
    	
        return null;
    }
    
    
    // Evento OnClick del botón de guardar evento
	public void onSaveClick(View v) {
		if (saveEvent()) {
			setResult(RESULT_OK, intent);
			finish();
		}
	}
    
    
    // Método que crea o edita un evento
    private boolean saveEvent()
    {
    	// Comprobaciones previas a la inserción/edición
    	// 1 - Se ha insertado un títutlo
    	if (edTitulo.getText().toString().isEmpty()) {
    		Toast.makeText(this, getResources().getString(R.string.msgNoTitle), Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	// 2 - No se ha indicado el tipo de evento (por si acaso)
    	else if (!radioTask.isChecked() && !radioWork.isChecked()) {
    		Toast.makeText(this, getResources().getString(R.string.msgNoTipo), Toast.LENGTH_SHORT).show();
			return false;
    	}
    	// 3 - Se ha marcado que se quiere notificar, pero no se ha indicado qué tipo de notificación o la fecha no es correcta
    	else if (chkNotify.isChecked()) {
    		// 3.1 - No se ha indicado tipo de notificación
    		if (!chkAlarm.isChecked() && !chkNotifyBar.isChecked()) {
    			Toast.makeText(this, getResources().getString(R.string.msgNoNotify), Toast.LENGTH_SHORT).show();
    			return false;
    		}
    		
    		// 3.2 - La fecha no es correcta (es anterior a la fecha actual)
    		if (!m_dDateTime.after(new Date())) {
    			Toast.makeText(this, getResources().getString(R.string.msgBadDate), Toast.LENGTH_SHORT).show();
    			return false;
    		}
    	}
    	
    	// Si hemos llegado hasta aquí, todo está correcto para empezar a crear o editar el evento
    	// Cogemos los datos de la actividad
    	m_sTitulo = edTitulo.getText().toString();
    	m_sDesc   = edDesc.getText().toString();
    	m_iTipo = 0;    	
    	if (radioTask.isChecked()) {
    		m_iTipo = EventosDBAdapter.TASK;
    	}
    	else if (radioWork.isChecked()) {
    		m_iTipo = EventosDBAdapter.WORK;
    	}    	
    	
    	m_dFecha = m_dDateTime;
    	// Sólo horas y minutos (los segundos siempre 0)
    	m_dFecha.setSeconds(0);
    	m_iNotify = EventosDBAdapter.NO_NOTIFY;
    	// Según el tipo de notificación, establecemos el valor correspondiente
    	if (chkNotify.isChecked() && chkAlarm.isChecked() && chkNotifyBar.isChecked()) {
    		m_iNotify = EventosDBAdapter.NOTIFY_ALARM_BAR;
    	}
    	else if (chkNotify.isChecked() && chkNotifyBar.isChecked()) {
    		m_iNotify = EventosDBAdapter.NOTIFY_BAR;
    	}
    	else if (chkNotify.isChecked() && chkAlarm.isChecked()) {
    		m_iNotify = EventosDBAdapter.NOTIFY_ALARM;
    	}
    	
    	// Al crear/editar, se presupone que no está finalizado el evento
    	m_bFin = false;
    	
    	intent.putExtra(GlobalValues.BUNDLE_ID, m_iID);
		intent.putExtra(GlobalValues.BUNDLE_TITLE, m_sTitulo);
		intent.putExtra(GlobalValues.BUNDLE_DESC, m_sDesc);
		intent.putExtra(GlobalValues.BUNDLE_TIPO, m_iTipo);
		intent.putExtra(GlobalValues.BUNDLE_NOTIFY, m_iNotify);
		intent.putExtra(GlobalValues.BUNDLE_DATE, m_dFecha.getTime());
		intent.putExtra(GlobalValues.BUNDLE_FIN, m_bFin);
    	
    	return true;
    }
    
    
    // Método que carga los datos de un evento en la actividad
    private void loadDataEvent() {
    	// Edición de evento, cargamos los datos del evento actuales
    	m_iID     = intent.getLongExtra(GlobalValues.BUNDLE_ID, -1);
		m_sTitulo = intent.getStringExtra(GlobalValues.BUNDLE_TITLE);
		m_sDesc   = intent.getStringExtra(GlobalValues.BUNDLE_DESC);
		m_iTipo   = intent.getIntExtra(GlobalValues.BUNDLE_TIPO, 0);
		m_iNotify = intent.getIntExtra(GlobalValues.BUNDLE_NOTIFY, 0);
		m_bFin    = (intent.getIntExtra(GlobalValues.BUNDLE_FIN, 0) != 0);
		long lFecha = intent.getLongExtra(GlobalValues.BUNDLE_DATE, 0);
		// Formateamos la fecha
		m_dFecha = new Date(lFecha);
    	
    	// Una vez obtenidos los datos los mostramos en la actividad
    	edTitulo.setText(m_sTitulo);
    	edDesc.setText(m_sDesc);
    	if (m_iTipo == EventosDBAdapter.WORK) {
    		radioTask.setChecked(false);
    		radioWork.setChecked(true);
    	}
    	else {
    		radioTask.setChecked(true);
    		radioWork.setChecked(false);
    	}
    	
		if (m_iNotify == EventosDBAdapter.NOTIFY_ALARM_BAR) {	
			chkNotify.setChecked(true);
			chkAlarm.setChecked(true);
			chkNotifyBar.setChecked(true);
		}
		else if (m_iNotify == EventosDBAdapter.NOTIFY_BAR) {
			chkNotify.setChecked(true);
			chkAlarm.setChecked(false);
			chkNotifyBar.setChecked(true);
		}
		else if (m_iNotify == EventosDBAdapter.NOTIFY_ALARM) {
			chkNotify.setChecked(true);
			chkAlarm.setChecked(true);
			chkNotifyBar.setChecked(false);
		}
		else if (m_iNotify == EventosDBAdapter.NO_NOTIFY) {
    		// Al desmarcar, se invoca el evento de ocultar el grupo de opciones de notificación
    		chkNotify.setChecked(false);
    	}
		
		edDateTimeSelected.setText(format.format(m_dFecha));
    }
    
    // Método de la actividad que se invoca cuando ésta finaliza
    @Override	
	public void finish() {
		super.finish();
	}
}
