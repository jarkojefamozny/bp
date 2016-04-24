package com.example.home.checkmyrouter;

import java.util.Map;

/**
 * Interface for services, like scan, fix, ...
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public interface ServiceManager {
    /**
     * Method to run given test
     */

    /**
     * Method to collect results from our service
     *
     * @return true if all s ervice passed, false if not
     */
    public Map getResults();

    /**
     * Method to run given tests
     *
     * @param callback sets a callback when finished
     */
    void startTests(ServiceCallback callback);

    interface ServiceCallback {
        /**
         * Method to get results from test
         *
         * @param testName name of test
         * @param result boolean value as a result
         */
        void onTestDone(String testName, boolean result);
    }
}
