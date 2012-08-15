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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/*********************************************************************************************************
** ChooseRideActivity is used to allow users to navigate to
** Give Ride(TakeRideActivity) or Take Ride(PlacesAutoCompleteActivity) screen
*********************************************************************************************************/ 
public class ChooseRideActivity extends Activity implements OnCheckedChangeListener{
    private RadioGroup mode;
    private String option_value;
    String str_usrid;

    /* Called when the activity is first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ride);
       
	   /* Get email ID from previous activity to maintain session */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	str_usrid = extras.getString("usrid");
        }
         
        mode=(RadioGroup)findViewById(R.id.radio_option);
        mode.setOnCheckedChangeListener(this);
        
    }
 
    //@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
         
        switch(checkedId){
            case R.id.opn_giveride:
				/* Redirect to Give Ride screen */
            	option_value="Give Ride";
                Intent optionselected_gr = new Intent(ChooseRideActivity.this,PlacesAutoCompleteActivity.class); 
                optionselected_gr.putExtra("usrid", str_usrid);
                startActivity(optionselected_gr);
                break;
            case R.id.opn_takeride:
				/* Redirect to Take Ride screen */
            	option_value="Take Ride";
                Intent optionselected_tr = new Intent(ChooseRideActivity.this,TakeRideActivity.class);
                optionselected_tr.putExtra("usrid", str_usrid);
                startActivity(optionselected_tr);
                break;     
        }           
              
    }
   
 }