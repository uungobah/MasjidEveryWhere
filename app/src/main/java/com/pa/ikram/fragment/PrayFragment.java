package com.pa.ikram.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pa.ikram.MainActivity;
import com.pa.ikram.ikrampa.R;
import com.pa.ikram.util.ConstantUtil;

/**
 * Created by user on 26/07/2016.
 */
public class PrayFragment extends Fragment {

    View v;
    Context ctx;

    ImageView img;
    Button btn_next,btn_prev;
    int a = 1;
    int size = 10;
    TextView tv_bacan;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_pray, container, false);
        ctx = v.getContext();
        MainActivity.hideItem(0);

        img = (ImageView) v.findViewById(R.id.img_pray);
        tv_bacan = (TextView) v.findViewById(R.id.tv_bacaan);
        btn_next = (Button) v.findViewById(R.id.btn_next);
        btn_prev = (Button) v.findViewById(R.id.btn_prev);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(img != null) {
                    ((BitmapDrawable)img.getDrawable()).getBitmap().recycle();
                }*/
                a = a + 1;
                String uri = "@drawable/s"+a;  // where myresource (without the extension) is the file
                if ( a == 1){
                    tv_bacan.setText(getString(R.string.s1));
                }else if (a ==2 ){
                    tv_bacan.setText(getString(R.string.s2));
                }else if (a ==3 ){
                    tv_bacan.setText(getString(R.string.s3));
                }
                else if (a ==4 ){
                    tv_bacan.setText(getString(R.string.s4));
                }
                else if (a ==5 ){
                    tv_bacan.setText(getString(R.string.s5));
                }
                else if (a ==6 ){
                    tv_bacan.setText(getString(R.string.s6));
                }
                else if (a ==7 ){
                    tv_bacan.setText(getString(R.string.s7));
                }else if (a ==8 ){
                    tv_bacan.setText(getString(R.string.s8));
                }else if (a ==9 ){
                    tv_bacan.setText(getString(R.string.s9));
                }else if (a ==10 ){
                    tv_bacan.setText(getString(R.string.s10));
                }
                else if (a ==11 ){
                    tv_bacan.setText(getString(R.string.s11));
                }
                else if (a ==12 ){
                    tv_bacan.setText(getString(R.string.s12));
                }
                else if (a ==13 ){
                    tv_bacan.setText(getString(R.string.s13));
                }
                else if (a ==14 ){
                    tv_bacan.setText(getString(R.string.s14));
                }
                else if (a ==15 ){
                    tv_bacan.setText(getString(R.string.s15));
                }
                else if (a ==16 ){
                    tv_bacan.setText(getString(R.string.s16));
                }
                else if (a ==17 ){
                    tv_bacan.setText(getString(R.string.s17));
                }
                else if (a ==18 ){
                    tv_bacan.setText(getString(R.string.s18));
                }


                int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());

                Drawable res = getResources().getDrawable(imageResource);
                img.setImageDrawable(res);
                /*Bitmap bitmapOriginal = ((BitmapDrawable)res).getBitmap();

                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bitmapOriginal,bitmapOriginal.getWidth() / size, bitmapOriginal.getHeight() / size, true);
                bitmapOriginal.recycle();
                img.setImageBitmap(bitmapsimplesize);*/


                if (a == 18){
                    btn_next.setVisibility(View.GONE);
                }

                if (a > 1){

                        btn_prev.setVisibility(View.VISIBLE);

                }
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(img != null) {
                    ((BitmapDrawable)img.getDrawable()).getBitmap().recycle();
                }*/
                a = a - 1;
                String uri = "@drawable/s"+a;

                if ( a == 1){
                    tv_bacan.setText(getString(R.string.s1));
                }else if (a ==2 ){
                    tv_bacan.setText(getString(R.string.s2));
                }else if (a ==3 ){
                    tv_bacan.setText(getString(R.string.s3));
                }
                else if (a ==4 ){
                    tv_bacan.setText(getString(R.string.s4));
                }
                else if (a ==5 ){
                    tv_bacan.setText(getString(R.string.s5));
                }
                else if (a ==6 ){
                    tv_bacan.setText(getString(R.string.s6));
                }
                else if (a ==7 ){
                    tv_bacan.setText(getString(R.string.s7));
                }else if (a ==8 ){
                    tv_bacan.setText(getString(R.string.s8));
                }else if (a ==9 ){
                    tv_bacan.setText(getString(R.string.s9));
                }else if (a ==10 ){
                    tv_bacan.setText(getString(R.string.s10));
                }
                else if (a ==11 ){
                    tv_bacan.setText(getString(R.string.s11));
                }
                else if (a ==12 ){
                    tv_bacan.setText(getString(R.string.s12));
                }
                else if (a ==13 ){
                    tv_bacan.setText(getString(R.string.s13));
                }
                else if (a ==14 ){
                    tv_bacan.setText(getString(R.string.s14));
                }
                else if (a ==15 ){
                    tv_bacan.setText(getString(R.string.s15));
                }
                else if (a ==16 ){
                    tv_bacan.setText(getString(R.string.s16));
                }
                else if (a ==17 ){
                    tv_bacan.setText(getString(R.string.s17));
                }
                else if (a ==18 ){
                    tv_bacan.setText(getString(R.string.s18));
                }




                int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());

                Drawable res = getResources().getDrawable(imageResource);
                /*Bitmap bitmapOriginal = ((BitmapDrawable)res).getBitmap();

                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bitmapOriginal,bitmapOriginal.getWidth() / size, bitmapOriginal.getHeight() / size, true);
                bitmapOriginal.recycle();
                img.setImageBitmap(bitmapsimplesize);*/
                img.setImageDrawable(res);

                if (a == 1){
                    btn_prev.setVisibility(View.GONE);
                }

                if (a < 18){
                    btn_next.setVisibility(View.VISIBLE);
                }
            }
        });
        return v;

    }
}
