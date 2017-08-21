package com.pa.ikram.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.pa.ikram.ikrampa.R;

/**
 * Created by user on 16/06/2016.
 */
public class SearchResultsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.form_login);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            /**
             * Use this query to display search results like
             * 1. Getting the data from SQLite and showing in listview
             * 2. Making webrequest and displaying the data
             * For now we just display the query only
             */
            //  txtQuery.setText("Search Query: " + query);
            Toast.makeText(getApplicationContext(),query,Toast.LENGTH_LONG).show();

        }

    }
}
