package com.fragments;

import java.util.ArrayList;
import com.adapters.MGListAdapter;
import com.adapters.MGListAdapter.OnMGListAdapterAdapterListener;
import com.app.vegogo.MainActivity;
import com.asynctask.MGAsyncTask;
import com.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.config.Config;
import com.config.UIConfig;
import com.dataparser.DataParser;
import com.db.Queries;
import com.fragments.activity.DetailActivity;
import com.fragments.activity.NewsDetailActivity;
import com.helpers.DateTimeHelper;
import com.imageview.MGImageView;
import com.models.Category;
import com.models.Criteria;
import com.models.Data;
import com.models.DataNews;
import com.models.News;
import com.models.Photo;
import com.models.Store;
import com.models.StoreCriteria;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.app.vegogo.R;
import com.slider.MGSlider;
import com.slider.MGSlider.OnMGSliderListener;
import com.slider.MGSliderAdapter;
import com.slider.MGSliderAdapter.OnMGSliderAdapterListener;
import com.utilities.MGUtilities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnItemClickListener, OnClickListener {
	
	private View viewInflate;
	DisplayImageOptions options;
	ArrayList<Store> storeList;
	ArrayList<News> newsList;
	MGAsyncTask task;
	
	public HomeFragment() { }
	
	@SuppressLint("InflateParams") 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		viewInflate = inflater.inflate(R.layout.fragment_home, null);
		return viewInflate;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(task != null)
			task.cancel(true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(UIConfig.SLIDER_PLACEHOLDER)
			.showImageForEmptyUri(UIConfig.SLIDER_PLACEHOLDER)
			.showImageOnFail(UIConfig.SLIDER_PLACEHOLDER)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		
		MainActivity main = (MainActivity) getActivity();
		main.showSwipeProgress();
		main.getDebugKey();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				getData();
			}
		}, Config.DELAY_SHOW_ANIMATION);
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
				// TODO Auto-generated method stub
				MainActivity main = (MainActivity) getActivity();
				Queries q = main.getQueries();
				storeList = q.getStoresFeatured();
				newsList = q.getNews();
				
				createSlider();
				showList();
				

				main.hideSwipeProgress();
			}
			
			@Override
			public void onAsyncTaskDoInBackground(MGAsyncTask asyncTask) {
				// TODO Auto-generated method stub
				try {
					DataParser parser = new DataParser();
					Data data = parser.getData(Config.DATA_JSON_URL);
					DataNews dataNews = parser.getDataNews(Config.DATA_NEWS_URL);
					
					MainActivity main = (MainActivity) getActivity();
					
					if(main == null)
						return;
					
					Queries q = main.getQueries();
					
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
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		task.execute();
	
	}
	
	@Override
    public void onDestroyView()  {
        super.onDestroyView();
        if (viewInflate != null) {
            ViewGroup parentViewGroup = (ViewGroup) viewInflate.getParent();
            if (parentViewGroup != null) {
            	
            	MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
        		slider.pauseSliderAnimation();
        		
                parentViewGroup.removeAllViews();
            }
        }
    }
	
	private void showList() {
		
		ListView listView = (ListView) viewInflate.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		MGListAdapter adapter = new MGListAdapter(
				getActivity(), newsList.size(), R.layout.news_entry);
		
		adapter.setOnMGListAdapterAdapterListener(new OnMGListAdapterAdapterListener() {
			
			@Override
			public void OnMGListAdapterAdapterCreated(MGListAdapter adapter, View v,
					int position, ViewGroup viewGroup) {
				// TODO Auto-generated method stub
				
				News news = newsList.get(position);
				
				MGImageView imgViewPhoto = (MGImageView) v.findViewById(R.id.imgViewPhoto);
				imgViewPhoto.setCornerRadius(0.0f);
				imgViewPhoto.setBorderWidth(UIConfig.BORDER_WIDTH);
				imgViewPhoto.setBorderColor(getResources().getColor(UIConfig.THEME_BLACK_COLOR));
				
				if(news.getPhoto_url() != null) {
					MainActivity.getImageLoader().displayImage(news.getPhoto_url(), imgViewPhoto, options);
				}
				else {
					MainActivity.getImageLoader().displayImage(null, imgViewPhoto, options);
				}
				
				imgViewPhoto.setTag(position);
				
				Spanned name = Html.fromHtml(news.getNews_title());
				Spanned address = Html.fromHtml(news.getNews_content());
				
				TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
				tvTitle.setText(name);
				
				TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
				tvSubtitle.setText(address);
				
				String date = DateTimeHelper.getStringDateFromTimeStamp(news.getCreated_at(), "MM/dd/yyyy" );
				TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
				tvDate.setText(date);
			}
		});
		listView.setAdapter(adapter);
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long resId) {
		// TODO Auto-generated method stub
		
		MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
		slider.stopSliderAnimation();
		
		News news = newsList.get(pos);
		Intent i = new Intent(getActivity(), NewsDetailActivity.class);
		i.putExtra("news", news);
		getActivity().startActivity(i);
	}
	
	// Create Slider
	private void createSlider() {
		
		if(storeList != null && storeList.size() == 0 && newsList != null && newsList.size() == 0) {
			MGUtilities.showNotifier(this.getActivity(), MainActivity.offsetY, R.string.failed_data);
			return;
		}
		
		final MainActivity main = (MainActivity) getActivity();
		final Queries q = main.getQueries();
		
		MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
		slider.setMaxSliderThumb(storeList.size());
    	MGSliderAdapter adapter = new MGSliderAdapter(
    			R.layout.slider_entry, storeList.size(), storeList.size());
    	
    	adapter.setOnMGSliderAdapterListener(new OnMGSliderAdapterListener() {
			
			@Override
			public void onOnMGSliderAdapterCreated(MGSliderAdapter adapter, View v,
					int position) {
				// TODO Auto-generated method stub
				
				final Store entry = storeList.get(position);
				Photo p = q.getPhotoByStoreId(entry.getStore_id());
				
				ImageView imageViewSlider = (ImageView) v.findViewById(R.id.imageViewSlider);
				
				if(p != null) {
					MainActivity.getImageLoader().displayImage(p.getPhoto_url(), imageViewSlider, options);
				}
				else {
					imageViewSlider.setImageResource(UIConfig.SLIDER_PLACEHOLDER);
				}
				
				imageViewSlider.setTag(position);
				imageViewSlider.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(), DetailActivity.class);
						i.putExtra("store", entry);
						getActivity().startActivity(i);
					}
				});
				
				Spanned name = Html.fromHtml(entry.getStore_name());
				Spanned address = Html.fromHtml(entry.getStore_address());
				
				TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
				tvTitle.setText(name);
				
				TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
				tvSubtitle.setText(address);
			}
		});
    	
    	slider.setOnMGSliderListener(new OnMGSliderListener() {
			
			@Override
			public void onItemThumbSelected(MGSlider slider, ImageView[] buttonPoint,
					ImageView imgView, int pos) { }
			
			@Override
			public void onItemThumbCreated(MGSlider slider, ImageView imgView, int pos) { }
			
			
			@Override
			public void onItemPageScrolled(MGSlider slider, ImageView[] buttonPoint, int pos) { }
			
			@Override
			public void onItemMGSliderToView(MGSlider slider, int pos) { }
			
			@Override
			public void onItemMGSliderViewClick(AdapterView<?> adapterView, View v,
					int pos, long resid) { }

			@Override
			public void onAllItemThumbCreated(MGSlider slider, LinearLayout linearLayout) { }
			
		});
    	
    	slider.setOffscreenPageLimit(storeList.size() - 1);
    	slider.setAdapter(adapter);
    	slider.setActivity(this.getActivity());
    	slider.setSliderAnimation(5000);
    	slider.resumeSliderAnimation();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
		slider.stopSliderAnimation();
		
		switch(v.getId()) { }
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
		slider.resumeSliderAnimation();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MGSlider slider = (MGSlider) viewInflate.findViewById(R.id.slider);
		slider.pauseSliderAnimation();
	}
	
}
