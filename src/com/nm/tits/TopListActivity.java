package com.nm.tits;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import com.nm.tits.MyProfileActivity.BackgroundGetData;
import com.nm.tits.MyProfileActivity.BackgroundGetMore;
import com.nm.utils.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TopListActivity extends Activity
{
	List<Hunter> hunters = null;
	List<Picture> pictures = null;
	GetData gd;
	String country = "";
	LinearLayout list;
	String sex = "Girl";
	BitmapManager bm;
	
	
	
	Button btn;
	
	Button btnHunters;
	Button btnGuys;
	Button btnGirls;
	
	int menu = 0; //0 hunter, 1 girl, 2 guy
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top_list);
		gd = App.getGetData();
		list = (LinearLayout) findViewById(R.id.list);
		bm = new BitmapManager();
		
		btnHunters = (Button)findViewById(R.id.butHunters);
		btnGuys = (Button)findViewById(R.id.butGuys);
		btnGirls = (Button)findViewById(R.id.butGirls);
		final Spinner spin = (Spinner) findViewById(R.id.spinnerCountry);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);

		String myString =  App.appSharedPrefs.getString("country", "Worlwide");

		int spinnerPosition = adapter.getPosition(myString);

		spin.setSelection(spinnerPosition);
		spin.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				country = (String) spin.getSelectedItem();
				if (country.equals("Worldwide"))
				{
					country = "";
				}
				list.removeAllViews();
				if (!Util.isOnline(TopListActivity.this))
				{
					Util.showNoIternetAlertDialog(TopListActivity.this);
				} else
				{
					//new BackgroundGetData().execute();
					switch (menu)
					{
					case 0:
						onClickHunters(null);
						break;
						
					case 1:
						onClickGirls(null);
						break;
						
					case 2:
						onClickGuys(null);
						break;

					default:
						onClickHunters(null);
						break;
					}
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});

		App.tracker.trackPageView("/HHTopListScreen");
	}
	
	public void onClickHunters(View v)
	{
		menu = 0;
		btnHunters.setEnabled(false);
		btnGirls.setEnabled(true);
		btnGuys.setEnabled(true);
		new BackgroundGetData().execute();
		
	}
	
	public void onClickGuys(View v)
	{
		menu = 2;
		btnHunters.setEnabled(true);
		btnGirls.setEnabled(true);
		btnGuys.setEnabled(false);
		sex ="Guy";
		if (pictures!= null)
		{
			pictures.clear();
		}
		new BackgroundGetBestData().execute();
	}
	
	public void onClickGirls(View v)
	{
		menu = 1;
		btnHunters.setEnabled(true);
		btnGirls.setEnabled(false);
		btnGuys.setEnabled(true);
		sex ="Girl";
		if (pictures!= null)
		{
			pictures.clear();
		}
		getBestPictures();
		setPictures();
		new BackgroundGetBestData().execute();
	}

	private void getData()
	{
		try
		{

			String jsonString = gd.getTopList(country);

			if (jsonString != null)
			{
				hunters = JsonParser.parseTopList(jsonString);
				
			}

		} catch (Exception e)
		{
			Log.e("TopListActivity.getData()", e.toString());
		}
	}
	
	private void getBestPictures()
	{
		try
		{

			String jsonString = gd.getBestImages(country, sex);

			if (jsonString != null)
			{
				pictures = JsonParser.parseProfilePictures(jsonString);
				
			}

		} catch (Exception e)
		{
			Log.e("TopListActivity.getData()", e.toString());
		}
	}
	
	private void setPictures()
	{
		try
		{	
			list.removeAllViews();
			if (pictures.size() > 0)				
			{
				LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				//list = (LinearLayout) findViewById(R.id.list);
				
				for (int i = 0; i < pictures.size(); i++)
				{

					final Picture m = pictures.get(i);
					View v = inflater.inflate(R.layout.row_list_best, null);
					

					if (m != null)
					{

						TextView tt = (TextView) v.findViewById(R.id.tvUpperText);
						TextView bt = (TextView) v.findViewById(R.id.tvBottomText);
						TextView mt = (TextView) v.findViewById(R.id.tvMidleText);
						ImageView pic = (ImageView) v.findViewById(R.id.picMain);
						if (tt != null)
						{
							tt.setText("From: " + m.getCountry() + ", " + m.getCity());
						}
						if (bt != null)
						{
							bt.setText("Rating: " + m.getRating() + " Sex: " + m.getSex());
						}
						if (mt != null)
						{
							mt.setText("Posted: " + m.getDateString());
						}
						if (pic != null)
						{
							bm.fetchBitmapOnThread(m.getThumbUrl(), pic, 1);
						}

					}

					v.setClickable(true);
					v.setFocusable(true);

					v.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							

							Intent i = new Intent(TopListActivity.this, ImageActivity.class);
							Bundle bun = new Bundle();

							bun.putString("url", m.getPictureUrl());
							bun.putString("country", m.getCountry());
							bun.putString("city", m.getCity());
							i.putExtras(bun);
							startActivity(i);
						}
					});

					
					list.addView(v);
				}
				
			}

		} catch (Exception e)
		{
			Log.e("ProfileActivity.setPictures()", e.toString());
		}
	}

	private void setData()
	{
		try
		{
			LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			//LinearLayout list = (LinearLayout) findViewById(R.id.list);
			list.removeAllViews();

			for (int i = 0; i < hunters.size(); i++)
			{

				final Hunter m = hunters.get(i);
				View v = inflater.inflate(R.layout.row_list_toplist, null);

				if (m != null)
				{

					TextView tt = (TextView) v.findViewById(R.id.tvUpperText);
					TextView bt = (TextView) v.findViewById(R.id.tvBottomText);
					// ImageView pic = (ImageView) v.findViewById(R.id.picMain);
					if (tt != null)
					{
						tt.setText(m.getPlaceAtList() + ". User: " + m.getDisplayName());
					}
					if (bt != null)						
					{
						String rating = m.getAverageRating();
						double dblRating = Double.valueOf(rating);
						DecimalFormat newFormat = new DecimalFormat("#.##");
						double twoDecimal =  Double.valueOf(newFormat.format(dblRating));
						String s = String.valueOf(twoDecimal);
						
						bt.setText("Average rating: " + s + " Pictures: " + m.getNumberOfPictures());
					}

					// if (pic != null)
					// {
					// String url = m.getImage();
					// bm.fetchBitmapOnThread(url, pic, 1);
					// }

				}
				v.setClickable(true);
				v.setFocusable(true);

				v.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

					}
				});
				
				list.addView(v);
			}
			

		} catch (Exception e)
		{
			Log.e("TopListActivity.setData()", e.toString());
		}
	}

	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(TopListActivity.this, "", "Talking with server...", true);
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
				getData();

			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}
	
	public class BackgroundGetBestData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(TopListActivity.this, "", "Talking with server...", true);
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
			setPictures();
			

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
				getBestPictures();

			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}
	
	
}
