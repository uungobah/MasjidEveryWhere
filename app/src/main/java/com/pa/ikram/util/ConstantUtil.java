package com.pa.ikram.util;

public interface ConstantUtil {


    interface APP {

    }

    interface WEB_SERVICE {
        String IP = "http://192.168.1.102:80";

        String IPDOMAIN = "http://tangaya.id/api_mosque_everywhere";


        String APIJADWAL = "1361613acfa87a0163c93825c742a9cd";


        String URL_GET_JADWAL = "http://muslimsalat.com/daily.json?key="+APIJADWAL;
        String URL_GET_JADWAL_BYCITY = "http://muslimsalat.com/";
        String URL_GET_AUTO_COMPLETE ="https://maps.googleapis.com/maps/api/place/autocomplete/json?types=(cities)&key=AIzaSyC4vnTHkhzFB9v8nr8LTkdRmG51S8XpbGI&language=id&input=";
        String URL_POST_DAFTAR_MASJID =IPDOMAIN+"/create_masjid.php";
        String URL_POST_DAFTAR_PENGGUNA =IPDOMAIN+"/create_pengguna.php";
        String URL_POST_LOGIN =IPDOMAIN+"/login.php";
        String URL_GET_ALL_MASJID =IPDOMAIN+"/get_all_masjid.php";
        String URL_GET_ALL_MASJID_UNVERIFIED =IPDOMAIN+"/get_all_masjid_unverified.php";
        String URL_POST_UPDATE_MASJID =IPDOMAIN+"/update_verified.php";


    }

    interface SHAREDPREF{
        String JADWAL_OFFLINE = "JADWAL_OFFLINE";
        String PENGGUNA = "PENGGUNA";
        String LOKASI = "LOKASI";
    }
    interface PDF {
        String PDF_DIRECTORY_PROD = "paperless";

    }

    interface PICTURE {
        // Dev
        // public String path =
        // "/ajax_ifesdev/ccfUpload/file/paperless/file_upload/";
        // Prod
        String path_prod = "/ccfUpload/file/paperless/file_upload/";
        int LOW = 50;
        int MED = 80;
        int HIGH = 100;

    }
}
