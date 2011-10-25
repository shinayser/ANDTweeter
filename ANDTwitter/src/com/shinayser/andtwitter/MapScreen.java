package com.shinayser.andtwitter;

import android.os.Bundle;
import android.view.Window;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;


public class MapScreen extends MapActivity {

	public static String GOOGLE_MAPS_KEY = "ABQIAAAAOvqTfntWfzPJM12899WA4RQnQVTxx0t_ZGnTzfHlujNdLbXm0BTvktm_3xLmXy7cAp-IhLZuclh9pw";
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MapView map = new MapView(this, GOOGLE_MAPS_KEY);
		setContentView(map);		
		
		GeoPoint point = new GeoPoint( (int) getIntent().getExtras().getDouble("latitude")*1000000 , (int) getIntent().getExtras().getDouble("longitude")*1000000);
		map.getController().setCenter(point);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}

	
}
