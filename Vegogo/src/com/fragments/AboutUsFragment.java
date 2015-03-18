package com.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.app.vegogo.R;

import com.config.Config;
import com.utilities.MGUtilities;

public class AboutUsFragment extends Fragment implements OnClickListener{

	private View viewInflate;
	private TextView tvFoot,tvCenter;

	public AboutUsFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		viewInflate = inflater.inflate(R.layout.fragment_about_us, null);

		tvFoot=(TextView)viewInflate.findViewById(R.id.tvAboutFoot);
		tvCenter=(TextView)viewInflate.findViewById(R.id.tvAboutCenter);

		tvCenter.setText(Html.fromHtml(getString(R.string.aboutus_cent)));
		tvFoot.setText(Html.fromHtml(getString(R.string.aboutus_foot)));
		
		tvCenter.setMovementMethod(LinkMovementMethod.getInstance());
		tvFoot.setMovementMethod(LinkMovementMethod.getInstance());
		
		return viewInflate;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		Button btnContactUs = (Button) viewInflate.findViewById(R.id.btnContactUs);
		btnContactUs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				web();
			}
		});
	}

	@Override
	public void onClick(View v) { }


	private void web()
	{
		Intent wi = new Intent(Intent.ACTION_VIEW);
		wi.setData(Uri.parse("http://vegogo.co.uk"));
		getActivity().startActivity(wi);
	}

	private void email() {

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ Config.ABOUT_US_EMAIL } );	

		emailIntent.putExtra(Intent.EXTRA_SUBJECT, 
				MGUtilities.getStringFromResource(getActivity(), R.string.email_subject_company) );

		emailIntent.putExtra(Intent.EXTRA_TEXT, 
				MGUtilities.getStringFromResource(getActivity(), R.string.email_body_company) );
		emailIntent.setType("message/rfc822");

		getActivity().startActivity(Intent.createChooser(emailIntent, 
				MGUtilities.getStringFromResource(getActivity(), R.string.choose_email_client)) );
	}
}
