package com.trianguloy.numericriddlegenerator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to store and retrieve preferences
 * Sort of a wrapper of SharedPreferences
 */

class Preferences {

    //the android SharedPreferences
    private static final String PREF_NAME = "pref";
    private SharedPreferences preferences;
    /**
     * Constructor, initializates the preferences
     * @param context the context from where load the SharedPreferences class
     */
    Preferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }



    private static final String KEY_VERSION = "version";
    private static final String DEFAULT_VERSION = "";

    String getVersion(){
        return preferences.getString(KEY_VERSION,DEFAULT_VERSION);
    }

    void setVersion(String version){
        preferences.edit().putString(KEY_VERSION,version).apply();
    }




    /**
     * Ask again
     */
    private static final String KEY_DONTSHOWAGAIN = "dontShow";
    private static final Boolean DEFAULT_DONTSHOWAGAIN = false;

    /**
     * Getter for the account name
     * @return the selected account name, null if nothing selected
     */
    Boolean getDontShowAgain(){
        return preferences.getBoolean(KEY_DONTSHOWAGAIN,DEFAULT_DONTSHOWAGAIN);
    }

    /**
     * Setter for the account name
     * @param dontShowAgain the account name
     */
    void setDontShowAgain(Boolean dontShowAgain){
        preferences.edit().putBoolean(KEY_DONTSHOWAGAIN,dontShowAgain).apply();
    }


}
