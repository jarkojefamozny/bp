package com.example.home.checkmyrouter;

/**
 * Interface for tests, like password, encryption, stack, ...
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public interface TestManager {
    /**
     * Method to get name of given test
     *
     * @return name of the test
     */
    public String testName();
    /**
     * Method to test network by some interface implementing criteria.
     * In this method private attribute will be set
     */
    public void test();

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
}
