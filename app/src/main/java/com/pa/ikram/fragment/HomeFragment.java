package com.pa.ikram.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.pa.ikram.MainActivity;
import com.pa.ikram.alarm.AlarmDBHelper;
import com.pa.ikram.alarm.AlarmManagerHelper;
import com.pa.ikram.alarm.AlarmModel;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.maps.GPSTracker;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 31/05/2016.
 */
public class HomeFragment extends Fragment {

    static View v;
    static Context ctx;

    static TextView tv_subuh, tv_zuhur, tv_ashar, tv_magrib, tv_isya, tv_tgl_jadwal, tv_sunrise, tv_lokasi;

    static JSONObject jobj;

    static SharedPreferences sharedPreferences;
    static SharedPreferences sharedPreferences2;

    LinearLayout home_layout;

    ArrayList resultList;

    static AutoCompleteTextView ac_cari;
    AppCompatButton btn_cari;
    private static AlarmDBHelper dbHelper;

    private static AlarmModel alarmDetails;
    static GPSTracker gpsTracker;

    static JSONArray jarr;

    String city;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);
        ctx = v.getContext();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getActivity().getSharedPreferences(ConstantUtil.SHAREDPREF.JADWAL_OFFLINE, Context.MODE_PRIVATE);
        sharedPreferences2 = getActivity().getSharedPreferences(ConstantUtil.SHAREDPREF.LOKASI, Context.MODE_PRIVATE);

        inisialisasi();

        ac_cari.setAdapter(new GooglePlacesAutocompleteAdapter(ctx, R.layout.list_item));

        btn_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                getJadwalByCity(ac_cari.getText().toString());
                ac_cari.setText("");
            }
        });


        getActivity().supportInvalidateOptionsMenu();


        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        city = city();
        dbHelper = new AlarmDBHelper(ctx);
        if (isNetworkConnected()) {
           /* if(city()== null || city().equals("")){
               getJadwal();
            }else{
                getJadwalByCity(city());
            }*/
            String lokasi = sharedPreferences2.getString("lokasi","");
            if(lokasi.equals("")){
                getJadwalByCity("Jawa Barat");
            }else{
                getJadwalByCity(lokasi);
            }


        } else if (!sharedPreferences.getString("response", "").equals("")) {
            getJadwalOffline();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(v, getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
                    .setAction("Close", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                                snackbar.dismiss();
                        }
                    });
            snackbar.show();


        }


    }

    public void inisialisasi() {
        home_layout = (LinearLayout) v.findViewById(R.id.home_layout);
        tv_subuh = (TextView) v.findViewById(R.id.tv_subuh);
        tv_zuhur = (TextView) v.findViewById(R.id.tv_zuhur);
        tv_ashar = (TextView) v.findViewById(R.id.tv_ashar);
        tv_magrib = (TextView) v.findViewById(R.id.tv_magrib);
        tv_isya = (TextView) v.findViewById(R.id.tv_isya);
        tv_sunrise = (TextView) v.findViewById(R.id.tv_sunrise);
        tv_tgl_jadwal = (TextView) v.findViewById(R.id.tv_tgl_jadwal);
        tv_lokasi = (TextView) v.findViewById(R.id.tv_lokasi);

        ac_cari = (AutoCompleteTextView) v.findViewById(R.id.ac_kota_search);
        btn_cari = (AppCompatButton) v.findViewById(R.id.btn_cari);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void getJadwal() {
        String tag_json_obj = "json_obj_jadwal_sholat";
        String url = ConstantUtil.WEB_SERVICE.URL_GET_JADWAL;
        Log.i("url", "url :" + url);
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Jadwal", response.toString());

                        try {
                            jobj = new JSONObject(response);
                            if (jobj.has("status_valid")) {
                                String status = jobj.getString("status_valid");
                                if (status.equals("1")) {
                                    // Save untuk offline


                                    tv_lokasi.setText(jobj.getString("state").toString().toUpperCase());

                                    jarr = jobj.getJSONArray("items");

                                    String pattern = "yyyy-MM-dd";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                                    for (int i = 0; i < jarr.length(); i++) {
                                        JSONObject obj = jarr.getJSONObject(i);
                                        String dateStr = obj.getString("date_for");
                                        String dateTampil = "";
                                        try {
                                            Date birthDate = simpleDateFormat.parse(dateStr);
                                            dateTampil = simpleDateFormat.format(birthDate);
                                            tv_tgl_jadwal.setText(dateTampil);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        tv_subuh.setText(obj.getString("fajr"));
                                        tv_zuhur.setText(obj.getString("dhuhr"));
                                        tv_ashar.setText(obj.getString("asr"));
                                        tv_magrib.setText(obj.getString("maghrib"));
                                        tv_isya.setText(obj.getString("isha"));
                                        tv_sunrise.setText(obj.getString("shurooq"));


                                        if (dbHelper.getAlarms() == null) {
                                            setAlarm(obj.getString("fajr"), "Subuh",0,0);
                                            setAlarm(obj.getString("dhuhr"), "Zuhur",0,0);
                                            setAlarm(obj.getString("asr"), "Ashar",0,0);
                                            setAlarm(obj.getString("maghrib"), "Magrib",0,0);
                                            setAlarm(obj.getString("isha"), "Isya",0,0);
                                            setAlarm(obj.getString("shurooq"), "Sunrise",0,0);
                                        }else{
                                            if (!sharedPreferences.getString("lokasi","").equals(tv_lokasi.getText().toString())){
                                                setAlarm("", "",1,0);
                                            }
                                        }

                                    }

                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.clear();
                                    edit.putString("response", response);
                                    edit.putString("lokasi", tv_lokasi.getText().toString().toUpperCase());
                                    edit.commit();


                                } else {
                                    Toast.makeText(ctx, "Data not found", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            final Snackbar snackbar = Snackbar
                                    .make(v, "Problem on provider API", Snackbar.LENGTH_LONG)
                                    .setAction("Close", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                snackbar.dismiss();
                                        }
                                    });
                            snackbar.show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("paper", "Error: " + error.getMessage());
                if (error instanceof NoConnectionError) {
//                    Toast.makeText(ctx, "No internet Access, Check your internet connection.", Toast.LENGTH_SHORT).show();

                    if (!sharedPreferences.getString("response", "").equals("")) {
                        getJadwalOffline();
                    } else {
                        final Snackbar snackbar = Snackbar
                                .make(v, getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
                                .setAction("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
//                                snackbar.dismiss();
                                    }
                                });
                        snackbar.show();
                    }


                }


            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

    public  static void getJadwalByCity(final String city) {
        String tag_json_obj = "json_obj_jadwal_sholat";
        String url = ConstantUtil.WEB_SERVICE.URL_GET_JADWAL_BYCITY + URLEncoder.encode(city) + "/daily.json?key=" + ConstantUtil.WEB_SERVICE.APIJADWAL;
        Log.i("url", "url :" + url);

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Jadwal", response.toString());

                        try {
                            jobj = new JSONObject(response);
                            if (jobj.has("status_valid")) {
                                String status = jobj.getString("status_valid");


                                if (status.equals("1")) {
                                    // Save untuk offline



                                    if (MainActivity.edtSeach == null){
                                        tv_lokasi.setText(city.toUpperCase());
                                    }else{
                                        tv_lokasi.setText(MainActivity.edtSeach.getText().toString().toUpperCase());
                                        MainActivity.edtSeach.setText("");
                                    }


                                    jarr = jobj.getJSONArray("items");

                                    String pattern = "yyyy-MM-dd";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                                    for (int i = 0; i < jarr.length(); i++) {
                                        JSONObject obj = jarr.getJSONObject(i);
                                        String dateStr = obj.getString("date_for");
                                        String dateTampil = "";
                                        try {
                                            Date birthDate = simpleDateFormat.parse(dateStr);
                                            dateTampil = simpleDateFormat.format(birthDate);
                                            tv_tgl_jadwal.setText(dateTampil);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }


                                        tv_subuh.setText(obj.getString("fajr"));
                                        tv_zuhur.setText(obj.getString("dhuhr"));
                                        tv_ashar.setText(obj.getString("asr"));
                                        tv_magrib.setText(obj.getString("maghrib"));
                                        tv_isya.setText(obj.getString("isha"));
                                        tv_sunrise.setText(obj.getString("shurooq"));

                                        if (dbHelper.getAlarms() == null) {
                                            setAlarm(obj.getString("fajr"), "Subuh",0,0);
                                            setAlarm(obj.getString("shurooq"), "Sunrise",0,0);
                                            setAlarm(obj.getString("dhuhr"), "Dzuhur",0,0);
                                            setAlarm(obj.getString("asr"), "Ashar",0,0);
                                            setAlarm(obj.getString("maghrib"), "Maghrib",0,0);
                                            setAlarm(obj.getString("isha"), "Isya",0,0);

                                        }else{

                                            if (!sharedPreferences.getString("lokasi","").equals(tv_lokasi.getText().toString())){
                                                setAlarm("", "",1,0);
                                            }
                                        }

                                    }

                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.clear();
                                    edit.putString("response", response);
                                    edit.putString("lokasi", tv_lokasi.getText().toString().toUpperCase());
                                    edit.commit();

                                    ac_cari.setText("");
                                } else {
                                    Toast.makeText(ctx, "Data not found", Toast.LENGTH_LONG).show();
                                }
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
                VolleyLog.d("paper", "Error: " + error.getMessage());
                pDialog.hide();
                pDialog.dismiss();
                final Snackbar snackbar1 = Snackbar
                        .make(v, "Check your connection!", Snackbar.LENGTH_LONG)
                        .setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                snackbar.dismiss();
                            }
                        });
                snackbar1.show();
                if (error instanceof NoConnectionError) {

                    final Snackbar snackbar = Snackbar
                            .make(v, "Check your connection!", Snackbar.LENGTH_LONG)
                            .setAction("Close", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                snackbar.dismiss();
                                }
                            });
                    snackbar.show();
                }


            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

    public void getJadwalOffline() {

        try {
            String response = sharedPreferences.getString("response", "");
            jobj = new JSONObject(response);
            if (jobj.has("status_valid")) {
                String status = jobj.getString("status_valid");
                if (status.equals("1")) {
                    tv_lokasi.setText(jobj.getString("state"));
                    JSONArray jarr = jobj.getJSONArray("items");
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject obj = jarr.getJSONObject(i);
                        String dateStr = obj.getString("date_for");
                        String dateTampil = "";
                        try {
                            Date birthDate = simpleDateFormat.parse(dateStr);
                            dateTampil = simpleDateFormat.format(birthDate);
                            tv_tgl_jadwal.setText(dateTampil);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        tv_subuh.setText(obj.getString("fajr"));
                        tv_zuhur.setText(obj.getString("dhuhr"));
                        tv_ashar.setText(obj.getString("asr"));
                        tv_magrib.setText(obj.getString("maghrib"));
                        tv_isya.setText(obj.getString("isha"));
                        tv_sunrise.setText(obj.getString("shurooq"));

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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


    public static void setAlarm(String jam, String name, int flag, int id) {


        if (flag == 1){
            List<AlarmModel> list = dbHelper.getAlarms();

            for (int i = 0 ; i < list.size() ; i++){

                JSONObject obj = null;
                try {
                    obj = jarr.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (list.get(i).name.equals("Subuh")){
                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("fajr");
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }else  if (list.get(i).name.equals("Zuhur")){

                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("dhuhr");
                        Log.d("Jam",jamm);
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }else  if (list.get(i).name.equals("Ashar")){
                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("asr");
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }else  if (list.get(i).name.equals("Magrib")){
                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("maghrib");
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }else  if (list.get(i).name.equals("Isya")){
                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("isha");
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }else  if (list.get(i).name.equals("Sunrise")){
                    alarmDetails = dbHelper.getAlarm(list.get(i).id);
                    try {
                        String jamm = obj.getString("shurooq");
                        if (jamm.length() < 8) {
                            jamm = "0" + jamm;
                        }
                        String minute = jamm.substring(3, 5);
                        alarmDetails.timeHour = Integer.valueOf(parsingJam(jamm));
                        alarmDetails.timeMinute = Integer.valueOf(minute);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.updateAlarm(alarmDetails);

                }

            }

        }else{
            alarmDetails = new AlarmModel();

            if (jam.length() < 8) {
                jam = "0" + jam;
            }

            String minute = jam.substring(3, 5);
            Log.d("Minute", minute);
            alarmDetails.name = name;
            alarmDetails.timeHour = Integer.valueOf(parsingJam(jam));
            alarmDetails.timeMinute = Integer.valueOf(minute);
            alarmDetails.isEnabled = true;


            dbHelper.createAlarm(alarmDetails);
        }


        //AlarmManagerHelper.setAlarms(ctx);

        Log.d("Masuk Alarm", "Alarm");
    }

    public static String parsingJam(String jam){
        String hour = "";
        if (jam.contains("am")) {
            if (jam.length() < 8) {
                jam = "0" + jam;
            }

            if (jam.substring(0, 2).equals("12")) {
                hour = "00";
            } else {
                hour = jam.substring(0, 2);
                Log.d("hour", hour);
            }

        } else if (jam.contains("pm")) {
            if (jam.length() < 8) {
                jam = "0" + jam;
            }

            if (jam.substring(0, 2).equals("01")) {
                hour = "13";
            } else if (jam.substring(0, 2).equals("02")) {
                hour = "14";
            } else if (jam.substring(0, 2).equals("03")) {
                hour = "15";
            } else if (jam.substring(0, 2).equals("04")) {
                hour = "16";
            } else if (jam.substring(0, 2).equals("05")) {
                hour = "17";
            } else if (jam.substring(0, 2).equals("06")) {
                hour = "18";
            } else if (jam.substring(0, 2).equals("07")) {
                hour = "19";
            } else if (jam.substring(0, 2).equals("08")) {
                hour = "20";
            } else if (jam.substring(0, 2).equals("09")) {
                hour = "21";
            } else if (jam.substring(0, 2).equals("10")) {
                hour = "22";
            } else if (jam.substring(0, 2).equals("11")) {
                hour = "23";
            } else if (jam.substring(0, 2).equals("12")) {
            hour = "12";
        }
        }

        return  hour;
    }

    public static String city() {
        gpsTracker = new GPSTracker(ctx);
        String cityname = "";
        if (gpsTracker.canGetLocation()) {

            Geocoder gcd=new Geocoder(ctx, Locale.getDefault());

            Log.d("Tag","1");
            List<Address> addresses;

            try {
                addresses=gcd.getFromLocation(gpsTracker.getLatitude(),gpsTracker.getLongitude(),1);
                if(addresses.size()>0)
                {
                    //while(locTextView.getText().toString()=="Location") {
                    cityname = addresses.get(0).getLocality().toString();
                    Log.d("CityName", cityname);
                    // }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            gpsTracker.showSettingsAlert();
        }
        return cityname;

    }
}

