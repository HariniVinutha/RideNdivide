package oss.ridendivideapp;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import oss.ridendivideapp.DBAdapter;

public class DynamicListActivity extends Activity {
	
	private DBAdapter sr_datasource;
	TextView tv_email, tv_from, tv_to, tv_dist;
	LinearLayout sr_llayout;
	LayoutInflater linflater;
	Button button_details;
	View custom_view;
	Cursor mCursor_sresults;
	String str_rideid, str_usrid;
	
	
        @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.listview_main);
	    
	    sr_llayout = (LinearLayout) findViewById(R.id.sr_layout);
	    linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    sr_datasource=new DBAdapter(this);
	    sr_datasource.open();
	    mCursor_sresults = sr_datasource.getSearchResults();
	    
	    int rideid_col= mCursor_sresults.getColumnIndex("s_rideid");
	    int usrid_col= mCursor_sresults.getColumnIndex("s_usrid");
	    int from_col= mCursor_sresults.getColumnIndex("s_ridefrom");
	    int to_col= mCursor_sresults.getColumnIndex("s_rideto");
	    int radius_col= mCursor_sresults.getColumnIndex("s_radius");
	     
	    if (mCursor_sresults != null) {
	        /* Check if at least one Result was returned. */
	        if (mCursor_sresults.moveToFirst()) {
	        	 int i = 0;
	             /* Loop through all Results */
	             do {
	            	 i++;
	                 
	                
	       custom_view = linflater.inflate(R.layout.customlist_viewitem,
	            null);
	        
	        tv_email = (TextView) custom_view.findViewById(R.id.txt_email01);
	        tv_from = (TextView) custom_view.findViewById(R.id.txt_from02);
	        tv_to = (TextView) custom_view.findViewById(R.id.txt_to03);
	        tv_dist = (TextView) custom_view.findViewById(R.id.txt_dist04);
	        //TextView tv5 = (TextView) customView.findViewById(R.id.TextView05);
	       
	        button_details = (Button) custom_view.findViewById(R.id.btn_details01);
	        tv_email.setId(i);
	        tv_from.setId(i);
	        tv_to.setId(i);
	        tv_dist.setId(i);
	        //tv5.setId(i);
	        button_details.setId(i);
	        tv_email.setText(" Email:" + mCursor_sresults.getString(usrid_col));
	        tv_from.setText(" From:" + mCursor_sresults.getString(from_col));
	        tv_to.setText(" To:" + mCursor_sresults.getString(to_col));
	        Double dist= Double.parseDouble(new DecimalFormat("#.##").format(mCursor_sresults.getDouble(radius_col))); 
	
	
	        str_rideid= mCursor_sresults.getString(rideid_col);
	        str_usrid = mCursor_sresults.getString(usrid_col);
	        tv_dist.setText(" Dist:" + dist+ " miles");
	        //tv5.setText("------------------------------------------------");
	        button_details.setText(">>");
	       
	        button_details.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	           	
	 		Intent goto_confirmride = new Intent(DynamicListActivity.this,ConfirmRideActivity.class);
	 		goto_confirmride.putExtra("usrid", str_usrid);
	 		goto_confirmride.putExtra("rideid", str_rideid);
	 		startActivity(goto_confirmride); 	
	        }
	        });
	        sr_llayout.addView(custom_view);
	    
	    }while(mCursor_sresults.moveToNext());
	}
	}
	    sr_datasource.close();
	}

}