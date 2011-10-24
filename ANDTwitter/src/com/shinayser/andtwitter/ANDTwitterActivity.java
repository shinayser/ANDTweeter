package com.shinayser.andtwitter;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ANDTwitterActivity extends Activity {
    
	public static String CONSUMER_KEY = "JGDGQkK06QfQDRSOMxCTQ";
	public static String CONSUMER_SECRET= "cz4bZc6uiVQL92QflxDCC99vaWnkQCARsMQZTuPRrHA";
	//public static String ACCESS_TOKEN = "67791138-vKYrviXCeYn0TEUwwVEvEaIPTbSN2uEbaVfYdQC7I";
	//public static String ACCESS_TOKEN_SECRET = "Qa8yThyheBWp54I4o2zylfqpe6bv05pjgAqPHssPs";
	//private ProgressDialog dialog; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
          .setOAuthConsumerKey(CONSUMER_KEY)
          .setOAuthConsumerSecret(CONSUMER_SECRET);
        final TwitterFactory tf = new TwitterFactory(cb.build());
         
        
		Button button = (Button) findViewById(R.id.button_ok);
		button.setOnClickListener(new OnClickListener() {					
			
			@Override
			public void onClick(View v) {
				
				if (isOnline())
				{
					setProgressBarIndeterminateVisibility(true);							
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							try {
								
					        	EditText login = (EditText) findViewById(R.id.editText1);
					        	EditText pass = (EditText) findViewById(R.id.editText2);
								        	
					        	Twitter twitter = tf.getInstance();           
					        	RequestToken rt = twitter.getOAuthRequestToken();  	
					        	        	    		
					    		String pin = getPIN(""+login.getText(), ""+pass.getText(), rt);
					    		
					    		if (pin==null)
					    		{
					    			Toast.makeText(ANDTwitterActivity.this, "Invalid login or password.", Toast.LENGTH_LONG).show();
					    			//dialog.dismiss();
					    		}
					    		else
					    		{
					    			AccessToken at = twitter.getOAuthAccessToken(rt, pin);
					    			
					    			Intent it = new Intent(ANDTwitterActivity.this, TimeLine.class);
					    			it.putExtra("twitter", twitter);
					    			it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					    			setProgressBarIndeterminateVisibility(false);
					    			startActivity(it);
					    			finish();		 			
					    			
					    		}
					    		
					    		
							} catch (Exception e) {
								Log.i("M", ""+e.getMessage());
							}
							
							setProgressBarIndeterminateVisibility(false);
							
						}
					}, 1000);				
					
				}
				else {
					Toast.makeText(ANDTwitterActivity.this, "No internet access.", Toast.LENGTH_SHORT).show();
				}
				
			}
		});		
		
    }
    
	public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 return cm.getActiveNetworkInfo().isConnectedOrConnecting();

	}
	
    public String getPIN(String login, String password, RequestToken requestToken){
    	HttpPost post = new HttpPost("http://api.twitter.com/oauth/authenticate");
    	
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("oauth_token", ""+requestToken.getToken()));
        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
        nameValuePairs.add(new BasicNameValuePair("session[username_or_email]", login));
        nameValuePairs.add(new BasicNameValuePair("session[password]", password));
        
        try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			HttpClient client = new DefaultHttpClient();
	    	HttpResponse resp = client.execute(post);        	
			
			BasicResponseHandler handler = new BasicResponseHandler();
			
			String pin = handler.handleResponse(resp);
			
			pin = pin.substring( pin.indexOf("<code>")+6 , pin.indexOf("</code>"));
			
			return pin;
		} catch (Exception e) {
			Log.i("M", ""+e.getMessage());
		}     	
        
		return null;
        
    	
    }
        
}