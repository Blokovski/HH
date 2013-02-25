package com.nm.tits;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfileActivity extends Activity
{
	GetData gd;
	Profile profile;
	Picture picture;
	BitmapManager bm;
	List<Picture> pictures = null;
	String resultPictures;

	int page = 0;
	boolean isMorePage = true;
	int firstTime = 1;

	LinearLayout list;
	Button btn;
	boolean notLoged;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		notLoged = false;

		if (!Util.isOnline(this))
		{
			Util.showNoIternetAlertDialog(this);
		} else
		{
			gd = App.getGetData();
			bm = new BitmapManager();
			forceLoging();
			new BackgroundGetData().execute();
		}
		
		App.tracker.trackPageView("/HHMyProfileScreen");
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
					Toast.makeText(MyProfileActivity.this, "Connect...", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	private void getData()
	{
		try
		{

			String jsonString = gd.getMyImages(Integer.toString(page));

			if (jsonString != null)
			{
				if (jsonString.equals("NA"))
				{
//					Toast.makeText(MyProfileActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();
//					Intent i = new Intent(MyProfileActivity.this, LoginActivity.class);
//					startActivity(i);
					notLoged = true;
				} else
				{
					pictures = JsonParser.parseProfilePictures(jsonString);
					if (pictures == null)
					{
						isMorePage = false;
					}
				}
			}

		} catch (Exception e)
		{
			Log.e("ProfileActivity.getData()", e.toString());
		}
	}

	private void getProfileData()
	{
		try
		{

			String jsonString = gd.getMyProfile();

			if (jsonString != null)
			{
				if (jsonString.equals("NA"))
				{
//					Toast.makeText(MyProfileActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();
//					Intent i = new Intent(MyProfileActivity.this, LoginActivity.class);
//					startActivity(i);
					notLoged = true;
					
				} else
				{
					profile = JsonParser.parseProfile(jsonString);

				}
			}

		} catch (Exception e)
		{
			Log.e("ProfileActivity.getProfileData()", e.toString());

		}
	}

	private void setData()
	{
		try
		{
			if (notLoged == true)
			{
				Toast.makeText(MyProfileActivity.this, getString(R.string.not_loged), Toast.LENGTH_LONG).show();
    			Intent i = new Intent(MyProfileActivity.this, LoginActivity.class);
				startActivity(i);
			}
			else
			if (profile != null)
			{
				TextView txtFirst = (TextView) findViewById(R.id.txtFirstRow);
				TextView txtSecond = (TextView) findViewById(R.id.txtSecondRow);
				TextView txtTree = (TextView) findViewById(R.id.txtTreeRow);
				TextView txtFour = (TextView) findViewById(R.id.txtFourRow);
				// ImageView imageView = (ImageView)
				// findViewById(R.id.picImage);
				txtFirst.setText("Display name: " + profile.getDisplayName());
				txtSecond.setText("Email: " + profile.getEmail());
				txtTree.setText("No. of pictures: " + profile.getNumberOfPictures());
				
				String rating = profile.getAverageRating();
				double dblRating = Double.valueOf(rating);
				DecimalFormat newFormat = new DecimalFormat("#.##");
				double twoDecimal =  Double.valueOf(newFormat.format(dblRating));
				String s = String.valueOf(twoDecimal);
				
				txtFour.setText("Average rating: " + s);
				// bm.fetchBitmapOnThread(profile.getAvatar(), imageView, 1);
			} else
			{
				
				Toast.makeText(MyProfileActivity.this, getString(R.string.no_more_data), Toast.LENGTH_LONG).show();
			}

		} catch (Exception e)
		{
			Log.e("ProfileActivity.getData()", e.toString());
		}
	}

	private void setPictures()
	{
		try
		{
			if (pictures.size() > 0)
			{
				LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				list = (LinearLayout) findViewById(R.id.listPictures);
				for (int i = 0; i < pictures.size(); i++)
				{

					final Picture m = pictures.get(i);
					View v = inflater.inflate(R.layout.row_list_profile, null);
					Button btnDelete = (Button) v.findViewById(R.id.btnDelete);

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
							App.tracker.trackEvent("MyProfile", // Category
									"openPicture", // Action
									"", // Label
									0); // Value

							Intent i = new Intent(MyProfileActivity.this, ImageActivity.class);
							Bundle bun = new Bundle();

							bun.putString("url", m.getPictureUrl());
							bun.putString("country", m.getCountry());
							bun.putString("city", m.getCity());
							i.putExtras(bun);
							startActivity(i);
						}
					});

					btnDelete.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{

							AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MyProfileActivity.this);
							myAlertDialog.setTitle("Delete");
							myAlertDialog.setMessage(getString(R.string.delete_question));
							myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
							{

								public void onClick(DialogInterface arg0, int arg1)
								{
									//Toast.makeText(MyProfileActivity.this, "id:" + m.getId().toString(), Toast.LENGTH_SHORT).show();
									String deleteResult = gd.deleteMyImage(m.getId().toString());
									if (deleteResult.equals("OK"))
									{
										Toast.makeText(MyProfileActivity.this, getString(R.string.picture_deleted), Toast.LENGTH_SHORT).show();
										list.removeAllViews();
										new BackgroundGetData().execute();
									} else
									{

										Toast.makeText(MyProfileActivity.this, deleteResult, Toast.LENGTH_SHORT).show();
									}
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
					});
					list.addView(v);
				}
				firstTime = 0;
				btn = new Button(this);
				// btn.setBackgroundResource(R.drawable.more_news_selector);
				btn.setTextSize(14);
				//btn.setBackgroundColor(Color.BLACK);
				btn.setTextColor(Color.WHITE);
				btn.setPadding(35, 0, 35, 0);
				btn.setGravity(Gravity.CENTER_VERTICAL);
				int pxb = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
				LayoutParams lpb = new LayoutParams(LayoutParams.FILL_PARENT, pxb);
				btn.setLayoutParams(lpb);
				btn.setText(getString(R.string.load_more));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						page++;
						if (isMorePage == true)
						{
							new BackgroundGetMore().execute();
						} else
						{
							Toast toast = Toast.makeText(App.getContext(), getString(R.string.no_more_data), Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				});
				list.addView(btn);
			}

		} catch (Exception e)
		{
			Log.e("ProfileActivity.setPictures()", e.toString());
		}
	}

	public class BackgroundGetData extends AsyncTask<URL, Integer, Long>
	{

		ProgressDialog dialog = ProgressDialog.show(MyProfileActivity.this, "", "Talking with server...", true);
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
				getProfileData();
				getData();

			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{

		}
	}

	public class BackgroundGetMore extends AsyncTask<URL, Integer, Long>
	{
		ProgressDialog dialog = ProgressDialog.show(MyProfileActivity.this, "", "Loading...", true);
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
			list.removeView(btn);
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
				getData();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Integer... progress)
		{
		}
	}

}
