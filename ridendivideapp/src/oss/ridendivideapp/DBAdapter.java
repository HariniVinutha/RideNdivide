package oss.ridendivideapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class DBAdapter
{
	//--Column names for User Details table--

public static final String KEY_NAME = "name";
public static final String KEY_PHNO = "phno";
public static final String KEY_EMAILID = "emailid";
public static final String KEY_MYLOC = "myloc";

private static final String TAG = "DBAdapter";
private static final String DATABASE_NAME = "dbnew_ridendivide";
private static final String DATABASE_USERTABLE = "dbnew_userdetails";
private static final String DATABASE_RIDEDETAILSTABLE = "dbnew_ridedetails";
private static final String DATABASE_SEARCHDETAILS = "dbnew_searchdetails";
private static final int DATABASE_VERSION = 1;

//--Column names of Give Ride table--
public static final String KEY_RIDEID = "rideid";
public static final String KEY_USRID = "usrid";
public static final String KEY_FROM = "ridefrom";
public static final String KEY_TO = "rideto";
public static final String KEY_RADIUS = "radius";
public static final String KEY_DATE = "ridedate";
public static final String KEY_TIME = "ridetime";
public static final String KEY_SEATS = "seats";
public static final String KEY_COST = "cost";
public static final String KEY_FROM_LAT = "fromlat";
public static final String KEY_FROM_LONG = "fromlong";
public static final String KEY_TO_LAT = "tolat";
public static final String KEY_TO_LONG = "tolong";

//--Column names of Search Results table--
public static final String SKEY_SEARCHID = "s_id";
public static final String SKEY_RIDEID = "s_rideid";
public static final String SKEY_USRID = "s_usrid";
public static final String SKEY_FROM = "s_ridefrom";
public static final String SKEY_TO= "s_rideto";
public static final String SKEY_FRMRADIUS = "s_radius";
public static final String SKEY_FRMSEATS="s_seats";


//--create USERDETAILS database query--
private static final String DATABASE_CREATE_USERDETAILS =
"create table "+ DATABASE_USERTABLE+"(name text not null, phno text not null, "
+ "emailid text primary key not null,myloc text not null);";

//--create RIDEDETAILS database query--
private static final String DATABASE_CREATE_RIDEDETAILS =
"create table "+ DATABASE_RIDEDETAILSTABLE+"(rideid integer primary key autoincrement, usrid text not null, ridefrom text not null, rideto text not null, radius float not null, ridedate text not null, ridetime real not null, "
+ "seats integer not null, cost real not null, fromlat double not null, fromlong double not null, tolat double not null, tolong double not null );";


//--create SEARCHDETAILS database query--
private static final String DATABASE_CREATE_SEARCHDETAILS =
"create table "+ DATABASE_SEARCHDETAILS+"(s_id integer primary key autoincrement, s_rideid integer not null, s_usrid text not null, s_ridefrom text not null, "
+ "s_rideto text not null, s_radius double not null, s_seats integer not null);";

private final Context context;
private DatabaseHelper DBHelper;
private SQLiteDatabase db;

public DBAdapter(Context ctx)
{
this.context = ctx;
DBHelper = new DatabaseHelper(context);
}
private static class DatabaseHelper extends SQLiteOpenHelper
{
DatabaseHelper(Context context)
{
super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db)
{
db.execSQL(DATABASE_CREATE_USERDETAILS);
db.execSQL(DATABASE_CREATE_RIDEDETAILS);
db.execSQL(DATABASE_CREATE_SEARCHDETAILS);
}
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion,
int newVersion)
{
Log.w(TAG, "Upgrading database from version " + oldVersion
+ " to "
+ newVersion + ", which will destroy all old data");
db.execSQL("DROP TABLE IF EXISTS userdetails");
db.execSQL("DROP TABLE IF EXISTS ridedetails");
db.execSQL("DROP TABLE IF EXISTS searchdetails");
onCreate(db);
}
}
//---opens the database---
public DBAdapter open() throws SQLException
{
db = DBHelper.getWritableDatabase();
return this;
}
//---closes the database---
public void close()
{
DBHelper.close();
}
//---insert a title into the database---
public long insertUser(String name, String phno, String emailid, String myloc)
{
ContentValues initialValues = new ContentValues();
initialValues.put(KEY_NAME, name);
initialValues.put(KEY_PHNO, phno);
initialValues.put(KEY_EMAILID, emailid);
initialValues.put(KEY_MYLOC, myloc);
return db.insert(DATABASE_USERTABLE, null, initialValues);
}

//---insert ride details---
public long insertridedetails(String usrid, String rfrom, String rto, Integer radius, String rdate, Long rtime, String seats, Float cost, Double fromlat, Double fromlong, Double tolat, Double tolong)
{
ContentValues initialValues = new ContentValues();
initialValues.put(KEY_USRID, usrid);
initialValues.put(KEY_FROM, rfrom);
initialValues.put(KEY_TO, rto);
initialValues.put(KEY_RADIUS, radius);
initialValues.put(KEY_DATE, rdate);
initialValues.put(KEY_TIME, rtime);
initialValues.put(KEY_SEATS, seats);
initialValues.put(KEY_COST, cost);
initialValues.put(KEY_FROM_LAT, fromlat);
initialValues.put(KEY_FROM_LONG, fromlong);
initialValues.put(KEY_TO_LAT, tolat);
initialValues.put(KEY_TO_LONG, tolong);
return db.insert(DATABASE_RIDEDETAILSTABLE, null, initialValues);
}

