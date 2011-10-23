package com.shinayser.andtwitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TimeLine extends ListActivity {

	private List<Status> timeline;
	private TweetAdapter adapter;
	private Twitter twitter;
	private boolean verify_tweets = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		twitter = (Twitter) getIntent().getSerializableExtra("twitter");
		
		adapter = new TweetAdapter();
		setListAdapter(adapter);				
	
		final Handler notificator = new Handler();
		notificator.postDelayed(new Runnable() {
			 
			@Override
			public void run() {
				
				if (verify_tweets)
				{
					startUpdateProcess();
					notificator.postDelayed(this, 1000);
				}
				
			}
		}, 1000 * 300 ); //5 minutos
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menu_new_tweet: 
				Intent it = new Intent(this, PostTweet.class);
				it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				it.putExtra("twitter", twitter);
				startActivity(it);
				return true;
				
			case R.id.menu_update: onResume();
		}
				
		return false;
		
	}
	
		
	@Override
	protected void onResume() {
		super.onResume();		
		startUpdateProcess();		
	}
	
	private void startUpdateProcess() {
		setProgressBarIndeterminateVisibility(true);

		Handler updater = new Handler();
		
		updater.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					List<Status> list = twitter.getHomeTimeline();
					
					if (list.get(0).getCreatedAt().getTime() != timeline.get(0).getCreatedAt().getTime() )
					{
						adapter.updateTweets(list);
						
						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
						List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
						
						final String packageName = TimeLine.this.getPackageName();
						if (l!=null){
								for (RunningAppProcessInfo app: l){
									if (app.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND && app.processName.equals( packageName ) ){
										notificate();
										break;
									}
								}
						}
						
					}
				} catch (TwitterException e) {					
					Toast.makeText(TimeLine.this, "An error occured when updating tweets.\nPlease try again later.", Toast.LENGTH_LONG).show();
				}

			}
		}, 100);
	}
	
	public void notificate(){
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
		Notification notification = new Notification(R.drawable.tweeter_bird, "New tweet!", System.currentTimeMillis());				
		
		notification.flags = Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL;
		
		PendingIntent pending = PendingIntent.getActivity(TimeLine.this, 1, getIntent(), PendingIntent.FLAG_CANCEL_CURRENT);		
				
		notification.setLatestEventInfo(TimeLine.this, "You got new updates!", "You got new updates!", pending);
		nm.notify(1, notification);
	}
	
	private class TweetAdapter extends BaseAdapter {
		
		private List<Twitter_DAO> tweets;
		
		@SuppressWarnings("deprecation")
		public TweetAdapter() {
			try {
				timeline = twitter.getHomeTimeline();
				tweets = new ArrayList<Twitter_DAO>();
				for (Status s: timeline) {
					Twitter_DAO td = new Twitter_DAO();
					td.setNome(s.getUser().getName());
					td.setTxt(s.getText());
					td.setImg( s.getUser().getProfileImageURL() );
					td.setDate(s.getCreatedAt());
					tweets.add(td);
				}
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
		}
		
		public void updateTweets(List<Status> tl) {			
			timeline = tl;
			
			tweets = new ArrayList<Twitter_DAO>();
			for (Status s: timeline) {
				Twitter_DAO td = new Twitter_DAO();
				td.setNome(s.getUser().getName());
				td.setTxt(s.getText());
				td.setImg( s.getUser().getProfileImageURL() );
				td.setDate(s.getCreatedAt());
				tweets.add(td);
			}
			
			notifyDataSetChanged();					
		}
		
		@Override
		public int getCount() {
			return timeline.size();
		}

		@Override
		public Object getItem(int position) {
			return timeline.get(position);
		}

		@Override
		public long getItemId(int position) {
			return timeline.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) TimeLine.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View v = inflater.inflate(R.layout.listitem, null);
			
			TextView nome = (TextView) v.findViewById(R.id.tweet_owner);
			TextView txt = (TextView) v.findViewById(R.id.tweet_text);
			TextView date = (TextView) v.findViewById(R.id.tweet_date);
			ImageView img = (ImageView) v.findViewById(R.id.imageView1);
			
			Twitter_DAO st = tweets.get(position);
			
			//String nomeText = st.getUser().getName();
					
			date.setText( getTweetDateText( st.getDate() ));
			nome.setText( "@"+st.getNome() );			
			txt.setText( st.getTxt() );
			img.setImageBitmap( st.getImg() );
			
			return v;
		}
		
		private String getTweetDateText(Date date) {
			
			long aux = date.getTime();
			long now = System.currentTimeMillis();
			
			long minuto = 60*1000;
			long hora = 60*minuto;
			long dia = 24*hora;
			
			
			if ( (now-aux) >= dia )
				return ""+date.getDay()+"/"+date.getMonth()+"/"+date.getYear();
			
			if ( (now-aux) >= hora  )
			{
				if ((now-aux)/hora == 1)
					return ""+(now-aux)/hora+" hour ago.";
				else
					return ""+(now-aux)/hora+" hours ago.";
			}
			
			if ( (now-aux) >= minuto  )
			{
				if ((now-aux)/minuto == 1)
					return ""+(now-aux)/minuto+" minute ago.";
				else
					return ""+(now-aux)/minuto+" minutes ago.";
			}
							
			return "Some seconds ago...";
		}
		
		
	}
	
	
}
