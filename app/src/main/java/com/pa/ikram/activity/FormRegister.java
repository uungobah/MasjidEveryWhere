package com.pa.ikram.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.pa.ikram.MainActivity;
import com.pa.ikram.controller.AppController;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.intrfc.ValidasiInterface;
import com.pa.ikram.util.ConstantUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nurul Akbar on 12/10/2015.
 */
public class FormRegister extends Activity implements ValidasiInterface {

    private EditText et_firstname, et_lastname, et_email, et_username, et_password, et_repassword;

    // Text Input Layout
    private TextInputLayout til_firstname, til_lastname, til_email, til_username, til_password, til_repassword;

    //AppCompatButton
    private AppCompatButton btnRegister;

    //TextView
    TextView tv_login, tv_msg_success;


    String s_tipe, s_img_profil, s_first_name, s_last_name, s_jk, s_email, s_nohp, s_username, s_password, s_repassword, s_ktp;

    JSONObject jobj;
    String status;
    SharedPreferences sharedpreferences;

    public FormRegister() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedpreferences = this.getSharedPreferences(
                ConstantUtil.SHAREDPREF.PENGGUNA, Context.MODE_PRIVATE);

        inisialisasi();


        et_password.addTextChangedListener(new MyTextWatcher(et_password));
        et_repassword.addTextChangedListener(new MyTextWatcher(et_repassword));
        et_firstname.addTextChangedListener(new MyTextWatcher(et_firstname));
        et_lastname.addTextChangedListener(new MyTextWatcher(et_lastname));
        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_username.addTextChangedListener(new MyTextWatcher(et_username));


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pencet button", "pencet button");
                submitForm();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FormRegister.this, FormLogin.class));
                finish();
            }
        });


    }

    public void inisialisasi() {
        // Button
        btnRegister = (AppCompatButton) findViewById(R.id.btn_register);
        //EditText

        et_firstname = (EditText) findViewById(R.id.et_register_firstname);
        et_lastname = (EditText) findViewById(R.id.et_register_lastname);
        et_email = (EditText) findViewById(R.id.et_register_email);
        et_username = (EditText) findViewById(R.id.et_register_username);
        et_password = (EditText) findViewById(R.id.et_register_password);
        et_repassword = (EditText) findViewById(R.id.et_register_repassword);

        // TextInputLayout
        til_firstname = (TextInputLayout) findViewById(R.id.input_layout_register_firstname);
        til_lastname = (TextInputLayout) findViewById(R.id.input_layout_register_lastname);

        til_username = (TextInputLayout) findViewById(R.id.input_layout_register_username);
        til_email = (TextInputLayout) findViewById(R.id.input_layout_register_email);
        til_password = (TextInputLayout) findViewById(R.id.input_layout_register_password);
        til_repassword = (TextInputLayout) findViewById(R.id.input_layout_register_repassword);

        //TextView
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_msg_success = (TextView) findViewById(R.id.tv_msg_success_register);

        // ImageView

        s_email = "";
        s_first_name = "";
        s_img_profil = "";
        s_jk = "";
        s_ktp = "";
        s_last_name = "";
        s_nohp = "";
        s_password = "";
        s_tipe = "";
        s_repassword = "";
        s_username = "";


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FormRegister.this, FormLogin.class));
        finish();
    }

    public void submitForm() {
        Log.d("validasi", "validasi");
        int x = 0;

//        if (!validateTipe()) {
//            x = x + 1;
//            return;
//        }

        if (!validateFirstName()) {
            x = x + 1;
            return;
        }

        if (!validateLastName()) {
            x = x + 1;
            return;
        }


        if (!validateEmail()) {
            x = x + 1;
            return;
        }

        if (!validateUsername()) {
            x = x + 1;
            return;
        }


        if (!validatePassword()) {
            x = x + 1;
            return;
        }

        if (!validateRePassword()) {
            x = x + 1;
            return;
        }

        if (x == 0) {
           // getParameter();
            // submitRegisterPost();

            submitDaftarUserPost();

        }
    }

    private boolean validateFirstName() {
        if (et_firstname.getText().toString().trim().isEmpty()) {
            til_firstname.setError(getString(R.string.err_msg_firstname));
            requestFocus(et_firstname);
            return false;
        } else {
            til_firstname.setErrorEnabled(false);

        }
        return true;


    }


    private boolean validateLastName() {
        if (et_lastname.getText().toString().trim().isEmpty()) {
            til_lastname.setError(getString(R.string.err_msg_lastname));
            requestFocus(et_lastname);
            return false;
        } else {
            til_lastname.setErrorEnabled(false);

        }
        return true;


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

    private boolean validateEmail() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            til_email.setError(getString(R.string.err_msg_email));
            requestFocus(et_email);
            return false;
        } else {
            til_email.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        } else if (et_password.getText().toString().length() <= 5) {
            til_password.setError("Password Harus Lebih Dari 5 Karakter");
            requestFocus(et_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);

        }
        return true;


    }

    private boolean validateRePassword() {
        if (et_repassword.getText().toString().trim().isEmpty()) {
            til_repassword.setError(getString(R.string.err_msg_password));
            requestFocus(et_repassword);
            return false;
        } else if (et_repassword.getText().toString().length() <= 5) {
            til_repassword.setError("Password Harus Lebih Dari 5 Karakter");
            requestFocus(et_repassword);
            return false;
        } else if (!et_repassword.getText().toString().equals(et_password.getText().toString())) {
            til_repassword.setError("Password Harus Sama");
            requestFocus(et_repassword);
            return false;
        } else {
            til_repassword.setErrorEnabled(false);

        }
        return true;

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


//    private static boolean isValidEmail(String email) {
//        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
//    }
//
//    private void requestFocus(View view) {
//        if (view.requestFocus()) {
//            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }

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
                case R.id.et_register_firstname:
                    validateFirstName();
                    break;
                case R.id.et_register_lastname:
                    validateLastName();
                    break;
                case R.id.et_register_password:
                    validatePassword();
                    break;
                case R.id.et_register_email:
                    validateEmail();
                    break;
                case R.id.et_register_username:
                    validateUsername();
                    break;
                case R.id.et_register_repassword:
                    validateRePassword();
                    break;
            }
        }
    }


    public void getParameter() {

        s_email = et_email.getText().toString();
        s_first_name = et_firstname.getText().toString();
        s_last_name = et_lastname.getText().toString();
        s_username = et_username.getText().toString();
        s_password = et_password.getText().toString();
        s_repassword = et_repassword.getText().toString();

    }

    public static String getStringBase64Bitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapBytes = bos.toByteArray();
        String encodedImage = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        Log.i("getStringBase64Bitmap", encodedImage);
        return encodedImage;
    }


    public void submitDaftarUserPost() {
        String tag_json_obj = "json_obj_req";

        String url = ConstantUtil.WEB_SERVICE.URL_POST_DAFTAR_PENGGUNA;

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

                                SharedPreferences.Editor edit = sharedpreferences.edit();

                                edit.putString("username", et_username.getText().toString());
                                edit.putString("name", et_firstname.getText().toString()+" "+et_lastname.getText().toString());
                                edit.putString("email", et_email.getText().toString());
                                edit.commit();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
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
                        .make((LinearLayout)findViewById(R.id.ll_register),getString(R.string.err_msg_jaringan), Snackbar.LENGTH_LONG)
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


                params.put("nama", et_firstname.getText().toString()+" "+et_lastname.getText().toString());
                params.put("username", et_username.getText().toString());
                params.put("password", et_password.getText().toString());
                params.put("email", et_email.getText().toString());

                Log.d("Params",params.toString());

//                params.put("remember_me","false");

                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

  public void reset(){
      et_firstname.setText("");
      et_lastname.setText("");
      et_username.setText("");
      et_password.setText("");
      et_email.setText("");
      et_repassword.setText("");
  }



  /*  public Bitmap getKtp() {
        Bitmap bitmap = ((BitmapDrawable) img_ktp.getDrawable()).getBitmap();
        return bitmap;
    }

    public Bitmap getImgProfile() {
        Bitmap bitmap = ((BitmapDrawable) img_ktp.getDrawable()).getBitmap();
        return bitmap;
    }*/

   /* public void submitRegisterPost() {
        String tag_json_obj = "json_obj_req";

        String url = ConstantUtil.WEB_SERVICE.URL_REGISTER;

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

                            if (jobj.has("message")) {
                                status = jobj.getString("message");
                                Log.v("message", jobj.getString("message"));
                                Toast.makeText(getApplication(), "message " + jobj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            if (status.equals("Success")){
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                finish();
                                tv_msg_success.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(getApplication(), "Register Gagal", Toast.LENGTH_LONG).show();
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Erorr", "Error: " + error.getMessage());
                pDialog.hide();
                Toast.makeText(getApplication(), "Register Gagal Ada Kesalahan Jaringan", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("useremail", s_email);
                params.put("first_name", s_first_name);
                params.put("last_name", s_last_name);
                params.put("phone", s_nohp);
                params.put("password", s_password);
                params.put("repassword", s_repassword);
//                params.put("gender", s_jk);
//                params.put("ktp", s_ktp);
                params.put("user_type","2");
                params.put("act", "2");


                return params;
            }

        };

        jsonObjReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }*/
}
