package com.example.parseuser;

import com.parse.Parse;
import com.parse.PushService;

public class Application extends android.app.Application 
{

	  public Application() 
	  {
	  }

	  @Override
	  public void onCreate() 
	  {
	    super.onCreate();
	    
	    //Enabling LocalDatastore
	    //Parse.enableLocalDatastore(this);
	    
	    //Registering ParseSubClass
	    //ParseObject.registerSubclass(Puser.class);

		// Initialize the Parse SDK
		// Required to use Parse
	    Parse.initialize(this, "", "");
		
		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, ScreenActivity.class);
		//PushService.setDefaultPushCallback(this, AnotherActivity.class);
	  }

	}
