package com.shinayser.andtwitter;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class PostTweet extends Activity implements OnClickListener, OnCheckedChangeListener{

	private Twitter twitter;
	private Location loc;
	private LocationDetection locationDetection = new LocationDetection();
	private LocationManager manager;
	//private boolean checked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.posttweet);
		
		twitter = (Twitter) getIntent().getSerializableExtra("twitter");
		
		Button but = (Button) findViewById(R.id.but_post_tweet);
		but.setOnClickListener(this);
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.post_check_box);
		checkbox.setOnCheckedChangeListener(this);
		//startLocationPositioning();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (manager!=null){
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				CheckBox cb = (CheckBox) findViewById(R.id.post_check_box);
				cb.setChecked(false);
			}
		}
		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if (isChecked) {
			manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationDetection);		
			else{
				Toast.makeText(PostTweet.this, "Your GPS is turned off. \nPlease turn it on to use location detection.", Toast.LENGTH_LONG).show();
				CheckBox cb = (CheckBox) findViewById(R.id.post_check_box);
				cb.setChecked(false);
			}
				
		}
		else {
			manager.removeUpdates(locationDetection);
		} 
		//checked = isChecked;
		
	}
	
	@Override
	public void onClick(View v) {

		EditText tv = (EditText) findViewById(R.id.posttweet_edit_text1);
		
		try {
			String tweet = tv.getText().toString().trim();
			if (!tweet.equals(""))
			{
				StatusUpdate status = new StatusUpdate(tweet);
				
				if ( manager.isProviderEnabled(LocationManager.GPS_PROVIDER) )
				 {					
					while(locationDetection.getLocation()==null);
					GeoLocation loc = new GeoLocation(locationDetection.getLocation().getLatitude(), locationDetection.getLocation().getLatitude());
					status.setLocation(loc);					
				 }
				
				twitter.updateStatus(status);
			    
				finish();
			}
			else
				Toast.makeText(PostTweet.this, "Type a message before posting1", Toast.LENGTH_SHORT);
			
		} catch (TwitterException e) {
			Toast.makeText(PostTweet.this, "Error updating your status.", Toast.LENGTH_LONG);
		}					
		
				
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
		if (manager!=null)
			manager.removeUpdates(locationDetection);
		
	}
	
	/*private void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Yout GPS is disabled, do you want to turn it on?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}*/
	
	
	
}
