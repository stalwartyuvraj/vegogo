package com.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.vegogo.LocationService;
import com.app.vegogo.MainActivity;
import com.app.vegogo.R;
import com.asynctask.MGAsyncTask;
import com.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.config.Config;
import com.dataparser.DataParser;
import com.db.DbHelper;
import com.db.Queries;
import com.fragments.activity.SearchResultActivity;
import com.google.gson.Gson;
import com.models.Category;
import com.models.Criteria;
import com.models.Data;
import com.models.DataNews;
import com.models.News;
import com.models.Photo;
import com.models.Store;
import com.models.StoreCriteria;
import com.utilities.MGUtilities;

public class SearchFragment extends Fragment implements OnClickListener{

	private View viewInflate;
	private EditText txtKeywords;
	private SeekBar seekbarRadius;
	private Spinner spinnerCategories;
	private ToggleButton toggleButtonNearby;
	private ImageButton btVegan,btVeg,btRaw,btGluten,btChild,btWifi,btList,btMap;
	private Button btSerarch;
	private Button btKm,btMiles;
	boolean flagKm=false;
	int selCriteria=1;
	ArrayList<Store> storeList,storesSorted;
	ArrayList<News> newsList;
	boolean isMap=false;
	boolean isVegan=false,isVege=false,isRaw=false,isGluten=false,isChild=false,isWifi=false;
	FragmentManager fm;
	int c1,c2;
	MGAsyncTask task;
	Queries q;
	MainActivity main;
	ArrayList<Integer> arrSels;
	DbHelper dbHelper;
	ArrayList<String> categories;
	ArrayAdapter<String> dataAdapter;

	public SearchFragment() { 
	}

	@SuppressLint("InflateParams") 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		viewInflate = inflater.inflate(R.layout.fragment_search, null);

		fm=getActivity().getSupportFragmentManager();
		return viewInflate;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void regsViews(View viewInflate2) {

		btVeg=(ImageButton)viewInflate.findViewById(R.id.btSrchVeg);
		btVegan=(ImageButton)viewInflate.findViewById(R.id.btSrchVegan);
		btChild=(ImageButton)viewInflate.findViewById(R.id.btSrchChild);
		btRaw=(ImageButton)viewInflate.findViewById(R.id.btSrchRaw);
		btGluten=(ImageButton)viewInflate.findViewById(R.id.btSrchgluten);
		btWifi=(ImageButton)viewInflate.findViewById(R.id.btSrchWifi);	

		btList=(ImageButton)viewInflate.findViewById(R.id.btSrchList);
		btMap=(ImageButton)viewInflate.findViewById(R.id.btSrchMap);


		btKm=(Button)viewInflate.findViewById(R.id.btSKm);
		btMiles=(Button)viewInflate.findViewById(R.id.btSM);

		btList.setOnClickListener(this);
		btMap.setOnClickListener(this);

		btKm.setOnClickListener(this);
		btMiles.setOnClickListener(this);
		btVeg.setOnClickListener(this);
		btVegan.setOnClickListener(this);
		btGluten.setOnClickListener(this);
		btWifi.setOnClickListener(this);
		btChild.setOnClickListener(this);
		btRaw.setOnClickListener(this);

		c1=getResources().getColor(R.color.green_color);
		c2=getResources().getColor(R.color.cream_color);

		btList.setBackgroundResource(R.drawable.list_sel);

		arrSels=new ArrayList<Integer>();
	}


