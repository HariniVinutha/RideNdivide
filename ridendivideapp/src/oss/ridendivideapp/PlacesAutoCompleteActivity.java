/********************************************************************************************************
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
*********************************************************************************************************/

package oss.ridendivideapp;

import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

/*********************************************************************************************************
** PlacesAutoCompleteActivity is used by the carpool host to enter ride details like from and to address, 
** radius, seats, date and time.
*********************************************************************************************************/
public class PlacesAutoCompleteActivity extends Activity implements OnItemClickListener {
    
	/* Defining variables */
	private AutoCompleteTextView gr_frm_acView, gr_to_acView;
	private  PlacesAutoCompleteAdapter gr_frm_adapter, gr_to_adapter;
	private Spinner sp_seats;
	String gr_frm_addr, gr_to_addr, str_usrid, str_date, str_time;
	private Button buttonSubmit, buttonCancel, datePicker, timePicker;
	private EditText et_radius = null;
	private EditText et_cost = null;
	String[] str_seats={"1","2","3","4","5","6","7"};
	String selected_seat="0";
	int rad_value;
    float cost_value;
    Long time;
	Double frm_lattitude, frm_longitude, to_lattitude, to_longitude;
	JSONObject jsonObject_main_frm, jsonObject_main_to;
	int flag;
	private DBAdapter gr_datasource;
	
	/* Create calendar instance to set/get date and time */
    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;
	
    /* Called when the activity is first created */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		try{
    	
            super.onCreate(savedInstanceState);
            setContentView(R.layout.give_ride_content);
            /* Creating DBAdapter instance */
            gr_datasource=new DBAdapter(this);
           
		    /* Get email ID from previous activity to maintain session */
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	str_usrid = extras.getString("usrid");
            }
            
            buttonSubmit = (Button)findViewById(R.id.btn_gr_submit);
            buttonCancel = (Button)findViewById(R.id.btn_gr_cancel);
            /* Get radius and cost */
			et_radius = (EditText) this.findViewById(R.id.txt_gr_radius);
            et_cost = (EditText) this.findViewById(R.id.txt_gr_cost);
             
			/* Adding PlacesAutoComplete adapter to the FROM autocomplete field */
            gr_frm_acView = (AutoCompleteTextView) findViewById(R.id.txt_gr_from);
            gr_frm_adapter = new PlacesAutoCompleteAdapter(this, R.layout.frm_item_list);
            gr_frm_acView.setAdapter(gr_frm_adapter);        
            gr_frm_acView.setOnItemClickListener(this);
            
            /* Adding PlacesAutoComplete adapter to the TO autocomplete field */
            gr_to_acView = (AutoCompleteTextView) findViewById(R.id.txt_gr_to);
            gr_to_adapter = new PlacesAutoCompleteAdapter(this, R.layout.to_item_list);
            gr_to_acView.setAdapter(gr_to_adapter);        
            gr_to_acView.setOnItemClickListener(this);
            
