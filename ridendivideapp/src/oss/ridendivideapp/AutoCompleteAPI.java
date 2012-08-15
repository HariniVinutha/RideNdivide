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
** References - https://developers.google.com/academy/apis/maps/places/autocomplete-android
** License: https://developers.google.com/readme/terms, http://www.google.com/intl/en/policies/terms/
**
*********************************************************************************************************/
package oss.ridendivideapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

/*********************************************************************************************************
** AutoCompleteAPI is used to integrate google's Autocomplete API
*********************************************************************************************************/
public class AutoCompleteAPI{
	
	private static final String LOG_TAG = "AutoComplete API";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyAQVgcQmyI59Lvr7H7M1TupEiLo1w2SBP0";

	public ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;
	    
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
			/* Code to form the URL and query against Autocomplete API via a http request */
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?&types=geocode&language=en&sensor=true&key=" + API_KEY);
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
	        URL url = new URL(sb.toString());
	       
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
	        
	        /* Load the results into a StringBuilder */
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        /* Create a JSON object hierarchy from the results */
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	        
	        /* Extract the Place descriptions from the results */
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    
	    return resultList;
	}
}
	
