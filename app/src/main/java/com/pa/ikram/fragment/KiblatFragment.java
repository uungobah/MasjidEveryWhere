package com.pa.ikram.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pa.ikram.MainActivity;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.util.ConstantUtil;

/**
 * Created by user on 01/06/2016.
 */
public class KiblatFragment extends Fragment {

    View v;
    Context ctx;

    private static final String TAG = "Compass";

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SampleView kiblat;
    private float[] mValues;
    private double lonMosque;
    private double latMosque;
    private LocationManager lm;
    private LocationListener locListenD;

    TextView tv_judul;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //v = inflater.inflate(R.layout.fragment_kiblat, container, false);
        kiblat = new SampleView(getContext());
        v = kiblat;
        ctx = v.getContext();

        MainActivity.hideItem(0);



        /*tv_judul = (TextView) v.findViewById(R.id.tv_judul_kiblat);

        tv_judul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

      //  getActivity().setContentView(kiblat);
       // getActivity().addContentView(kiblat,new LinearLayoutCompat.LayoutParams(800,800));


        // for calling the gps
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location loc = lm.getLastKnownLocation("gps");

        // ask the Location Manager to send us location updates
        locListenD = new DispLocListener();
        lm.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);

        locListenD = new DispLocListener();
        lm.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);

        return v;
    }


    //for finding north direction
    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Config.DEBUG) Log.d(TAG,
                    "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
            mValues = event.values;
            if (kiblat != null) {
                kiblat.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private double QiblaCount(double lngMasjid, double latMasjid) {
        double lngKabah=39.82616111;
        double latKabah=21.42250833;
        double lKlM=(lngKabah-lngMasjid);
        double sinLKLM= Math.sin(lKlM*2.0*Math.PI/360);
        double cosLKLM= Math.cos(lKlM*2.0*Math.PI/360);
        double sinLM = Math.sin(latMasjid *2.0 * Math.PI/360);
        double cosLM =  Math.cos(latMasjid *2.0 * Math.PI/360);
        double tanLK = Math.tan(latKabah*2*Math.PI/360);
        double denominator= (cosLM*tanLK)-sinLM*cosLKLM;

        double Qibla;
        double direction;

        Qibla = Math.atan2(sinLKLM, denominator)*180/Math.PI;
        direction= Qibla<0 ? Qibla+360 : Qibla;
        return direction;

    }

    //resume location update when we are resume
    @Override
    public void onResume()
    {
        if (Config.DEBUG) Log.d(TAG, "onResume");
        super.onResume();

        mSensorManager.registerListener(mListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    //stop location update when we are stop
    @Override
    public void onStop()
    {
        if (Config.DEBUG) Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }

    private class SampleView extends View {
        private Paint mPaint = new Paint();
        private Path mPath = new Path();
        private boolean mAnimate;


        public SampleView(Context context) {
            super(context);

            // Construct a wedge-shaped path
            mPath.moveTo(0, -100);
            mPath.lineTo(20, 120);
            mPath.lineTo(0, 100);
            mPath.lineTo(-20, 120);
            mPath.close();
        }

        //make an arrow for pointing direction

        protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;

            this.setBackgroundResource(R.drawable.kib);


            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);



            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;
            float Qibla=(float) QiblaCount(lonMosque,latMosque);
            //   float Qiblat = mValues [0] + Qibla;
            canvas.translate(cx, cy);
            if (mValues != null) {
                canvas.rotate(-(mValues[0]+ Qibla));
            }
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        protected void onAttachedToWindow() {
            mAnimate = true;
            if (Config.DEBUG) Log.d(TAG, "onAttachedToWindow. mAnimate=" + mAnimate);
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            mAnimate = false;
            if (Config.DEBUG) Log.d(TAG, "onDetachedFromWindow. mAnimate=" + mAnimate);
            super.onDetachedFromWindow();
        }

    }

    private class DispLocListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            // update TextViews
            latMosque = loc.getLatitude();
            lonMosque = loc.getLongitude();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
