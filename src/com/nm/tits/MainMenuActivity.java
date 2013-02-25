package com.nm.tits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import com.nm.tits.RateActivity.BackgroundGetData;
import com.nm.utils.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity
{
	String country = "";
	String city = "";
	Spinner spin;
	EditText etCity;
	ArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		country = App.appSharedPrefs.getString("country", "");
		city = App.appSharedPrefs.getString("city", "");

		
		if (country.equals(""))
		{

			new BackgroundGetData().execute();
		} else
		{
			setContentView(R.layout.mainmenu);
			setGridView();
			
		}

		boolean wifiEnabled = Util.isWifiLocationEnabled(getApplicationContext());

		if (!wifiEnabled)
		{
			ShowAlertBox(getString(R.string.enable_networks));
		}
		App.tracker.trackPageView("/HHHomeScreen");
		
	}
	
	private void setGridView()
	{
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(MainMenuActivity.this));

		gridview.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				switch (position)
				{

				case 0:
					onClickBtnRate(null);
					break;
				case 1:
					onClickBtnTakePhoto(null);
					break;

				case 2:
					onClickHunters(null);
					break;
					
				case 3:
					onClickBtnMyProfile(null);
					break;
					
				case 4:
					onClickLogIn(null);
					break;
				case 5:
					onClickCreateUser(null);
					break;

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{

		case R.id.changeLocation:
			changeLocation();
			break;

		case R.id.about:
			openAbout();
			break;

		case R.id.openGalery:
			openGalery();
			break;

		}

		return true;

	}
	
	private void openAbout()
	{
		try
		{
			InputStream is = getResources().getAssets().open("termofuse.txt");
			String terms = convertStreamToString(is);
			
			AlertDialog.Builder alertbox = new AlertDialog.Builder(MainMenuActivity.this);
			alertbox.setMessage(terms);
			alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					
				}
				
			});

			alertbox.show();
			
		} catch (Exception e)
		{

		}
	}

	private void openGalery()
	{
		try
		{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);//
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), 22);
		} catch (Exception e)
		{

		}
	}

	private void changeLocation()
	{
		try
		{
			final Dialog dialog = new Dialog(MainMenuActivity.this);
			dialog.setContentView(R.layout.pop_up_set_location);
			dialog.setTitle(getString(R.string.enter_location));

			spin = (Spinner) dialog.findViewById(R.id.spinnerCountry);
			etCity = (EditText) dialog.findViewById(R.id.etCity);
			Button cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
			adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
			cancelButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
				}
			});

			Button searchButton = (Button) dialog.findViewById(R.id.btnSearch);
			searchButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// setLocationInfo();
					new BackgroundGetLocation().execute();

					// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// spin.setAdapter(adapter);
					//
					// spin.setSelection(adapter.getPosition(country));
					// etCity.setText(city);

				}
			});

			Button dialogButton = (Button) dialog.findViewById(R.id.btnLocationOk);

			dialogButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					city = etCity.getText().toString();
					country = spin.getSelectedItem().toString();
					App.prefsEditor.putString("country", country);
					App.prefsEditor.putString("city", city);
					App.prefsEditor.commit();

					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e)
		{

		}
	}

	private Location getLocationFast()
	{
		Location lastKnownLocation = null;
		try
		{
			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			String locationProvider = LocationManager.NETWORK_PROVIDER;
			lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

		} catch (Exception e)
		{
			return null;
		}
		return lastKnownLocation;
	}

	private void setLocationInfo()
	{
		Location location = getLocationFast();

		if (location != null)
		{

			Geocoder gCoder = new Geocoder(getBaseContext());

			ArrayList<Address> addresses;
			try
			{
				addresses = (ArrayList<Address>) gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if (addresses != null && addresses.size() > 0)
				{
					country = addresses.get(0).getCountryName();
					city = addresses.get(0).getLocality();

				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case 1:
			Toast.makeText(this, String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
			{

				try
				{
					boolean wifiEnabled = Util.isWifiLocationEnabled(getApplicationContext());
					if ((!wifiEnabled) || (country.equals("")))
					{
						final Dialog dialog = new Dialog(MainMenuActivity.this);
						dialog.setContentView(R.layout.pop_up_location);
						dialog.setTitle(getString(R.string.enter_location));

						final Spinner spin = (Spinner) dialog.findViewById(R.id.spinnerCountry);
						final EditText etCity = (EditText) dialog.findViewById(R.id.etCity);
						Button cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
						cancelButton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});

						Button dialogButton = (Button) dialog.findViewById(R.id.btnLocationOk);

						dialogButton.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								city = etCity.getText().toString();
								country = spin.getSelectedItem().toString();
								new BackgroundGetData().execute();
								dialog.dismiss();
							}
						});

						dialog.show();
					}
				} catch (Exception e)
				{
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();

				}
			}
		case 22:
		{
			if (resultCode == RESULT_OK)
			{
				Uri selectedImage = data.getData();
				Intent intent = new Intent(MainMenuActivity.this, PhotoActivity.class);
				intent.setData(selectedImage);
				startActivity(intent);
			}

		}
		}

	}

	private void ShowAlertBox(String text)
	{
		AlertDialog.Builder alertbox = new AlertDialog.Builder(MainMenuActivity.this);
		alertbox.setMessage(text);
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
			}
		});

		alertbox.show();
	}
	
	private String convertStreamToString(InputStream is) throws IOException
	{
		Writer writer = new StringWriter();
		char[] buffer = new char[2048];
		try
		{
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1)
			{
				writer.write(buffer, 0, n);
			}
		} finally
		{
			is.close();
		}
		String text = writer.toString();
		return text;
	}

	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(MainMenuActivity.this, "", "Talking with server...", true);
		private AsyncTask<URL, Integer, Long> updateTask = null;

		@Override
		protected void onPostExecute(Long result)
		{
			try
			{
				dialog.dismiss();
				dialog = null;
			} catch (Exception e)
			{
			}
			setContentView(R.layout.mainmenu);
			App.prefsEditor.putString("country", country);
			App.prefsEditor.putString("city", city);
			App.prefsEditor.commit();
			
			setGridView();

		}

		@Override
		protected void onPreExecute()
		{
			setContentView(R.layout.splash);
			updateTask = this;
			dialog.setCancelable(true);
			dialog.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					updateTask.cancel(true);
				}
			});
			dialog.show();
		}

		@Override
		protected Long doInBackground(URL... params)
		{
			if (isCancelled())
			{
				return null;
			} else
			{
				setLocationInfo();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

	public class BackgroundGetLocation extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(MainMenuActivity.this, "", "Locating...", true);
		private AsyncTask<URL, Integer, Long> updateTask = null;

		@Override
		protected void onPostExecute(Long result)
		{
			try
			{
				dialog.dismiss();
				dialog = null;
			} catch (Exception e)
			{
			}

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin.setAdapter(adapter);

			spin.setSelection(adapter.getPosition(country));
			etCity.setText(city);

			// App.prefsEditor.putString("country", country);
			// App.prefsEditor.putString("city", city);
			// App.prefsEditor.commit();

		}

		@Override
		protected void onPreExecute()
		{

			updateTask = this;
			dialog.setCancelable(true);
			dialog.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					updateTask.cancel(true);
				}
			});
			dialog.show();
		}

		@Override
		protected Long doInBackground(URL... params)
		{
			if (isCancelled())
			{
				return null;
			} else
			{
				setLocationInfo();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

	public class ImageAdapter extends BaseAdapter
	{
		private Context mContext;

		public ImageAdapter(Context c)
		{
			mContext = c;
		}

		public int getCount()
		{
			return mThumbIds.length;
		}

		public Object getItem(int position)
		{
			return null;
		}

		public long getItemId(int position)
		{
			return 0;
		}

	
		public View getView(int position, View convertView, ViewGroup parent)
		{
			//ImageView imageView;
			View v;

			if (convertView == null)
			{ 

				
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.icon, null);
			
		
			} else
			{
				v = convertView;
			}

			TextView tv = (TextView)v.findViewById(R.id.icon_text);
			tv.setText(mStringIds[position]);
			ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
			
			iv.setImageResource(mThumbIds[position]);
			return v;
		}

		// references to our images
		private Integer[] mThumbIds =
		{ 
			R.drawable.pics_menu, R.drawable.shoot_menu, R.drawable.ratings_menu, R.drawable.profil_menu, R.drawable.login_menu,R.drawable.icon_app
		};
		
		private String[] mStringIds =
			{ 
				"Look&Rate", "Shot it!", "Bests", "My Profile","Login","Create Account"
			};
		
		
	}

	public void onClickBtnTakePhoto(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, PhotoActivity.class);
		startActivity(i);
	}

	public void onClickBtnRate(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, RateActivity.class);
		startActivity(i);
	}

	public void onClickCreateUser(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, CreateUserActivity.class);
		startActivity(i);
	}

	public void onClickLogIn(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, LoginActivity.class);
		startActivity(i);
	}

	public void onClickBtnMyProfile(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, MyProfileActivity.class);
		startActivity(i);
	}

	public void onClickHunters(View view)
	{
		Intent i = new Intent(MainMenuActivity.this, TopListActivity.class);
		startActivity(i);
	}
}
