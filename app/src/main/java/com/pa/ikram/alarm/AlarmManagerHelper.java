package com.pa.ikram.alarm;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmManagerHelper extends BroadcastReceiver {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TIME_HOUR = "timeHour";
    public static final String TIME_MINUTE = "timeMinute";


    AlarmDBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new AlarmDBHelper(context);
        setAlarms(context);

    }

    public  void setAlarms(Context context) {



        final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);

        AlarmModel alarm = dbHelper.getAlarmByTime(nowHour, nowMinute);

        dbHelper.close();


        Log.d("Masuk Jam Sekarang", "" + nowHour);

        if (alarm != null) {
            if (alarm.isEnabled) {

                PendingIntent pIntent = createPendingIntent(context, alarm);

                Log.d("Now Hour", "" + nowHour);
                Log.d("Now Minute", "" + nowMinute);

                Log.d("Now Hour Db", "" + alarm.timeHour);
                Log.d("Now Minute DB", "" + alarm.timeMinute);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarm.timeHour);
                calendar.set(Calendar.MINUTE, alarm.timeMinute);
                calendar.set(Calendar.SECOND, 00);

                setAlarm(context, calendar, pIntent);

            }

        }


    }


    @SuppressLint("NewApi")
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    /*public static void cancelAlarms(Context context) {
        AlarmDBHelper dbHelper = new AlarmDBHelper(context);

        List<AlarmModel> alarms = dbHelper.getAlarms();

        if (alarms != null) {
            for (AlarmModel alarm : alarms) {
                if (alarm.isEnabled) {
                    PendingIntent pIntent = createPendingIntent(context, alarm);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);
                }
            }
        }
    }*/

    private static PendingIntent createPendingIntent(Context context, AlarmModel model) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.putExtra(ID, model.id);
        intent.putExtra(NAME, model.name);
        intent.putExtra(TIME_HOUR, model.timeHour);
        intent.putExtra(TIME_MINUTE, model.timeMinute);

        return PendingIntent.getService(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
