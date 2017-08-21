package com.pa.ikram.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.maps.GPSTracker;
import com.pa.ikram.maps.MyMarker;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 18/06/2016.
 */
public class Masjid extends ActionBarActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Double stringLatitude, stringLongitude;
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();

    SupportMapFragment mapFragment;

    GPSTracker gpsTracker;
    JSONObject jobj;

    String status;

    Double latitude, longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid);

        gpsTracker = new GPSTracker(this);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (statusOfGPS) {
            if (mMap == null) {
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map2);
                mapFragment.getMapAsync(this);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMarkersHashMap = new HashMap<Marker, MyMarker>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            /*LatLng myCoordinates = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions().position(myCoordinates).title("My Position"));*/

        } else {
            gpsTracker.showSettingsAlert();
        }


        LatLng myCoordinates = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(15)                   // Sets the zoom 17
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getMasjid();

    }

    public void getMasjid() {


        String tag_json_obj = "json_obj_list_masjid";

        String url = ConstantUtil.WEB_SERVICE.URL_GET_ALL_MASJID;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Searching Masjid...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", response.toString());
                        try {
                            jobj = new JSONObject(response);

                            if (jobj.has("success")) {
                                status = jobj.getString("success");
                                Log.v("success", jobj.getString("success"));
                            }


                            if (status.equals("1")) {
                                mMyMarkersArray.clear();
                                JSONArray jarr = jobj.getJSONArray("masjid");
                                for (int i = 0; i < jarr.length(); i++) {
                                    JSONObject c = jarr.getJSONObject(i);
                                    String title = c.getString("mosque_name");
                                    String alamat = c.getString("mosque_address");
                                    Double lat = c.getDouble("latitude");
                                    Double lng = c.getDouble("longitude");
                                    String id = c.getString("mosque_id");

                                    mMyMarkersArray.add(new MyMarker(title, alamat,  lat, lng, id));

                                }

                                mMap.clear();
                                if (mMyMarkersArray.size() > 0) {

                                    plotMarkers(mMyMarkersArray);
                                  /*  LatLng myCoordinates = new LatLng(mMyMarkersArray.get(mMyMarkersArray.size()-1).getmLatitude(), mMyMarkersArray.get(mMyMarkersArray.size()-1).getmLongitude());
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                                            .zoom(8)                   // Sets the zoom 17
                                            .bearing(0)                // Sets the orientation of the camera to east
                                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                            .build();                   // Creates a CameraPosition from the builder
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                                }else {
                                    Toast.makeText(getApplicationContext(),"Data not found",Toast.LENGTH_SHORT).show();
                                }

                            }else{

                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("paper", "Error: " + error.getMessage());
                pDialog.hide();

            }
        });
        // AppController.getInstance().getRequestQueue().getCache().remove(url);
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void plotMarkers(ArrayList<MyMarker> markers) {

        // Untuk clear map yg sebelumnya
        mMarkersHashMap.clear();
        if (markers.size() > 0) {
            for (MyMarker myMarker : markers) {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getmLatitude(), myMarker.getmLongitude()));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconmasjid32));
                markerOption.title(myMarker.getmTitle());
                Marker currentMarker = mMap.addMarker(markerOption);

                mMarkersHashMap.put(currentMarker, myMarker);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());

            }

//                mapFragment.getMapAsync(this);

        }
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = getLayoutInflater().inflate(R.layout.infomap_layout, null);

            MyMarker myMarker = mMarkersHashMap.get(marker);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerTitle = (TextView) v.findViewById(R.id.marker_titel);
            TextView markerAlamat = (TextView) v.findViewById(R.id.marker_alamat);

            markerTitle.setText(myMarker.getmTitle());
            markerAlamat.setText(myMarker.getmAlamat());


            return v;
        }


    }
}
