package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import java.io.File;

/**
 * Performance Settings
 */
public class PerformanceSettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String COMPCACHE_PREF = "pref_compcache_size";
    private static final String COMPCACHE_PERSIST_PROP = "persist.service.compcache";
    private static final String COMPCACHE_DEFAULT = SystemProperties.get("ro.compcache.default");
    private static final String GENERAL_CATEGORY = "general_category";
    private static final String JIT_PREF = "pref_jit_mode";
    private static final String JIT_ENABLED = "int:jit";
    private static final String JIT_DISABLED = "int:fast";
    private static final String JIT_PERSIST_PROP = "persist.sys.jit-mode";
    private static final String JIT_PROP = "dalvik.vm.execution-mode";
    private static final String HEAPSIZE_PREF = "pref_heapsize";
    private static final String HEAPSIZE_PROP = "dalvik.vm.heapsize";
    private static final String HEAPSIZE_PERSIST_PROP = "persist.sys.vm.heapsize";
    private static final String HEAPSIZE_DEFAULT = "32m";
    private static final String USE_DITHERING_PREF = "pref_use_dithering";
    private static final String USE_DITHERING_PERSIST_PROP = "persist.sys.use_dithering";
    private static final String USE_DITHERING_DEFAULT = "0";
    private static final String DISABLE_BOOTANIMATION_PREF = "pref_disable_bootanimation";
    private static final String DISABLE_BOOTANIMATION_PERSIST_PROP = "persist.sys.nobootanimation";
    private static final String DISABLE_BOOTANIMATION_DEFAULT = "0"; 
    private static final String LOCK_HOME_PREF = "pref_lock_home";
    private static final String LOCK_MMS_PREF = "pref_lock_mms";
    private static final String LOCK_PHONE_PREF = "pref_lock_phone";
    private static final String LOCK_CONTACTS_PREF = "pref_lock_contacts";
    private static final String LOCK_SU_PREF = "pref_lock_su";
    private static final int LOCK_HOME_DEFAULT = 0;
    private static final int LOCK_MMS_DEFAULT = 0;
    private static final int LOCK_PHONE_DEFAULT = 0;
    private static final int LOCK_CONTACTS_DEFAULT = 0;
    private static final int LOCK_SU_DEFAULT = 0;

    private ListPreference mCompcachePref;
    private CheckBoxPreference mJitPref;
    private CheckBoxPreference mUseDitheringPref;
    private CheckBoxPreference mDisableBootanimPref;
    private CheckBoxPreference mLockHomePref;
    private CheckBoxPreference mLockMmsPref;
    private CheckBoxPreference mLockPhonePref;
    private CheckBoxPreference mLockContactsPref;
    private CheckBoxPreference mLockSuPref;
    private ListPreference mHeapsizePref;
    private AlertDialog alertDialog;
    private int swapAvailable = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.performance_settings_title_subhead);
        addPreferencesFromResource(R.xml.performance_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        
        PreferenceCategory generalCategory = (PreferenceCategory)prefSet.findPreference(GENERAL_CATEGORY);

        mCompcachePref = (ListPreference) prefSet.findPreference(COMPCACHE_PREF);
        if (isSwapAvailable()) {
	    if (SystemProperties.get(COMPCACHE_PERSIST_PROP) == "1")
                SystemProperties.set(COMPCACHE_PERSIST_PROP, COMPCACHE_DEFAULT);
            mCompcachePref.setValue(SystemProperties.get(COMPCACHE_PERSIST_PROP, COMPCACHE_DEFAULT));
            mCompcachePref.setOnPreferenceChangeListener(this);
        } else {
            generalCategory.removePreference(mCompcachePref);
        }

        mJitPref = (CheckBoxPreference) prefSet.findPreference(JIT_PREF);
        String jitMode = SystemProperties.get(JIT_PERSIST_PROP,
                SystemProperties.get(JIT_PROP, JIT_ENABLED));
        mJitPref.setChecked(JIT_ENABLED.equals(jitMode));

        mUseDitheringPref = (CheckBoxPreference) prefSet.findPreference(USE_DITHERING_PREF);
        String useDithering = SystemProperties.get(USE_DITHERING_PERSIST_PROP, USE_DITHERING_DEFAULT);
        mUseDitheringPref.setChecked("1".equals(useDithering));

        mHeapsizePref = (ListPreference) prefSet.findPreference(HEAPSIZE_PREF);
        mHeapsizePref.setValue(SystemProperties.get(HEAPSIZE_PERSIST_PROP,
                SystemProperties.get(HEAPSIZE_PROP, HEAPSIZE_DEFAULT)));
        mHeapsizePref.setOnPreferenceChangeListener(this);

        mDisableBootanimPref = (CheckBoxPreference) prefSet.findPreference(DISABLE_BOOTANIMATION_PREF);
        String disableBootanimation = SystemProperties.get(DISABLE_BOOTANIMATION_PERSIST_PROP, DISABLE_BOOTANIMATION_DEFAULT);
        mDisableBootanimPref.setChecked("1".equals(disableBootanimation));

        mLockHomePref = (CheckBoxPreference) prefSet.findPreference(LOCK_HOME_PREF);
        mLockHomePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_HOME_IN_MEMORY, LOCK_HOME_DEFAULT) == 1);

        mLockMmsPref = (CheckBoxPreference) prefSet.findPreference(LOCK_MMS_PREF);
        mLockMmsPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_MMS_IN_MEMORY, LOCK_MMS_DEFAULT) == 1);

        mLockPhonePref = (CheckBoxPreference) prefSet.findPreference(LOCK_PHONE_PREF);
        mLockPhonePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_PHONE_IN_MEMORY, LOCK_PHONE_DEFAULT) == 1);

        mLockContactsPref = (CheckBoxPreference) prefSet.findPreference(LOCK_CONTACTS_PREF);
        mLockContactsPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_CONTACTS_IN_MEMORY, LOCK_CONTACTS_DEFAULT) == 1);

        mLockSuPref = (CheckBoxPreference) prefSet.findPreference(LOCK_SU_PREF);
        mLockSuPref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_SUPERUSER_IN_MEMORY, LOCK_SU_DEFAULT) == 1);

        // Set up the warning
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.performance_settings_warning_title);
        alertDialog.setMessage(getResources().getString(R.string.performance_settings_warning));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alertDialog.show();
    }
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	if (preference == mJitPref) {
            SystemProperties.set(JIT_PERSIST_PROP,
                    mJitPref.isChecked() ? JIT_ENABLED : JIT_DISABLED);
            return true;
        }

        if (preference == mUseDitheringPref) {
            SystemProperties.set(USE_DITHERING_PERSIST_PROP,
                    mUseDitheringPref.isChecked() ? "1" : "0");
            return true;
        }


        if (preference == mDisableBootanimPref) {
            SystemProperties.set(DISABLE_BOOTANIMATION_PERSIST_PROP,
                    mDisableBootanimPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mLockHomePref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_HOME_IN_MEMORY, mLockHomePref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mLockMmsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_MMS_IN_MEMORY, mLockMmsPref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mLockPhonePref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_PHONE_IN_MEMORY, mLockPhonePref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mLockContactsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_CONTACTS_IN_MEMORY, mLockContactsPref.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mLockSuPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCK_SUPERUSER_IN_MEMORY, mLockSuPref.isChecked() ? 1 : 0);
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeapsizePref) {
            if (newValue != null) {
                SystemProperties.set(HEAPSIZE_PERSIST_PROP, (String)newValue);
                return true;
            }
        }

        if (preference == mCompcachePref) {
            if (newValue != null) {
                SystemProperties.set(COMPCACHE_PERSIST_PROP, (String)newValue);
                return true;
	    }
        }

        return false;
    }

    /**
     * Check if swap support is available on the system
     */
    private boolean isSwapAvailable() {
        if (swapAvailable < 0) {
            swapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return swapAvailable > 0;
    }

}
