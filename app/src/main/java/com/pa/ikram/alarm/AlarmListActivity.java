package com.pa.ikram.alarm;

import android.app.ActionBar;
import android.app.AlertDialog;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AlarmListActivity extends ListActivity {

	private AlarmListAdapter mAdapter;
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	private Context mContext;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	AutoCompleteTextView ac_cari;
	AppCompatButton btn_set;

	JSONObject jobj;

	SharedPreferences sharedPreferences;

	ArrayList resultList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mContext = this;

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_alarm_list);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ac_cari = (AutoCompleteTextView) findViewById(R.id.ac_kota_setting);
		btn_set = (AppCompatButton) findViewById(R.id.btn_set);

		mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());

		setListAdapter(mAdapter);

		sharedPreferences = getSharedPreferences(ConstantUtil.SHAREDPREF.LOKASI, Context.MODE_PRIVATE);

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

		ac_cari.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

		btn_set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				View view = getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				SharedPreferences.Editor edit = sharedPreferences.edit();
				edit.putString("lokasi",ac_cari.getText().toString());
				edit.commit();
				Toast.makeText(getApplicationContext(),"Location has been set to "+ac_cari.getText().toString() , Toast.LENGTH_LONG).show();
				ac_cari.setText("");
			}
		});
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_list, menu);
		return true;
	}*/
/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_add_new_alarm: {
				startAlarmDetailsActivity(-1);
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			mAdapter.setAlarms(dbHelper.getAlarms());
			mAdapter.notifyDataSetChanged();
		}
	}

	public void setAlarmEnabled(long id, boolean isEnabled) {
		//AlarmManagerHelper.cancelAlarms(this);

		AlarmModel model = dbHelper.getAlarm(id);
		model.isEnabled = isEnabled;
		dbHelper.updateAlarm(model);

		//AlarmManagerHelper.setAlarms(this);
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


	/*public void startAlarmDetailsActivity(long id) {
		//Intent intent = new Intent(this, AlarmDetailsActivity.class);
//		intent.putExtra("id", id);
//		startActivityForResult(intent, 0);
	}
	
	public void deleteAlarm(long id) {
		final long alarmId = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please confirm")
		.setTitle("Delete set?")
		.setCancelable(true)
		.setNegativeButton("Cancel", null)
		.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Cancel Alarms
				AlarmManagerHelper.cancelAlarms(mContext);
				//Delete alarm from DB by id
				dbHelper.deleteAlarm(alarmId);
				//Refresh the list of the alarms in the adaptor
				mAdapter.setAlarms(dbHelper.getAlarms());
				//Notify the adapter the data has changed
				mAdapter.notifyDataSetChanged();
				//Set the alarms
				AlarmManagerHelper.setAlarms(mContext);
			}
		}).show();
	}*/
}
