package com.inobix.messangero;

public class MenuItem
{
	private int ImageID = 0;
	private String Caption = "";
	
	public int getImageID()
	{
		return ImageID;
	}
	public void setImageID(int imageID)
	{
		ImageID = imageID;
	}
	public String getCaption()
	{
		return Caption;
	}
	public void setCaption(String caption)
	{
		Caption = caption;
	}
	
	public MenuItem(int imageID, String caption)
	{
		this.ImageID = imageID;
		this.Caption = caption;
	}
}
