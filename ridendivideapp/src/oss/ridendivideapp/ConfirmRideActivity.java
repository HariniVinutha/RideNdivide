
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
*********************************************************************************************************/
package oss.ridendivideapp;

import android.app.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import oss.ridendivideapp.DBAdapter;

/*******************************************************************************************************
** ConfirmRideActivity allows the user to accept/reject a chosen ride. If accepted it launches a  
** dialer to call the carpool host and if rejected, it offers the user to browse through more rides
** or to exit the application.
*********************************************************************************************************/
public class ConfirmRideActivity extends Activity  {
	private DBAdapter cr_datasource;
	Cursor mCursor_user, mCursor_ride;
	String str_emailid, str_phno;
	int rideid;
	private TextView tv_name, tv_phno, tv_emailid, tv_from, tv_to, tv_date, tv_time;
	private Button btnAccept = null;
	private Button btnReject = null;	 
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {		
			try {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.confirm_ride);
	    
				tv_name= (TextView)findViewById(R.id.lbl_displayname);
				tv_phno = (TextView)findViewById(R.id.lbl_displayphno);
				tv_emailid= (TextView)findViewById(R.id.lbl_displayemail);
				tv_from = (TextView)findViewById(R.id.lbl_displayfromloc);
				tv_to = (TextView)findViewById(R.id.lbl_displaytoloc);
				tv_date = (TextView)findViewById(R.id.lbl_displaydate);
				tv_time = (TextView)findViewById(R.id.lbl_displaytime);
	    
				Bundle extras = getIntent().getExtras();
				if (extras != null) {
					str_emailid = extras.getString("usrid");
					rideid= Integer.valueOf(extras.getString("rideid"));
				}
	    
				/* Create a DBAdapter object and set up a connection
				and have a cursor to retrieve the user details for the input email id
				and one more cursor to get the ride details for the input ride id */
				cr_datasource=new DBAdapter(this);
				cr_datasource.open();
				mCursor_user = cr_datasource.getUserdetails(str_emailid);
				mCursor_ride = cr_datasource.getConfirmRide(rideid);
	    
				if (mCursor_user != null) {
				
					/* Check if at least one result was returned. */
					if (mCursor_user.moveToFirst()) {
						int i = 0;
						
						/* Loop through all the results */
						do {
							i++;	            	 
							tv_name.setText(mCursor_user.getString(mCursor_user.getColumnIndex("name")));
							str_phno = mCursor_user.getString(mCursor_user.getColumnIndex("phno"));
							tv_phno.setText(str_phno);
							tv_emailid.setText(str_emailid);
						}while(mCursor_user.moveToNext());
					}
				}
	    
				if(mCursor_ride != null) {
				
					/* Check if at least one Result was returned. */
					if (mCursor_ride.moveToFirst()) {
						int j = 0;
						
						/* Loop through all the results */
						do {
							j++;	            	 
							tv_from.setText(mCursor_ride.getString(mCursor_ride.getColumnIndex("ridefrom")));
							tv_to.setText(mCursor_ride.getString(mCursor_ride.getColumnIndex("rideto")));
							tv_date.setText(mCursor_ride.getString(mCursor_ride.getColumnIndex("ridedate")));
							Long time= mCursor_ride.getLong(mCursor_ride.getColumnIndex("ridetime"));
							
							/* To convert time stored as milliseconds into time format */
							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(time);
							tv_time.setText(timeFormatter.format(calendar.getTime()));
						
						}while(mCursor_user.moveToNext());
	             
					}
				}
	    
				btnAccept = (Button) this.findViewById(R.id.btn_accept);
				btnReject = (Button) this.findViewById(R.id.btn_reject);  
        
				btnAccept.setOnClickListener(new View.OnClickListener() {				
					@Override
					public void onClick(View v) {
					
					/* Create a new alert dialog builder object for accept button */
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmRideActivity.this);
					
					/* set title for the dialog box */
					alertDialogBuilder.setTitle("Ride N Divide");
		
					/* set dialog message and to give user option to call the carpool host or to check more rides */
					alertDialogBuilder
						.setMessage("Thank you, for taking a ride! Do you want to call the person giving the ride?")
						.setCancelable(false)
						.setNegativeButton("Check Rides",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
							
								/* If this button is clicked it allows user to check for more rides */								
								Intent reselect = new Intent(ConfirmRideActivity.this,ChooseRideActivity.class);
								reselect.putExtra("usrid", str_emailid);
								startActivity(reselect);
								ConfirmRideActivity.this.finish();
							}
						})
						.setPositiveButton("Call",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
    						
							/* If this button is clicked it will start the dialer with the host's
							phone number */
    						Intent dial = new Intent();
    						dial.setAction("android.intent.action.DIAL");
    						dial.setData(Uri.parse("tel:"+ str_phno));
    						startActivity(dial); 
    						ConfirmRideActivity.this.finish();
    						
    					}
    				});
     
    				/* create alert dialog and show it */
    				AlertDialog alertDialog = alertDialogBuilder.create();
    				alertDialog.show();
					}
				});
        
				btnReject.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
 		   
					/* Create a new alert dialog builder object for reject button */
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmRideActivity.this);
 			
					/* set title for the dialog box */
					alertDialogBuilder.setTitle("Ride N Divide");

					/* set dialog message and to give user option to check for a  
					different ride or to exit the application */
					alertDialogBuilder
					.setMessage("Do you want to select a different ride?")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
						
							/* if this button is clicked, allows user to browse through
							other rides and to reselect a different ride */
							Intent reselect = new Intent(ConfirmRideActivity.this,DynamicListActivity.class);
							reselect.putExtra("usrid", str_emailid);
							startActivity(reselect);
						}
					})
					.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							
							/* if this button is clicked, we exit the application */
							dialog.cancel();
							ConfirmRideActivity.this.finish();
						}
					});

					/* create alert dialog and show it */
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
					}
				});
	    
				mCursor_ride.close();
				mCursor_user.close();
				/* Close the DBAdapter object */
				cr_datasource.close();
	
			} catch(Exception e) {
				Log.e("Confirm Ride Activity:", e.toString());
			}
		};
	}