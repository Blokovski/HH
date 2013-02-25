package com.nm.tits;

import java.sql.Date;

public class Picture
{
	private int pictureId;
	private String pictureUrl;
	private String thumbUrl;
	private String country;
	private String city;
	private double locX;
	private double locY;
	private int rating;
	private Long id;
	private Date dateCreated;
	private String dateString;

	private String sex;

	public int getPictureId()
	{
		return pictureId;
	}

	public void setPictureId(int pictureId)
	{
		this.pictureId = pictureId;
	}

	public String getPictureUrl()
	{
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl)
	{
		this.pictureUrl = pictureUrl;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public double getLocX()
	{
		return locX;
	}

	public void setLocX(double locX)
	{
		this.locX = locX;
	}

	public double getLocY()
	{
		return locY;
	}

	public void setLocY(double locY)
	{
		this.locY = locY;
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDateCreated()
	{
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getDateString()
	{
		return dateString;
	}

	public void setDateString(String dateString)
	{
		this.dateString = dateString;
	}

	public String getThumbUrl()
	{
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl)
	{
		this.thumbUrl = thumbUrl;
	}
}
