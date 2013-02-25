package com.nm.tits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class ImageActivity extends Activity
{
	String url;
	String country;
	String city;
	Button butShare;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);
		
		butShare = (Button) findViewById(R.id.btnShare);
		butShare.setVisibility(View.VISIBLE);

		Bundle bun = getIntent().getExtras();
		url = bun.getString("url");
		country = bun.getString("country");
		city = bun.getString("city");
		WebView wv = (WebView) findViewById(R.id.webviewImage);
		wv.getSettings().setBuiltInZoomControls(true);
		if (url != null)
		{
			wv.loadUrl(url);
		}
	}
	
	public void onClickBtnShare(View view)
	{
		try
		{
			App.tracker.trackEvent("ImageScreen", // Category
					"share", // Action
					"", // Label
					0); // Value

			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "From: "+ country+"," + city);

			startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
		} catch (Exception e)
		{
			Log.e("ImageActivity.Share()", e.toString());
		}
	}
}
