
/*********************************************************************************************************
**
** RidenDivide- An open source project for the Android platform, helps users to carpool
** Application written in Java
** Application uses Google Places API
** 
** Copyright (C) 2012 Harini Ramakrishnan and Vinutha Veerayya Hiremath
**
** Please see the file License in this distribution for license terms. 
** Below is the link to the file License.
** https://github.com/HariniVinutha/RideNdivide/blob/master/License
**
** Following is the link for the repository- https://github.com/HariniVinutha/RideNdivide
**
** This program is free software: you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation, either version 3 of the License, or
** (at your option) any later version.
**  
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
** 
** You should have received a copy of the GNU General Public License
** along with this program.  If not, see <http://www.gnu.org/licenses/>.
** 
** Written by Harini Ramakrishnan <harini.ramki@gmail.com> and 
** Vinutha Veerayya Hiremath <mail2vintu@gmail.com>
** 
** References - http://android.vexedlogic.com/2011/07/16/android-date-time-setting-dialog/
** http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
** License- http://stackexchange.com/legal
** https://developers.google.com/academy/apis/maps/places/autocomplete-android
** License- https://developers.google.com/readme/terms, http://www.google.com/intl/en/policies/terms/
**
*********************************************************************************************************/
package oss.ridendivideapp;

import android.os.Bundle;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.util.Log;
import android.widget.Toast;
import oss.ridendivideapp.R;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.app.Dialog;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import oss.ridendivideapp.PlacesAutoCompleteAdapter;
import oss.ridendivideapp.DBAdapter;

/*******************************************************************************************************
** TakeRideActivity allows the users to search for rides that suits their commute needs. The user will
** be able to provide his/her preferences such as From location, To location, Date and Time, Flexible mins 
** and Number of Seats required. 
*********************************************************************************************************/
public class TakeRideActivity extends Activity implements OnItemClickListener {
	
	private AutoCompleteTextView tkr_frm_acView, tkr_to_acView;
	private  PlacesAutoCompleteAdapter tkr_frm_adapter, tkr_to_adapter;
	private Spinner sp_seats, sp_hrs;
	String tkr_frm_addr, tkr_to_addr, str_usrid, str_tk_date;
	private Button buttonSearch, buttonCancel, datePicker, timePicker;
	String[] str_seats={"1","2","3","4","5","6","7"};
	String[] str_hrs={"10","20","30","40","60"};
	String selected_hrs="0"; 
	Long timeinms, flexhrsinms, start_totaltime, end_totaltime;
	Double frm_lattitude, frm_longitude, to_lattitude, to_longitude;
	
