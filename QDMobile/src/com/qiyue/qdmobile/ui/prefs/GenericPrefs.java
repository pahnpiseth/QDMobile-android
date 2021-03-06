package com.qiyue.qdmobile.ui.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;

@SuppressWarnings("deprecation")
public abstract class GenericPrefs extends PreferenceActivity implements
        OnSharedPreferenceChangeListener, IPreferenceHelper {

    private static final String THIS_FILE = "GenericPrefs";

    /**
     * Get the xml preference resource for this screen
     * 
     * @return the resource reference
     */
    protected abstract int getXmlPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeBuildPrefs();
        addPreferencesFromResource(getXmlPreferences());
        afterBuildPrefs();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateDescriptions();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateDescriptions();
    }

    /**
     * Process update of description of each preference field
     */
    protected abstract void updateDescriptions();

    /**
     * Optional hook for doing stuff before preference xml is loaded
     */
    protected void beforeBuildPrefs() {
        // By default, nothing to do
    };

    /**
     * Optional hook for doing stuff just after preference xml is loaded
     */
    protected void afterBuildPrefs() {
        // By default, nothing to do
    };

    // Utilities for update Descriptions
    /**
     * Get field summary if nothing set. By default it will try to add _summary
     * to name of the current field
     * 
     * @param field_name Name of the current field
     * @return Translated summary for this field
     */
    protected String getDefaultFieldSummary(String field_name) {
        try {
            String keyid = R.string.class.getField(field_name + "_summary").get(null).toString();
            return getString(Integer.parseInt(keyid));
        } catch (SecurityException e) {
            // Nothing to do : desc is null
        } catch (NoSuchFieldException e) {
            // Nothing to do : desc is null
        } catch (IllegalArgumentException e) {
            // Nothing to do : desc is null
        } catch (IllegalAccessException e) {
            // Nothing to do : desc is null
        }

        return "";
    }

    /**
     * Set summary of a standard string field If empty will display the default
     * summary Else it displays the preference value
     * 
     * @param fieldName the preference key name
     */
    public void setStringFieldSummary(String fieldName) {
        PreferenceScreen pfs = getPreferenceScreen();
        SharedPreferences sp = pfs.getSharedPreferences();
        Preference pref = pfs.findPreference(fieldName);

        String val = sp.getString(fieldName, null);
        if (TextUtils.isEmpty(val)) {
            val = getDefaultFieldSummary(fieldName);
        }
        setPreferenceSummary(pref, val);
    }

    /**
     * Set summary of a password field If empty will display default summary If
     * password will display a * char for each letter of password
     * 
     * @param fieldName the preference key name
     */
    public void setPasswordFieldSummary(String fieldName) {
        PreferenceScreen pfs = getPreferenceScreen();
        SharedPreferences sp = pfs.getSharedPreferences();
        Preference pref = pfs.findPreference(fieldName);

        String val = sp.getString(fieldName, null);

        if (TextUtils.isEmpty(val)) {
            val = getDefaultFieldSummary(fieldName);
        } else {
            val = val.replaceAll(".", "*");
        }
        setPreferenceSummary(pref, val);
    }

    /**
     * Set summary of a list field If empty will display default summary If one
     * item selected will display item name
     * 
     * @param fieldName the preference key name
     */
    public void setListFieldSummary(String fieldName) {
        PreferenceScreen pfs = getPreferenceScreen();
        ListPreference pref = (ListPreference) pfs.findPreference(fieldName);
        if (pref == null) {
            Log.w(THIS_FILE, "Unable to find preference " + fieldName);
            return;
        }

        CharSequence val = pref.getEntry();
        if (TextUtils.isEmpty(val)) {
            val = getDefaultFieldSummary(fieldName);
        }
        setPreferenceSummary(pref, val);
    }

    /**
     * Safe setSummary on a Preference object that make sure that the preference
     * exists before doing anything
     * 
     * @param pref the preference to change summary of
     * @param val the string to set as preference summary
     */
    protected void setPreferenceSummary(Preference pref, CharSequence val) {
        if (pref != null) {
            pref.setSummary(val);
        }
    }

    /**
     * Hide a preference from the screen so that user can't see and modify it
     * 
     * @param parent the parent group preference if any, leave null if
     *            preference is a root pref
     * @param fieldName the preference key name to hide
     */
    public void hidePreference(String parent, String fieldName) {
        PreferenceScreen pfs = getPreferenceScreen();
        PreferenceGroup parentPref = pfs;
        if (parent != null) {
            parentPref = (PreferenceGroup) pfs.findPreference(parent);
        }

        Preference toRemovePref = pfs.findPreference(fieldName);

        if (toRemovePref != null && parentPref != null) {
            parentPref.removePreference(toRemovePref);
        } else {
            Log.w("Generic prefs", "Not able to find" + parent + " " + fieldName);
        }
    }


    @Override
    public void setPreferenceScreenType(String key, int type) {
        setPreferenceScreenType(getClass(), key, type);
    }

    @Override
    public void setPreferenceScreenSub(String key, Class<?> activityClass, Class<?> fragmentClass, int type) {
        setPreferenceScreenType(activityClass, key, type);
    }
    
    private void setPreferenceScreenType(Class<?> classObj, String key, int type) {
        Preference pf = findPreference(key);
        Intent it = new Intent(this, classObj);
        it.putExtra(PrefsLogic.EXTRA_PREFERENCE_TYPE, type);
        pf.setIntent(it);
    }
    
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#isValidFragment(java.lang.String)
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        // This pref activity does not include any fragment
        return false;
    }
}
