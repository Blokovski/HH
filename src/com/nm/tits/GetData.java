package com.nm.tits;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

public class GetData
{

	CookieStore cookieStore;
	HttpContext localContext;
	HttpClient httpclient;

	public GetData()
	{
		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();

		HttpParams httpParameters = new BasicHttpParams();

		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		httpclient = new DefaultHttpClient(httpParameters);
		httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, App.USER_AGENT_NAME);

		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public String dataUpload(Bitmap bitmap, String locX, String locY, String sex, String comment, String country, String city)
	{

		String str = null;
		List<Cookie> cookies = ((AbstractHttpClient) httpclient).getCookieStore().getCookies();
		if (cookies.isEmpty())
		{
			return "empty";
		}

		try
		{
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
			byte[] ba = bao.toByteArray();
			String ba1 = Base64.encodeToString(ba, 0);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("image", ba1));
			nameValuePairs.add(new BasicNameValuePair("locX", locX));
			nameValuePairs.add(new BasicNameValuePair("locY", locY));
			nameValuePairs.add(new BasicNameValuePair("sex", sex));
			nameValuePairs.add(new BasicNameValuePair("comment", comment));
			nameValuePairs.add(new BasicNameValuePair("country", country));
			nameValuePairs.add(new BasicNameValuePair("city", city));

			HttpPost httppost = new HttpPost(App.SERVER_URL + "imageUpload");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			str = EntityUtils.toString(entity);
		} catch (Exception e)
		{
			Log.e("GetData.dataUpload()", e.toString());
			return e.toString();
		}
		return str;
	}

	public String getPicture(String country, String city, String sex, String page)
	{
		String str = null;
		try
		{
			String trimCountry = country.replace(" ", "%20");
			String trimCity = city.replace(" ", "%20");
			String url;
			String countryUrl = "";
			String cityUrl = "";
			String sexUrl ="";
			
			if (!country.equals(""))
			{
				countryUrl = "&country=" + trimCountry;
			}
			
			if (!city.equals(""))
			{
				cityUrl = "&city=" + trimCity;
			}
			
			if (!sex.equals(""))
			{
				sexUrl = "&sex=" + sex;
			}
			
			url = App.SERVER_URL + "getImages" + "?pageNo=" + page + countryUrl+cityUrl+sexUrl ;
//			if (sex.equals(""))
//			{
//				url = App.SERVER_URL + "getImages" + "?country=" + trimCountry + "&city=" + trimCity + "&pageNo=" + page;
//			}
//			else
//			{
//			
//				url = App.SERVER_URL + "getImages" + "?country=" + trimCountry + "&city=" + trimCity + "&sex=" + sex + "&pageNo=" + page;
//			}
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getPicture()", e.toString());
			return e.toString();
		}
		return str;
		
	}
	
	public String deleteMyImageGET(String imageId)
	{
		String str = null;
		try
		{
			String url = App.SERVER_URL + "deleteMyImage" + "?imageId=" + imageId;
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.deleteMyImage()", e.toString());
			return e.toString();
		}
		return str;		
	}
	
	public String deleteMyImage(String imageId)
	{
		String str = null;

		try
		{
			String url = App.SERVER_URL + "deleteMyImage";
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("imageId", imageId));
			
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			str = EntityUtils.toString(entity);
		} catch (Exception e)
		{
			Log.e("GetData.deleteMyImage()", e.toString());
			return e.toString();
		}
		return str;
	}
	
	public String getMyProfile()
	{
		String str = null;
		try
		{
			String url = App.SERVER_URL + "getMyProfile";
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getMyProfile()", e.toString());
			return e.toString();
		}
		return str;
		
	}
	
	public String getMyImages(String pageNo)
	{
		String str = null;
		try
		{
			String url = App.SERVER_URL + "getMyImages" + "?pageNo=" + pageNo;
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getMyImages()", e.toString());
			return e.toString();
		}
		return str;	
	}
	
	public String getBestImages(String country,String sex)
	{
		String str = null;
		try
		{
			String trimCountry = country.replace(" ", "%20");
			String url = App.SERVER_URL + "getBestImages" + "?country=" + trimCountry + "&sex=" + sex;
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getMyImages()", e.toString());
			return e.toString();
		}
		return str;	
	}

	public String getTopList(String country)
	{
		String str = null;
		try
		{
			String trimCountry = country.replace(" ", "%20");
			String url = App.SERVER_URL + "getTopList" + "?country="+trimCountry;
			if (country.equals(""))
			{
				url = App.SERVER_URL + "getTopList";
			}
			
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getTopList()", e.toString());
			return e.toString();
		}
		return str;
	}

	public String getProfile()
	{
		String str = null;
		try
		{
			String url = App.SERVER_URL + "getMyProfile";
			HttpResponse response = httpclient.execute(new HttpGet(url));
			HttpEntity r_entity = response.getEntity();
			str = EntityUtils.toString(r_entity);
		} catch (Exception e)
		{
			Log.e("GetData.getProfile()", e.toString());
			return e.toString();
		}
		return str;
	}

	public String ratePicture(long pictureId, int rate, boolean flag)
	{
		String str = null;

		try
		{
			String url = App.SERVER_URL + "rateImage";
			String id = String.valueOf(pictureId);
			String r = String.valueOf(rate);
			String b = String.valueOf(flag);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("imageId", id));
			nameValuePairs.add(new BasicNameValuePair("rate", r));
			nameValuePairs.add(new BasicNameValuePair("wrongFlag", b));
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			str = EntityUtils.toString(entity);
		} catch (Exception e)
		{
			Log.e("GetData.ratePicture()", e.toString());
			return e.toString();
		}
		return str;
	}

	public String createUser(String userName, String country, String email, String pass, String sex)
	{

		String str = null;

		try
		{

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("displayName", userName));
			nameValuePairs.add(new BasicNameValuePair("country", country));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", pass));
			nameValuePairs.add(new BasicNameValuePair("sex", sex));

			HttpPost httppost = new HttpPost(App.SERVER_URL + "createUser");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			str = EntityUtils.toString(entity);
		} catch (Exception e)
		{
			Log.e("GetData.createUser()", e.toString());
			return e.toString();
		}
		return str;
	}

	public String login(String userName, String pass)
	{

		String str = null;

		try
		{

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", userName));
			nameValuePairs.add(new BasicNameValuePair("password", pass));

			HttpPost httppost = new HttpPost(App.SERVER_URL + "loginUser");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			str = EntityUtils.toString(entity);
		} catch (Exception e)
		{
			Log.e("GetData.loginUser()", e.toString());
			return e.toString();
		}
		return str;
	}
}
