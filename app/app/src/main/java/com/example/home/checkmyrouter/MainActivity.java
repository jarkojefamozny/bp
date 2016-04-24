package com.example.home.checkmyrouter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class MainActivity is main activity, takes care of UI
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class MainActivity extends BindingActivity {
    private Map<TestManager, Boolean> results = new HashMap<>();
    private List<TestManager> tests = new ArrayList<TestManager>();

    private double actual = 0;

    ProgressBar progressBar;
    TextView percentage;

    @Override
    void onServiceBound(ServiceManager service) {
        service.startTests(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_phase);

        Log.w("MAIN", "LAYOUT");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        percentage = (TextView) findViewById(R.id.textPercentage);

        tests.add(new TestPassword());
        tests.add(new TestEncryption());
        tests.add(new TestSpam());
        setListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTestDone(final String serviceName, final boolean result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI(serviceName, result);
            }
        });
    }

    private void updateUI(String testName, boolean result) {
        int index = 0;
        switch(testName){
            case "Default password test": results.put(new TestPassword(), result);
                index = 0;
                break;
            case "Encryption test": results.put(new TestEncryption(), result);
                index = 1;
                break;
            case "SPAM test": results.put(new TestSpam(), result);
                index = 2;
                break;
        }

        final ListView list = (ListView) findViewById(R.id.testListView);

        View v = getViewByPosition(index, list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.item_testProgressBar);

        if(result){
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("progress", String.valueOf(finalI));
                            percentage.setText(String.valueOf(finalI) + " %");
                            progressBar.setProgress(finalI);
                        }
                    });
                }
            }, 20 * finalI);
        }
        actual += 100 / tests.size();
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

            Log.w("ADAPTER", "TEST.LENGTH = " + tests.size());
            Log.w("ADAPTER", "POSITION = " + position);

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
                boolean passed = false;
                for (Map.Entry<TestManager, Boolean> wtest : results.entrySet())
                {
                    if(wtest.getKey().testName().equals(test.testName())){
                        test = wtest.getKey();
                        passed = wtest.getValue();
                    }
                }
                Log.w("RegisterClickCallback", test.toString());

                if (passed) {
                    new AlertDialog.Builder(bContext)
                            .setTitle("Test Passed")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                } else {
                    new AlertDialog.Builder(bContext)
                            .setTitle("Test Failed")
                            .setMessage(test.getSolution())
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }
}
