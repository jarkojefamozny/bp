package com.example.home.checkmyrouter;

/**
 * Interface for tests, like password, encryption, stack, ...
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public interface TestManager  {

    /**
     * Method to get name of given test
     *
     * @return name of the test
     */
    public String testName();

    /**
     * Method to test network by some interface implementing criteria.
     *
     * @param callback after finishing sends a callback to service that it is finished
     */
    void runTest(ITestCallback callback);

    /**
     * Method to check whether specific test passed or not.
     *
     * @return true if test passed - network is fine, false if there is some
     * vulnerability in network.
     */
    public boolean testPassed();

    /**
     * Method with text consisting solution to given vulnerability we are testing.
     *
     * @return solution to given problem scenario.
     */
    public String getSolution();

    /**
     * Interface for callbacks
     */
    interface ITestCallback {
        /**
         * Method to send result after end of test.
         * @param result result
         */
        void onTestDone(boolean result);
    }


}
