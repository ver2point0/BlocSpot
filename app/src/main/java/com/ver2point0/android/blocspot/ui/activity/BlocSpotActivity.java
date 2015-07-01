package com.ver2point0.android.blocspot.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ver2point0.android.blocspot.R;


public class BlocSpotActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocspot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_blocspot);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this add items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_blocspot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_one:
                break;
            case R.id.action_two:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}
