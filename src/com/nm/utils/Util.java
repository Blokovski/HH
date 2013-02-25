package com.nm.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.nm.tits.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

public class Util
{
	public static boolean isOnline(final Activity act)
	{
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
		return false;
	}

	public static boolean isWifiLocationEnabled(Context context)
	{
		ContentResolver cr = context.getContentResolver();
		String enabledProviders = Settings.Secure.getString(cr, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!TextUtils.isEmpty(enabledProviders))
		{

			String[] providersList = TextUtils.split(enabledProviders, ",");
			for (String provider : providersList)
			{
				if (LocationManager.NETWORK_PROVIDER.equals(provider))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static void showNoIternetAlertDialog(final Activity act, final String msg, final String tryAgain, final String cancel)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setMessage(msg).setCancelable(false).setPositiveButton(tryAgain, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				Intent intent = act.getIntent();
				act.finish();
				act.startActivity(intent);
			}
		}).setNegativeButton(cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				act.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showNoIternetAlertDialog(final Activity act)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setMessage(act.getString(R.string.check_internet_conection)).setCancelable(false)
				.setPositiveButton(act.getString(R.string.try_again), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						Intent intent = act.getIntent();
						act.finish();
						act.startActivity(intent);
					}
				}).setNegativeButton(act.getString(R.string.cancel), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						act.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void ShowAlertBox(final Activity act, String text)
	{
		AlertDialog.Builder alertbox = new AlertDialog.Builder(act);
		alertbox.setMessage(text);
		alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
			}
		});

		alertbox.show();
	}

	public static void showShortToastMessage(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showShortToastMessage(Context context, int messageId)
	{
		Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
	}

	public static void showLongToastMessage(Context context, String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void showLongToastMessage(Context context, int messageId)
	{
		Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
	}

	public static void showErrorMessage(Context c, String message)
	{
		createErrorDialog(c, message, null).show();
	}

	public static void showErrorMessage(Context c, String message, DialogInterface.OnClickListener listener)
	{
		createErrorDialog(c, message, listener).show();
	}

	public static void deleteFile(String path)
	{
		deleteFile(new File(path));
	}

	public static void deleteFile(String path, boolean toDeleteSelf)
	{
		deleteFile(new File(path), toDeleteSelf);
	}

	public static void deleteFile(File f)
	{
		deleteFile(f, true);
	}

	public static void deleteFile(File f, boolean toDeleteSelf)
	{
		if (f.isDirectory())
		{
			for (File child : f.listFiles())
				deleteFile(child, true);
		}
		if (toDeleteSelf)
			f.delete();
	}

	public static boolean isFileOrEmptyDirectory(String path)
	{
		File f = new File(path);
		if (!f.isDirectory())
			return true;

		String[] list = f.list();
		return ((list == null) || (list.length == 0));
	}

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";

	public static String getDateName()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	private static AlertDialog createErrorDialog(Context c, String message, DialogInterface.OnClickListener okListener)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(c).setTitle(android.R.string.dialog_alert_title).setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message);
		if (okListener != null)
		{
			b.setPositiveButton(android.R.string.ok, okListener);
		} else
		{
			b.setPositiveButton(android.R.string.ok, null);
		}
		return b.create();
	}
}
