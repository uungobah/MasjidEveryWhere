package com.pa.ikram.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pa.ikram.MainActivity;
import com.pa.ikram.adapter.AdapterVerification;
import com.pa.ikram.adapter.InformationVerification;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 24/08/2016.
 */
public class VerificationFragment extends Fragment {

    View v;
    Context ctx;

    RecyclerView rv_ver;

    JSONObject jobj;
    AdapterVerification adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.verification, container, false);
        ctx = v.getContext();

        MainActivity.hideItem(0);

        rv_ver = (RecyclerView) v.findViewById(R.id.rv_list_verification);

        getListVerification();

        return v;

    }

    public void getListVerification() {

        final List<InformationVerification> data = new ArrayList<>();
        String tag_json_obj = "json_obj_list_apartment";


        String url = ConstantUtil.WEB_SERVICE.URL_GET_ALL_MASJID_UNVERIFIED;

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", response.toString());
                        try {
                            jobj = new JSONObject(response);
                            String success = jobj.getString("success");
                            if (success.equals("0")){
                                Toast.makeText(ctx,"Masjid Not Found",Toast.LENGTH_LONG).show();
                            }
                            JSONArray jarr = jobj
                                    .getJSONArray("masjid");
                            Log.v("Masuk sukses", "Masuk");
                            for (int i = 0; i < jarr.length(); i++) {
                                JSONObject c = jarr.getJSONObject(i);
                                Log.v("Masuk looping", "Masuk");
                                InformationVerification current = new InformationVerification();
                                current.id = c.getString("mosque_id");
                                current.nama = c.getString("mosque_name");
                                current.alamat = c.getString("mosque_address");
                                current.latitude = c.getString("latitude");
                                current.longitude = c.getString("longitude");

                                data.add(current);

                            }

                            rv_ver.setLayoutManager(new LinearLayoutManager(ctx));
                            adapter = new AdapterVerification(ctx, data);
                            Log.d("data",""+data.size());

                            rv_ver.setAdapter(adapter);


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
                Snackbar snackbar1 = Snackbar
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
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }

}
