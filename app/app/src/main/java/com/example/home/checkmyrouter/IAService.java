package com.example.home.checkmyrouter;

import java.util.List;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public interface IAService {

    void startTests(IAServiceCallback callback);

    List<String> getTestNames();

    interface IAServiceCallback {
        void onTestDone(String serviceName, boolean result);
    }
}
