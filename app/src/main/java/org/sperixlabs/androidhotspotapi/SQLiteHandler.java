package org.sperixlabs.androidhotspotapi;

/**
 * Created by Jay Lux Ferro on 12/19/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "HotspotAssistant";

    // configuration table name
    private static final String CONFIGURATION = "configuration";

    // configuration Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SSID = "ssid";
    private static final String KEY_PASSWORD = "password";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONFIG_TABLE = "CREATE TABLE " + CONFIGURATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SSID + " TEXT,"
                + KEY_PASSWORD + " TEXT)";
        db.execSQL(CREATE_CONFIG_TABLE);
        //adding default username and password
        db.execSQL("insert into configuration(ssid,password) values('HotSpot','123456789')");

        db.execSQL("create table current_status(id integer primary key, status integer)");
        //adding default status
        db.execSQL("insert into current_status(status) values(0)");

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CONFIGURATION);
        db.execSQL("DROP TABLE IF EXISTS current_status");
        // Create tables again
        onCreate(db);
    }

    //getting current configuration
    public Cursor getConfiguration(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(CONFIGURATION, new String[]{KEY_ID,KEY_SSID,KEY_PASSWORD},null,null,null,null,null,"1");
        return mCursor;
    }

    //update configuration
    boolean updateConfiguration(String ssid1, String password1){
        boolean updated = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from configuration");

        ContentValues values = new ContentValues();
        values.put(KEY_SSID,ssid1);
        values.put(KEY_PASSWORD,password1);
        Long id = db.insert(CONFIGURATION,null,values);
        Log.d(TAG,"Configuration updated: " + id);
        if(id > 0){
            updated = true;
        }
        return updated;
    }

    public boolean getStatus(){
        boolean on = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query("current_status", new String[]{"status"},null, null, null, null, null, "1");
        if(mCursor.getCount() >= 1){
            mCursor.moveToFirst();
            if(Integer.valueOf(mCursor.getString(mCursor.getColumnIndex("status"))) == 1){
                on = true;
            }
        }
        return on;
    }

    public boolean updateStatus(String status){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from current_status");

        ContentValues values = new ContentValues();
        values.put("status", status);
        Long id = db.insert("current_status",null,values);
        Log.d(TAG,"CurrentStatus updated: " + id);
        if(id > 0){
            return true;
        }
        return false;
    }
}