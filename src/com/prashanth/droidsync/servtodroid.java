package com.prashanth.droidsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFile.Listener;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.PathListener;
import com.dropbox.sync.android.DbxFileSystem.PathListener.Mode;
import com.dropbox.sync.android.DbxPath;

public class servtodroid {
	private static TextView mTestOutput;
    private static TextView syncoutput;
    private static NotificationManager notificationManager;

    private static DbxAccountManager mDbxAcctMgr;
    private static File dir;
    static Context c;
    static int myicon;
	private static void createfiles(final DbxFileSystem dbxFs,DbxPath dbpath) {
		try{
			List<DbxFileInfo> infos = dbxFs.listFolder(dbpath);
	        //mTestOutput.setText("\nContents of app folder:\n");
	        //delete files that have been deleted on dropbox
			makenotification(1,"Downloading all new files");
	        
	        
	        for (final DbxFileInfo info : infos) {
	            //mTestOutput.append("    " + info.path + ", " + info.modifiedTime + '\n');
	            if(dbxFs.isFile(info.path))
	            {
	            	final File file = new File(dir, info.path.toString().substring(1));
	                if(!file.exists())
	                {
	                				makenotification(1,"Downloading file "+file.toString());
	                	        	DbxFile myfile = dbxFs.open(info.path);
					                FileInputStream fs=myfile.getReadStream();
				                	toastmessage("Downloading file "+myfile.toString());
					                FileOutputStream f = new FileOutputStream(file);
				                	int read = 0;
				            		byte[] bytes = new byte[1024];
				             
				            		while ((read = fs.read(bytes)) != -1) {
				            			f.write(bytes, 0, read);
				            		}
				            		f.close();
					                fs.close();
					                myfile.close();
					                //notificatoinManager.cancel(2);
	                }
	                else
	                {
	                	//file already exists check for updates
	                	
	                }
				}
	            else
	            {
	            	//it is a folder
	            	System.out.println("this is a folder");
	            	File mydir = new File (dir.getAbsolutePath() + info.path.toString());
	        		mydir.mkdirs();
	        		createfiles(dbxFs,info.path);
	                dbxFs.addPathListener(mylistener, info.path, Mode.PATH_OR_DESCENDANT);

	            }
	       
	        }

			
		}catch(Exception e)
		{
			System.out.println("LOOK HERE "+e.toString());
		}
		
				
	}
	
