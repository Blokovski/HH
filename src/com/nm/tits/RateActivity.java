package com.nm.tits;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.nm.tits.MyProfileActivity.BackgroundGetData;
import com.nm.utils.UrlImageCache;
import com.nm.utils.UrlImageViewHelper;
import com.nm.utils.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class RateActivity extends Activity
{
	GetData gd;
	Picture picture;
	Button butNext;
	Button butWrong;
	Button butShare;
	Button butZoom;
	RatingBar rating;
	BitmapManager bm;
	ImageView imageView;
	String country;// = "Serbia";
	String city;// = "Belgrade";
	String sex = "Girl";
	TextView txtLocation;
	List<Picture> pictures = null;
	int brojac = 0;
	int page = 0;
	int rate;
	boolean wrongFlag;

	RadioGroup radioSexGroup;
	RadioButton radioSexButton;

	String rateResult = "";
	Button butOK;

	private static final int SWIPE_MIN_DISTANCE = 80;// 120
	private static final int SWIPE_MAX_OFF_PATH = 200; // 250
	private static final int SWIPE_THRESHOLD_VELOCITY = 150; // 200

	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate);
		gd = App.getGetData();
		bm = new BitmapManager();
		rating = (RatingBar) findViewById(R.id.ratingBar);
		// etCity = (EditText) findViewById(R.id.etCity);

		city = App.appSharedPrefs.getString("city", "");
		country = App.appSharedPrefs.getString("country", "Worlwide");

		rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
		{
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
			{

			}
		});

		pictures = new ArrayList<Picture>();
		butNext = (Button) findViewById(R.id.butNext);
		butWrong = (Button) findViewById(R.id.butWrong);
		butShare = (Button) findViewById(R.id.btnShare);
		butZoom = (Button) findViewById(R.id.btnZoom);
		//butShare.setVisibility(View.VISIBLE);
		butZoom.setVisibility(View.VISIBLE);
		imageView = (ImageView) findViewById(R.id.image);
		txtLocation = (TextView) findViewById(R.id.txtLocation);

		// Ovde videti da li su country i city prazni pa automatsku upaliti da
		// se setuje lokacija
		txtLocation.setText("Location: " + country + ", " + city);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				return gestureDetector.onTouchEvent(event);
			}
		};

		imageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

			}
		});
		imageView.setOnTouchListener(gestureListener);

		if (!Util.isOnline(this))
		{
			Util.showNoIternetAlertDialog(this);
		} else
		{
			new BackgroundGetData().execute();
		}
		App.tracker.trackPageView("/HHRateScreen");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		super.dispatchTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}

	public void onClickBtnZoom(View view)
	{
		try
		{
			Intent i = new Intent(RateActivity.this, ImageActivity.class);
			Bundle bun = new Bundle();

			bun.putString("url", picture.getPictureUrl());
			i.putExtras(bun);
			startActivity(i);

		} catch (Exception e)
		{
			Log.e("DetailsActivity.Share()", e.toString());
		}
	}

	public void onClickChangeFilter(View view)
	{
		try
		{

			final Dialog dialog = new Dialog(RateActivity.this);
			dialog.setContentView(R.layout.pop_up_location);
			dialog.setTitle("Choose location");

			final Spinner spin = (Spinner) dialog.findViewById(R.id.spinnerCountry);

			ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin.setAdapter(adapter);

			// String myString = country;

			// int spinnerPosition = adapter.getPosition(country);

			spin.setSelection(adapter.getPosition(country));
			final EditText etCity = (EditText) dialog.findViewById(R.id.etCity);
			etCity.setText(city);
			radioSexGroup = (RadioGroup) dialog.findViewById(R.id.radioSexLocation);
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
					if (country.equals("Worldwide"))
					{
						country = "";
						city="";
						txtLocation.setText("Location: " + "Worldwide");
					} else
					{
						txtLocation.setText("Location: " + country + ", " + city);
					}
					int selectedId = radioSexGroup.getCheckedRadioButtonId();
					radioSexButton = (RadioButton) dialog.findViewById(selectedId);
					String test = (String) radioSexButton.getText();
					if (test.equals("Girls"))
					{
						sex = "Girl";
					} else if (test.equals("Guys"))
					{
						sex = "Guy";

					} else
					{
						sex = "";
					}

					// Toast.makeText(RateActivity.this, "Pol: " + test,
					// Toast.LENGTH_LONG).show();
					pictures.clear();
					brojac = 0;
					page = 0;
					new BackgroundGetData().execute();
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e)
		{
			Log.e("RateActivity.onClickChangeFilter()", e.toString());
		}
	}

	private void setData()
	{
		try
		{
			if (brojac < pictures.size())
			{
				picture = pictures.get(brojac);
				if (picture != null)
				{

					
					UrlImageViewHelper.setUrlDrawable(imageView, picture.getPictureUrl(),null,null);
					
					rating.setRating(picture.getRating());

				}
			} else
			{
				Toast.makeText(RateActivity.this, getString(R.string.no_more_data), Toast.LENGTH_LONG).show();
				brojac--;
			}

		} catch (Exception e)
		{
			Log.e("RateActivity.setData", e.toString());
			Toast.makeText(RateActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public void onClickVote(View view)
	{
		try
		{
			if (butWrong.isSelected() == false)
			{
				rate = (int) rating.getRating();
				new BackgroundRate().execute();
			} else
			{
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(RateActivity.this);
				myAlertDialog.setTitle("Inappropriate Image");
				myAlertDialog.setMessage(getString(R.string.wrong_flag));
				myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface arg0, int arg1)
					{

						rate = (int) rating.getRating();
						new BackgroundRate().execute();
					}
				});
				myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface arg0, int arg1)
					{

					}
				});
				myAlertDialog.show();
			}

			App.tracker.trackEvent("RateScreen", // Category
					"rate", // Action
					"", // Label
					0); // Value

		} catch (Exception e)
		{
			Log.e("RateActivity.onClickVote()", e.toString());
		}
	}

	private void checkRateResults()
	{
		if (rateResult.equals("OK"))
		{
			Toast.makeText(RateActivity.this, getString(R.string.rate_ok), Toast.LENGTH_LONG).show();
		} else if (rateResult.equals("NA"))
		{
			Toast.makeText(RateActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();

		} else
		{
			Toast.makeText(RateActivity.this, "Error: " + rateResult, Toast.LENGTH_LONG).show();
		}
	}

	private void loadData(int pageN)
	{
		try
		{
			String br = String.valueOf(pageN);
			String jsonString = gd.getPicture(country, city, sex, br);
			List<Picture> pic = new ArrayList<Picture>();

			if (jsonString != null)
			{

				pic = JsonParser.parseProfilePictures(jsonString);
				if (pic.size() > 0)
				{
					pictures.addAll(pic);
				}

			}
		} catch (Exception e)
		{
			Log.e("DetailsActivity.loadData()", e.toString());
		}
	}

	public void onClickWrong(View view)
	{
		try
		{
			if (butWrong.isSelected())
			{
				butWrong.setSelected(false);
				wrongFlag = false;
			} else
			{
				butWrong.setSelected(true);
				wrongFlag = true;
			}

		} catch (Exception e)
		{
			Log.e("RateActivit.onClickWrong()", e.toString());
		}
	}

	public void onClickNext(View view)
	{
		try
		{

			int test = pictures.size();
			brojac++;

			if (brojac >= test)
			{
				page++;
				// brojac = 0;

				new BackgroundGetData().execute();

			} else
			{
				setData();
			}

		} catch (Exception e)
		{
			Log.e("RateActivity.onClickNext()", e.toString());
		}
	}

	public void onClickBack(View view)
	{
		try
		{

			if (brojac > 0)
			{
				brojac--;
				setData();

			}

		} catch (Exception e)
		{
			Log.e("RateActivity.onClickBack()", e.toString());
		}
	}

	public class BackgroundRate extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(RateActivity.this, "", "Talking with server...", true);
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
			checkRateResults();
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
				rateResult = gd.ratePicture(picture.getId(), rate, wrongFlag);
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

	class MyGestureDetector extends SimpleOnGestureListener
	{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			try
			{
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{
					onClickNext(null);

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
				{

					onClickBack(null);
				}
			} catch (Exception e)
			{

			}
			return false;
		}
	}

	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(RateActivity.this, "", "Talking with server...", true);
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
			setData();
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
				loadData(page);
			}
			return null;

		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

}