	public void exportDatabse(String databaseName) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {

				String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+databaseName+"";
				String backupDBPath = "resturant.db";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}

			}
		} catch (Exception e) { e.printStackTrace();}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		Handler h1 = new Handler();
		h1.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData();
			}
		}, 100);

		main = (MainActivity) this.getActivity();

		q = Queries.getInstance(DbHelper.getInstance(main));

		exportDatabse("restaurant_db");
		
		regsViews(viewInflate);

		btSerarch  = (Button) viewInflate.findViewById(R.id.btnSearch);

		btSerarch.setOnClickListener(this);

		txtKeywords = (EditText) viewInflate.findViewById(R.id.txtKeywords);
		txtKeywords.setHintTextColor(getResources().getColor(R.color.cream_color));
		toggleButtonNearby = (ToggleButton) viewInflate.findViewById(R.id.toggleButtonNearby);
		toggleButtonNearby.setOnClickListener(this);

		final TextView tvRadiusText = (TextView) viewInflate.findViewById(R.id.tvRadiusText);

		tvRadiusText.setText("Radius: 10 miles");
		seekbarRadius = (SeekBar) viewInflate.findViewById(R.id.seekbarRadius);
		seekbarRadius.setMax(Config.MAX_SEARCH_RADIUS);
		seekbarRadius.setProgress(6);
		seekbarRadius.setEnabled(true);
		seekbarRadius.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@SuppressLint("DefaultLocale") 
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				double m=progress;
				String unit=MGUtilities.getStringFromResource(getActivity(), R.string.km);
				if(!flagKm)
				{
					m=0.62137*progress;
					m=Math.floor(m);
					unit=MGUtilities.getStringFromResource(getActivity(), R.string.miles);
				}

				String strSeekVal = String.format("%s: %.0f %s",MGUtilities.getStringFromResource(getActivity(), R.string.radius),m,unit);

				tvRadiusText.setText(strSeekVal);
			}
		});

		categories=new ArrayList<String>();

		categories= q.getCategoryNames();

		Log.d("tag","Size cat "+categories.size());

		String allCategories = this.getActivity().getResources().getString(R.string.all_categories);
		categories.add(0, allCategories);

		dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_style, categories);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerCategories = (Spinner)viewInflate.findViewById(R.id.spinnerCategories);
		spinnerCategories.setAdapter(dataAdapter);
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch(v.getId()) {

		case R.id.btSrchMap:{
			isMap=true;
			btMap.setBackgroundResource(R.drawable.map_sel);
			btList.setBackgroundResource(R.drawable.list);
		}

		break;
		case R.id.btSrchList:
		{
			isMap=false;
			btMap.setBackgroundResource(R.drawable.map);
			btList.setBackgroundResource(R.drawable.list_sel);
		}
		break;

		case R.id.btSKm:{
			flagKm=true;
			btKm.setBackgroundResource(R.drawable.check_search);
			btMiles.setBackgroundResource(R.drawable.uncheck_search);
		}
		break;

		case R.id.btSM:{
			flagKm=false;
			btMiles.setBackgroundResource(R.drawable.check_search);
			btKm.setBackgroundResource(R.drawable.uncheck_search);
		}
		break;

		case R.id.toggleButtonNearby:
			if(toggleButtonNearby.isChecked()) {
				
				seekbarRadius.setEnabled(true);
			}
			else
				seekbarRadius.setEnabled(false);

			break;

		case R.id.btnSearch:
			asyncSearch();
			break;

		case R.id.btSrchVegan:

			selCriteria=1;
			if(isVegan){
				isVegan=false;
				btVegan.setBackgroundResource(R.drawable.vegan);	
			}
			else{
				isVegan=true;
				btVegan.setBackgroundResource(R.drawable.vegan_sel);
			}

			break;

		case R.id.btSrchVeg:
			selCriteria=2;
			if(isVege){
				isVege=false;
				btVeg.setBackgroundResource(R.drawable.vege);

			}
			else{
				isVege=true;
				btVeg.setBackgroundResource(R.drawable.vege_sel);
			}
			break;	

		case R.id.btSrchRaw:
			selCriteria=3;

			if(isRaw){
				isRaw=false;
				btRaw.setBackgroundResource(R.drawable.raw);	
			}
			else{
				isRaw=true;
				btRaw.setBackgroundResource(R.drawable.raw_sel);
			}

			break;

		case R.id.btSrchgluten:
			selCriteria=4;
			if(isGluten)
			{
				isGluten=false;
				btGluten.setBackgroundResource(R.drawable.gluten);
			}else{
				isGluten=true;
				btGluten.setBackgroundResource(R.drawable.gluten_sel);
			}

			break;

		case R.id.btSrchChild:
			selCriteria=5;
			if(isChild)
			{
				isChild=false;
				btChild.setBackgroundResource(R.drawable.child_friendly);
			}else
			{
				isChild=true;
				btChild.setBackgroundResource(R.drawable.child_friendly_sel);
			}

			break;

		case R.id.btSrchWifi:
			selCriteria=6;
			if(isWifi)
			{
				isWifi=false;
				btWifi.setBackgroundResource(R.drawable.wifi);
			}else{
				isWifi=true;
				btWifi.setBackgroundResource(R.drawable.wifi_sel);
			}

			break;
		}

		Config.CRITERIA_ID=selCriteria;
	}






	private void asyncSearch() {

		MGAsyncTask task = new MGAsyncTask(getActivity());
		task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {

			ArrayList<Store> arrayFilter;

			@Override
			public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				asyncTask.dialog.hide();
				main.showSwipeProgress();

			}

			@Override
			public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub

				main.hideSwipeProgress();

				if(arrayFilter != null && arrayFilter.size() == 0) {
					MGUtilities.showNotifier(SearchFragment.this.getActivity(), MainActivity.offsetY);
					return;
				}

				if(isMap){
					Config.IS_MAP=isMap;
					Config.arrFilter=arrayFilter;
					((MainActivity)getActivity()).displayView(2,isMap);
					//fm.beginTransaction().replace(R.id.frame_container, new MapFragment()).commitAllowingStateLoss();
				}
				else{
					Intent i = new Intent(getActivity(), SearchResultActivity.class);
					i.putExtra("searchResults", arrayFilter);
					getActivity().startActivity(i);
				}

			}

			@Override
			public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				arrayFilter = search();
			}
		});
		task.execute();
	}

	@SuppressLint("DefaultLocale") 
	private ArrayList<Store> search() {

		q.countCriteria();
		String strKeywords = txtKeywords.getText().toString().trim();

		int radius = seekbarRadius.getProgress();
		String category = spinnerCategories.getSelectedItem().toString();

		int countParams = strKeywords.length() > 0 ? 1 : 0;
		countParams += radius > 0 && toggleButtonNearby.isChecked() ? 1 : 0;
		arrSels.clear();

		if(isVegan)
			arrSels.add(new Integer(1));
		if(isVege)
			arrSels.add(new Integer(2));
		if(isRaw)
			arrSels.add(new Integer(3));
		if(isGluten)
			arrSels.add(new Integer(4));
		if(isChild)
			arrSels.add(new Integer(5));
		if(isWifi)
			arrSels.add(new Integer(6));

		Config.arrSelCriterias=arrSels;
		int pos=0;
		if(spinnerCategories.getSelectedItemPosition()!=0){
			Category cat=q.getCategoryByCategory(category);

			pos=cat.getCategory_id();
		}


		Config.SEL_CATEGORY_ID=pos;
		ArrayList<Store> arrayStores;
		if(arrSels.size()>0)
			arrayStores = q.getStoresByCriteria2(arrSels,pos);
		else
			arrayStores=q.getStores(pos);

		Log.d("tag", "Size Arr Stors "+arrayStores.size()+" Sels Size "+arrSels.size()+"  Cat ID "+pos);

		arrayStores=sortStoresByDistance(arrayStores);

		if(strKeywords.length() <=0 && !toggleButtonNearby.isChecked())
			return arrayStores;

		ArrayList<Store> arrayFilter = new ArrayList<Store>();
		for(Store store : arrayStores) {

			int qualifyCount = 0;
			boolean matched=false;

			boolean isFoundKeyword = store.getStore_name().toLowerCase().contains(strKeywords) || store.getStore_address().toLowerCase().contains(strKeywords);

			if( strKeywords.length() > 0  && isFoundKeyword)
			{
				qualifyCount += 1;
				matched=true;
			}


			store.setDistance(-1);

			if(toggleButtonNearby.isChecked()) {

				Log.d("tag","Toggle Checked");

				if(MainActivity.location != null) {
					Location locStore = new Location("Store");
					locStore.setLatitude(store.getLat());
					locStore.setLongitude(store.getLon());

					Log.d("tag","Latlng "+MainActivity.location.getLatitude()+" : "+MainActivity.location.getLongitude());

					double distance = locStore.distanceTo(MainActivity.location) / 1000;

					Log.d("tag12","Distance "+distance+" : "+radius);

					store.setDistance(distance);

					if(distance <= radius){
						qualifyCount += 1;
						matched=true;
					}


				}
			}else
				Log.d("tag","Toggle UnChecked");


			if(qualifyCount == countParams){
				if(matched)
					arrayFilter.add(store);
			}
		}
		return arrayFilter;
	}


	private ArrayList<Store> sortStoresByDistance(ArrayList<Store> arrStores) {

		for(Store store:arrStores)
		{	
			if(MainActivity.location != null) {

				Location locStore = new Location("Store");
				locStore.setLatitude(store.getLat());
				locStore.setLongitude(store.getLon());

				Log.d("tag","Latlng "+MainActivity.location.getLatitude()+" : "+MainActivity.location.getLongitude());

				double distance = locStore.distanceTo(MainActivity.location) / 1000;
				store.setDistance(distance);

			}
		}

		storesSorted=arrStores;
		if(arrStores.size()>1)
			quickSort(0, storesSorted.size()-1);

		return storesSorted;
		//traverseArray(storesSorted);

	}




	private void traverseArray(ArrayList<Store> ss) {
		for(int i=0;i<ss.size();i++)
		{
			Log.d("tag123","D"+i+" : "+ss.get(i).getDistance());


		}
	}

	void quickSort(int l,int h)
	{
		int i=l,j=h;
		Store pivot=storesSorted.get(l+(h-l)/2);

		while (i <= j) {
			while (storesSorted.get(i).getDistance() < pivot.getDistance()) {
				i++;
			}
			while (storesSorted.get(j).getDistance() > pivot.getDistance()) {
				j--;
			}

			if(i<=j)
			{
				Store temp=storesSorted.get(i);
				storesSorted.set(i,storesSorted.get(j));
				storesSorted.set(j, temp);
				i++;
				j--;
			}
		}

		if (l < j)
			quickSort(l, j);
		if (i < h)
			quickSort(i, h);

	}


	public void getData() {

		task = new MGAsyncTask(getActivity());
		task.setMGAsyncTaskListener(new OnMGAsyncTaskListener() {

			@Override
			public void onAsyncTaskProgressUpdate(MGAsyncTask asyncTask) { }

			@Override
			public void onAsyncTaskPreExecute(MGAsyncTask asyncTask) {
				asyncTask.dialog.hide();
			}

			@Override
			public void onAsyncTaskPostExecute(MGAsyncTask asyncTask) {
				storeList = q.getStoresFeatured();
				newsList = q.getNews();
				main.hideSwipeProgress();

				if(categories.size()<=1)
				{				
					categories=q.getCategoryNames();
					String allCategories = getActivity().getResources().getString(R.string.all_categories);
					categories.add(0, allCategories);
					dataAdapter.clear();
					dataAdapter.addAll(categories);
				}

			}

			@Override
			public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				try {
					DataParser parser = new DataParser();
					Data data = parser.getData(Config.DATA_JSON_URL);
					DataNews dataNews = parser.getDataNews(Config.DATA_NEWS_URL);

					if(main == null)
						return;

					if(data == null)
						return;
					else{
						Log.d("tag", "Store count =" + data.getStores().size());
						Log.d("tag", "Categories count =" + data.getCategories().size());
						Log.d("tag", "Criteria count =" + data.getCriteria().size());
						Log.d("tag", "Store Cri count =" + data.getStoreCriterias().size());
					}

					if(data.getCategories() != null && data.getCategories().size() > 0) {						
						q.deleteTable("categories");
						for(Category cat : data.getCategories()) {
							q.insertCategory(cat);
						}
						Log.e("HOME FRAGMENT LOG", "Categories count =" + data.getCategories().size());
					}

					if(data.getPhotos() != null && data.getPhotos().size() > 0) {
						q.deleteTable("photos");
						for(Photo photo : data.getPhotos()) {
							q.insertPhoto(photo);
						}
					}

					if(data.getStores() != null && data.getStores().size() > 0) {

						q.deleteTable("stores");
						for(Store store : data.getStores()) {
							q.insertStore(store);
						}
						Log.e("tag", "Store count =" + data.getStores().size());
					}

					if(dataNews.getNews() != null && dataNews.getNews().size() > 0) {

						q.deleteTable("news");
						for(News news : dataNews.getNews()) {
							q.insertNews(news);
						}
						Log.d("tag", "News count =" + dataNews.getNews().size());
					}

					if(data.getCriteria() !=null && data.getCriteria().size()>0)
					{
						q.deleteTable("criteria");
						for(Criteria criteria : data.getCriteria()) {
							q.insertCriteria(criteria);
						}
						Log.d("tag", "Criteria Count =" + data.getCriteria().size());

					}

					if(data.getStoreCriterias() !=null && data.getStoreCriterias().size()>0)
					{
						q.deleteTable("store_criteria");
						for(StoreCriteria criteria : data.getStoreCriterias()) {
							q.insertStoreCriteria(criteria);
						}

						Log.d("tag", "Store count =" + data.getStoreCriterias().size());
					}

					LocationService.arrStores=q.getStores(0);
					
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		task.execute();
		
	}
		
		
		
	}

	
	

