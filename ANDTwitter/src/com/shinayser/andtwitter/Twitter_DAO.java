package com.shinayser.andtwitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import twitter4j.GeoLocation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Twitter_DAO {

	private static Map<URL, Bitmap> imageMap = new HashMap<URL, Bitmap>();
	
	private Date date;
	private String nome, txt;
	private URL imgUrl;
	private GeoLocation geo;
	
	
	
	public GeoLocation getGeo() {
		return geo;
	}
	public void setGeo(GeoLocation geo) {
		this.geo = geo;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public Bitmap getImg() {
		
		Bitmap bat = imageMap.get(imgUrl);
		
		if (bat==null) {
			downloadImage(imgUrl);
			bat = imageMap.get(imgUrl);
		}
		
		return bat;
	}
	public void setImg(URL url) {
		this.imgUrl = url;
	}

	public static void addImageToDatabase(URL key, Bitmap image) {
		imageMap.put(key, image);
	}
	
	private void downloadImage(URL url){
        
		try {
               HttpURLConnection conn = (HttpURLConnection)url.openConnection();
               conn.setDoInput(true);
               conn.connect();
               InputStream is = conn.getInputStream();
               
               Bitmap bmImg;
               
               bmImg = BitmapFactory.decodeStream(is);
               conn.disconnect();
               
               imageMap.put(url, bmImg);
               
          } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
          }
          
     }
	
}
