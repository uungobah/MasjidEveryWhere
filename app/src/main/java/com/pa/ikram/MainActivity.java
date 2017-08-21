package com.pa.ikram;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.pa.ikram.activity.Add_Masjid;
import com.pa.ikram.activity.FormLogin;
import com.pa.ikram.activity.Masjid;
import com.pa.ikram.alarm.AlarmListActivity;
import com.pa.ikram.alarm.AlarmManagerHelper;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.fragment.AboutFragment;
import com.pa.ikram.fragment.HomeFragment;
import com.pa.ikram.fragment.KiblatFragment;
import com.pa.ikram.fragment.PrayFragment;
import com.pa.ikram.fragment.VerificationFragment;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.maps.GPSTracker;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NavigationView navigationView;
    private Toolbar toolbar;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private DrawerLayout drawer;

    ImageView imgProfile;
    TextView tv_nama, tv_email;

    GPSTracker gpsTracker;

    String user,level;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences2;
    static Menu menu;

    SearchView searchView;


    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    public static AutoCompleteTextView edtSeach;

    ArrayList resultList;
    JSONObject jobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        gpsTracker = new GPSTracker(this);


        // Kalau mau pakai Icon Pada ActionBar
        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setIcon(R.drawable.logo);*/

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();




        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);

//        navigationView.addHeaderView(header);

        imgProfile = (ImageView) header.findViewById(R.id.img_profile);
        tv_nama = (TextView) header.findViewById(R.id.tv_nama);
        tv_email = (TextView) header.findViewById(R.id.tv_email);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        navigationView.setNavigationItemSelectedListener(this);

        checkLogin();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HomeFragment fragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();


        sharedPreferences2 = getSharedPreferences(ConstantUtil.SHAREDPREF.LOKASI,Context.MODE_PRIVATE);

        if (sharedPreferences2.getString("lokasi","").equals("")){

            SharedPreferences.Editor edit = sharedPreferences2.edit();
            edit.putString("lokasi","Jawa Barat");
            edit.commit();

        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        this.menu = menu;

        return true;


    }



    public static void hideItem(int index)
    {
        MenuItem mi = menu.getItem(index);
        mi.setVisible(false);
    }

    public static void showItem(int index)
    {
        MenuItem mi = menu.getItem(index);
        mi.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open
            Log.d("Masuk Do serach", "Masuk If");

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);*/
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (AutoCompleteTextView) action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            edtSeach.setAdapter(new GooglePlacesAutocompleteAdapter(MainActivity.this, R.layout.list_item));

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        Log.d("Masuk Do serach", "Masuk");
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_24dp));

            isSearchOpened = true;
        }
    }


    private void doSearch() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        HomeFragment.getJadwalByCity(edtSeach.getText().toString());
        isSearchOpened = true;
        handleMenuSearch();


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HomeFragment fragment = new HomeFragment();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_mosque) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS) {
                startActivity(new Intent(MainActivity.this, Masjid
                        .class));
            } else {
                gpsTracker.showSettingsAlert();
            }


        } else if (id == R.id.nav_add_mosque) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS) {
                startActivity(new Intent(MainActivity.this, Add_Masjid
                        .class));
            } else {
                gpsTracker.showSettingsAlert();
            }

        }else if (id == R.id.nav_alarm) {

                startActivity(new Intent(MainActivity.this, AlarmListActivity
                        .class));

        } else if (id == R.id.nav_doa) {

            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            PrayFragment fragment = new PrayFragment();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_kiblat) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            KiblatFragment fragment = new KiblatFragment();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_ver) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            VerificationFragment fragment = new VerificationFragment();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_about) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            AboutFragment fragment = new AboutFragment();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }  else if (id == R.id.nav_loginorlogout) {
            startActivity(new Intent(MainActivity.this, FormLogin
                    .class));
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.commit();
            this.finish();
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkLocation() {

        boolean status = false;


        if (gpsTracker.canGetLocation()) {
            status = true;
            Log.d("Can Get Location", "" + gpsTracker.getLatitude());

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
            status = false;
        }
        return status;
    }

    public void checkLogin() {

        sharedPreferences = getSharedPreferences(ConstantUtil.SHAREDPREF.PENGGUNA, Context.MODE_PRIVATE);
        user = sharedPreferences.getString("username", "");
        level = sharedPreferences.getString("level", "");

        if (user.equals("")) {
            navigationView.getMenu().findItem(R.id.nav_add_mosque).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_ver).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_loginorlogout).setTitle("Login");

        } else {
            /*tv_email.setText(sharedPreferences.getString("name",""));
            tv_nama.setText(sharedPreferences.getString("name",""));*/
            navigationView.getMenu().findItem(R.id.nav_add_mosque).setVisible(true);
            if (level.equals("Admin")){
                navigationView.getMenu().findItem(R.id.nav_ver).setVisible(true);
            }else{
                navigationView.getMenu().findItem(R.id.nav_ver).setVisible(false);
            }
            navigationView.getMenu().findItem(R.id.nav_loginorlogout).setTitle("Logout");
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

}
