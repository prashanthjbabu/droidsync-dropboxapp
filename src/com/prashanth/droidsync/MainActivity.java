package com.prashanth.droidsync;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.dropbox.sync.android.DbxAccountManager;


public class MainActivity extends SherlockActivity  {

	private static final String appKey = "m8w5uz7aa4k3k24";
    private static final String appSecret = "etj8voezjuqujw6";

    private static final int REQUEST_LINK_TO_DBX = 0;
  
    private Button mLinkButton;
    private DbxAccountManager mDbxAcctMgr;
    private File dir;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		File sdCard = Environment.getExternalStorageDirectory();
		dir = new File (sdCard.getAbsolutePath() + "/prashdropsync/test/");
		dir.mkdirs();
		
		mLinkButton = (Button) findViewById(R.id.link_button);
		//startbutton = (Button) findViewById(R.id.start_button);
		//startbutton.setEnabled(false);
		mLinkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLinkToDropbox();
            }
        });
        
        /*startbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickLinkToDropbox();
        		int icon=R.drawable.ic_launcher;

            	servtodroid.servtodroidsync(DbxPath.ROOT,getApplicationContext(),syncoutput,mTestOutput,mDbxAcctMgr,icon);
            }
        });*/
      
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
	}
    private void onClickLinkToDropbox() {
        mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
    }

    private void startservice()
    {
    	WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	if (wifi.isWifiEnabled()==true)
    	{
    		Intent service = new Intent(this,droidsyncservice.class);
    		//service.putExtra("icon",icon);
    		this.startService(service);
    	}
    	else
    	{
    	   // not connected..dont do anything..
    	
    	}
    	//int icon=R.drawable.ic_launcher;
		
    }
	@Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
		    showLinkedView();
    		startservice();
		} else {
			showUnlinkedView();
		}
	}
    private void showLinkedView() {
        mLinkButton.setVisibility(View.GONE);
    }

    private void showUnlinkedView() {
        mLinkButton.setVisibility(View.VISIBLE);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
        		startservice();
            } else {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
		
}