	JSONObject jsonObject_main_frm, jsonObject_main_to;
	/* int flag below is used to flag an error */
	int flag, selected_seat;
	private DBAdapter tkr_datasource;
	
    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MMMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(
            "hh:mm a"); 
    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_ride_content);
		
		/* Create a DBAdapter object */
        tkr_datasource=new DBAdapter(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	str_usrid = extras.getString("usrid");
        }
        
        buttonSearch = (Button)findViewById(R.id.btn_tr_search);
        buttonCancel = (Button)findViewById(R.id.btn_tr_cancel);
        
		/* Create a PlacesAutoCompleteAdapter object for the From field */
        tkr_frm_acView = (AutoCompleteTextView) findViewById(R.id.txt_tr_from);
        tkr_frm_adapter = new PlacesAutoCompleteAdapter(this, R.layout.frm_item_list);
        tkr_frm_acView.setAdapter(tkr_frm_adapter);        
        tkr_frm_acView.setOnItemClickListener(this);
        
		/* Create a PlacesAutoCompleteAdapter object for the To field */
        tkr_to_acView = (AutoCompleteTextView) findViewById(R.id.txt_tr_to);
        tkr_to_adapter = new PlacesAutoCompleteAdapter(this, R.layout.to_item_list);
        tkr_to_acView.setAdapter(tkr_to_adapter);        
        tkr_to_acView.setOnItemClickListener(this);
        
		/* Create a spinner for the Seats field */
        sp_seats = (Spinner)findViewById(R.id.spn_tr_seats); 
        ArrayAdapter<String> seats_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_seats);
        sp_seats.setAdapter(seats_adapter);
        
		/* Create a spinner for the Flexible hrs field */
        sp_hrs =(Spinner)findViewById(R.id.spn_tr_flexhrs);
        ArrayAdapter<String> hrs_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_hrs);
        sp_hrs.setAdapter(hrs_adapter);

		/* Date picker pops up a dialog with the given date format 
		and allows user to pick a date for a ride */
        datePicker = (Button) findViewById(R.id.btn_tr_datepicker);
        datePicker.setText(dateFormatter.format(dateTime.getTime()));
        datePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });
     
		/* Time picker pops up a dialog with the given time format 
		and allows user to pick a time for a ride */
        timePicker = (Button) findViewById(R.id.btn_tr_timepicker);
        timePicker.setText(timeFormatter.format(dateTime.getTime()));
        timePicker.setOnClickListener(new View.OnClickListener() {     
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });
        
        buttonSearch.setOnClickListener(buttonSearchOnClickListener);
        buttonCancel.setOnClickListener(buttonCancelOnClickListener);
        sp_seats.setOnItemSelectedListener(spinnerseatsOnItemSelectedListener);
        sp_hrs.setOnItemSelectedListener(spinnerhrsOnItemSelectedListener);
	}
	
	/* This spinner picks up the number of seats chosen by the user from the drop down menu */
	Spinner.OnItemSelectedListener spinnerseatsOnItemSelectedListener =new Spinner.OnItemSelectedListener(){
		@Override		
		public void onItemSelected(AdapterView<?> arg0, View arg1,
 				int arg2, long arg3) {
 			int item = sp_seats.getSelectedItemPosition();
 			selected_seat=Integer.valueOf(str_seats[item].toString());
  		}
 		public void onNothingSelected(AdapterView<?> arg0) {
 			flag=1;
 			
 		}
	};		
	
	/* This spinner picks up the flexible hours chosen by the user from the drop down menu */
	Spinner.OnItemSelectedListener spinnerhrsOnItemSelectedListener =new Spinner.OnItemSelectedListener(){
		@Override
		
		public void onItemSelected(AdapterView<?> arg0, View arg1,
 				int arg2, long arg3) {
 			int item = sp_hrs.getSelectedItemPosition();
 			selected_hrs=str_hrs[item].toString();
 		}
 		public void onNothingSelected(AdapterView<?> arg0) {
 			flag=1;
 			
 		}
	};		
	
	/* The following are the functions performed on the click of the search button */
	Button.OnClickListener buttonSearchOnClickListener = new Button.OnClickListener(){
		@Override
		public void onClick(View arg0) {
     	    /* Set up the connection with the database */
			tkr_datasource.open();
			tkr_frm_addr=tkr_frm_acView.getText().toString();
			tkr_to_addr=tkr_to_acView.getText().toString();
			flag=0;
  
			if(tkr_frm_addr.length()==0)  	
			{
				tkr_frm_acView.setError("Enter from address");
				flag=1;
			}else if(tkr_to_addr.length()==0)
			{
				tkr_to_acView.setError("Enter to address");
				flag=1;
			}
                 
			if(flag==0)
			{
				/* Get the lattitude and longitude for the From address
				and the To address */
				jsonObject_main_frm=getLocationInfo(tkr_frm_addr);
				frm_lattitude=getLattitude(jsonObject_main_frm);
				frm_longitude=getLongitude(jsonObject_main_frm);
				
				jsonObject_main_to=getLocationInfo(tkr_to_addr);
				to_lattitude=getLattitude(jsonObject_main_to);
				to_longitude=getLongitude(jsonObject_main_to);				
				
				/* Convert the chosen time into milliseconds 
				helpful to make comparisons in the Database */
				
				str_tk_date= datePicker.getText().toString();                
				timeinms = dateTime.getTimeInMillis();
						
				if(selected_hrs=="10")
				{
					flexhrsinms=Long.valueOf(600000);
				} else if(selected_hrs=="20")
				{
					flexhrsinms=Long.valueOf(1200000); 
				}else if(selected_hrs=="30")
				{
				flexhrsinms=Long.valueOf(1800000); 
				}else if(selected_hrs=="40")
				{
				flexhrsinms=Long.valueOf(2400000); 
				}else if(selected_hrs=="60")
				{
				flexhrsinms=Long.valueOf(3600000); 
				}
				
				start_totaltime = timeinms-flexhrsinms;
				end_totaltime = timeinms+flexhrsinms;
				
				/* Search for rides which matches with the users preference */
				tkr_datasource.getRideDetails(str_tk_date, start_totaltime, end_totaltime, frm_lattitude, frm_longitude, to_lattitude, to_longitude, selected_seat);
								
				/* Close the Database connection and invoke the DynamicListActivity 
				which will display the search results */
				tkr_datasource.close();
				Intent searchresults = new Intent(TakeRideActivity.this,DynamicListActivity.class);				
				startActivity(searchresults);
				
			}
         	  
		}
	   
	};      
	  
	Button.OnClickListener buttonCancelOnClickListener = new Button.OnClickListener(){
	    @Override
	    public void onClick(View arg0) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TakeRideActivity.this);
			
  			/* set title for the dialog box */
			alertDialogBuilder.setTitle("Ride 'n Divide");
 
			/* set dialog message and to give user option to check other rides or to exit the application */
			alertDialogBuilder
				.setMessage("Do you want to check other rides?")
				.setCancelable(false)
				.setPositiveButton("Check Rides",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						/* If this button is clicked it allows user to check for more rides */								
						Intent reselect = new Intent(TakeRideActivity.this,ChooseRideActivity.class);
						reselect.putExtra("usrid", str_usrid);
                		startActivity(reselect);
					}
				  })
				.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						/* If this button is clicked it will exit the application */
						dialog.cancel();
						TakeRideActivity.this.finish();					
					}
				});
 
			/* create alert dialog and display it */
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}
	};

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    	if(view == tkr_frm_acView){
    		tkr_frm_addr = (String) adapterView.getItemAtPosition(position);
    	}
    	else if(view == tkr_to_acView){
    		tkr_to_addr = (String) adapterView.getItemAtPosition(position);
    	}
    }
    
	/* Code to get top 5 matching addresses to populate autocomplete list
    by parsing through values in a JSON object */
	public static JSONObject getLocationInfo(String address) {
	    StringBuilder stringBuilder = new StringBuilder();
	    try {

			address = address.replaceAll(" ","%20");    

			HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();

	        response = client.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
			Log.e("JSON Object", "JSON Exception", e);
	    } catch (IOException e) {
			Log.e("JSON Object", "JSON Exception", e);
		}

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {
	        Log.e("JSON Object", "JSON Exception", e);
	        e.printStackTrace();
	    }
		
	    return jsonObject;
	}
	
	/* The getLattitude functions as the name reads gets the lattitude 
	of the input location passed as a JSONObject */
	public static double getLattitude(JSONObject jsonObject) {
		double lattitude=0;
	    try {
	        lattitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	        .getJSONObject("geometry").getJSONObject("location")
	        .getDouble("lat");
	    } catch (JSONException e) {	            
	    	Log.e("JSON Object", "JSON Exception", e);
	    }
		
	    return lattitude;
	}
	
	/* The getLongitude functions as the name reads gets the longitude 
	of the input location passed as a JSONObject */
	public static double getLongitude(JSONObject jsonObject) {
		double longitude=0;
	    try {

	        longitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	            .getJSONObject("geometry").getJSONObject("location")
	            .getDouble("lng");
	       

	    } catch (JSONException e) {
	    	Log.e("JSON Object", "JSON Exception", e);
	    }

	    return longitude;
	}
	
	/* The below onCreateDialog allows user to pick Date and Time
	from the corresponding dialog boxes */
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	        case DIALOG_DATE:
	            return new DatePickerDialog(this, new OnDateSetListener() {
	                @Override
	                public void onDateSet(DatePicker view, int year,
	                    int monthOfYear, int dayOfMonth) {
	                    dateTime.set(year, monthOfYear, dayOfMonth);
	                    datePicker.setText(dateFormatter
	                            .format(dateTime.getTime()));
	                }
	            }, dateTime.get(Calendar.YEAR),
	            dateTime.get(Calendar.MONTH),
	            dateTime.get(Calendar.DAY_OF_MONTH));
	 
	        case DIALOG_TIME:
	            return new TimePickerDialog(this, new OnTimeSetListener() {	 
	                @Override
	                public void onTimeSet(TimePicker view, int hourOfDay,
	                        int minute) {
	                    dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
	                    dateTime.set(Calendar.MINUTE, minute);
	                    timePicker.setText(timeFormatter
	                            .format(dateTime.getTime()));
	 
	                }
	            }, dateTime.get(Calendar.HOUR_OF_DAY),
	            dateTime.get(Calendar.MINUTE), false);
	 
	    }
	    return null;
	}
	
}

