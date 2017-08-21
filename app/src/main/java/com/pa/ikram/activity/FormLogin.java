package com.pa.ikram.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.pa.ikram.MainActivity;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.util.ConstantUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Nurul Akbar on 12/10/2015.
 */
public class FormLogin extends Activity {

    private EditText et_username, et_password;

    // Text Input Layout
    private TextInputLayout til_username, til_password;

    //AppCompatButton
    private AppCompatButton btnLogin;

    //TextView
    TextView tv_register;

    JSONObject jobj;
    String status;
    SharedPreferences sharedpreferences;


    public FormLogin() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        inisialisasi();

        et_username.addTextChangedListener(new MyTextWatcher(et_username));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));

        sharedpreferences = this.getSharedPreferences(
                ConstantUtil.SHAREDPREF.PENGGUNA, Context.MODE_PRIVATE);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormLogin.this, FormRegister.class));
                finish();

            }
        });
    }

    public void inisialisasi() {
        // Button
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        //EditText
        et_username = (EditText) findViewById(R.id.et_login_username);
        et_password = (EditText) findViewById(R.id.et_login_password);

        // TextInputLayout
        til_username = (TextInputLayout) findViewById(R.id.input_layout_login_username);
        til_password = (TextInputLayout) findViewById(R.id.input_layout_login_password);

        //TextView
        tv_register = (TextView) findViewById(R.id.tv_daftar);


        status = "";

    }


    public void submitForm() {
        int x = 0;
        if (!validateUsername()) {
            x = x + 1;
            return;
        }
        if (!validatePassword()) {
            x = x + 1;
            return;
        }

        if (x == 0) {
            submitLoginPost();
        }

    }


    private boolean validateUsername() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError(getString(R.string.err_msg_username));
            requestFocus(et_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);

        }
        return true;


    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.et_login_username:
                    validateUsername();
                    break;
                case R.id.et_login_password:
                    validatePassword();
                    break;

            }
        }
    }

    public void submitLoginPost() {
        String tag_json_obj = "json_obj_req";

        String url = ConstantUtil.WEB_SERVICE.URL_POST_LOGIN;

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
                                JSONArray jarr = jobj.getJSONArray("pengguna");

                                SharedPreferences.Editor edit = sharedpreferences.edit();
                                JSONObject jobj2 = jarr.getJSONObject(0);

                                edit.putString("username", jobj2.getString("username"));
                                edit.putString("name", jobj2.getString("name"));
                                edit.putString("email", jobj2.getString("email"));
                                edit.putString("level", jobj2.getString("level"));
                                edit.commit();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), jobj.getString("message"), Toast.LENGTH_LONG).show();
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
                pDialog.hide();
                pDialog.dismiss();
                final Snackbar snackbar = Snackbar
                        .make((LinearLayout) findViewById(R.id.ll_login), getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
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


                params.put("username", et_username.getText().toString());
                params.put("password", et_password.getText().toString());
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FormLogin.this, MainActivity
                .class));
        this.finish();
    }
}
