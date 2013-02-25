package com.nm.tits;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;



public class JsonParser
{
	public static Picture parsePicture(String input)
	{
		final Picture picture = new Picture();

		try
		{
			JSONArray jsonArray = new JSONArray(input);
			//JSONObject jsonObject = new JSONObject(input);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			picture.setPictureId(jsonObject.getInt("pictureId"));
			picture.setPictureUrl(jsonObject.getString("pictureUrl"));
			picture.setCountry(jsonObject.getString("country"));
			picture.setCity(jsonObject.getString("city"));
			picture.setLocX(jsonObject.getDouble("locX"));
			picture.setLocY(jsonObject.getDouble("locY"));
			picture.setRating(jsonObject.getInt("rating"));
			
			
		} catch (JSONException e)
		{
			Log.e("JsonParser.parseNews()", e.toString());
			return null;
		}

		return picture;
	}
	
	public static Profile parseProfile(String input)
	{
		final Profile profile = new Profile();

		try
		{
			
			JSONObject jsonObject = new JSONObject(input);
			
			profile.setDisplayName(jsonObject.getString("displayName"));
			try
			{
				profile.setAverageRating(jsonObject.getString("averageRating"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			try
			{
				profile.setNumberOfPictures(jsonObject.getString("numberOfPictures"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			try
			{
				profile.setAvatar(jsonObject.getString("avatar"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			profile.setEmail(jsonObject.getString("email"));
			try
			{
				profile.setTopListPosition(jsonObject.getString("topListPosition"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			try
			{
				profile.setCountry(jsonObject.getString("country"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			try
			{
				profile.setCity(jsonObject.getString("city"));
			} catch (Exception e)
			{
				Log.w("", e.toString());
			}
			
			
			
		} catch (JSONException e)
		{
			Log.e("JsonParser.parseProfile()", e.toString());
			return null;
		}

		return profile;
	}
	
	public static List<Picture> parseProfilePictures(String input)
	{
		Picture currentPicture;
		final List<Picture> messages = new ArrayList<Picture>();
		try
		{
			JSONArray jsonArray = new JSONArray(input);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				currentPicture = new Picture();
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				currentPicture.setId(jsonObject.getLong("id"));
				currentPicture.setDateString(jsonObject.getString("dateCreated"));
				try
				{
					currentPicture.setRating(jsonObject.getInt("rating"));
				} catch (Exception e)
				{
					
				}
				currentPicture.setLocX(jsonObject.getDouble("locX"));
				currentPicture.setLocY(jsonObject.getDouble("locY"));
				currentPicture.setSex(jsonObject.getString("sex"));
				currentPicture.setThumbUrl(jsonObject.getString("url").replace(".jpg", "t.jpg"));
				currentPicture.setPictureUrl(jsonObject.getString("url"));
				try
				{
					currentPicture.setCountry(jsonObject.getString("country"));
				} catch (Exception e)
				{
					currentPicture.setCountry("Worldwide");
				}
				try
				{
					currentPicture.setCity(jsonObject.getString("city"));
				} catch (Exception e)
				{
					
				}
							
				messages.add(currentPicture);
				
			}
		} catch (Exception e)
		{
			Log.e("JsonParser.parseProfilePictures", e.toString());
			return null;		
		}
		return messages;
	}
	
	public static List<Hunter> parseTopList(String input) 
	{
		Hunter currentHunter;
		final List<Hunter> messages = new ArrayList<Hunter>();
		try
		{

			JSONArray jsonArray = new JSONArray(input);

			for (int i = 0; i < jsonArray.length(); i++)
			{
				currentHunter = new Hunter();
				
				JSONArray ja = jsonArray.getJSONArray(i);
				
				currentHunter.setDisplayName(ja.getString(0));
				currentHunter.setAverageRating(ja.getString(3));
				currentHunter.setNumberOfPictures(ja.getString(2));
				currentHunter.setPlaceAtList(Integer.toString(i+1));
				
				messages.add(currentHunter);
			}

		} catch (JSONException e)
		{
			Log.e("JsonParser.parseHomeNews", e.toString());
			return null;
		}

		return messages;
	}
}
