package com.shinayser.andtwitter;

import java.util.concurrent.TimeoutException;

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
import android.net.ConnectivityManager;
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
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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

		final EditText tv = (EditText) findViewById(R.id.posttweet_edit_text1);
		
		if (isOnline())
		{
			setProgressBarIndeterminateVisibility(true);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					try {
						String tweet = tv.getText().toString().trim();
						if (!tweet.equals(""))
						{
							StatusUpdate status = new StatusUpdate(tweet);
							
							if ( manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) )
							 {			
								long starttime = System.currentTimeMillis();								
								while(locationDetection.getLocation()==null){
									if (System.currentTimeMillis() - starttime > 10000)
										throw new TimeoutException();
								}
								GeoLocation loc = new GeoLocation(locationDetection.getLocation().getLatitude(), locationDetection.getLocation().getLatitude());
								status.setLocation(loc);					
							 }
							
							twitter.updateStatus(status);
							setProgressBarIndeterminateVisibility(false);
							finish();
						}
						else
							Toast.makeText(PostTweet.this, "Type a message before posting!", Toast.LENGTH_SHORT).show();
						
					} catch (TwitterException e) {
						Toast.makeText(PostTweet.this, "Error updating your status.", Toast.LENGTH_LONG).show();
					} catch (TimeoutException e) {
						Toast.makeText(PostTweet.this, "Your location could not be detected.", Toast.LENGTH_SHORT).show();
						setProgressBarIndeterminateVisibility(false);
					}		
					
					setProgressBarIndeterminateVisibility(false);
				}
			}, 1000);
								
		}
		else {
			Toast.makeText(PostTweet.this, "Please, check your internet connection.", Toast.LENGTH_LONG).show();
		}
				
	}
	
	public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		 }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
		if (manager!=null)
			manager.removeUpdates(locationDetection);
		
	}
	
}
