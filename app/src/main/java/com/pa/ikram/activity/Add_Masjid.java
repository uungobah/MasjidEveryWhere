package com.pa.ikram.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.intrfc.ValidasiInterface;
import com.pa.ikram.maps.GPSTracker;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Add_Masjid extends ActionBarActivity implements OnMapReadyCallback, ValidasiInterface {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Double latitude, longitude;

    Marker currentMarker;

    GPSTracker gpsTracker;

    ArrayList resultList;

    JSONObject jobj;

    EditText et_nama, et_alamat,et_latitude, et_longitude;
    AutoCompleteTextView ac_kota;


    AppCompatButton btnDaftar;

    private TextInputLayout til_nama, til_alamat, til_latitude, til_longitude, til_kota;

    String status;
    SharedPreferences sharedPreferences;
    String user;
    String id, nama, alamat, slatitude, slongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_masjid);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        inisialisasi();

        if (getIntent().getStringExtra("id") != null){
           id = getIntent().getStringExtra("id");
           nama = getIntent().getStringExtra("nama");
           alamat = getIntent().getStringExtra("alamat");
           slatitude = getIntent().getStringExtra("latitude");
           slongitude = getIntent().getStringExtra("longitude");

            et_nama.setText(nama);
            et_longitude.setText(slongitude);
            et_latitude.setText(slatitude);
            et_alamat.setText(alamat);
            btnDaftar.setText("Approve");

            getSupportActionBar().setTitle("tetjhj");
        }


        sharedPreferences = getSharedPreferences(ConstantUtil.SHAREDPREF.PENGGUNA, Context.MODE_PRIVATE);
        user = sharedPreferences.getString("username", "");

        gpsTracker = new GPSTracker(this);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        et_nama.addTextChangedListener(new MyTextWatcher(et_nama));
        et_alamat.addTextChangedListener(new MyTextWatcher(et_alamat));
        ac_kota.addTextChangedListener(new MyTextWatcher(ac_kota));
        et_longitude.addTextChangedListener(new MyTextWatcher(et_longitude));
        et_latitude.addTextChangedListener(new MyTextWatcher(et_latitude));



        ac_kota.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        if (statusOfGPS) {
            if (mMap == null) {
                mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                // setLatLong();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
            }
        });

    }


    public void inisialisasi(){
        et_nama = (EditText) findViewById(R.id.et_nama);
        et_alamat = (EditText) findViewById(R.id.et_alamat);
        et_latitude = (EditText) findViewById(R.id.et_latitude);
        et_longitude = (EditText) findViewById(R.id.et_longitude);
        ac_kota = (AutoCompleteTextView) findViewById(R.id.ac_kota);

        ac_kota.setVisibility(View.GONE);



        til_nama = (TextInputLayout) findViewById(R.id.input_layout_masjid_nama);
        til_alamat = (TextInputLayout) findViewById(R.id.input_layout_masjid_alamat);
        til_kota = (TextInputLayout) findViewById(R.id.input_layout_masjid_kota);
        til_latitude = (TextInputLayout) findViewById(R.id.input_layout_masjid_latitude);
        til_longitude = (TextInputLayout) findViewById(R.id.input_layout_masjid_longitude);

        btnDaftar = (AppCompatButton) findViewById(R.id.btn_daftar_masjid);

        status = "";
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;



        if (gpsTracker.canGetLocation()) {

            if (slongitude != null && slatitude != null){
                latitude = Double.parseDouble(slatitude);
                longitude = Double.parseDouble(slongitude);
            }else {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            }

            et_latitude.setText(""+latitude);
            et_longitude.setText(""+longitude);

            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(latitude, longitude));
            currentMarker = mMap.addMarker(markerOption);
            currentMarker.setDraggable(true);

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

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                LatLng l = marker.getPosition();
                et_latitude.setText(String.valueOf(l.latitude));
                et_longitude.setText(String.valueOf(l.longitude));

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                LatLng l = marker.getPosition();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                LatLng l = marker.getPosition();
                et_latitude.setText(String.valueOf(l.latitude));
                et_longitude.setText(String.valueOf(l.longitude));


            }
        });

        // Add a marker in Sydney and move the camera

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLatLong() {


        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();

//            et_latitude.setText(latitude);

            longitude = gpsTracker.getLongitude();

            Log.d("Latitude", "" + latitude);
            Log.d("Longitude", "" + longitude);

            LatLng myCoordinates = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions().position(myCoordinates).title("Marker"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                    .zoom(50)                   // Sets the zoom 17
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

//            et_longitude.setText(longitude);

//            String country = gpsTracker.getCountryName(this);
//            textview = (TextView)findViewById(R.id.fieldCountry);
//            textview.setText(country);
//
//            String city = gpsTracker.getLocality(this);
//            EditText textview = (EditText) findViewById(R.id.et_alamat);
//            textview.setText(city);
//
//            String postalCode = gpsTracker.getPostalCode(this);
//            textview = (TextView)findViewById(R.id.fieldPostalCode);
//            textview.setText(postalCode);
//
//            String addressLine = gpsTracker.getAddressLine(this);
//            textview = (TextView)findViewById(R.id.fieldAddressLine);
//            textview.setText(addressLine);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    public ArrayList setAutoCompleteLocationGoogle(String input) {
        String tag_json_obj = "json_obj_location";
        String url = null;
        try {
            url = ConstantUtil.WEB_SERVICE.URL_GET_AUTO_COMPLETE + URLEncoder.encode(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        Log.i("url", "url :" + url);
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("auto_complete", response.toString());

                        try {
                            jobj = new JSONObject(response);
                            JSONArray jarr = jobj.getJSONArray("predictions");

                            resultList = new ArrayList(jarr.length());


                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject jsonObject = jarr.getJSONObject(i);

                                JSONArray jarr2 = jsonObject.getJSONArray("terms");

                                JSONObject c = jarr2.getJSONObject(0);

                                resultList.add(c.getString("value"));


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("paper", "Error: " + error.getMessage());

            }
        });

        // AppController.getInstance().getRequestQueue().getCache().remove(url);
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        return resultList;
    }

    @Override
    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
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
                        // Retrieve the autocomplete results.
                        resultList = setAutoCompleteLocationGoogle(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

    }

    private boolean validateNama() {
        if (et_nama.getText().toString().trim().isEmpty()) {
            til_nama.setError(getString(R.string.err_msg_field));
            requestFocus(et_nama);
            return false;
        } else {
            til_nama.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validateAlamat() {
        if (et_alamat.getText().toString().trim().isEmpty()) {
            til_alamat.setError(getString(R.string.err_msg_field));
            requestFocus(et_alamat);
            return false;
        } else {
            til_alamat.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validateKota() {
        if (ac_kota.getText().toString().trim().isEmpty()) {
            til_kota.setError(getString(R.string.err_msg_field));
            requestFocus(ac_kota);
            return false;
        } else {
            til_kota.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validateLatitude() {
        if (et_latitude.getText().toString().trim().isEmpty()) {
            til_latitude.setError(getString(R.string.err_msg_field));
            requestFocus(et_latitude);
            return false;
        } else {
            til_latitude.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validateLongitude() {
        if (et_longitude.getText().toString().trim().isEmpty()) {
            til_longitude.setError(getString(R.string.err_msg_field));
            requestFocus(et_longitude);
            return false;
        } else {
            til_longitude.setErrorEnabled(false);

        }
        return true;


    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_nama:
                    validateNama();
                    break;
                case R.id.et_alamat:
                    validateAlamat();
                    break;
                case R.id.ac_kota:
                    validateKota();
                    break;
                case R.id.et_latitude:
                    validateLatitude();
                    break;
                case R.id.et_longitude:
                    validateLongitude();
                    break;

            }
        }
    }

    public void checkForm() {
        Log.d("validasi", "validasi");
        int x = 0;

        if (!validateNama()) {
            x = x + 1;
            return;
        }

        if (!validateAlamat()) {
            x = x + 1;
            return;
        }


        /*if (!validateKota()) {
            x = x + 1;
            return;
        }*/

        if (!validateLatitude()) {
            x = x + 1;
            return;
        }


        if (!validateLongitude()) {
            x = x + 1;
            return;
        }



        if (x == 0) {
            //getParameter();
            // submitRegisterPost();

            if (btnDaftar.getText().equals("Approve")){
                updateDaftarMasjidPost();
            }else{
                submitDaftarMasjidPost();
            }



        }
    }

    public void submitDaftarMasjidPost() {
        String tag_json_obj = "json_obj_req";

        String url = ConstantUtil.WEB_SERVICE.URL_POST_DAFTAR_MASJID;

        Log.v("URL", url);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response.toString());
                        try {

                            jobj = new JSONObject(response);
                            if (jobj.has("success")) {
                                status = jobj.getString("success");
                                Log.v("success", jobj.getString("success"));
                            }


                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(),jobj.getString("message") , Toast.LENGTH_LONG).show();
                                reset();
                                requestFocus(btnDaftar);
                            } else {
                                Toast.makeText(getApplicationContext(),jobj.getString("message") , Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pDialog.hide();
                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Erorr", "Error: " + error.getMessage());
                pDialog.hide();
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make((LinearLayout)findViewById(R.id.ll_masjid), getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
                        .setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                snackbar.dismiss();
                            }
                        });
                snackbar.show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("mosque_name", et_nama.getText().toString());
                params.put("mosque_address", et_alamat.getText().toString());
                params.put("latitude", et_latitude.getText().toString());
                params.put("longitude", et_longitude.getText().toString());
                params.put("username", user);
//                params.put("remember_me","false");

                return params;
            }

        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void updateDaftarMasjidPost() {
        String tag_json_obj = "json_obj_req";

        String url = ConstantUtil.WEB_SERVICE.URL_POST_UPDATE_MASJID;

        Log.v("URL", url);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response.toString());
                        try {

                            jobj = new JSONObject(response);
                            if (jobj.has("success")) {
                                status = jobj.getString("success");
                                Log.v("success", jobj.getString("success"));
                            }


                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(),jobj.getString("message") , Toast.LENGTH_LONG).show();
                                reset();
                                requestFocus(btnDaftar);
                            } else {
                                Toast.makeText(getApplicationContext(),jobj.getString("message") , Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pDialog.hide();
                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Erorr", "Error: " + error.getMessage());
                pDialog.hide();
                pDialog.dismiss();
                final Snackbar snackbar = Snackbar
                        .make((LinearLayout)findViewById(R.id.ll_masjid), getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
                        .setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                snackbar.dismiss();
                            }
                        });
                snackbar.show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("mosque_name", et_nama.getText().toString());
                params.put("mosque_address", et_alamat.getText().toString());
                params.put("latitude", et_latitude.getText().toString());
                params.put("longitude", et_longitude.getText().toString());
                params.put("id", id);
                params.put("verification", user);
//                params.put("remember_me","false");

                Log.d("params",params.toString());
                return params;
            }

        };

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void reset(){
        et_alamat.setText("");
        et_nama.setText("");
        et_latitude.setText("");
        et_longitude.setText("");
        ac_kota.setText("");
    }

}
