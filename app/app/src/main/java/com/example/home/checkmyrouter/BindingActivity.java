package com.example.home.checkmyrouter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Home on 24.03.2016.
 */
public class BindingActivity extends Activity {
    private ServiceManager mService;
    private boolean mBound = false;
    private BroadcastReceiver wifiReceiver;
    private IntentFilter intentFilter;
    private BindingActivity bContext = this;

    protected TextView percentage;
    protected ProgressBar progressBar;

    private static final int ACCESS_FINE_LOCATION_RESULT = 1;

    private Map<TestManager, Boolean> results = new HashMap<>();
    private List<TestManager> tests = new ArrayList<TestManager>();

    private double actual = 0;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_phase);
        setListView();
        intent = new Intent(this, ScanService.class);

        percentage = (TextView) findViewById(R.id.textPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        wifiReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();

                NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();

                if (!(state == NetworkInfo.State.CONNECTED)|| !(info.getType() == ConnectivityManager.TYPE_WIFI)) {
                    Intent i = new Intent(BindingActivity.this, StateNoWifi.class);
                    startActivity(i);
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
        registerReceiver(broadcastReceiver, new IntentFilter(ScanService.BROADCAST_ACTION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startScan();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Access fine location required to access your wifi", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_RESULT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, intentFilter);
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(ScanService.BROADCAST_ACTION));
    }

    @Override
     protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions, int [] grantResults){
        if(requestCode == ACCESS_FINE_LOCATION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this,
                        "Access fine location was not granted, app can not run", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ScanService.LocalBinder binder = (ScanService.LocalBinder) service;
            mService = binder.getService();

            tests.add(new TestPassword());
            tests.add(new TestEncryption());
            tests.add(new TestSpam());

            setListView();

            Log.w("Bactivity", "start of service");
            mService.getSources(tests);
            mService.run();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        Bundle data = intent.getExtras();
        TestManager test = data.getParcelable("test");
        int index = Integer.parseInt(intent.getStringExtra("index"));
        results.put(test, test.testPassed());

        final ListView list = (ListView) findViewById(R.id.testListView);

        View v = getViewByPosition(index, list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.item_testProgressBar);

        if(test.testPassed()){
            Log.w("PRESIEL", "mal by som nastavit na fajku index " + String.valueOf(index));
            ImageView imageView = (ImageView) v.findViewById(R.id.item_icon);
            imageView.setImageResource(R.drawable.check);
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Log.w("NEPRESIEL", "mal by som nastavit na krizik");
            ImageView imageView = (ImageView) v.findViewById(R.id.item_icon);
            imageView.setImageResource(R.drawable.rejected);
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }

        updateProgressBar();
        registerClickCallback();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void updateProgressBar() {
        int end = 0;
        if( 100 - actual < (100 / tests.size()) + tests.size() ) { // Let actual be 66 -> 100 - 66 < 33 + 3 (for reminder from division)
            end = 100;
        } else {
            end = (int) (actual + 100 / tests.size());
        }

        for (int i = (int) actual; i <= end ; i++) {
            final int finalI = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    bContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("progress", String.valueOf(finalI));
                            bContext.percentage.setText(String.valueOf(finalI) + " %");
                            bContext.progressBar.setProgress(finalI);
                        }
                    });
                }
            }, 20 * finalI);
        }
        actual += 100 / tests.size();
    }

    private void startScan(){
        Intent intent = new Intent(this, ScanService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setListView() {
        ArrayAdapter<TestManager> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.testListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<TestManager> {
        public MyListAdapter() {
            super(bContext, R.layout.item_view, tests);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = bContext.getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            TestManager myTest = tests.get(position);

            TextView textView = (TextView) itemView.findViewById(R.id.item_testName);
            textView.setText(myTest.testName());

            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.item_testProgressBar);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);

            if(results.get(myTest) == null){
                progressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
            return itemView;
        }
    }

    private void registerClickCallback(){

        ListView list = (ListView) bContext.findViewById(R.id.testListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TestManager test = tests.get(position);

                for (Map.Entry<TestManager, Boolean> wtest : results.entrySet())
                {
                    if(wtest.getKey().testName().equals(test.testName())){
                        test = wtest.getKey();
                    }
                }
                Log.w("RegisterClickCallback", test.toString());

                if (test.testPassed()) {
                    Toast.makeText(bContext, "Test passed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(bContext, test.getSolution(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}