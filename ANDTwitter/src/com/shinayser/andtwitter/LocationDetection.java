package com.shinayser.andtwitter;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationDetection implements LocationListener{

	private Location loc;
	
	public Location getLocation() {
		return loc;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
