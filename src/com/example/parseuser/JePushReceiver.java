package com.example.parseuser;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class JePushReceiver extends BroadcastReceiver 
{
	private static final String TAG = "DAKSH";

	Context context;
	Intent intent;
	
	ScreenActivity screenActivity;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context = context;
		this.intent = intent;
		
		if(ScreenActivity.screenActivity != null)
		{
			this.screenActivity = (ScreenActivity) ScreenActivity.screenActivity;
		}
		
			if (intent == null)
			{
				Log.i(TAG, "Receiver intent null");
			}
			else
			{
				String action = intent.getAction();
				Log.i(TAG, "got action " + action );
				
				if (action.equals("com.example.parseuser.GOTMESSAGE"))
				{
					getMessage();
				}
			}

		}
	
	
	public void getMessage()
	{

		try
		{
		JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

		String myUsername = ParseUser.getCurrentUser().getUsername();
		
		String username;
		String message;
		String objId;
		
		username = json.getString("username");
		message  = json.getString("message");
		objId    = json.getString("objId");
		
		if(screenActivity != null)
		{
			Log.i(TAG, "UPDATING");
			screenActivity.updateList(username, message);
		}else
		{
			Log.i(TAG, "CREATING-NOTIFICATION");
			showNotification(username, message);
		}
		
		ParseObject pObject = ParseObject.createWithoutData("Messages", objId);
		pObject.add("to", myUsername );
		pObject.add("uInvolved", myUsername);
		
		pObject.saveInBackground(new SaveCallback() 
		{
			
			@Override
			public void done(ParseException e) 
			{
				if(e==null)
				{
					Log.i(TAG, "Message-Item-Updated");
				}else
				{
					Log.i(TAG, "Error:Message" + e.getMessage());
					Log.i(TAG, "Error:Code"    +  Integer.toString(e.getCode()));
				}
			}
		});
		
		//END TRY
		}catch (JSONException e)
		{
			Log.i(TAG, "JSONException: " + e.getMessage());
		}
	
	
	}
	
	@SuppressLint("NewApi")
	public void showNotification(String username, String message)
	{
		
		String title = username;
		String body  = message;
		
		Intent nIntent = new Intent(context,ScreenActivity.class); 
		//PendingIntent pi = PendingIntent.getActivity(context, 0, nIntent, 0);
		PendingIntent pi = PendingIntent.getActivity(context, 0, nIntent, PendingIntent.FLAG_ONE_SHOT);
		
		final int uniqueID = 813972;
		final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{

			Notification n = new Notification.Builder(context)
			     .setTicker(message)
			     .setContentTitle(title)
			     .setContentText(body)
			     .setAutoCancel(true)
			     .setContentIntent(pi)
			     .setDefaults(Notification.DEFAULT_ALL)
			     .setSmallIcon(R.drawable.ic_launcher)
			     .setWhen(System.currentTimeMillis())
			     .build();

			nm.notify(uniqueID,n);
		}else
		{
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			     .setTicker(message)
			     .setContentTitle(title)
			     .setContentText(body)
			     .setAutoCancel(true)
			     .setContentIntent(pi)
			     .setDefaults(Notification.DEFAULT_ALL)
			     .setSmallIcon(R.drawable.ic_launcher)
			     .setWhen(System.currentTimeMillis());
		
			nm.notify(uniqueID, mBuilder.build());
		}
	}
	
}