	static void servtodroidsync(final DbxPath pathtosync,Context context,DbxAccountManager manager,int icon) {
        try {
        	myicon=icon;
        	mDbxAcctMgr=manager;
        	c=context;
        	notificationManager = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        	 File sdCard = Environment.getExternalStorageDirectory();
        		dir = new File (sdCard.getAbsolutePath() + "/prashdropsync/");
        		dir.mkdirs();
        		toastmessage("Starting the process");
        		makenotification(1,"Performing Initial Sync");
            // Create DbxFileSystem for synchronized file access.
          //  syncoutput.setText("INITIAL SYNC STARTED");
        	final DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            dbxFs.syncNowAndWait();
            // Print the contents of the root folder.  This will block until we can
            // sync metadata the first time.
            dbxFs.addPathListener(mylistener, pathtosync, Mode.PATH_OR_DESCENDANT);
           
			Thread thread=new Thread()
			{
				public void run()
				{
					toastmessage("Deleting unwanted files");
		            deleteunwantedfiles(dbxFs,pathtosync);
				}
			};
			thread.start();

			Thread thread2=new Thread()
			{
				public void run()
				{
					toastmessage("Creating the required files");
		            createfiles(dbxFs,pathtosync);
				}
			};
			thread2.start();
			thread.join();
			thread2.join();
			//notificatoinManager.cancel(1);
			makenotification(1,"Initial Sync Complete");
            // Create a test file only if it doesn't already exist.
           // syncoutput.setText("INITIAL SYNC COMPLETE");
        } catch (Exception e) {
            //mTestOutput.setText("Dropbox test failed: " + e);
        }
    }
	private static void deleteunwantedfiles(DbxFileSystem dbfs, DbxPath registeredPath)
	{
		try{
			makenotification(1,"Deleting files");
			List<DbxFileInfo> infos = dbfs.listFolder(registeredPath);
	 		//some change has happened in registered path , look for deletion
	 		File myfile=null;
			if(registeredPath.toString().length()>1)
	 			myfile=new File(dir,registeredPath.toString().substring(1));
	 		else
	 			myfile=new File(dir.toString());
			
	 		File[] myfiles=myfile.listFiles();
			int flag=0;
			for (int fileInList = 0; fileInList < myfiles.length; fileInList++)  
	 		{  
				flag=0;
				String filename=myfiles[fileInList].getName();
				for (DbxFileInfo info : infos) {
					String dbfile=info.path.toString().substring(info.path.toString().lastIndexOf("/")+1);
					//System.out.println("dbfile = "+dbfile+"local file =  "+filename);
					if(filename.equals(dbfile))
						flag=1;
				}
				if(flag == 0)
				{
					toastmessage("Deleting File "+myfiles[fileInList].toString());
					//file object not found.. delete the file or directory
					if(myfiles[fileInList].isFile())
						myfiles[fileInList].delete();					
					else
						deleteDir(myfiles[fileInList]);
					
				}

	 		} 
			
		}catch(Exception e){
			System.out.println("LOOK delete"+e.toString());
		}
	}
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                                return false;
                        }
                }
        }
        return dir.delete();
	}
	private static PathListener mylistener = new PathListener() {
        @Override
        public void onPathChange(final DbxFileSystem dbfs,final DbxPath registeredPath, Mode registeredMode) {
        	 	try{
        	 		
        	 		Thread thread=new Thread()
					{
						public void run()
						{
							pathchangefunc(dbfs,registeredPath);
							
						}
					};
					thread.start();
					
        	 		        	 	}catch(Exception e) {
        	 		System.out.println("LOOK path "+e.toString());
        	 	}
        }
    };
    private static void toastmessage(final String msg)
    {
		Handler h = new Handler(c.getMainLooper());

	    h.post(new Runnable() {
	        @Override
	        public void run() {
	             Toast.makeText(c,msg,Toast.LENGTH_SHORT).show();
	        }
	    });

    }
    private static void makenotification(int id,String msg)
    {
    	try{
		Notification noti = new Notification.Builder(c)
        .setContentTitle("DroidSync")
        .setContentText(msg)
        .setSmallIcon(myicon).build();
    		//toastmessage("BUILT NOTIFICATION");
      
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
		//toastmessage("NOTIFIED");

	    notificationManager.notify(id, noti); 
    	}catch(Exception e)
    	{
    		System.out.println("NOTIFICATION PROBLEM "+e.toString());
    		
    	}

    }
    private static void pathchangefunc(final DbxFileSystem dbfs, DbxPath registeredPath)
    {
    	try{
    		toastmessage("Performing sync");
    		makenotification(1,"Performing Sync");

    		
		    //Toast.makeText(this, "Performing local sync on an update",Toast.LENGTH_SHORT).show();
     		deleteunwantedfiles(dbfs,registeredPath);
    		final List<DbxFileInfo> infos = dbfs.listFolder(registeredPath);
     		
     		//DbxPath mypath=new DbxPath("/hey");
     		
     		//mTestOutput.setText("\nContents of app folder:\n");
            for (final DbxFileInfo info : infos) {
               // mTestOutput.append("    " + info.path + ", " + info.modifiedTime + '\n');
                if(dbfs.isFile(info.path))
                {
                	
                    final File file = new File(dir, info.path.toString().substring(1));
                    if(!file.exists())
                    {
                		//Handler h = new Handler(c.getMainLooper());
                    
    								makenotification(1,"DOWNLOADING FILE "+file.toString());
    								toastmessage("Downloading file "+file.toString());
    								System.out.println("DOWNLOADING FILE "+file.toString());
        	                    	DbxFile dbmyfile = dbfs.open(info.path);
        	                        FileInputStream fs=dbmyfile.getReadStream();
        	                        toastmessage("Download complete for file  "+file.toString());
        	                        //System.out.println("ADDING LISTENER TO FILE "+file.toString());
        	                       // dbmyfile.addListener(myfilelistener);
        	                        //System.out.println("SHOULD HAVE ADDED LISTENER TO FILE "+file.toString());

        	                        FileOutputStream f = new FileOutputStream(file);
        	                    	int read = 0;
        	                		byte[] bytes = new byte[1024];
        	                 
        	                		while ((read = fs.read(bytes)) != -1) {
        	                			f.write(bytes, 0, read);
        	                		}
        	                		f.close();
        	                		fs.close();
        	                        dbmyfile.close();
    								//notificatoinManager.cancel(4);
    							
                    }
                    else
                    {
                    	//file exists and needs to be checked for updation
                    	System.out.println("FOR FILE : "+info.path+" SD FILE MODIFIED TIME :"+file.lastModified()+" DROPBOX FILE MODIFIED TIME : "+info.modifiedTime);
                    }
                   

                }
                else
                {
                	//it is a folder
                	System.out.println("this is a folder");
                	File mydir = new File (dir.getAbsolutePath() + info.path.toString());
                	if(!mydir.exists())
                	{
                		toastmessage("Creating Folder "+mydir.toString());
                    	mydir.mkdirs();
                	}
                		
            		
                    dbfs.addPathListener(mylistener, info.path, Mode.PATH_OR_DESCENDANT);

                }
    	 		//Toast.makeText(c, "Local Sync On Update Complete",Toast.LENGTH_SHORT).show();

            }
            toastmessage("Sync Done");
            makenotification(1,"Sync Complete , All files are up to date");
    		//notificatoinManager.cancel(3);
    		
    	}catch(Exception e){
    		System.out.println("LOOK pathfunc"+e.toString());
    	}

    }
}
