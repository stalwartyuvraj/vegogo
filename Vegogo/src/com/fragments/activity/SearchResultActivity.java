package com.fragments.activity;

import java.util.ArrayList;
import com.adapters.MGListAdapter;
import com.adapters.MGListAdapter.OnMGListAdapterAdapterListener;
import com.app.vegogo.MainActivity;
import com.config.UIConfig;
import com.db.DbHelper;
import com.db.Queries;
import com.imageview.MGImageView;
import com.models.Photo;
import com.models.Store;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.utilities.MGUtilities;
import com.app.vegogo.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchResultActivity extends FragmentActivity implements OnItemClickListener{

	private ArrayList<Store> arrayData;
	DisplayImageOptions options;
	private Queries q;
	private SQLiteDatabase db;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.fragment_search_result);

		DbHelper dbHelper = DbHelper.getInstance(this);
		q = Queries.getInstance(dbHelper);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		this.getActionBar().setIcon(R.drawable.header_logo);

		this.getActionBar().setTitle("");

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(UIConfig.SLIDER_PLACEHOLDER)
		.showImageForEmptyUri(UIConfig.SLIDER_PLACEHOLDER)
		.showImageOnFail(UIConfig.SLIDER_PLACEHOLDER)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		arrayData =(ArrayList<Store>)this.getIntent().getSerializableExtra("searchResults");

		showList();

		if(arrayData != null && arrayData.size() == 0) {
			MGUtilities.showNotifier(this, MainActivity.offsetY);
			return;
		}
	}

	private void showList() {

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		MGListAdapter adapter = new MGListAdapter(SearchResultActivity.this, arrayData.size(), R.layout.store_entry);

		adapter.setOnMGListAdapterAdapterListener(new OnMGListAdapterAdapterListener() {

			@SuppressLint("DefaultLocale") 
			@Override
			public void OnMGListAdapterAdapterCreated(MGListAdapter adapter, View v,
					int position, ViewGroup viewGroup) {
				// TODO Auto-generated method stub

				final Store store = arrayData.get(position);

				Photo p = q.getPhotoByStoreId(store.getStore_id());

				MGImageView imgViewPhoto = (MGImageView) v.findViewById(R.id.imgViewPhoto);
				imgViewPhoto.setCornerRadius(0.0f);
				imgViewPhoto.setBorderWidth(UIConfig.BORDER_WIDTH);
				imgViewPhoto.setBorderColor(getResources().getColor(UIConfig.THEME_BLACK_COLOR));
				imgViewPhoto.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						Intent i = new Intent(SearchResultActivity.this, DetailActivity.class);
						i.putExtra("store", store);
						SearchResultActivity.this.startActivity(i);

					}
				});

				if(p != null) {
					MainActivity.getImageLoader().displayImage(p.getPhoto_url(), imgViewPhoto, options);
				}
				else {
					imgViewPhoto.setImageResource(UIConfig.SLIDER_PLACEHOLDER);
				}

				Spanned name = Html.fromHtml(store.getStore_name());
				Spanned address = Html.fromHtml(store.getStore_address());
				
				Location locStore = new Location("Store");
				locStore.setLatitude(store.getLat());
				locStore.setLongitude(store.getLon());

				double distance = locStore.distanceTo(MainActivity.location) / 1000;
				
				Spanned dist=Html.fromHtml(String.format("%.2f", distance)+" km");
				TextView tvDist=(TextView)v.findViewById(R.id.tvRowDistance);
				tvDist.setText(dist);
				TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
				tvTitle.setText(name);

				TextView tvSubtitle = (TextView) v.findViewById(R.id.tvSubtitle);
				tvSubtitle.setText(address);

				

				float rating = 0;

				if(store.getRating_total() > 0 && store.getRating_count() > 0)
					rating = store.getRating_total() / store.getRating_count();

				String strRating = String.format("%.2f %s %d %s", 
						rating, 
						SearchResultActivity.this.getResources().getString(R.string.average_based_on),
						store.getRating_count(),
						SearchResultActivity.this.getResources().getString(R.string.rating));

				RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
				ratingBar.setRating(rating);

				TextView tvRatingBarInfo = (TextView) v.findViewById(R.id.tvRatingBarInfo);

				if(rating > 0)
					tvRatingBarInfo.setText(strRating);
				else
					tvRatingBarInfo.setText(
							SearchResultActivity.this.getResources().getString(R.string.no_rating));

				ImageView imgViewFeatured = (ImageView) v.findViewById(R.id.imgViewFeatured);
				imgViewFeatured.setVisibility(View.VISIBLE);

				if(store.getFeatured() == 0)
					imgViewFeatured.setVisibility(View.INVISIBLE);

			}
		});
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int pos, long resId) {
		// TODO Auto-generated method stub
		Store store = arrayData.get(pos);
		//		DetailFragment fragment = new DetailFragment();
		//		Bundle b = new Bundle();
		//		b.putSerializable("store", entry);
		//		fragment.setArguments(b);
		//		
		//		MainActivity main = (MainActivity)this.SearchResultActivity.this;
		//		main.switchContent(fragment, true);

		Intent i = new Intent(SearchResultActivity.this, DetailActivity.class);
		i.putExtra("store", store);
		SearchResultActivity.this.startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		default:
			finish();	
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.menu_default, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		// if nav drawer is opened, hide the action items
		return super.onPrepareOptionsMenu(menu);
	}

}
