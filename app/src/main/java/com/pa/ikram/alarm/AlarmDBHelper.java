package com.pa.ikram.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pa.ikram.alarm.AlarmContract.Alarm;

import java.util.ArrayList;
import java.util.List;


public class AlarmDBHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarmclock.db";
	
	private static final String SQL_CREATE_ALARM = "CREATE TABLE " + Alarm.TABLE_NAME + " (" +
			Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Alarm.COLUMN_NAME_ALARM_NAME + " TEXT," +
			Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
			Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
			Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" +
	    " )";
	
	private static final String SQL_DELETE_ALARM =
		    "DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;
    
	public AlarmDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ALARM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
	}
	
	private AlarmModel populateModel(Cursor c) {
		AlarmModel model = new AlarmModel();
		model.id = c.getLong(c.getColumnIndex(Alarm._ID));
		model.name = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_NAME));
		model.timeHour = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
		model.timeMinute = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
		model.isEnabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;

		return model;
	}
	
	private ContentValues populateContent(AlarmModel model) {
		ContentValues values = new ContentValues();
        values.put(Alarm.COLUMN_NAME_ALARM_NAME, model.name);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.timeHour);
        values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.timeMinute);
        values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, model.isEnabled);

        
        return values;
	}
	
	public long createAlarm(AlarmModel model) {
		ContentValues values = populateContent(model);
        return getWritableDatabase().insert(Alarm.TABLE_NAME, null, values);
	}
	
	public long updateAlarm(AlarmModel model) {
		ContentValues values = populateContent(model);
        return getWritableDatabase().update(Alarm.TABLE_NAME, values, Alarm._ID + " = ?", new String[] { String.valueOf(model.id) });
	}
	
	public AlarmModel getAlarm(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
        String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm._ID + " = " + id;
		
		Cursor c = db.rawQuery(select, null);
		
		if (c.moveToNext()) {
			return populateModel(c);
		}

		db.close();
		
		return null;
	}

	public AlarmModel getAlarmByTime(int hour, int minute) {
		SQLiteDatabase db = this.getReadableDatabase();

		String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " = " + hour + " and " + Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " = " + minute;

		Cursor c = db.rawQuery(select, null);

		if (c.moveToNext()) {
			return populateModel(c);
		}

		db.close();

		return null;
	}
	
	public List<AlarmModel> getAlarms() {
		SQLiteDatabase db = this.getReadableDatabase();
		
        String select = "SELECT * FROM " + Alarm.TABLE_NAME;
		
		Cursor c = db.rawQuery(select, null);
		
		List<AlarmModel> alarmList = new ArrayList<AlarmModel>();
		
		while (c.moveToNext()) {
			alarmList.add(populateModel(c));
		}
		
		if (!alarmList.isEmpty()) {
			return alarmList;
		}

		db.close();

		return null;
	}
	
	public int deleteAlarm(long id) {
		return getWritableDatabase().delete(Alarm.TABLE_NAME, Alarm._ID + " = ?", new String[] { String.valueOf(id) });
	}
}
