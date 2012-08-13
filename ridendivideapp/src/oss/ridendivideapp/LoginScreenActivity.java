package oss.ridendivideapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import oss.ridendivideapp.DBAdapter;
import android.database.sqlite.SQLiteDatabase;


public class LoginScreenActivity extends Activity{
	
	private EditText et_loginid=null;
	private EditText et_password=null;
	String str_loginid=null, str_password=null;
	int flag;
	//private SQLiteDatabase logindb;
	private DBAdapter logindb;
	Button buttonLogin;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.login_screen);
	    logindb=new DBAdapter(LoginScreenActivity.this);
	    logindb.open();
	    
	    try {
	    	
	    	 et_loginid = (EditText) this.findViewById(R.id.txt_login_email);
	    	 et_password = (EditText) this.findViewById(R.id.txt_password);
	    	
	    	  View.OnClickListener handler = new View.OnClickListener(){
	                public void onClick(View v) {
	                    //we will use switch statement and just
	                    //get thebutton's id to make things easier
	                    switch (v.getId()){
	                    
	                    case R.id.btn_login_signin:            	
	                    	// Email id validation
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
	                    	
	                    	// Password validation
	                    	
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
	                     			                     		
	                     		//Cursor logincursor = logindb.rawQuery("select * from userdetails where emailid = '"+str_loginid+"'", null);
	                     		Cursor logincursor = logindb.getUserAuthenticate(str_loginid, str_password);
	                     		
	                     		if (logincursor.moveToFirst()) {
	                     			
	                     			logincursor.close();
			                     	logindb.close();
			                     	Intent signuptochoose = new Intent(LoginScreenActivity.this,ChooseRideActivity.class);
			                     	signuptochoose.putExtra("usrid", str_loginid);
		                     		startActivity(signuptochoose);
	                     		}
	                     		else
	                     		{
	                     			et_password.setError("Incorrect EmailId or Password" );
	                     			et_password.setText("");
		                     		flag=1;
	                     			
	                     		}
	                     		/*if(logincursor == null)
	                     		{
	                     			
	                     		}
	                     		else{
			                     	        Toast.makeText(LoginScreenActivity.this,
			                     			"NAME: " + logincursor.getString(0) + "\n" +
			                     			"PHNO: " + logincursor.getString(1) + "\n" +
			                     			"EMAILID: " + logincursor.getString(2) + "\n" +
			                     			"MYLOC: " + logincursor.getString(3),
			                     			Toast.LENGTH_LONG).show();	
			                     	
			                     	
	                     		}*/
	                     		
	                     		logincursor.close();                    		

	                     	}
	                    	break;
	                    
	                    case R.id.btn_login_cancel:	                    	
	                    	
	                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreenActivity.this);
	                    	                				
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
	                						LoginScreenActivity.this.finish();
	                					}
	                				  })
	                				.setNegativeButton("No",new DialogInterface.OnClickListener() {
	                					public void onClick(DialogInterface dialog,int id) {
	                						// if this button is clicked, just close
	                						// the dialog box and do nothing
	            	                    	//et_loginid.setText("");
	            	                    	et_password.setText("");
	                						dialog.cancel();
	                					}
	                				});
	                 
	                				// create alert dialog
	                				AlertDialog alertDialog = alertDialogBuilder.create();
	                 
	                				// show it
	                				alertDialog.show();
	                    	
	                    	break;	
	                    	
	                    case R.id.btn_create_account:
	                    	
	                    	Intent createAcc = new Intent(LoginScreenActivity.this,NewUserActivity.class);
	                    	LoginScreenActivity.this.startActivity(createAcc);
	                   
	                    }
	                    
	                }
	    	  };
	    	  	    	             
	         findViewById(R.id.btn_login_signin).setOnClickListener(handler);
	         findViewById(R.id.btn_login_cancel).setOnClickListener(handler);
	         findViewById(R.id.btn_create_account).setOnClickListener(handler);
	    	 }
	    
	    catch(Exception e){
            Log.e("New User", e.toString());
	    }
	    finally
	    {
	    	
	    }
	    
	}
}

