package oss.ridendivideapp;

import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.Editable;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.text.Editable;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import oss.ridendivideapp.R;
//import oss.ridendivide.MainActivity;
import oss.ridendivideapp.ChooseRideActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;

public class GiveRideActivity extends Activity {
		
	private Button btnSearch;
	private AutoCompleteTextView et_frmaddr= null;
	private EditText et_toaddr= null;
	private Geocoder gc;
	private double lat;
	private double lon;
	String str_frmaddr=null;
	private ArrayList<String> dispaddr;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.give_ride_content);
		
		 
		et_frmaddr= (AutoCompleteTextView) this.findViewById(R.id.txt_gr_from);
		et_toaddr= (EditText) this.findViewById(R.id.txt_gr_to);
		et_frmaddr.setOnKeyListener(new OnKeyListener(){
			
					public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
					Toast.makeText(GiveRideActivity.this,"key press event", Toast.LENGTH_LONG).show();
					// get value from address auto-complete
					str_frmaddr = et_frmaddr.getText().toString();
					
					try {
						gc = new Geocoder(GiveRideActivity.this, Locale.getDefault());
						Toast.makeText(GiveRideActivity.this,"from value recd", Toast.LENGTH_LONG).show();
						List<Address> foundAddr = gc.getFromLocationName(str_frmaddr, 3); // Search addresses
						Toast.makeText(GiveRideActivity.this,"size" + foundAddr.size() + "\n", Toast.LENGTH_LONG).show();			
						
						while (foundAddr.size()!=3) {
						foundAddr = gc.getFromLocationName(str_frmaddr, 5);
						//if(foundAddr.size()!=5)
						//continue;
					    }
				    Toast.makeText(GiveRideActivity.this,"while done", Toast.LENGTH_LONG).show();
				    Toast.makeText(GiveRideActivity.this,"size" + foundAddr.size() + "\n", Toast.LENGTH_LONG).show();
				    
					    if (foundAddr.size()>0) { // else display address on map
							for (int i = 0; i < foundAddr.size(); ++i) 	{
								// Save results as Longitude and Latitude
								// @todo: if more than one result, then show a
								// select-list
								
								dispaddr = new ArrayList<String>();
								Address x = foundAddr.get(i);
																								
								Toast.makeText(GiveRideActivity.this,
		                     			"lat " + x.getLatitude() + "\n" +
		                     			"long: " + x.getLongitude() + "\n" ,
		                     			Toast.LENGTH_LONG).show();
								
							      dispaddr.add(x.toString());
								  Toast.makeText(GiveRideActivity.this,"for loop", Toast.LENGTH_LONG).show();
							}
																		    	
					    	ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(GiveRideActivity.this, android.R.layout.simple_list_item_1, dispaddr);
					    	et_frmaddr.setAdapter(addressAdapter);									
						}
						
					    }catch (Exception e) {
							Log.e("New User", e.toString());
						}
					}					
				return false;	
					}
					});
	}
}

	
	