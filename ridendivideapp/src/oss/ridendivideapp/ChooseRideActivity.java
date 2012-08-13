package oss.ridendivideapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
 
public class ChooseRideActivity extends Activity implements OnCheckedChangeListener{
    private RadioGroup mode;
    private String option_value;
    String str_usrid;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ride);
       
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	str_usrid = extras.getString("usrid");
        }
         
        Toast.makeText(this, "usrid: " + str_usrid, Toast.LENGTH_SHORT).show();
        mode=(RadioGroup)findViewById(R.id.radio_option);
        mode.setOnCheckedChangeListener(this);
        
    }
 
    //@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
         
        switch(checkedId){
            case R.id.opn_giveride:
            	option_value="Give Ride";
                Intent optionselected_gr = new Intent(ChooseRideActivity.this,PlacesAutoCompleteActivity.class); 
                optionselected_gr.putExtra("usrid", str_usrid);
                startActivity(optionselected_gr);
                break;
            case R.id.opn_takeride:
            	option_value="Take Ride";
                Intent optionselected_tr = new Intent(ChooseRideActivity.this,TakeRideActivity.class);
                optionselected_tr.putExtra("usrid", str_usrid);
                startActivity(optionselected_tr);
                break;     
        }
               
        //Toast.makeText(ChooseRideActivity.this, str , Toast.LENGTH_SHORT).show();
         
    }
     
}