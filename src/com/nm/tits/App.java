package com.nm.tits;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
//import com.bugsense.trace.BugSenseHandler;
//
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class App extends Application
{
	public static final String DATA_IMAGES_DIR = "HottieHello";
	public static final String SERVER_URL = "http://www.hottiehello.com:8080/ABAK/rest/";
	public static final String USER_AGENT_NAME = "HH-A-Native";
	public static final String APP_NAME = "Hottie Hello";
	public static final String GA_CODE = "UA-31703028-1";

	private static Context mContext;
	private static GetData gd;
	public static GoogleAnalyticsTracker tracker;
	
	private static final String APP_SHARED_PREFS = "com.nm.hh_preferences";
	public static SharedPreferences appSharedPrefs;
	public static Editor prefsEditor;

	@Override
	public void onCreate()
	{

		super.onCreate();
		String version = "100";
		try
		{
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{

		}
		appSharedPrefs = getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		prefsEditor = appSharedPrefs.edit();
		mContext = this;
		gd = new GetData();
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(App.GA_CODE, 20, this);

		App.tracker.trackEvent(App.APP_NAME + "App", // Category
				"start", // Action
				"Android " + version, // Label
				0); // Value
	}

	public static Context getContext()
	{
		return mContext;
	}

	public static GetData getGetData()
	{
		return gd;
	}
}
