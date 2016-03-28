package com.example.home.checkmyrouter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class ScanService extends Service implements ServiceManager {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    protected BindingActivity bContext;

    private List<TestManager> tests = new ArrayList<TestManager>();


    public class LocalBinder extends Binder {
        ScanService getService() {
            // Return this instance of LocalService so clients can call public methods
            //Log.w("LOCALBINDER", "✓");
            return ScanService.this;
        }
    }

    public ScanService() {
    }

    public void setbContext(BindingActivity bContext) {
        //Log.w("SERVICA", bContext.toString());
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
        //Log.w("SCAN SERVICE - RUN", "✓");
        TestPassword.sContext = this;
        TestPassword.bContext = bContext;
        TestEncryption.sContext = this;

        for (TestManager test : tests) {
            test.test();
            Log.w(test.testName(), String.valueOf(test.testPassed()));
            updateListView();
        }
    }

    private void updateListView() {
        ArrayAdapter<TestManager> adapter = new MyListAdapter();
        ListView list = (ListView) bContext.findViewById(R.id.testListView);
        list.setAdapter(adapter);
    }

    @Override
    public boolean getResult() {
        return false;
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
            progressBar.setVisibility(View.VISIBLE);
            return itemView;
        }
    }
}
