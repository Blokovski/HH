package com.nm.tits;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class BitmapManager
{
	private final Map<String, Bitmap> bitmapMap;

	public BitmapManager()
	{
		bitmapMap = new HashMap<String, Bitmap>();
	}

	// default method, i used this one when i want to reduce size of pictures
	public Bitmap fetchBitmap(String urlString, int samplingSize)
	{

		if (bitmapMap.containsKey(urlString))
		{
			return bitmapMap.get(urlString);
		}

		try
		{
			InputStream is = fetch(urlString);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = samplingSize;
			options.inPurgeable = true;
			options.inDither = false;
			options.inInputShareable = true;
			options.inTempStorage = new byte[32 * 1024];

			Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

			if (bitmap != null)
			{
				bitmapMap.put(urlString, bitmap);

			}

			return bitmap;
		} catch (MalformedURLException e)
		{
			return null;
		} catch (IOException e)
		{
			return null;
		}
	}

	// Here you can set options for bitmap creation
	public Bitmap fetchBitmap(String urlString, int samplingSize, boolean purgeable, boolean dihter, boolean inputShareable)
	{

		if (bitmapMap.containsKey(urlString))
		{
			return bitmapMap.get(urlString);
		}
		try
		{
			InputStream is = fetch(urlString);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = samplingSize;
			options.inPurgeable = purgeable;
			options.inDither = dihter;
			options.inInputShareable = inputShareable;
			options.inTempStorage = new byte[32 * 1024];

			Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

			if (bitmap != null)
			{
				bitmapMap.put(urlString, bitmap);
			}
			return bitmap;
		} catch (MalformedURLException e)
		{
			return null;
		} catch (IOException e)
		{
			return null;
		}
	}

	// default method, i used this one when i want to reduce size of pictures
	public Bitmap fetchBitmap(String urlString, int IMAGE_MAX_SIZE, boolean resizing)
	{
		if (bitmapMap.containsKey(urlString))
		{
			return bitmapMap.get(urlString);
		}
		try
		{
			InputStream is = fetch(urlString);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(is, null, options);
			int scale = 1;
			if (options.outHeight > IMAGE_MAX_SIZE || options.outWidth > IMAGE_MAX_SIZE)
			{
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
			}
			// Decode with inSampleSize
			BitmapFactory.Options optionsInSample = new BitmapFactory.Options();
			optionsInSample.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, optionsInSample);
			if (bitmap != null)
			{
				bitmapMap.put(urlString, bitmap);
			}
			return bitmap;
		} catch (MalformedURLException e)
		{
			return null;
		} catch (IOException e)
		{
			return null;
		}
	}

	public void fetchBitmapOnThread(final String urlString, final ImageView imageView, final int samplingSize)
	{
		if (bitmapMap.containsKey(urlString))
		{
			imageView.setImageBitmap(bitmapMap.get(urlString));
		}

		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message message)
			{
				imageView.setImageBitmap((Bitmap) message.obj);
			}
		};

		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Bitmap bitmap = fetchBitmap(urlString, samplingSize);
					Message message = handler.obtainMessage(1, bitmap);
					handler.sendMessage(message);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private InputStream fetch(String urlString) throws MalformedURLException, IOException
	{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}
}
