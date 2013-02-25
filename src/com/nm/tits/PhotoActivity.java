package com.nm.tits;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.nm.tits.CreateUserActivity.BackgroundGetData;
import com.nm.utils.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PhotoActivity extends Activity
{
	Uri imageUri;
	final int PICTURE_ACTIVITY = 100;
	InputStream is;
	Bitmap pic;
	Bitmap resized;
	ImageView imageView;
	GetData gd;
	Location location;
	EditText etComment;
	RadioGroup radioSexGroup;
	RadioButton radioSexButton;

	EditText etCountry;
	EditText etCity;

	String country;
	String city;
	double latitude;
	double longitude;

	String x;
	String y;
	String sex;

	boolean hasErrors;
	String result;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();

		gd = App.getGetData();
		// location = getLocationFast();
		etComment = (EditText) findViewById(R.id.editComment);
		etCountry = (EditText) findViewById(R.id.etCountry);
		etCity = (EditText) findViewById(R.id.etCity);
		imageView = (ImageView) findViewById(R.id.image);
		radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
		hasErrors = false;

		country = App.appSharedPrefs.getString("country", "");
		city = App.appSharedPrefs.getString("city", "");
		if (country.equals("") || (city.equals("")))
		{
			setLocationInfo();
		} else
		{
			etCountry.setText(country);
			etCity.setText(city);
		}
		// forceLoging();

		if (Intent.ACTION_SEND.equals(action))
		{
			if (extras.containsKey(Intent.EXTRA_STREAM))
			{
				try
				{
					// Get resource path from intent callee
					imageUri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

					// selectedImage);
					pic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					resized = resizeBitmap(pic);
					pic = null;
					imageView.setImageBitmap(resized);

				} catch (Exception e)
				{
					Log.e(this.getClass().getName(), e.toString());
				}
			}

		} else if (getIntent().getData() != null)
		{
			Toast.makeText(PhotoActivity.this, "Image from Gallery", Toast.LENGTH_SHORT).show();
			imageUri = (Uri) getIntent().getData();
			try
			{
				pic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
			} catch (FileNotFoundException e)
			{

			} catch (IOException e)
			{

			}
			resized = resizeBitmap(pic);
			pic = null;
			imageView.setImageBitmap(resized);

		} else
		{
			takePhoto8();
		}
		App.tracker.trackPageView("/HHPhotoScreen");
	}

	private void forceLoging()
	{
		try
		{
			if (App.appSharedPrefs.getString("email", "") != null && !App.appSharedPrefs.getString("email", "").equals(""))
			{
				String email = App.appSharedPrefs.getString("email", "");
				String pass = App.appSharedPrefs.getString("pass", "");

				String result = gd.login(email, pass);

				if (result.equals("OK"))
				{
					Toast.makeText(PhotoActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	private void setLocationInfo()
	{
		location = getLocationFast();

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

			// if ((country == null) || (city == null))
			// {
			// country = App.appSharedPrefs.getString("country", "");
			// city = App.appSharedPrefs.getString("city", "");
			// }

			latitude = location.getLatitude();
			longitude = location.getLongitude();
			etCountry.setText(country);
			etCity.setText(city);
		} else
		{
			try
			{
				country = App.appSharedPrefs.getString("country", "");
				city = App.appSharedPrefs.getString("city", "");
				etCountry.setText(country);
				etCity.setText(city);
			} catch (Exception e)
			{
				// TODO: handle exception
			}
		}

	}

	public void takePhoto8()
	{

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, PICTURE_ACTIVITY);

	}

	public boolean createDirIfNotExists(File file)
	{
		boolean ret = true;

		// File file = new File(Environment.getExternalStorageDirectory(),
		// path);
		if (!file.exists())
		{
			if (!file.mkdirs())
			{
				Log.e("TravellerLog :: ", "Problem creating Image folder");
				ret = false;
			}
		}
		return ret;
	}

	public void onClickSend(View view)
	{
		try
		{
			location = getLocationFast();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		} catch (Exception e)
		{

		}

		x = String.valueOf(latitude);
		y = String.valueOf(longitude);

		int selectedId = radioSexGroup.getCheckedRadioButtonId();
		radioSexButton = (RadioButton) findViewById(selectedId);
		sex = (String) radioSexButton.getText();

		city = etCity.getText().toString();
		country = etCountry.getText().toString();
		forceLoging();

		new BackgroundGetData().execute();

	}

	private void setResult()
	{
		try
		{
			if (result.equals("OK"))
			{
				Toast.makeText(PhotoActivity.this, getString(R.string.upload_ok), Toast.LENGTH_LONG).show();
				App.prefsEditor.putString("country", country);
				App.prefsEditor.putString("city", city);
				App.prefsEditor.commit();
			} else if (result.equals("NA"))
			{
				Toast.makeText(PhotoActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();
				Intent i = new Intent(PhotoActivity.this, LoginActivity.class);
				startActivity(i);

			} else if (result.equals("empty"))
			{
				Toast.makeText(PhotoActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();
				Intent i = new Intent(PhotoActivity.this, LoginActivity.class);
				startActivity(i);
			} else
			{
				Toast.makeText(PhotoActivity.this, "Error: " + result, Toast.LENGTH_LONG).show();
			}
			Log.i("res:", result);
		} catch (Exception e)
		{
			Log.e("PhotoActivity.setResult()", e.toString());
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case PICTURE_ACTIVITY:
		{
			if (resultCode == RESULT_OK)
			{
				try
				{

					imageView.setImageDrawable(null);
					pic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
		
					resized = resizeBitmap(pic);
					pic = null;

					imageView.setImageBitmap(resized);
					resized = null;

				} catch (Exception e)
				{
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
					Log.e("Camera", e.toString());
				}
			} else if (resultCode == RESULT_CANCELED)
			{
				finish();
			} else
			{
				Toast.makeText(this, "GRESKAAAA!", Toast.LENGTH_SHORT).show();
			}
		}
		}

	}

	

	private Bitmap resizeBitmap(Bitmap original)
	{

		if (original.getWidth() < original.getHeight())
		{
			// return Bitmap.createScaledBitmap(original, 768, 1024, false);
			return Bitmap.createScaledBitmap(original, 384, 640, false);
		} else
		{
			// return Bitmap.createScaledBitmap(original, 1024, 768, false);
			return Bitmap.createScaledBitmap(original, 640, 384, false);
		}
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type)
	{
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type)
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// File mediaStorageDir = new
		// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile(),
		// App.APP_NAME);
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), App.APP_NAME);
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d(App.APP_NAME, "failed to create directory");
				return null;
			}
		}
		Calendar cal = Calendar.getInstance();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else
		{
			return null;
		}

		return mediaFile;
	}

	public static boolean hasStorage(boolean requireWriteAccess)
	{
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		} else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			return true;
		}
		return false;
	}
	
	

	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(PhotoActivity.this, "", "Uploading image...", true);
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
			setResult();
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
				result = gd.dataUpload(resized, x, y, sex, etComment.getText().toString(), country, city);
			}

			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

}


