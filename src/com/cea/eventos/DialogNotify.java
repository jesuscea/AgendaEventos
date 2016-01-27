package com.cea.eventos;


import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


// Clase que genera un dialogo cuando se activa un evento con alarma, permitiendo silenciarla
public class DialogNotify extends Activity {
	
	private long iID;
	private String sMsg;
    private String sDesc;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notify);

        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        
        iID  = this.getIntent().getLongExtra(GlobalValues.BUNDLE_DIALOG_ID, -1);
        sMsg = this.getIntent().getStringExtra(GlobalValues.BUNDLE_DIALOG_MSG);
        sDesc = this.getIntent().getStringExtra(GlobalValues.BUNDLE_DIALOG_DESC);

        // Gestión para despertar y desbloquear el teléfono para mostrar la alarma
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock kl=km.newKeyguardLock("EventosActivity");
        kl.disableKeyguard();

        ImageView imgAppIcon = (ImageView)this.findViewById(R.id.imgAppIcon);
        TextView txtAppName = (TextView)this.findViewById(R.id.txtAppName);
        TextView txtEvent = (TextView)this.findViewById(R.id.txtEvent);
        TextView txtDesc  = (TextView)this.findViewById(R.id.txtDesc);
        Button   btnClose = (Button)this.findViewById(R.id.btnClose);

        txtAppName.setText(getResources().getString(R.string.app_name));
        imgAppIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        txtEvent.setText(sMsg);
        txtDesc.setText(sDesc);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detenemos el servicio de notificacion correspondiente al ID
                stopAlarm();
                // Una vez silenciado, cerramos la actividad
                DialogNotify.this.finish();
            }
        });
    }


	@Override
	protected void onPause() {
		super.onPause();
		// Detenemos el servicio de notificacion correspondiente al ID
		stopAlarm();
		// Una vez silenciado, cerramos la actividad
		DialogNotify.this.finish();
	}
    
    
    // Detiene el sonido de alarma
 	private void stopAlarm()
 	{
 		/*Intent iCancel = new Intent(this, NotifyService.class);
		iCancel.putExtra(GlobalValues.SERVICE_ID, iID);
		iCancel.setAction(NotifyService.CANCEL);
        this.stopService(iCancel);*/
        
        // Detenemos el audio de alarma (si esta sonando)
        try {
	        if (AgendaEventos.mRingTone.isPlaying()) {
	        	AgendaEventos.mRingTone.stop();
	        	//AgendaEventos.mMediaPlayer.reset();
	        }
        }
        catch(Exception e) {
        	// Nothing to do
        }
 	}
}
