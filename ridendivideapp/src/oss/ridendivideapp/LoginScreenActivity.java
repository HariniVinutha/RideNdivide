
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import oss.ridendivideapp.DBAdapter;

/*********************************************************************************************************
** LoginScreenActivity is used to authenticate users logging into the application 
*********************************************************************************************************/
public class LoginScreenActivity extends Activity{
	
	private EditText et_loginid=null;
	private EditText et_password=null;
	String str_loginid=null, str_password=null;
	int flag;
	private DBAdapter logindb;
	Button buttonLogin;
	
	/* Called when the activity is first created */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.login_screen);
		
		/* Create DBAdapter instance and open the database */
	    logindb=new DBAdapter(LoginScreenActivity.this);
	    logindb.open();
	    
	       	/* Code to get Login ID and password */
	    	 et_loginid = (EditText) this.findViewById(R.id.txt_login_email);
	    	 et_password = (EditText) this.findViewById(R.id.txt_password);
	    	
	    	  View.OnClickListener handler = new View.OnClickListener(){
	                public void onClick(View v) {
					
	                   switch (v.getId()){
	                    
						/* Code to handle Sign In button click event
						   Verify if user is already existing */
	                    case R.id.btn_login_signin:            	
	                    	
							/* Login ID (email id) validation*/
	                    	String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	                    	str_loginid= et_loginid.getText().toString();
	                    	Pattern pattern_email = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                    	    Matcher matcher_email = pattern_email.matcher(str_loginid);
	                    	
	                    	if( str_loginid.length() == 0 ){
	                    		et_loginid.setError( "Email Id is required" );
	                    		flag=1;
	                    	}
	                    	else if (!matcher_email.matches()){
	                    		et_loginid.setError( "Email Id is not valid" );
	                    	    flag=1;
	                    	}
	                    	
	                    	/* Password validation */	                    	
	                    	String pwd_pattern = "^[a-zA-Z0-9_-]{3,15}$";
	                    	str_password= et_password.getText().toString();
	                    	Pattern pattern_pwd = Pattern.compile(pwd_pattern);
                    		Matcher matcher_pwd = pattern_pwd.matcher(str_password);
                    		if( str_password.length() == 0 ){
	                     		et_password.setError( "Password is required" );
	                     		flag=1;
	                     	}
	                       	else if(!matcher_pwd.matches()) {
	                       		et_password.setError( "Password is not in the required format" ); 
                    			flag=1;
	                       	}	                    	
	                     	
	                     	if(flag==0){
	                     			                     		
	                     		/* Function called to check if Login ID matches the password, results retrieved in a cursor */
	                     		Cursor logincursor = logindb.getUserAuthenticate(str_loginid, str_password);
	                     		
	                     		if (logincursor.moveToFirst()) {
	                     		    /* If user is verified, show ChooseRideActivity */
								    logincursor.close();
			                     	logindb.close();
			                     	Intent signuptochoose = new Intent(LoginScreenActivity.this,ChooseRideActivity.class);
			                     	signuptochoose.putExtra("usrid", str_loginid);
		                     		startActivity(signuptochoose);
	                     		}
	                     		else
	                     		{
								    /* If user is not verified, display error message */
	                     			et_password.setError("Incorrect EmailId or Password" );
	                     			et_password.setText("");
		                     		flag=1;
	                     			
	                     		}
	                     		logincursor.close();                    		

	                     	}
	                    	break;
	                    
	                    case R.id.btn_login_cancel:	                    	
	                    	
							/* Code to handle Cancel button click event*/
	                    	
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreenActivity.this);
	                    	/* Show alert dialog to give an exit/reset option to user  */		
								alertDialogBuilder.setTitle("Ride N Divide");
								/* set dialog message */
	                			alertDialogBuilder
	                				.setMessage("Are you sure you want to exit?")
	                				.setCancelable(false)
	                				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	                					public void onClick(DialogInterface dialog,int id) {
	                						LoginScreenActivity.this.finish();
	                					}
	                				  })
	                				.setNegativeButton("No",new DialogInterface.OnClickListener() {
	                					public void onClick(DialogInterface dialog,int id) {
	                						et_password.setText("");
	                						dialog.cancel();
	                					}
	                				});
	                       	break;	
	                    	
	                    case R.id.btn_create_account:
	                    	/* Code to handle create account button click event
							   Redirect to NewUserActivity in case of a new user */
	                    	Intent createAcc = new Intent(LoginScreenActivity.this,NewUserActivity.class);
	                    	LoginScreenActivity.this.startActivity(createAcc);
	                   
	                    }
	                    
	                }
	    	  };
	    	 /* Adding listeners for button click events */ 	    	             
	         findViewById(R.id.btn_login_signin).setOnClickListener(handler);
	         findViewById(R.id.btn_login_cancel).setOnClickListener(handler);
	         findViewById(R.id.btn_create_account).setOnClickListener(handler);
	    	 };
	    
	       
	}


