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

public class droidsyncservice extends Service {
	 
	 private static final String appKey = "m8w5uz7aa4k3k24";
	 private static final String appSecret = "etj8voezjuqujw6";

	    private static final int REQUEST_LINK_TO_DBX = 0;
	    private DbxAccountManager mDbxAcctMgr;
	    private File dir;
	    private int icon;
	    @Override
	 public IBinder onBind(Intent arg0) {  
	  Log.d(getClass().getSimpleName(), "onBind()");
	  return null;
	 }  
	 
	 
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    //TODO do something useful
		 toastmessage("hello service world");
		 //icon=(int)intent.getIntExtra("icon", 0);
		 icon=R.drawable.ic_launcher;
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			File sdCard = Environment.getExternalStorageDirectory();
			dir = new File (sdCard.getAbsolutePath() + "/prashdropsync/test/");
			dir.mkdirs();
		  mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
		  if (mDbxAcctMgr.hasLinkedAccount()) {

			  servtodroid.servtodroidsync(DbxPath.ROOT,getApplicationContext(),mDbxAcctMgr,icon);
  
		  }
		  else
				 toastmessage("Open the DroidSync App and link your Dropbox Account to it");

		  

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
	     toastmessage("DroidSync Service Created");
	    }
	    
	    @Override
	    public void onDestroy() {
	     super.onDestroy();
	     toastmessage("DroidSync Service Destroyed");
	    }
	    
	    @Override
	    public void onStart(Intent intent, int startId) {
	     super.onStart(intent, startId);  
	     toastmessage("DroidSync Service Started");
	    }

	  
}