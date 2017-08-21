package com.pa.ikram.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.crashlytics.android.answers.Answers;
import com.pa.ikram.MainActivity;
import com.pa.ikram.UnCaughtException;
import com.pa.ikram.alarm.AlarmManagerHelper;
import com.pa.ikram.ikrampa.R;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Nurul Akbar on 16/10/2015.
 */
public class SplashAct extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    PackageInfo pInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this,new Answers());
        setContentView(R.layout.splash);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(SplashAct.this));


//        gifView = (GifView) findViewById(R.id.gif_view);


//        String stringInfo = "";
//        stringInfo += "Duration: " + gifView.getMovieDuration() + "\n";
//        stringInfo += "W x H: " + gifView.getMovieWidth() + " x " + gifView.getMovieHeight() + "\n";


        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }





        Intent alarm_inbox = new Intent(this, AlarmManagerHelper.class);
//        boolean alarmInboxRunning = (PendingIntent.getBroadcast(this, 0, alarm_inbox, PendingIntent.FLAG_NO_CREATE) != null);
//        if(alarmInboxRunning == false) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm_inbox, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashAct.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
