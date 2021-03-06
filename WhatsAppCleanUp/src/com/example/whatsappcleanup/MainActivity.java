package com.example.whatsappcleanup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	boolean EXTRNLSTROAGEAVAILABLE = false;
	boolean EXTRNLSTROAGEWRITABLE = false;
	boolean ISHIDDENFROMGALLERY = false;
	String WHATSAPP_IMAGE_PATH = "";
	String WHATSAPP_VIDEO_PATH = "" ;
	String NOMEDIA_VIDEO_PATH = "" ;
	String NOMEDIA_IMAGE_PATH = "";
	String DeletePath = "";
	Button clearImgesButton;
	Button clearVideoButton;
	ToggleButton visibiltyButton;
	static int APP_VERSION = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 String json = "";
		
		
		try {
//			System.setProperty("http.proxyHost", "https://dl.dropbox.com");
//			System.setProperty("http.proxyPort", "80");
			URL dropBxurl = new URL("https://dl.dropbox.com/s/v193yo0qj504l89/version.txt?token_hash=AAFu9uaSizF-9uwukhEf1GLETy61RhA_wFMK0k8X3S_tVw&dl=1");
		
		InputStream	inputStream = dropBxurl.openConnection().getInputStream();
		 BufferedReader reader = new BufferedReader(new InputStreamReader(
				 inputStream, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	        inputStream.close();
	        // hier habe ich das JSON-File als String
	        json = sb.toString();
	        System.out.println("++++++++++++++++++++  JSON Parser"+json);
		
		
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		JSONObject jObj = null;
		int serverVersion = APP_VERSION;
		try {
	        jObj = new JSONObject(json);
	        
	        System.out.println("++++++++++++++++++++  jObj jObj "+jObj.getInt("version"));
	        serverVersion = jObj.getInt("version");
	    } catch (JSONException e) {
	    	System.out.println("++++++++++++++JSON Parser Error parsing data " + e.toString());
	    }
		
		if (APP_VERSION < serverVersion) {
			Toast.makeText(this, "New Version Available for Download at ", Toast.LENGTH_LONG).show();
		}
		
		clearImgesButton = (Button) findViewById(R.id.buttonImagePath);
		clearVideoButton = (Button) findViewById(R.id.buttonVideoPath);
		visibiltyButton = (ToggleButton) findViewById(R.id.toggleButton1);

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			EXTRNLSTROAGEAVAILABLE = EXTRNLSTROAGEWRITABLE = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			EXTRNLSTROAGEAVAILABLE = true;
			EXTRNLSTROAGEWRITABLE = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			EXTRNLSTROAGEAVAILABLE = EXTRNLSTROAGEWRITABLE = false;
		}

		if (EXTRNLSTROAGEAVAILABLE) {
			System.out.println("-- *** path "+Environment.getExternalStorageDirectory());
			File dir = new File(Environment.getExternalStorageDirectory() + "/WhatsApp");
			if(dir.exists() && dir.isDirectory()) {
				// do something here
				WHATSAPP_IMAGE_PATH = dir.getPath()+"/Media/WhatsApp Images";
				WHATSAPP_VIDEO_PATH = dir.getPath()+"/Media/WhatsApp Video";
				NOMEDIA_IMAGE_PATH = dir.getPath() +WHATSAPP_IMAGE_PATH +"/.nomedia";
				NOMEDIA_VIDEO_PATH = dir.getPath() +WHATSAPP_VIDEO_PATH +"/.nomedia";
			}

		}else{
			Toast.makeText(this, "NO MEDIA ", Toast.LENGTH_LONG).show();
		}

		System.out.println("==dirNOMEDIA_IMAGE_PATH======   "+NOMEDIA_IMAGE_PATH);
		File dirNOMEDIA_IMAGE_PATH = new File(WHATSAPP_IMAGE_PATH,".nomedia");
		File dirNOMEDIA_VIDEO_PATH = new File(WHATSAPP_VIDEO_PATH,".nomedia");

		System.out.println("==dirNOMEDIA_IMAGE_PATH======   "+dirNOMEDIA_IMAGE_PATH.isFile());
		//if (dirNOMEDIA_IMAGE_PATH.exists()) {
		if (dirNOMEDIA_IMAGE_PATH.exists() && dirNOMEDIA_VIDEO_PATH.exists()) {
			visibiltyButton.setChecked(true);
		}else{
			visibiltyButton.setChecked(false);
		}

		final AlertDialog.Builder  alertConfirmDelete = new AlertDialog.Builder(this);
		alertConfirmDelete.setTitle("Confirm Delete");

		alertConfirmDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if (EXTRNLSTROAGEAVAILABLE) {
					File dir = new File(DeletePath);
					if (dir.isDirectory()) {
						String[] children = dir.list();

						if (children.length > 0 ) {
							for (int i = 0; i < children.length; i++) {
								new File(dir, children[i]).delete();
							}
						}else{
							//No Files to Delete
						}

					}
				}

			}
		});

		alertConfirmDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DeletePath = "";
			}
		});

		clearImgesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (EXTRNLSTROAGEAVAILABLE) {
					System.out.println("--**************************imageFinal path "+WHATSAPP_IMAGE_PATH );
					File dirWHATSAPP_IMAGE_PATH = new File(WHATSAPP_IMAGE_PATH);
					if(dirWHATSAPP_IMAGE_PATH.exists() && dirWHATSAPP_IMAGE_PATH.isDirectory()) {
						DeletePath = WHATSAPP_IMAGE_PATH;
						alertConfirmDelete.setMessage("Are You sure you want to delete all file at "+DeletePath);
						alertConfirmDelete.show();
					}
				}


			}
		});

		clearVideoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (EXTRNLSTROAGEAVAILABLE) {
					System.out.println("--imageFinal path "+WHATSAPP_VIDEO_PATH );
					File dirWHATSAPP_IMAGE_PATH = new File(WHATSAPP_VIDEO_PATH);
					if(dirWHATSAPP_IMAGE_PATH.exists() && dirWHATSAPP_IMAGE_PATH.isDirectory()) {
						DeletePath = WHATSAPP_VIDEO_PATH;
						alertConfirmDelete.setMessage("Are You sure you want to delete all file at "+DeletePath);
						alertConfirmDelete.show();
					}
				}

			}
		});

		visibiltyButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
					if (isChecked) {
						//	new File(WHATSAPP_IMAGE_PATH, ".nomedia");


						File createNOMEDIA_IMAGE_PATH = new File(WHATSAPP_IMAGE_PATH,".nomedia");
						File createNOMEDIA_VIDEO_PATH = new File(WHATSAPP_VIDEO_PATH,".nomedia");

						try {
							createNOMEDIA_IMAGE_PATH.createNewFile();
							createNOMEDIA_VIDEO_PATH.createNewFile();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						scanMedia(createNOMEDIA_IMAGE_PATH.getPath());
						scanMedia(createNOMEDIA_VIDEO_PATH.getPath());

					}else{
						File dirImg = new File(WHATSAPP_IMAGE_PATH);
						deleteHideFile(dirImg);
						dirImg = null;
						File dirVid = new File(WHATSAPP_VIDEO_PATH);
						deleteHideFile(dirVid);
						dirVid = null;



					}
				

			}
		});
	}


	private void deleteHideFile(File dFile) {
		if (dFile.isDirectory()) {
			String[] children = dFile.list();

			if (children.length > 0 ) {
				for (int i = 0; i < children.length; i++) {
					//new File(dir, children[i]).delete();
					String fileName = children[i];
					System.out.println("-=fileName----- "+fileName);

					if (fileName.contains(".nomedia")) {
						System.out.println("++++++++++++++++++++++++++++++++++++"+dFile.getPath());
						new File(dFile, children[i]).delete();
					}
				}
			}else{
				//No Files to Delete
			}

		}

		scanMedia(dFile.getPath());

	}

	private void scanMedia(String path) {
		//	    File file = new File(path);
		//	    Uri uri = Uri.fromFile(file);
		//	    System.out.println("++++++++++++++++++++++++++++++++++++"+uri);
		//
		//	    Intent scanFileIntent = new Intent(
		//	            Intent.ACTION_MEDIA, uri);
		//	    sendBroadcast(scanFileIntent);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
				.parse("file://"
						+ Environment.getExternalStorageDirectory())));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
