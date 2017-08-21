package com.pa.ikram.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.pa.ikram.activity.Add_Masjid;
import com.pa.ikram.ikrampa.R;


import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by Nurul Akbar on 05/11/2015.
 */
public class AdapterVerification extends RecyclerView.Adapter<AdapterVerification.MyViewHolder> {

    JSONObject jobj;
    String status;

    private LayoutInflater inflater;
    List<InformationVerification> data = Collections.emptyList();
    private Context context;

    SharedPreferences sharedPreferences;

    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    public AdapterVerification(Context context, List<InformationVerification> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

        //sharedPreferences = context.getSharedPreferences(ConstantUtil.SHAREDPREFERENCE.LOGIN, Context.MODE_PRIVATE);

    }


    @Override
    public AdapterVerification.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.custom_verification, viewGroup, false);
        MyViewHolder hoder = new MyViewHolder(view);
        return hoder;
    }



    @Override
    public void onBindViewHolder(final AdapterVerification.MyViewHolder holder, int i) {
        InformationVerification current = data.get(i);

        Log.d("Masuk Adapter",""+data.size());
        holder.nama.setText(current.nama);
        holder.alamat.setText(current.alamat);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView nama;
        TextView alamat;
        TableRow tr_ver;

        public MyViewHolder(View itemView) {

            super(itemView);
            context = itemView.getContext();

            nama = (TextView) itemView.findViewById(R.id.tv_ver_nama);
            alamat = (TextView) itemView.findViewById(R.id.tv_ver_lokasi);
            tr_ver = (TableRow) itemView.findViewById(R.id.tr_ver);

            tr_ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationVerification im = data.get(getPosition());

                    Intent i = new Intent(context, Add_Masjid.class);
                    i.putExtra("id",im.id);
                    i.putExtra("nama",im.nama);
                    i.putExtra("alamat",im.alamat);
                    i.putExtra("latitude",im.latitude);
                    i.putExtra("longitude",im.longitude);
                    context.startActivity(i);
                }
            });

//            spinPilihan = (AppCompatSpinner) itemView.findViewById(R.id.spin_manage_action);
        }

        @Override
        public void onClick(View v) {

        }
    }




}
