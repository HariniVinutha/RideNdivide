
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
** References - http://misc.phillipmartin.info/misc_carpool_01.htm
** License - http://www.phillipmartin.info/clipart/homepage2.htm
**
*********************************************************************************************************/
package oss.ridendivideapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import oss.ridendivideapp.R;
import oss.ridendivideapp.DBAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/********************************************************************************************************
** NewUserActivity performs a basic validation on all the information entered by a new user 
** such as Name, Phone Number, Email Id and Password and on successful input saves the user
** information onto the database and asks user to re-login.
*********************************************************************************************************/
public class NewUserActivity extends Activity{
	
	private EditText et_name = null;
	private EditText et_phno=null;
	private EditText et_emailid=null;
	private EditText et_mypwd=null;
	String str_name=null, str_phno=null, str_emailid=null, str_mypwd=null;
	
	/* flag is user to flag an error in the user input */
	int flag;
	private DBAdapter datasource;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_user);
		
		/* Create a DBAdapter object and set up a connection to database */
	    datasource=new DBAdapter(this);
	    datasource.open();
	    
	    try {
	    	
	    	et_name = (EditText) this.findViewById(R.id.txt_name);
	    	et_phno = (EditText) this.findViewById(R.id.txt_phno);
	    	et_emailid = (EditText) this.findViewById(R.id.txt_email);
	    	et_mypwd = (EditText) this.findViewById(R.id.txt_nu_password);
	    	
	    	View.OnClickListener handler = new View.OnClickListener(){
	            public void onClick(View v) {               					   
	                switch (v.getId()){
	                   
						/* Validations are performed if the sign in button is selected */					  
						case R.id.btn_new_signin:	                   	
							flag=0;
							
							/* Name field validation */
							String username_pattern = "^[a-zA-Z0-9_-]{3,15}$";
							str_name= et_name.getText().toString();
							Pattern pattern_name = Pattern.compile(username_pattern);
							Matcher matcher_name = pattern_name.matcher(str_name);
	                      	if( str_name.length() == 0 ){
								et_name.setError( "Name is required!" );
								flag=1;
	                      	}
	                      	else if(!matcher_name.matches()) {
								et_name.setError( "Name is not in the required format!" ); 
								flag=1;
	                      	}	                      	
	                       	
	                       	/* Phone number validation */
	                       	str_phno= et_phno.getText().toString();
	                       	//Pattern pattern_phno = Pattern.compile("d{3}-d{7}");
                    		//Matcher matcher_phno = pattern_phno.matcher(str_phno);	                       	
	                    	if( str_phno.length() == 0 ){
	                    		et_phno.setError( "Phone Number is required!" );
	                    		flag=1;
	                    	}
	                    	/*else if (!matcher_phno.matches()) {
	                    		et_phno.setError( "Phone Number should be 10 digits!" );
	                    		flag=1;
	                    	}*/
	                    		
	                    	
	                    	/* Email id validation */
	                    	String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	                    	str_emailid= et_emailid.getText().toString();
	                    	Pattern pattern_email = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    	    Matcher matcher_email = pattern_email.matcher(str_emailid);	                    	
	                    	if( str_emailid.length() == 0 ){
	                    		et_emailid.setError( "Email Id is required!" );
	                    		flag=1;
	                    	}
	                    	else if (!matcher_email.matches()){
	                    	    et_emailid.setError( "Email Id is not valid!" );
	                    	    flag=1;
	                    	}
	                    	
	                    	/* Password validation */	                     	
	                    	String pwd_pattern = "^[a-zA-Z0-9_-]{3,15}$";
	                    	str_mypwd= et_mypwd.getText().toString();
	                    	Pattern pattern_pwd = Pattern.compile(pwd_pattern);
                    		Matcher matcher_pwd = pattern_pwd.matcher(str_mypwd);
                    		if( str_mypwd.length() == 0 ){
                    			et_mypwd.setError( "Password is required" );
	                     		flag=1;
	                     	}
	                       	else if(!matcher_pwd.matches()) {
	                       		et_mypwd.setError( "Password is not in the required format" ); 
                    			flag=1;
	                       	}
	                     	
	                     	/* If no error is flagged then we insert the user details into the database */							 
	                     	if(flag==0){
	                     		
	                     		long id;
		                     	id= datasource.insertUser(str_name, str_phno, str_emailid, str_mypwd);
								/* Close the database connection and invoke the LoginScreenActivity */
		                       	datasource.close();
	                     		Intent signuptochoose = new Intent(NewUserActivity.this,LoginScreenActivity.class);
								signuptochoose.putExtra("usrid", str_emailid);
	                     		startActivity(signuptochoose);	                     		
	                     	}
	                    	break;
	                    
	                    case R.id.btn_new_cancel:
							/* On click of cancel button, create a new alert dialog builder object */
	                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewUserActivity.this);
	                    	                				
	                  		/* set title for the dialog box */
	                		alertDialogBuilder.setTitle("Ride 'n Divide");
	                 
	                		/* set dialog message and to ensure that the user wants to exit the application */
	                		alertDialogBuilder
	                			.setMessage("Are you sure you want to exit?")
	                			.setCancelable(false)
	                			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	                				public void onClick(DialogInterface dialog,int id) {
	                					/* If this button is clicked, it will exit the application */
	                					NewUserActivity.this.finish();
	                				}
	                			  })
	                			.setNegativeButton("No",new DialogInterface.OnClickListener() {
	                				public void onClick(DialogInterface dialog,int id) {
	                					/* If this button is clicked, it clears all the fields 
										and waits for the user to re-enter */
	                					et_name.setText("");
	            	                   	et_phno.setText("");
	            	                   	et_emailid.setText("");
	            	                   	et_mypwd.setText("");
	                					dialog.cancel();
	                				}
	                			});
	                 
	                		/* create alert dialog and display it */
	                		AlertDialog alertDialog = alertDialogBuilder.create();	                
	                		alertDialog.show();
	                    	
							break;
	                }
	                    
	            }
	    	};
	    	  	    	             
	        findViewById(R.id.btn_new_signin).setOnClickListener(handler);
	    	findViewById(R.id.btn_new_cancel).setOnClickListener(handler);
	    }
	    catch(Exception e){
            Log.e("New User Activity:", e.toString());
	    }
		
	    finally
	    {
	    	
	    }
	    
	}
}