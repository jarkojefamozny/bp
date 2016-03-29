package com.example.home.checkmyrouter;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Home on 23.03.2016.
 */
public class ScanService extends Service implements ServiceManager {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    protected BindingActivity bContext;
    private Map<TestManager, Boolean> results = new HashMap<>();

    private List<TestManager> tests = new ArrayList<TestManager>();
    private double actual = 0;

    public class LocalBinder extends Binder {
        ScanService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ScanService.this;
        }
    }

    public ScanService() {
    }

    public void setbContext(BindingActivity bContext) {
        this.bContext = bContext;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void run() {
        tests.add(new TestPassword());
        tests.add(new TestEncryption());
        tests.add(new TestPassword());
        tests.add(new TestEncryption());

        setListView();
        TestPassword.sContext = this;
        TestPassword.bContext = bContext;
        TestEncryption.bContext = bContext;
        TestEncryption.sContext = this;

        int index = 0;
        for (TestManager test : tests) {
            test.test();
            results.put(test, test.testPassed());
            updateProgressBar();
            //updateListView(index, results.get(test));
            index++;
        }


        registerClickCallback();
    }
/*
    private void updateListView(int index, boolean passed) {
        final ListView list = (ListView) bContext.findViewById(R.id.testListView);

        View v = list.getChildAt(index);
        Log.w("UPDATE", v.toString());
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.item_testProgressBar);

        Log.w("UPDATIKY", String.valueOf(passed));
        if(passed){
            ImageView imageView = (ImageView) v.findViewById(R.id.item_icon);
            imageView.setImageResource(R.drawable.check);
            progressBar.setVisibility(View.GONE);
        } else {
            ImageView imageView = (ImageView) v.findViewById(R.id.item_icon);
            imageView.setImageResource(R.drawable.rejected);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
*/


    private void updateProgressBar() {
        for (int i = (int) actual; i <= actual + 100 / tests.size() ; i++) {
            final int finalI = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    bContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bContext.percentage.setText(String.valueOf(finalI) + " %");
                            bContext.progressBar.setProgress(finalI);
                        }
                    });
                }
            }, 20 * finalI);
        }
        actual += 100 / tests.size();
    }

    private void setListView() {
        ArrayAdapter<TestManager> adapter = new MyListAdapter();
        ListView list = (ListView) bContext.findViewById(R.id.testListView);
        list.setAdapter(adapter);
    }

    @Override
    public Map getResults() {
        return results;
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

            if(results.get(myTest) == null){
                progressBar.setVisibility(View.VISIBLE);
            }
            else if(results.get(myTest).booleanValue()){
                ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
                imageView.setImageResource(R.drawable.check);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
                imageView.setImageResource(R.drawable.rejected);
                progressBar.setVisibility(View.INVISIBLE);
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

                if(results.get(test).booleanValue()){
                    Toast.makeText(bContext, "Test passed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(bContext, test.getSolution(), Toast.LENGTH_LONG). show();
                }
            }
        });
    }
}
