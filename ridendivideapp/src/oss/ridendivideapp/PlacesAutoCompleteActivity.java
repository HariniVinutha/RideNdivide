package oss.ridendivideapp;

import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import oss.ridendivideapp.GiveRideActivity;

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

public class PlacesAutoCompleteActivity extends Activity implements OnItemClickListener {
    
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
	
	
    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
 
    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
            super.onCreate(savedInstanceState);
            setContentView(R.layout.give_ride_content);
            
            gr_datasource=new DBAdapter(this);
           
            
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	str_usrid = extras.getString("usrid");
            }
            
            buttonSubmit = (Button)findViewById(R.id.btn_gr_submit);
            buttonCancel = (Button)findViewById(R.id.btn_gr_cancel);
            et_radius = (EditText) this.findViewById(R.id.txt_gr_radius);
            et_cost = (EditText) this.findViewById(R.id.txt_gr_cost);
             
            gr_frm_acView = (AutoCompleteTextView) findViewById(R.id.txt_gr_from);
            gr_frm_adapter = new PlacesAutoCompleteAdapter(this, R.layout.frm_item_list);
            Toast.makeText(this, "gr_frm_adapter set", Toast.LENGTH_LONG).show();
            gr_frm_acView.setAdapter(gr_frm_adapter);        
            gr_frm_acView.setOnItemClickListener(this);
            
            
            gr_to_acView = (AutoCompleteTextView) findViewById(R.id.txt_gr_to);
            gr_to_adapter = new PlacesAutoCompleteAdapter(this, R.layout.to_item_list);
            Toast.makeText(this, "gr_to_adapter set", Toast.LENGTH_LONG).show();
            gr_to_acView.setAdapter(gr_to_adapter);        
            gr_to_acView.setOnItemClickListener(this);
            
            sp_seats = (Spinner)findViewById(R.id.spn_gr_seats); 
            ArrayAdapter<String> seats_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_seats);
            sp_seats.setAdapter(seats_adapter);
           
            Toast.makeText(this, "usrid: " + str_usrid, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "seat " + selected_seat, Toast.LENGTH_SHORT).show();
		
            datePicker = (Button) findViewById(R.id.btn_gr_datepicker);
            datePicker.setText(dateFormatter.format(dateTime.getTime()));
            datePicker.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(DIALOG_DATE);
                }
            });
     
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
	}
	
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

		  gr_datasource.open();
     	 Toast.makeText(PlacesAutoCompleteActivity.this, "into button submit", Toast.LENGTH_SHORT).show();
     	 gr_frm_addr=gr_frm_acView.getText().toString();
     	 gr_to_addr=gr_to_acView.getText().toString();
     	 Toast.makeText(PlacesAutoCompleteActivity.this, gr_frm_addr, Toast.LENGTH_LONG).show();
     	 Toast.makeText(PlacesAutoCompleteActivity.this, gr_to_addr, Toast.LENGTH_LONG).show();
         flag=0;
  
         if(gr_frm_addr.length()==0)  	
         {
        	 gr_frm_acView.setError("Enter from address");
        	 flag=1;
         }else if(gr_to_addr.length()==0)
         {
        	 gr_to_acView.setError("Enter to address");
        	 flag=1;
         }
   
         Toast.makeText(PlacesAutoCompleteActivity.this, selected_seat, Toast.LENGTH_LONG).show();
        /* if(selected_seat.equals("0"))
         {
        	 flag=1;
        	 AlertDialog.Builder builder = new AlertDialog.Builder(PlacesAutoCompleteActivity.this);
        	builder.setTitle("Ride 'n Divide");
        	 builder.setMessage("Choose seats available")
        	        .setCancelable(false)
        	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int id) {
        	                 dialog.cancel();
        	            }
        	        });
        	 AlertDialog alert = builder.create();
        	 alert.show();
        	 
        		 }
          */
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
        	 jsonObject_main_frm=getLocationInfo(gr_frm_addr);
        	 frm_lattitude=getLattitude(jsonObject_main_frm);
        	 frm_longitude=getLongitude(jsonObject_main_frm);
        	 
        	 jsonObject_main_to=getLocationInfo(gr_to_addr);
        	 to_lattitude=getLattitude(jsonObject_main_to);
        	 to_longitude=getLongitude(jsonObject_main_to);
        	 
        	 Toast.makeText(PlacesAutoCompleteActivity.this, "Frm_long" +frm_longitude.toString(), Toast.LENGTH_SHORT).show();
           	 Toast.makeText(PlacesAutoCompleteActivity.this, "frm_lat" +frm_lattitude.toString(), Toast.LENGTH_SHORT).show();
           	 
           	//public long insertridedetails(String usrid, String from, String to, Integer radius, String date, 
           	// String time, String seats, Float cost, Double fromlat, Double fromlong, Double tolat, Double tolong)
           	
           	 str_date= datePicker.getText().toString();
        	 time = dateTime.getTimeInMillis();
           
           	long id;
           	 id= gr_datasource.insertridedetails(str_usrid, gr_frm_addr, gr_to_addr, rad_value, str_date, time, selected_seat, cost_value, frm_lattitude, frm_longitude, to_lattitude, to_longitude);
                  	       	
         	gr_datasource.close();
         	
         	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlacesAutoCompleteActivity.this);
				
   			// set title
 			alertDialogBuilder.setTitle("Ride 'n Divide");
  
 			// set dialog message
 			alertDialogBuilder
 				.setMessage("Thank you, for giving a ride. Do you want to check other rides?")
 				.setCancelable(false)
 				.setPositiveButton("Check Rides",new DialogInterface.OnClickListener() {
 					public void onClick(DialogInterface dialog,int id) {
 						// if this button is clicked, close
 						// current activity
 						Intent reselect = new Intent(PlacesAutoCompleteActivity.this,ChooseRideActivity.class);
                 		startActivity(reselect);
 					}
 				  })
 				.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
 					public void onClick(DialogInterface dialog,int id) {
 						// if this button is clicked, just close
 						// the dialog box and do nothing
 						dialog.cancel();
 						PlacesAutoCompleteActivity.this.finish();
 						
 					}
 				});
  
 				// create alert dialog
 				AlertDialog alertDialog = alertDialogBuilder.create();
  
 				// show it
 				alertDialog.show();

     }
     	  
  }
   
  };
  
	  
  Button.OnClickListener buttonCancelOnClickListener = new Button.OnClickListener(){
	   @Override
	   public void onClick(View arg0) {
		   
		   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlacesAutoCompleteActivity.this);
			
			// set title
			alertDialogBuilder.setTitle("Ride 'n Divide");

			// set dialog message
			alertDialogBuilder
				.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						PlacesAutoCompleteActivity.this.finish();
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						gr_frm_acView.setText("");
						gr_to_acView.setText("");
						et_radius.setText("");
						et_cost.setText("");
						dialog.cancel();
					}
				});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

	   }

	   };

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    	Toast.makeText(PlacesAutoCompleteActivity.this, "onitemclick", Toast.LENGTH_LONG).show();
    	if(view == gr_frm_acView){
    		gr_frm_addr = (String) adapterView.getItemAtPosition(position);
    		Toast.makeText(PlacesAutoCompleteActivity.this, gr_frm_addr, Toast.LENGTH_LONG).show();
    	}
    	else if(view == gr_to_acView){
    		gr_to_addr = (String) adapterView.getItemAtPosition(position);
    		Toast.makeText(PlacesAutoCompleteActivity.this, gr_to_addr, Toast.LENGTH_LONG).show();
    	}
    	}
    
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
	