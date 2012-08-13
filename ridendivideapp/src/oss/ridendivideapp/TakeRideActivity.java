package oss.ridendivideapp;

import android.os.Bundle;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import oss.ridendivideapp.R;


import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
            
            tkr_datasource=new DBAdapter(this);
            
            
            
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
            	str_usrid = extras.getString("usrid");
            }
            
            buttonSearch = (Button)findViewById(R.id.btn_tr_search);
            buttonCancel = (Button)findViewById(R.id.btn_tr_cancel);
           
             
            tkr_frm_acView = (AutoCompleteTextView) findViewById(R.id.txt_tr_from);
            tkr_frm_adapter = new PlacesAutoCompleteAdapter(this, R.layout.frm_item_list);
            Toast.makeText(this, "gr_frm_adapter set", Toast.LENGTH_LONG).show();
            tkr_frm_acView.setAdapter(tkr_frm_adapter);        
            tkr_frm_acView.setOnItemClickListener(this);
            
            
            tkr_to_acView = (AutoCompleteTextView) findViewById(R.id.txt_tr_to);
            tkr_to_adapter = new PlacesAutoCompleteAdapter(this, R.layout.to_item_list);
            Toast.makeText(this, "gr_to_adapter set", Toast.LENGTH_LONG).show();
            tkr_to_acView.setAdapter(tkr_to_adapter);        
            tkr_to_acView.setOnItemClickListener(this);
            
            sp_seats = (Spinner)findViewById(R.id.spn_tr_seats); 
            ArrayAdapter<String> seats_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_seats);
            sp_seats.setAdapter(seats_adapter);
            
            sp_hrs =(Spinner)findViewById(R.id.spn_tr_flexhrs);
            ArrayAdapter<String> hrs_adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,str_hrs);
            sp_hrs.setAdapter(hrs_adapter);
            Toast.makeText(this, "seat " + selected_seat, Toast.LENGTH_SHORT).show();
		
            Toast.makeText(this, "usrid: " + str_usrid, Toast.LENGTH_SHORT).show();
            
            datePicker = (Button) findViewById(R.id.btn_tr_datepicker);
            datePicker.setText(dateFormatter.format(dateTime.getTime()));
            datePicker.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(DIALOG_DATE);
                }
            });
     
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
	
		 Button.OnClickListener buttonSearchOnClickListener = new Button.OnClickListener(){
	  @Override
	  public void onClick(View arg0) {

     	 //Toast.makeText(TakeRideActivity.this, "into button submit", Toast.LENGTH_SHORT).show();
		  tkr_datasource.open();
		  tkr_frm_addr=tkr_frm_acView.getText().toString();
     	 tkr_to_addr=tkr_to_acView.getText().toString();
     	 //Toast.makeText(TakeRideActivity.this, tkr_frm_addr, Toast.LENGTH_LONG).show();
     	 //Toast.makeText(TakeRideActivity.this, tkr_to_addr, Toast.LENGTH_LONG).show();
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
   
         //Toast.makeText(TakeRideActivity.this, selected_seat, Toast.LENGTH_LONG).show();
                   
         //Toast.makeText(TakeRideActivity.this, selected_hrs, Toast.LENGTH_LONG).show();
                          
         if(flag==0)
         {
        	 jsonObject_main_frm=getLocationInfo(tkr_frm_addr);
        	 frm_lattitude=getLattitude(jsonObject_main_frm);
        	 frm_longitude=getLongitude(jsonObject_main_frm);
        	 
        	 jsonObject_main_to=getLocationInfo(tkr_to_addr);
        	 to_lattitude=getLattitude(jsonObject_main_to);
        	 to_longitude=getLongitude(jsonObject_main_to);
        	 
        	 Toast.makeText(TakeRideActivity.this, "Frm_long:" +frm_longitude.toString(), Toast.LENGTH_SHORT).show();
        	 Toast.makeText(TakeRideActivity.this, "Frm_lat:" +frm_lattitude.toString(), Toast.LENGTH_SHORT).show();
        	 Toast.makeText(TakeRideActivity.this, "To_long:" +to_longitude.toString(), Toast.LENGTH_SHORT).show();
        	 Toast.makeText(TakeRideActivity.this, "To_lat:" +to_lattitude.toString(), Toast.LENGTH_SHORT).show();
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
        	
        	tkr_datasource.getRideDetails(str_tk_date, start_totaltime, end_totaltime, frm_lattitude, frm_longitude, to_lattitude, to_longitude, selected_seat);
        	Toast.makeText(TakeRideActivity.this, "done", Toast.LENGTH_SHORT).show();
        	
        	tkr_datasource.close();
        	Toast.makeText(TakeRideActivity.this, "done2", Toast.LENGTH_LONG).show();
        	Intent searchresults = new Intent(TakeRideActivity.this,DynamicListActivity.class);
			//signuptochoose.putExtra("usrid", str_emailid);
     		startActivity(searchresults);
        	
         }
         	  
	  }
	   
	  };
      
	  
	  Button.OnClickListener buttonCancelOnClickListener = new Button.OnClickListener(){
	   @Override
	   public void onClick(View arg0) {

	   }

	   };

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    	Toast.makeText(TakeRideActivity.this, "onitemclick", Toast.LENGTH_LONG).show();
    	if(view == tkr_frm_acView){
    		tkr_frm_addr = (String) adapterView.getItemAtPosition(position);
    		Toast.makeText(TakeRideActivity.this, tkr_frm_addr, Toast.LENGTH_LONG).show();
    	}
    	else if(view == tkr_to_acView){
    		tkr_to_addr = (String) adapterView.getItemAtPosition(position);
    		Toast.makeText(TakeRideActivity.this, tkr_to_addr, Toast.LENGTH_LONG).show();
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

