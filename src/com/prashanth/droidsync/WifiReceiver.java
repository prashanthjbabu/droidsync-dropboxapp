package com.prashanth.droidsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {     
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) 
            {
        	System.out.println("WIFI ON");
        	  Intent service = new Intent(context, droidsyncservice.class);
  			context.startService(service);
            }
        else
        	{
        	System.out.println("WIFI OFF");
        	  Intent service = new Intent(context, droidsyncservice.class);
  			context.stopService(service);
        	}
    }   
};