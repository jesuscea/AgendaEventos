package com.cea.eventos;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;


// Clase que genera un diálogo cuando se activa un evento con alarma, permitiendo silenciarla
public class DialogNotify extends Activity {
	
	private long iID;
	private String sMsg;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notify);
        
        iID  = this.getIntent().getLongExtra(GlobalValues.BUNDLE_DIALOG_ID, -1);
        sMsg = this.getIntent().getStringExtra(GlobalValues.BUNDLE_DIALOG_MSG);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(getResources().getString(R.string.app_name));
		builder.setMessage(sMsg);
		builder.setPositiveButton(getResources().getString(R.string.dialogBtnCerrar), 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int boton) {
					// Detenemos el servicio de notificación correspondiente al ID
					stopAlarm();
					// Una vez silenciado, cerramos la actividad
					DialogNotify.this.finish();
				}
			}
		);		
		
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		dialog.show();
    }
    
    
    // Detiene el sonido de alarma
 	private void stopAlarm()
 	{
 		Intent iCancel = new Intent(this, NotifyService.class);
		iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
		iCancel.setAction(NotifyService.CANCEL);
        this.stopService(iCancel);
        
        // Detenemos el audio de alarma (si está sonando)
        try {
	        if (AgendaEventos.mMediaPlayer.isPlaying()) {
	        	AgendaEventos.mMediaPlayer.stop();
	        	AgendaEventos.mMediaPlayer.reset();
	        }
        }
        catch(Exception e) {
        	// Nothing to do
        }
 	}
}
