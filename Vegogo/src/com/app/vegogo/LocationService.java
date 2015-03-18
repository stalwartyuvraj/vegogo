package com.app.vegogo;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.config.Config;
import com.db.DbHelper;
import com.db.Queries;
import com.models.Store;

public class LocationService extends Service 
{
	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	ArrayList<Store> as1;
	Context mContext;
	Intent intent,reciever2;
	int counter = 0;
	public static ArrayList<Store> arrStores=new ArrayList<Store>();
	SharedPreferences sp;
	Queries queries;


	@Override
	public void onCreate() 
	{
		super.onCreate();
		as1=new ArrayList<Store>();
		intent = new Intent(Config.RECIEVER);   
		reciever2=new Intent(Config.RECIEVER2);
		mContext=this;
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{      
		sp= getApplicationContext().getSharedPreferences("preff", Context.MODE_PRIVATE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();        
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, listener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		DbHelper dbHelper=DbHelper.getInstance(LocationService.this);
		queries=Queries.getInstance(dbHelper);
		arrStores=queries.getStores(0);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}



	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}



	@Override
	public void onDestroy() {       
		// handler.removeCallbacks(sendUpdatesToUI);     
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);        
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}




	public class MyLocationListener implements LocationListener
	{

		public void onLocationChanged(final Location loc)
		{
			Log.d("tag@", "Location Updated "+loc.getLatitude()+" : "+loc.getLongitude());
			if(isBetterLocation(loc, previousBestLocation)) {
				loc.getLatitude();
				loc.getLongitude();        	
				
				MainActivity.location = loc;
				int size=arrStores.size();
	
				Log.d("tag@", "Size : "+size);

				if(size>0)
				{

					for(Store store:arrStores)
					{
						Location locStore = new Location("Store");
						locStore.setLatitude(store.getLat());
						locStore.setLongitude(store.getLon());

						double distance2 = locStore.distanceTo(loc) ;
						distance2=Math.round(distance2);

						//store.getLat()+" : "+ store.getLon() + "Distance "+distance2 +" : D2 "+distance2);

						if(distance2<=100)
						{
//							Log.d("tag","Distance "+distance2);
//							Log.d("tag12"," AS "+as1.size());
							
							boolean b=hasStore(as1, store);
							//Log.d("tag@","Store name "+store.getStore_name()+ " Sent Notif "+b);
						
							if(!b)
							{
								//Log.d("tag@","Store Notify "+store.getStore_name()+ " Sent Notif "+b);

								as1.add(store);
								intent.putExtra("store",store);		
								sendBroadcast(intent);
							}
						}			
						store.setDistance(distance2);
					}       
				}
				sendBroadcast(reciever2);
			}
		}
		
		
		public void onProviderDisabled(String provider)
		{
			//Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}


		public void onProviderEnabled(String provider)
		{
			//Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}


		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}
	}
	
	
	boolean hasStore(ArrayList<Store> as,Store store)
	{
		for(int i=0;i<as.size();i++)
		{
			if(as.get(i).getStore_name().equals(store.getStore_name())) 
					return true;
		}
		return false;
	}
}