//---insert search results---
public long insertsearchdetails(Integer rideid, String usrid, String ridefrom, String rideto, Double radius, Integer seats)
{
	ContentValues initialValues = new ContentValues();
	initialValues.put(SKEY_RIDEID, rideid);
	initialValues.put(SKEY_USRID, usrid);
	initialValues.put(SKEY_FROM, ridefrom);
	initialValues.put(SKEY_TO, rideto);
	initialValues.put(SKEY_FRMRADIUS,radius);
	initialValues.put(SKEY_FRMSEATS,seats);
	return db.insert(DATABASE_SEARCHDETAILS, null, initialValues);
}
//---deletes a particular title---
public boolean deleteUser(String emailid)
{
return db.delete(DATABASE_USERTABLE, KEY_EMAILID +
"=" + emailid, null) > 0;
}

//---used for authenticating user---
public Cursor getUserAuthenticate(String emailid, String password) throws SQLException
{
Cursor mCursor = db.rawQuery("select * from "+DATABASE_USERTABLE+ " where emailid = '"+emailid+"' and myloc='" +password+"'", null);
return mCursor;
}

//---retrieves all the ride details---
public Cursor getAllUserDetails()
{
return db.query(DATABASE_USERTABLE, new String[] {
KEY_NAME,
KEY_PHNO,
KEY_EMAILID,
KEY_MYLOC},
null,
null,
null,
null,
null);
}
//---retrieves search results---
public Cursor getSearchResults() throws SQLException
{ 
	Cursor mCursor = db.rawQuery("select * from "+DATABASE_SEARCHDETAILS,null);
	return mCursor;
}
//---retrieves all the titles---
public void getRideDetails(String rdate, long start_time, long end_time, double tk_frmlat, double tk_frmlong, double tk_tolat, double tk_tolong, int seats )
{
String selectquery= "SELECT * FROM " + DATABASE_RIDEDETAILSTABLE + " WHERE " +KEY_DATE+ "='" +rdate+ "' AND " +KEY_SEATS+">="+seats+ " AND " +KEY_TIME+ " BETWEEN "+start_time+ " AND "+ end_time ;
Cursor mCursor = db.rawQuery(selectquery, null);

int gr_frmLatcol = mCursor.getColumnIndex(KEY_FROM_LAT);
int gr_frmLongcol = mCursor.getColumnIndex(KEY_FROM_LONG);
int gr_toLatcol = mCursor.getColumnIndex(KEY_TO_LAT);
int gr_toLongcol = mCursor.getColumnIndex(KEY_TO_LONG);
int gr_rideidcol = mCursor.getColumnIndex(KEY_RIDEID);
int gr_usridcol = mCursor.getColumnIndex(KEY_USRID);
int gr_frmloccol = mCursor.getColumnIndex(KEY_FROM);
int gr_toloccol = mCursor.getColumnIndex(KEY_TO);
int gr_frmradcol = mCursor.getColumnIndex(KEY_RADIUS);
int gr_seatscol = mCursor.getColumnIndex(KEY_SEATS);

double grfrmlatvalue, grfrmlongvalue, grtolatvalue, grtolongvalue;
int grradusvalue, gr_rideid, gr_seats;
String gr_usrid, fromloc, toloc;

/* Check if our result was valid. */
if (mCursor != null) {
        /* Check if at least one Result was returned. */
        if (mCursor.moveToFirst()) {
                int i = 0;
                /* Loop through all Results */
                do {
                     i++;
                     
                     grfrmlatvalue = mCursor.getDouble(gr_frmLatcol);
                     grfrmlongvalue= mCursor.getDouble(gr_frmLongcol);
                     grtolatvalue = mCursor.getDouble(gr_toLatcol);
                     grtolongvalue = mCursor.getDouble(gr_toLongcol);
                    
                     gr_rideid = mCursor.getInt(gr_rideidcol);
                     gr_usrid = mCursor.getString(gr_usridcol);
                     fromloc=mCursor.getString(gr_frmloccol);
                     toloc = mCursor.getString(gr_toloccol);
                     grradusvalue = mCursor.getInt(gr_frmradcol);
                     gr_seats=mCursor.getInt(gr_seatscol);
                     
                     float [] frmdist = new float[1];
                     Location.distanceBetween(grfrmlatvalue, grfrmlongvalue, tk_frmlat, tk_frmlong, frmdist);
                     
                     float frmradius = frmdist[0];
                     
                     double frmradinmiles= frmradius * 0.000621371;
                                         		 
                     float [] todist = new float[1];
                     Location.distanceBetween(grtolatvalue, grtolongvalue, tk_tolat, tk_tolong, todist);
                     
                     float toradius = todist[0];
                     
                     double toradinmiles = toradius * 0.000621371;
                     
                     if(frmradinmiles<=grradusvalue & toradinmiles<=grradusvalue)
                     {
                    	                  	 
                    	 long id= insertsearchdetails(gr_rideid, gr_usrid, fromloc, toloc, frmradinmiles, gr_seats);
                    	 
                     }
                   
                                                                
                } while (mCursor.moveToNext());
        }
}
mCursor.close();

}

//---updates a title---
public Cursor getUserdetails(String emailid) throws SQLException
{ 
	Cursor mCursor = db.rawQuery("select * from "+DATABASE_USERTABLE+" where "+KEY_EMAILID+"='"+emailid+"'",null);
	return mCursor;
}
//public boolean deleteDatabase(Context context) {
  //  return context.deleteDatabase("dbridendivide");
//}

}
