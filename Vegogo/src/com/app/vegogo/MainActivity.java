package com.app.vegogo;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import twitter4j.auth.AccessToken;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.MGListAdapter;
import com.adapters.MGListAdapter.OnMGListAdapterAdapterListener;
import com.config.Config;
import com.config.UIConfig;
import com.db.DbHelper;
import com.db.Queries;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.fragments.AboutUsFragment;
import com.fragments.FavoriteFragment;
import com.fragments.FeaturedFragment;
import com.fragments.MapFragment;
import com.fragments.NewsFragment;
import com.fragments.SearchFragment;
import com.fragments.SplashFragment;
import com.fragments.activity.DetailActivity;
import com.fragments.activity.LoginActivity;
import com.fragments.activity.ProfileActivity;
import com.fragments.activity.RegisterActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.location.LocationUtils;
import com.models.Menu;
import com.models.Menu.HeaderType;
import com.models.Store;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.refreshlayout.SwipeRefreshActivity;
import com.social.twitter.TwitterApp;
import com.social.twitter.TwitterApp.TwitterAppListener;
import com.usersession.UserAccessSession;
import com.usersession.UserSession;

public class MainActivity extends SwipeRefreshActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	// used to store app title
	private CharSequence mTitle;
	private Menu[] MENUS;
	public static Location location;
	public static List<Address> address;
	public static int offsetY = 0;

	private static DbHelper dbHelper;
	private static Queries q;
	protected static ImageLoader imageLoader;
	private static boolean isShownSplash = false;
	static LocationRequest mLocationRequest;
	static GoogleApiClient mGoogleApiClient;
	boolean mUpdatesRequested = false;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;
	FragmentManager fm;
	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;
	private  Fragment currFragment;
	public static int fragPos=1;
	private GetAddressTask getAddressTask;
	int dis=5;
	ProximityIntentReceiver mReceiver;

	private BroadcastReceiver mReceiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Location targetLocation = new Location("loc");
		    targetLocation.setLatitude(MainActivity.location.getLatitude());
		    targetLocation.setLongitude(MainActivity.location.getLongitude());

			if(address == null) {
				getAddressTask = new MainActivity.GetAddressTask(MainActivity.this);
		     	getAddressTask.execute(targetLocation);
			}

		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		this.getActionBar().setCustomView(R.layout.custom_action_bar);
		this.getActionBar().setIcon(R.drawable.header_logo);
		this.getActionBar().setTitle("");

     //   RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION,      Uri.parse("android.resource://com.app.vegogo/raw/comm.mp3"));
		com.config.Constants.sound=Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.comm);
		dbHelper = DbHelper.getInstance(this);
		q = new Queries(dbHelper);

		fm= MainActivity.this.getSupportFragmentManager();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));

		statusCallback = new SessionStatusCallback();
		mTwitter = new TwitterApp(this, twitterAppListener);

		mTitle = mDrawerTitle = "";
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		updateMenuList();

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_menu, //nav menu toggle icon
				R.string.no_name, // nav drawer open - description for accessibility
				R.string.no_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			offsetY = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}


		if(!isShownSplash) {
			isShownSplash = true;
			this.getActionBar().hide();
			FragmentManager fragmentManager = this.getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, new SplashFragment()).commit();
		}

		else if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(1,false);
		}

		mUpdatesRequested = false;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		// Get an editor
		mEditor = mPrefs.edit();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();

		Intent mServiceIntent = new Intent(this, LocationService.class);

		startService(mServiceIntent);

		Config.mContext=MainActivity.this;
		 mReceiver=new ProximityIntentReceiver(MainActivity.this);
	}

	public void showMainView() {
		getActionBar().show();
		displayView(1,false);
		//showAds();
	}


	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d("tag",position+" : "+fragPos);

			//			if(fragPos==2)
			//				position=1;

			fragPos=position;

			// display view for selected nav drawer item
			displayView(position,false);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			updateMenuList();
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		//	        case R.id.action_settings:
		//	            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i("GoogleApiClient", "GoogleApiClient connection has been suspend");
	}


	public void displayView(int position, boolean isMap) {

		// clear back stack
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
			fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); 
		}

		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {

		case 1:
			fragment = new SearchFragment();
			break;
		case 2:
			Config.IS_MAP=isMap;
			fragment=new MapFragment();
			break;
		case 3:
			fragment = new FeaturedFragment();
			break;
		case 4:
			fragment = new FavoriteFragment();
			break;
		case 5:
			fragment = new NewsFragment();
			break;

		case 7:
			UserAccessSession session = UserAccessSession.getInstance(this);
			if(session.getUserSession() == null) {
				Intent i = new Intent(MainActivity.this, RegisterActivity.class);
				startActivity(i);
			}
			else { 
				Intent i = new Intent(this, ProfileActivity.class);
				startActivity(i);
			}

			break;

		case 8:
			Intent i = new Intent(this, LoginActivity.class);
			this.startActivity(i);
			break;

		case 10:
			fragment = new AboutUsFragment();
			break;

		default:
			break;
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		//        setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

		if(currFragment != null && fragment != null) {
			boolean result = fragment.getClass().equals(currFragment.getClass());
			if(result)
				return;
		}

		if (fragment != null) {

			if(fragment instanceof MapFragment && position==2) {
				currFragment = fragment;
				Handler h = new Handler();
				h.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						fm.beginTransaction().replace(R.id.frame_container, currFragment).commitAllowingStateLoss();
					}
				}, Config.DELAY_SHOW_ANIMATION + 200);
			}
			else {

				Log.d("tag","Fragment s");
				currFragment = fragment;
				fm.beginTransaction().replace(R.id.frame_container, currFragment).commitAllowingStateLoss();
			}

		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void updateMenuList() {

		UserAccessSession accessSession = UserAccessSession.getInstance(this);
		UserSession userSession = accessSession.getUserSession();

		if(userSession == null) {
			MENUS = UIConfig.MENUS_NOT_LOGGED;
		}
		else {
			MENUS = UIConfig.MENUS_LOGGED;
		}

		showList();
	}

	public void showList() {

		MGListAdapter adapter = new MGListAdapter(this, MENUS.length, R.layout.menu_entry);

		adapter.setOnMGListAdapterAdapterListener(new OnMGListAdapterAdapterListener() {

			@Override
			public void OnMGListAdapterAdapterCreated(MGListAdapter adapter, View v,
					int position, ViewGroup viewGroup) {
				// TODO Auto-generated method stub

				FrameLayout frameCategory = (FrameLayout) v.findViewById(R.id.frameCategory);
				FrameLayout frameHeader = (FrameLayout) v.findViewById(R.id.frameHeader);

				frameCategory.setVisibility(View.GONE);
				frameHeader.setVisibility(View.GONE);

				Menu menu = MENUS[position];

				if(menu.getHeaderType() == HeaderType.HeaderType_CATEGORY) {
					frameCategory.setVisibility(View.VISIBLE);
					Spanned title = Html.fromHtml(MainActivity.this.getResources().getString(menu.getMenuResTitle()));
					TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
					tvTitle.setText(title);

					ImageView imgViewIcon = (ImageView) v.findViewById(R.id.imgViewIcon);
					imgViewIcon.setImageResource(menu.getMenuResIconSelected());
				}
				else {
					frameHeader.setVisibility(View.VISIBLE);

					Spanned title = Html.fromHtml(MainActivity.this.getResources().getString(menu.getMenuResTitle()));
					TextView tvTitleHeader = (TextView) v.findViewById(R.id.tvTitleHeader);
					tvTitleHeader.setText(title);
				}
			}
		});
		mDrawerList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}



	// ====================================================================================
	// ====================================================================================
	// ====================================================================================



	public Queries getQueries() {	
		return q;
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}


	OnSocialAuthenticationListener mCallback;
	OnActivityResultListener mCallbackActivityResult;
	private static TwitterApp mTwitter;
	private AdView adView;
	private Session.StatusCallback statusCallback;

	public static TwitterApp getTwitterAppInstance() {

		return mTwitter;
	}

	@Override
	public void onStart()  {
		super.onStart();

		if(Session.getActiveSession() != null)
			Session.getActiveSession().addCallback(statusCallback);

		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();

		if(Session.getActiveSession() != null)
			Session.getActiveSession().removeCallback(statusCallback);

		// After disconnect() is called, the client is considered "dead".
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(mCallbackActivityResult != null) {
			mCallbackActivityResult.onActivityResultCallback(this, requestCode, resultCode, data);
		}
		else {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	}

	// ###############################################################################################
	// FACEBOOK INTEGRATION METHODS
	// ###############################################################################################
	public void loginToFacebook(Bundle savedInstanceState) {
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();

		if (session == null) {

			session = new Session(this);
			Session.setActiveSession(session);
		}

		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
			.setPermissions(Arrays.asList("public_profile", "email"))
			.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}

		updateView();
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			//           URL_PREFIX_FRIENDS + session.getAccessToken();
			getUsername(session);

		} 
		else {
			//         	onClickLogin();
		}
	}

	//     private void onClickLogin() {
	//         Session session = Session.getActiveSession();
	//         if (!session.isOpened() && !session.isClosed()) {
	//             session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	//         } else  {
	//             Session.openActiveSession(this, true, Arrays.asList("user_likes", "user_status"), statusCallback);
	//         }
	//     }

	public void onClickLogout() {
		Session session = Session.getActiveSession();
		if (session != null && !session.isClosed()) {
			session.closeAndClearTokenInformation();
		}

	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			updateView();
		}
	}

	private void getUsername(final Session session) {
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						// Set the id for the ProfilePictureView
						// view that in turn displays the profile picture.
						Log.e("FACEBOOK USERNAME**", user.getName());
						Log.e("FACEBOOK ID**", user.getId());
						Log.e("FACEBOOK EMAIL**", ""+user.asMap().get("email"));

						if(mCallback != null) {
							mCallback.socialAuthenticationFacebookCompleted(
									MainActivity.this, 
									user, 
									response);
						}


						//     	            	facebookSession.storeAccessToken(session, user.getName());
						//     	            	grantApplication();

					}
				}

				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}

		});
		request.executeAsync();
	}

	public void getDebugKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", "------------------------------------------");
				Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				Log.e("KeyHash:", "------------------------------------------");
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}


	// ###############################################################################################
	// TWITTER INTEGRATION METHODS
	// ###############################################################################################
	public void loginToTwitter() {
		if (mTwitter.hasAccessToken() == true) {
			try {
				// 				grantApplication();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		} 
		else {
			mTwitter.loginToTwitter();
		}
	}


	public TwitterApp getTwitterApp() {
		return mTwitter;
	}

	TwitterAppListener twitterAppListener = new TwitterAppListener() {

		@Override
		public void onError(String value)  {
			// TODO Auto-generated method stub
			Log.e("TWITTER ERROR**", value);
		}

		@Override
		public void onComplete(AccessToken accessToken) {
			// TODO Auto-generated method stub
			// 			grantApplication();
		}
	};

	public boolean isLoggedInToFacebook() {
		Session session = Session.getActiveSession();
		return (session != null && session.isOpened());
	}

	// LISTENERS
	public interface OnSocialAuthenticationListener {
		public void socialAuthenticationFacebookCompleted(
				Activity activity, GraphUser user, Response response);
	}

	public void setOnSocialAuthenticationListener(OnSocialAuthenticationListener listener) {
		try {
			mCallback = (OnSocialAuthenticationListener) listener;
		} catch (ClassCastException e)  {
			throw new ClassCastException(this.toString() + " must implement OnSocialAuthenticationListener");
		}
	}


	public interface OnActivityResultListener {
		public void onActivityResultCallback(
				Activity activity, int requestCode, int resultCode, Intent data);
	}

	public void setOnActivityResultListener(OnActivityResultListener listener) {
		try {
			mCallbackActivityResult = (OnActivityResultListener) listener;
		} catch (ClassCastException e)  {
			throw new ClassCastException(this.toString() + " must implement OnActivityResultListener");
		}
	}



	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG, "Google Play Service available.");

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an	 error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
			dialog.show();

			return false;
		}
	}


	@SuppressLint("NewApi")
	public void getAddress(View v) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(this, "No Geocoder available", Toast.LENGTH_LONG).show();
			return;
		}

		if (servicesConnected()) {
			// Get the current location
			//            Location currentLocation = mLocationClient.getLastLocation();
			// Turn the indefinite activity indicator on
			//            mActivityIndicator.setVisibility(View.VISIBLE);

			// Start the background task
			//            (new MainActivity3.GetAddressTask(this)).execute(currentLocation);
		}
	}


	@Override
	public void onResume() {
		super.onResume();

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);


			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}

		registerReceiver(mReceiver, new IntentFilter(Config.RECIEVER));
		
		registerReceiver(mReceiver2, new IntentFilter(Config.RECIEVER2));

	}

	@Override
	public void onPause() {

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
		mEditor.commit();
		unregisterReceiver(mReceiver);
		unregisterReceiver(mReceiver2);
		super.onPause();
	}

	public void showAds() {

		FrameLayout frameAds = (FrameLayout) findViewById(R.id.frameAds);
		if(Config.WILL_SHOW_ADS) {

			frameAds.setVisibility(View.VISIBLE);

			// Create an ad.
			if(adView == null) {
				adView = new AdView(this);
				adView.setAdSize(AdSize.SMART_BANNER);
				adView.setAdUnitId(Config.BANNER_UNIT_ID);

				// Add the AdView to the view hierarchy. The view will have no size
				// until the ad is loaded.            
				frameAds.addView(adView);

				// Create an ad request. Check logcat output for the hashed device ID to
				// get test ads on a physical device.
				Builder builder = new AdRequest.Builder();

				if(Config.TEST_ADS_USING_EMULATOR)
					builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

				if(Config.TEST_ADS_USING_TESTING_DEVICE)
					builder.addTestDevice(Config.TESTING_DEVICE_HASH);

				AdRequest adRequest = builder.build();
				// Start loading the ad in the background.
				adView.loadAd(adRequest);
			}
		}
		else {
			frameAds.setVisibility(View.GONE);
		}
	}


	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		
		@Override
		protected String doInBackground(Location... params) {
			
			String addressStr ="";
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			List <Address> addresses = null;

			try {

				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1
						);
			} catch (IOException exception1) {
				exception1.printStackTrace();
			} catch (IllegalArgumentException exception2) {
				exception2.printStackTrace();
			}
			
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				address = addresses;

				Address address = MainActivity.address.get(0);

				String locality = address.getLocality();
				String countryName = address.getCountryName();

				addressStr = String.format("%s, %s", locality, countryName);
				Log.e("Location LOG", addressStr);

				// If there aren't any addresses, post a message
			}

			return addressStr;
		}

		@Override
		protected void onPostExecute(String address) {
		}
	}


	@Override
	public void onBackPressed() {
	openDialog();
	}



	private void openDialog() 		{
		AlertDialog.Builder ab=new AlertDialog.Builder(this);
		
		ab.setTitle("Vegogo ");
		ab.setMessage("Do you want to exit?");

		ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface di, int arg1) {
				di.dismiss();
				MainActivity.this.finish();
			}
		});

		ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface di, int arg1) {
				di.dismiss();
			}
		});

		ab.show();
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {

		// TODO Auto-generated method stub
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000); // Update location every second
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with the error.
			//            showErrorDialog(connectionResult.getErrorCode());
		}
	}


	public double haverSineDistance(double lat1, double lat2, double lon1, double lon2){
		/*PRE: All the input values are in radians!*/

		double R = 6371; // Radius of the earth in km
		double dLat = deg2rad(lat2-lat1);  // deg2rad below
		double dLon = deg2rad(lon2-lon1); 
		double a = 
				Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
				Math.sin(dLon/2) * Math.sin(dLon/2)
				; 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c; // Distance in km
		return d;

	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification(Store store,String str,int notifyId) {
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("store",store);
		PendingIntent pIntent = PendingIntent.getActivity(this, notifyId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(this)
		.setContentTitle(str)
		.setContentText("Now less than 50 meters away").setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent)
		.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(notifyId, noti);

	}
	
	double deg2rad(double deg) {
		return deg * (Math.PI/180);
	}
}
