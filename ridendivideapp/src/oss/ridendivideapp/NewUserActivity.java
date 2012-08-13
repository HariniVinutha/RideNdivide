package oss.ridendivideapp;

import android.os.Bundle;
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
import oss.ridendivideapp.R;
import oss.ridendivideapp.DBAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
//import oss.ridendivide.MainActivity;
import oss.ridendivideapp.ChooseRideActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserActivity extends Activity{
	
	private EditText et_name = null;
	private EditText et_phno=null;
	private EditText et_emailid=null;
	private EditText et_mypwd=null;
	String str_name=null, str_phno=null, str_emailid=null, str_mypwd=null;
	int flag;
	private DBAdapter datasource;
	
			
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_user);
	    datasource=new DBAdapter(this);
	    datasource.open();
	    
	    try {
	    	
	    	 et_name = (EditText) this.findViewById(R.id.txt_name);
	    	 et_phno = (EditText) this.findViewById(R.id.txt_phno);
	    	 et_emailid = (EditText) this.findViewById(R.id.txt_email);
	    	 et_mypwd = (EditText) this.findViewById(R.id.txt_nu_password);
	    	 
	    	  View.OnClickListener handler = new View.OnClickListener(){
	                public void onClick(View v) {
	                    //we will use switch statement and just
	                    //get thebutton's id to make things easier
	                    switch (v.getId()){
	                    
	                    case R.id.btn_new_signin:
	                    	
	                    	flag=0;
	                    	//Name validation
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
	                       	
	                       	
	                       	//Phone number validation
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
	                    		
	                    	
	                    	// Email id validation
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
	                    	
	                    	// Password validation	                     	
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
	                     	
	                     	
	                     	if(flag==0){
	                     		
	                     		long id;
		                     	id= datasource.insertUser(str_name, str_phno, str_emailid, str_mypwd);		                     	                                       	
		                       	datasource.close();
	                     		Intent signuptochoose = new Intent(NewUserActivity.this,LoginScreenActivity.class);
								signuptochoose.putExtra("usrid", str_emailid);
	                     		startActivity(signuptochoose);
	                     		
	                     	}
	                    	break;
	                    
	                    case R.id.btn_new_cancel:
	                    
	                    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewUserActivity.this);
	                    	                				
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
	                						NewUserActivity.this.finish();
	                					}
	                				  })
	                				.setNegativeButton("No",new DialogInterface.OnClickListener() {
	                					public void onClick(DialogInterface dialog,int id) {
	                						// if this button is clicked, just close
	                						// the dialog box and do nothing
	                						et_name.setText("");
	            	                    	et_phno.setText("");
	            	                    	et_emailid.setText("");
	            	                    	et_mypwd.setText("");
	                						dialog.cancel();
	                					}
	                				});
	                 
	                				// create alert dialog
	                				AlertDialog alertDialog = alertDialogBuilder.create();
	                 
	                				// show it
	                				alertDialog.show();
	                    	
	                    	break;
	                    }
	                    
	                }
	    	  };
	    	  	    	             
	         findViewById(R.id.btn_new_signin).setOnClickListener(handler);
	    	 findViewById(R.id.btn_new_cancel).setOnClickListener(handler);
	    	 }
	    
	    catch(Exception e){
            Log.e("New User", e.toString());
	    }
	    finally
	    {
	    	
	    }
	    
	}
}