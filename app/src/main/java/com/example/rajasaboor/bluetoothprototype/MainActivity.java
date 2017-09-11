package com.example.rajasaboor.bluetoothprototype;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rajasaboor.bluetoothprototype.databinding.ActivityMainBinding;
import com.example.rajasaboor.bluetoothprototype.fragments.Contract;
import com.example.rajasaboor.bluetoothprototype.fragments.DevicesListFragment;
import com.example.rajasaboor.bluetoothprototype.fragments.MainFragment;
import com.example.rajasaboor.bluetoothprototype.fragments.Presenter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mainBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mainFragment)
                    .commit();
        }

        Contract.Presenter presenter = new Presenter(getSharedPreferences(BuildConfig.BROADCAST_PREFS_NAME, MODE_PRIVATE));
        mainFragment.setPresenter(presenter);
        ((Presenter) presenter).setOnDiscoveryComplete(mainFragment);

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment_container);

        if (searchFragment == null) {
            Log.d(TAG, "onCreate: Search fragment is setting up");
            searchFragment = SearchFragment.newInstance();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.search_fragment_container, searchFragment)
                    .commit();
            Log.d(TAG, "onCreate: Setting up done");
        }
        presenter.showSearchFragment(getSupportFragmentManager(), false);

        DevicesListFragment listFragment = (DevicesListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment_container);

        if (listFragment == null) {
            Log.d(TAG, "addListFragment: List fragment is NULL");
            listFragment = DevicesListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.list_fragment_container, listFragment)
                    .commit();
        }

        listFragment.setDeviceClickListener((DevicesListFragment.OnDeviceClickListener) presenter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
