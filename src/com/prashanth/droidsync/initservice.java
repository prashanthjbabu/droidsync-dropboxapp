package com.prashanth.droidsync;

import java.io.File;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxPath;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class initservice extends Service {
	    @Override
	 public IBinder onBind(Intent arg0) {  
	  Log.d(getClass().getSimpleName(), "onBind()");
	  return null;
	 }  
	 
	 
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	   
	
		 
		  

		  return Service.START_STICKY;
	  }

	    private void toastmessage(final String msg)
	    {
			Handler h = new Handler(this.getMainLooper());

		    h.post(new Runnable() {
		        @Override
		        public void run() {
		             Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
		        }
		    });

	    }
	   
	    @Override 
	    public void onCreate() {
	     super.onCreate();
	     //toastmessage("DroidSync Service Created");
	   
	     
	    }
	    
	    @Override
	    public void onDestroy() {
	     super.onDestroy();
	     toastmessage("DroidSync Service Destroyed");
	    }
	    
	    @Override
	    public void onStart(Intent intent, int startId) {
	     super.onStart(intent, startId);  
	    // toastmessage("DroidSync Service Started");
	    }

	  
}