			/* Adding array adapter to spinner control for seats */
            sp_seats = (Spinner)findViewById(R.id.spn_gr_seats); 
            ArrayAdapter<String> seats_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_seats);
            sp_seats.setAdapter(seats_adapter);
           
		   /* Prompt a dialog box upon Date button click */
            datePicker = (Button) findViewById(R.id.btn_gr_datepicker);
            datePicker.setText(dateFormatter.format(dateTime.getTime()));
            datePicker.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(DIALOG_DATE);
                }
            });
			
			/* Prompt a dialog box upon Time button click */
            timePicker = (Button) findViewById(R.id.btn_gr_timepicker);
            timePicker.setText(timeFormatter.format(dateTime.getTime()));
            timePicker.setOnClickListener(new View.OnClickListener() {
     
                public void onClick(View v) {
                    showDialog(DIALOG_TIME);
                }
            });
            
            buttonSubmit.setOnClickListener(buttonSubmitOnClickListener);
            buttonCancel.setOnClickListener(buttonCancelOnClickListener);
            sp_seats.setOnItemSelectedListener(spinnerseatsOnItemSelectedListener);
	}catch(Exception e)
	{
		 Log.e("Places AutoComplete Activity OnCreate:", e.toString());
    }
	
	}
		/* Get selected seat from spinner control */
			Spinner.OnItemSelectedListener spinnerseatsOnItemSelectedListener =new Spinner.OnItemSelectedListener(){
				@Override
				
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					int item = sp_seats.getSelectedItemPosition();
					selected_seat=str_seats[item].toString();
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					flag=1;
					
				}
			};		
		
			Button.OnClickListener buttonSubmitOnClickListener = new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try{
				/* Open Database */
				gr_datasource.open();
				/* Get from and to address from autocomplete fields */
				gr_frm_addr=gr_frm_acView.getText().toString();
				gr_to_addr=gr_to_acView.getText().toString();
					
				flag=0;
				/* Checking if FROM and TO address is empty */
				if(gr_frm_addr.length()==0)  	
				{
					gr_frm_acView.setError("Enter from address");
					flag=1;
				}else if(gr_to_addr.length()==0)
				{
					gr_to_acView.setError("Enter to address");
					flag=1;
				}
			/* Radius validation */
			String str_radius=et_radius.getText().toString();
			if(str_radius.length() == 0){
				et_radius.setError( "Enter valid radius" );
				flag =1;
			}
			else 
				{
				rad_value = Integer.valueOf(et_radius.getText().toString()); 
					if (rad_value < 0 || rad_value > 20) {  
						et_radius.setError( "Limit 20 miles" ); 
						flag =1;
					}
			}
			/* Cost Validation */
			String str_cost=et_cost.getText().toString();
			if(str_cost.length() == 0){
				et_cost.setError( "Enter valid cost" );
				flag =1;
			}	 
			else
				{
				cost_value =  Float.valueOf(et_cost.getText().toString());  
					if (cost_value < 0 || cost_value > 1000) {  
						et_cost.setError( "Limit 1000 dollars" ); 
						flag =1;
					}
				}
											
			if(flag==0)
			{	
				/* Function call to calculate lattitudes and longitudes for FROM address */
				jsonObject_main_frm=getLocationInfo(gr_frm_addr);
				frm_lattitude=getLattitude(jsonObject_main_frm);
				frm_longitude=getLongitude(jsonObject_main_frm);
				
				/* Function call to calculate lattitudes and longitudes for TO address */
				jsonObject_main_to=getLocationInfo(gr_to_addr);
				to_lattitude=getLattitude(jsonObject_main_to);
				to_longitude=getLongitude(jsonObject_main_to);
											
				str_date= datePicker.getText().toString();
				/* Convert time to milliseconds */
				time = dateTime.getTimeInMillis();
				/* Insert into RIDEDETAILS table */
				long id;
				id= gr_datasource.insertridedetails(str_usrid, gr_frm_addr, gr_to_addr, rad_value, str_date, time, selected_seat, cost_value, frm_lattitude, frm_longitude, to_lattitude, to_longitude);
				/* Close database object */				
				gr_datasource.close();
				
				/* On inserting Ride Details redirect to choose ride screen or exit */
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlacesAutoCompleteActivity.this);
				alertDialogBuilder.setTitle("Ride 'n Divide");
				alertDialogBuilder
					.setMessage("Thank you, for giving a ride! Do you want to check other rides?")
					.setCancelable(false)
					.setPositiveButton("Check Rides",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							Intent reselect = new Intent(PlacesAutoCompleteActivity.this,ChooseRideActivity.class);
							reselect.putExtra("usrid", str_usrid);
							startActivity(reselect);
						}
					})
					.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
							PlacesAutoCompleteActivity.this.finish();
							
						}
					});
	
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();	
		}	
     	  
  }catch(Exception e)
  {
	  Log.e("Places AutoComplete Activity buttonsubmit:", e.toString()); 
	  }
  }
		 
  };
  	  
  Button.OnClickListener buttonCancelOnClickListener = new Button.OnClickListener(){
	   @Override
	   public void onClick(View arg0) {
		   /* On clicking cancel, code to exit/reset */
		    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlacesAutoCompleteActivity.this);
		    alertDialogBuilder.setTitle("Ride 'n Divide");
			alertDialogBuilder
				.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						PlacesAutoCompleteActivity.this.finish();
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						gr_frm_acView.setText("");
						gr_to_acView.setText("");
						et_radius.setText("");
						et_cost.setText("");
						dialog.cancel();
					}
				});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
	   }
	   };

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    	/* Get selected FROM and TO address from matching list values */
    	if(view == gr_frm_acView){
    		gr_frm_addr = (String) adapterView.getItemAtPosition(position);
    		
    	}
    	else if(view == gr_to_acView){
    		gr_to_addr = (String) adapterView.getItemAtPosition(position);
    		
    	}
    	}
    
	 public static JSONObject getLocationInfo(String address) {
	        StringBuilder stringBuilder = new StringBuilder();
	        try {
			/* Code to get top 5 matching addresses to populate autocomplete list
			 by parsing through values in a JSON object	*/
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
	        } catch (IOException e) {
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
	 
	 public static double getLattitude(JSONObject jsonObject) {
			double lattitude=0;
	        try {
				/* Get lattitude from JSON object */
	            lattitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	                .getJSONObject("geometry").getJSONObject("location")
	                .getDouble("lat");

	        } catch (JSONException e) {
	            
	        	Log.e("JSON Object", "JSON Exception", e);
	        }

	        return lattitude;
	    }
	 
	 public static double getLongitude(JSONObject jsonObject) {
		 double longitude=0;
	        try {
				/* Get longitude from JSON object */
	            longitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	                .getJSONObject("geometry").getJSONObject("location")
	                .getDouble("lng");
	           

	        } catch (JSONException e) {
	        	Log.e("JSON Object", "JSON Exception", e);
	        }

	        return longitude;
	    }
	 
	 @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case DIALOG_DATE:
	            return new DatePickerDialog(this, new OnDateSetListener() {
					/* Code to set/get date in a dialog box */
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
					/* Code to set/get time in a dialog box */
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
	