package com.fragments;

import java.util.ArrayList;

import com.app.vegogo.MainActivity;
import com.asynctask.MGAsyncTask;
import com.asynctask.MGAsyncTask.OnMGAsyncTaskListener;
import com.config.Config;
import com.dataparser.DataParser;
import com.db.Queries;
import com.models.Category;
import com.models.Criteria;
import com.models.Data;
import com.models.DataNews;
import com.models.News;
import com.models.Photo;
import com.models.Store;
import com.models.StoreCriteria;
import com.app.vegogo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {
	
	private View viewInflate;
	private Activity act;
	MGAsyncTask task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		viewInflate = inflater.inflate(R.layout.fragment_splash, null);
		
		return viewInflate;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		
	}
	   @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		act=activity;
	
	   }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
		
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				MainActivity act = (MainActivity)getActivity();
				if(act!=null)
				act.showMainView();
				else
				{	
					MainActivity act2= (MainActivity)act;
				if(act2!=null);
				act2.showMainView();
				}
			}
		}, 3000);
	}
	



}

