package com.pa.ikram.maps;

/**
 * Created by Nurul Akbar on 10/11/2015.
 */
public class MyMarker {
    private String mId;
    private String mTitle;
    private String mHarga;
    private String mAlamat;
    private String mTipe;
    private String mIcon;
    private Double mLatitude;
    private Double mLongitude;

    public MyMarker(String title,  String alamat, Double latitude, Double longitude, String id) {
        this.mId = id;
        this.mTitle = title;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mAlamat = alamat;

    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String icon) {
        this.mIcon = icon;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public String getmHarga() {
        return mHarga;
    }

    public void setmHarga(String mHarga) {
        this.mHarga = mHarga;
    }

    public String getmAlamat() {
        return mAlamat;
    }

    public void setmAlamat(String mAlamat) {
        this.mAlamat = mAlamat;
    }

    public String getmTipe() {
        return mTipe;
    }

    public void setmTipe(String mTipe) {
        this.mTipe = mTipe;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }
}
