package com.pa.ikram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pa.ikram.MainActivity;
import com.pa.ikram.ikrampa.R;

/**
 * Created by user on 25/08/2016.
 */
public class AboutFragment extends Fragment {

    View v;
    Context ctx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.about, container, false);
        ctx = v.getContext();

        MainActivity.hideItem(0);

        return v;
    }
}
