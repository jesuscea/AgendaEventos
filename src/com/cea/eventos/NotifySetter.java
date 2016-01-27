package com.cea.eventos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


// Esta clase se encarga de iniciar la gestion de las alarmas creadas y
// Notficara aquellas que haya cumplido la fecha y hora
public class NotifySetter extends BroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, NotifyService.class);
        service.setAction(NotifyService.ALL);
        context.startService(service);
    }

}
