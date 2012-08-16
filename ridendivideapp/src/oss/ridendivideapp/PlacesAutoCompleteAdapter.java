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
** License- https://developers.google.com/readme/terms, http://www.google.com/intl/en/policies/terms/
** http://misc.phillipmartin.info/misc_carpool_01.htm
** License - http://www.phillipmartin.info/clipart/homepage2.htm
**
*********************************************************************************************************/

package oss.ridendivideapp;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.content.Context;
import java.util.ArrayList;
import oss.ridendivideapp.AutoCompleteAPI;

/*********************************************************************************************************
** PlacesAutoCompleteAdapter is used to provide the places autocomplete result to 
** AutoCompleteTextView. Also used in order to capture the user input from the AutoCompleteTextView 
** and pass it to the Places Autocomplete service.
*********************************************************************************************************/
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    AutoCompleteAPI ac=new AutoCompleteAPI();
    
    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }
    
    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    /* Retrieve the autocomplete results */
                    resultList = ac.autocomplete(constraint.toString());
                   
                    /* Assign the data to the FilterResults */
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}