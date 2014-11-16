package com.example.parseuser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;

public class LePush 
{
	Context context;
	String uName;

	public LePush(Context context, String uName) 
	{
		this.context = context;
		this.uName = uName;
	}
	
	public void sendPush(String message, final int mPosition, String objId)
	{
		//Push Parameters
		//Push jSon
		//Update status
		

		//Key   : Friends
		//Value : CurrentUsername
		//ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		//query.whereEqualTo("friends", ParseUser.getCurrentUser().getUsername() );
		
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereEqualTo("channels", "SPA");
		//query.whereContains("username", uName);
		query.whereNotEqualTo("username",uName);
		
		JSONObject obj=null;
		
		obj =new JSONObject();
		try 
		{
			//obj.put("title" , uName);
			//obj.put("alert" , message);
			obj.put("action","com.example.parseuser.GOTMESSAGE");
			obj.put("username", uName);
			obj.put("message",  message );
			obj.put("objId",    objId );
		} catch (JSONException e1) {
			e1.printStackTrace();
		}


		ParsePush push = new ParsePush();
		push.setQuery(query);
		//push.setChannel("SPA");
		push.setData(obj);
		
		push.sendInBackground(new SendCallback() 
		{
			@Override
			public void done(ParseException e) 
			{
				if(e==null)
				{
					Log.i("DAKSH","Push-Sent");
					
					//Update Status
					ScreenActivity sActivity = (ScreenActivity) context;
					sActivity.updateStatus(mPosition);
				}else
				{
					Log.i("DAKSH","Push-Error");
					Log.i("DAKSH","Error:"+Integer.toString(e.getCode()) );
					Log.i("DAKSH","Error:"+e.getMessage() );
				}
				
			}
		});
		
	
		

	}
	
	
}
