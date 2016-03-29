package com.example.home.checkmyrouter;

import android.content.ServiceConnection;

import java.util.Map;

/**
 * Interface for services, like scan, fix, ...
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public interface ServiceManager {
    /**
     * Method to run given service
     */
    public void run();

    /**
     * Method to collect results from our service
     *
     * @return true if all s ervice passed, false if not
     */
    public Map getResults();

    public void setbContext(BindingActivity context);
}
