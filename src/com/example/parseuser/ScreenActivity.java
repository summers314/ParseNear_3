package com.example.parseuser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class ScreenActivity extends Activity implements android.view.View.OnClickListener 
{
	Button bt1,bt2;
	TextView tv1;
	EditText et1,et2,et3,et4;
	String s1,s2,s3,s4;
	
	EditText et_message;
	Button bt_message;
	ListView lv1;
	
	//ArrayList<String> sList = new ArrayList<String>();
	//ArrayAdapter<String> adapter;
	
	ArrayList<LeMessages> messageList;
	ChatAdapter adapter;
	
	String s_message;
	String sUser;
	String name;
	String TAG = "FOODTAG";
	
	String uName;
	LePush sPush;
	static ScreenActivity screenActivity;
	
	RelativeLayout rl_login;
	RelativeLayout rl_chat;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.chat_options, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.action_logout:
			ParseInstallation pInstall = ParseInstallation.getCurrentInstallation();
			pInstall.put("user", "");
			pInstall.put("username", "");
			pInstall.saveInBackground();
			
			ParseUser.logOut();
			
			showLogin();
			
			break;
		}
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setContentView(R.layout.main_screen);
		
		initVariables();
		
		if(ParseUser.getCurrentUser()==null)
		{
			showLogin();
		}else
		{
			showChat();
		}
	}
	
	public void initVariables()
	{
		//COMMON
		rl_login = (RelativeLayout) findViewById(R.id.inc_login);
		rl_chat = (RelativeLayout) findViewById(R.id.inc_chat);
		
		//LOGIN
		bt1 = (Button) findViewById(R.id.bt1);
		bt2 = (Button) findViewById(R.id.bt2);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		
		et1 = (EditText) findViewById(R.id.et1);
		et2 = (EditText) findViewById(R.id.et2);
		et3 = (EditText) findViewById(R.id.et3);
		et4 = (EditText) findViewById(R.id.et4);
		
		tv1 = (TextView) findViewById(R.id.tv1);
		
		
		//CHAT
		screenActivity = this;
		et_message = (EditText) findViewById(R.id.editText1);
		messageList = new ArrayList<LeMessages>();
		lv1 = (ListView) findViewById(R.id.listView1);
		bt_message = (Button) findViewById(R.id.button1);
		bt_message.setOnClickListener(this);
	}
	
	public void showLogin()
	{
		rl_login.setVisibility(View.VISIBLE);
		rl_chat.setVisibility(View.GONE);
	}
	
	public void showChat()
	{
		rl_login.setVisibility(View.GONE);
		rl_chat.setVisibility(View.VISIBLE);
		
		messageList.add(new LeMessages("", "LOADING...", "PATIENCE."));
		
		adapter = new ChatAdapter(this, messageList);

		lv1.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		//lv1.setStackFromBottom(true);
		lv1.setAdapter(adapter);

		uName = ParseUser.getCurrentUser().getUsername();
		sPush = new LePush(this,uName);
		
		loadList();

		//FIRST TIME ONLY 
		//-------
		
		// Save the current Installation to Parse.
		ParseInstallation.getCurrentInstallation().saveInBackground();
		
		// When users indicate they are Giants fans, we subscribe them to that channel.
		PushService.subscribe(this, "SPA", ScreenActivity.class);
		
		// Associate the device with a user
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put("username",uName);
		installation.put("user",ParseUser.getCurrentUser());
		installation.saveInBackground();
		
		//-------
	}
	
	public void loadList()
	{
		//Check all messages with uName in 'To' and 'From'
		
		ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("Messages");
		//pQuery.whereContains("uInvolved", uName);
		pQuery.whereEqualTo("uInvolved", uName);
		//pQuery.orderByAscending("createdAt");
		pQuery.orderByDescending("createdAt");
		pQuery.setLimit(30);
		
		pQuery.findInBackground(new FindCallback<ParseObject>() 
		{
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) 
			{
				if(objects!=null)
				{
					Log.i(TAG, "Messages Retreved:" + Integer.toString( objects.size() ));
					
					messageList.clear();
					
					String fUser;
					String message;
					String status="";
					
					for(int i=0;i<objects.size();i++)
					{
						fUser   = objects.get(i).getString("fUser");
						message = objects.get(i).getString("message");
						
						messageList.add(new LeMessages(fUser, message, status));
					}
					
					Collections.reverse(messageList);
					
					adapter.notifyDataSetChanged();
				}else
				{
					Log.i(TAG, "Error:Message" + e.getMessage());
					Log.i(TAG, "Error:Code"    +  Integer.toString(e.getCode()));
				}
			}
		});
		
	}
	
	public void sendMessage()
	{
		//Add to adapter & Notify
		s_message = et_message.getText().toString();
		et_message.setText("");
		messageList.add(new LeMessages(uName, s_message, "Sending.") );
		adapter.notifyDataSetChanged();
		
		//Upload to Parse
		final ParseObject pObject = new ParseObject("Messages");
		pObject.put("fUser", uName);
		pObject.put("message", s_message);
		pObject.add("uInvolved", uName);
		//pObject.put("curLocation", "");
		//pObject.put("groupName", "Hostel");
		
		pObject.saveInBackground(new SaveCallback() 
		{
			@Override
			public void done(ParseException e) 
			{
				if(e==null)
				{
					Log.i(TAG, "Message:DONE");
					
					int mPosition = messageList.size() - 1 ;
					
					//Send Push
					sPush.sendPush(s_message, mPosition, pObject.getObjectId());
				}else
				{
					Log.i(TAG, "Error:Message" + e.getMessage());
					Log.i(TAG, "Error:Code"    +  Integer.toString(e.getCode()));
				}
			}
		});
		
		//Send Push
		//Update status & Notify
	}
	
	public void updateStatus(int mPosition)
	{
		messageList.get(mPosition).setStatus("Sent");
		adapter.notifyDataSetChanged();
	}
	
	public void updateList(String username, String message)
	{
		messageList.add(new LeMessages(username, message, ""));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.bt1:
			signUp();
			break;
			
		case R.id.bt2:
			logIn();
			break;
		
		case R.id.button1:
			sendMessage();
			break;
		}
	}
	
	public void signUp()
	{
		s1 = et1.getText().toString();
		s2 = et2.getText().toString();
		
		ParseUser user = new ParseUser();
		user.setUsername(s1);
		user.setPassword(s2);
		
		//Other fields can be set just like with ParseObject
		user.put("name", "n"+s1);
		
		user.signUpInBackground(new SignUpCallback() 
		{
			  public void done(ParseException e) 
			  {
			    if (e == null)
			    {
			      // Hooray! Let them use the app now.
			    	Toast.makeText(ScreenActivity.this, "Woohoo!", Toast.LENGTH_SHORT).show();
			    	
			    	launchActivity();
			    } else 
			    {
			      // Sign up didn't succeed. Look at the ParseException
			      // to figure out what went wrong
			    	
			    	Toast.makeText(ScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			    }
			  }
		});
	}
	
	public void logIn()
	{
		s3 = et3.getText().toString();
		s4 = et4.getText().toString();
		
		ParseUser.logInInBackground(s3, s4, new LogInCallback() 
		{
			  public void done(ParseUser user, ParseException e) {
			    if (user != null) 
			    {
			      // Hooray! The user is logged in.
			    	Toast.makeText(ScreenActivity.this, "Woohoo!", Toast.LENGTH_SHORT).show();
			    	name = user.getString("name");
			    	actionComplete(true);
			    	
			    	launchActivity();
			    } else 
			    {
			      // Signup failed. Look at the ParseException to see what happened.
			    	Toast.makeText(ScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			    	actionComplete(false);
			    }
			  }
			});
	}
	
	public void actionComplete(boolean success)
	{
		//Call this method once SignUp/SignIn is successful
		if(success)
		{
			tv1.setText("Hooray!");
		}else
		{
			tv1.setText("Woops!");
		}
	}
	
	public void launchActivity()
	{
		//Hide Login-Layout
		//Show Chat-Layout
		showChat();
	}

	

}
