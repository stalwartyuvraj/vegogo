package com.app.vegogo;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.config.Constants;
import com.fragments.activity.DetailActivity;
import com.models.Store;

public class ProximityIntentReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID = 1000;
    Context mContext;
	 NotificationManager notificationManager ;
    public ProximityIntentReceiver()
    {
    	super();
    }
    
    public ProximityIntentReceiver(Context mContext)
    {
    	this.mContext=mContext;
    	  
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
    	Store store=(Store)intent.getSerializableExtra("store");
		
    	if(store!=null){
		Log.d("tag@",store.getStore_name()+" "+store.getStore_address()+" id "+store.getStore_id());
		
		notificationManager = 
	            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       Uri sound;
		
		if(Constants.sound==null)
		sound=Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.comm);
		else
	    sound=Constants.sound;
			
         Intent intent2 = new Intent(context, DetailActivity.class);
		intent2.putExtra("store",store);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, store.getStore_id(), intent2, PendingIntent.FLAG_CANCEL_CURRENT); 
       
        String title=store.getStore_name()+" "+store.getStore_address();
        String msg="Now less than 100 meters away";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon( R.drawable.ic_launcher)
        .setContentTitle(title)
        .setContentText(msg)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setSound(sound);
        
        
//        Notification notification = createNotification();
//        notification.setLatestEventInfo(context,, pendingIntent);
//       
       // notification.sound=Uri.parse("android.resource://com.app.vegogo/raw/comm.mp3");
        // Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       // notification.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(store.getStore_id(), mBuilder.build());
        
    	}
        
        
    }
    
    private Notification createNotification() {
        Notification notification = new Notification();
        
        notification.icon = R.drawable.ic_launcher;
        notification.when = System.currentTimeMillis();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.sound=Uri.parse("android.resource://com.app.vegogo/raw/comm.mp3");

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults|= Notification.DEFAULT_VIBRATE;
        
        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;
        
        
        return notification;
    }
    
}