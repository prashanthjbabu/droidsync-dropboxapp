package com.prashanth.droidsync;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxPath;


public class MainActivity extends Activity  {

	private static final String appKey = "m8w5uz7aa4k3k24";
    private static final String appSecret = "etj8voezjuqujw6";

    private static final int REQUEST_LINK_TO_DBX = 0;
    private TextView mTestOutput;
    private TextView readyfield;
    private TextView syncoutput;
    private Button mLinkButton;
    private DbxAccountManager mDbxAcctMgr;
    private File dir;
    private Button startbutton;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		File sdCard = Environment.getExternalStorageDirectory();
		dir = new File (sdCard.getAbsolutePath() + "/prashdropsync/test/");
		dir.mkdirs();
		mTestOutput = (TextView) findViewById(R.id.test_output);
		readyfield = (TextView) findViewById(R.id.ready_field);
		syncoutput = (TextView) findViewById(R.id.sync_output);
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
    	int icon=R.drawable.ic_launcher;
		Intent service = new Intent(this, droidsyncservice.class);
		service.putExtra("icon",icon);
		this.startService(service);
    }
	@Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
		    showLinkedView();
		    readyfield.setText("Ready to sync");
    		
    		startservice();
        	//servtodroid.servtodroidsync(DbxPath.ROOT,getApplicationContext(),mDbxAcctMgr,icon);

		    //startbutton.setEnabled(true);
//		    servtodroidsync();
		} else {
			showUnlinkedView();
		}
	}
    private void showLinkedView() {
        mLinkButton.setVisibility(View.GONE);
        mTestOutput.setVisibility(View.VISIBLE);
    }

    private void showUnlinkedView() {
        mLinkButton.setVisibility(View.VISIBLE);
        mTestOutput.setVisibility(View.GONE);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
    		    readyfield.setText("Ready to sync");
//    		    startbutton.setEnabled(true);
        		int icon=R.drawable.ic_launcher;
        		startservice();
            	servtodroid.servtodroidsync(DbxPath.ROOT,getApplicationContext(),mDbxAcctMgr,icon);
        		        		
            } else {
                mTestOutput.setText("Link to Dropbox failed or was cancelled.");
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
