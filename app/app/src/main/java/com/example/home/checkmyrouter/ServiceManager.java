package com.example.home.checkmyrouter;

import android.content.ServiceConnection;

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
     * @return true if all service passed, false if not
     */
    public boolean getResult();

    public void setbContext(BindingActivity context);
}
