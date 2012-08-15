
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
** References - http://about-android.blogspot.com/2010/04/creating-dynamic-customized-list-view.html
** License - http://www.google.com/intl/en/policies/terms/
**
*********************************************************************************************************/
package oss.ridendivideapp;

import java.text.DecimalFormat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import oss.ridendivideapp.DBAdapter;

/*********************************************************************************************************
** DynamicListActivity is used to display the search results. This activity maps the layout provided 
** in the listview_main.xml and customlist_viewitem.xml to the database search results and loops to 
** print the results until the search results are exhausted.
*********************************************************************************************************/

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
		try{
			sr_llayout = (LinearLayout) findViewById(R.id.sr_layout);
			linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			/* Create a DBAdapter object and set up a connection */
			sr_datasource=new DBAdapter(this);
			sr_datasource.open();
			
			/* Create a cursor for the database search results */
			mCursor_sresults = sr_datasource.getSearchResults();
			
			int rideid_col= mCursor_sresults.getColumnIndex("s_rideid");
			int usrid_col= mCursor_sresults.getColumnIndex("s_usrid");
			int from_col= mCursor_sresults.getColumnIndex("s_ridefrom");
			int to_col= mCursor_sresults.getColumnIndex("s_rideto");
			int radius_col= mCursor_sresults.getColumnIndex("s_radius");
			
			if (mCursor_sresults != null) {
				
				/* Check if at least one result was returned. */
				if (mCursor_sresults.moveToFirst()) {
				int i = 0;
				
					/* Loop through all the results */
					do {
						i++;
						
						/* Map the layout of the customlist_viewitem xml file */
						custom_view = linflater.inflate(R.layout.customlist_viewitem,
						null);	        
						tv_email = (TextView) custom_view.findViewById(R.id.txt_email01);
						tv_from = (TextView) custom_view.findViewById(R.id.txt_from02);
						tv_to = (TextView) custom_view.findViewById(R.id.txt_to03);
						tv_dist = (TextView) custom_view.findViewById(R.id.txt_dist04);	
						button_details = (Button) custom_view.findViewById(R.id.btn_details01);
						
						tv_email.setId(i);
						tv_from.setId(i);
						tv_to.setId(i);
						tv_dist.setId(i);	        
						button_details.setId(i);
						
						/* Set the text to be displayed to the user */
						tv_email.setText("   Email ->  " + mCursor_sresults.getString(usrid_col));
						tv_from.setText("   From -> " + mCursor_sresults.getString(from_col));
						tv_to.setText("   To -> " + mCursor_sresults.getString(to_col));
						Double dist= Double.parseDouble(new DecimalFormat("#.##").format(mCursor_sresults.getDouble(radius_col)));	
						str_rideid= mCursor_sresults.getString(rideid_col);
						str_usrid = mCursor_sresults.getString(usrid_col);
						tv_dist.setText("   Dist -> " + dist+ " miles");	        
						button_details.setText(">>");
		
						/* If details button is clicked then invoke the ConfirmRideActivity  
						which will list the carpool host details */
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
			mCursor_sresults.close();
			sr_datasource.close();
		}catch(Exception e){ 
			Log.e("Dynamic List Activity:", e.toString());		
		}
	};
}