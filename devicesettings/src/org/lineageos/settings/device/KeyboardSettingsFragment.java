/*
 * Copyright (C) 2018-2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.device;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.lineageos.internal.util.FileUtils;

import org.lineageos.settings.device.R;

public class KeyboardSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = KeyboardSettingsFragment.class.getSimpleName();

    private ListPreference mLayoutPref;
    private SharedPreferences mPrefs;
    private SwitchPreference mKeymapCustomPref;
    private SwitchPreference mKeymapSpacePowerPref;
    private SwitchPreference mKeymapFnKeysPref;
    private SwitchPreference mFastPollPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.keyboard_panel);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLayoutPref = findPreference(Constants.KEYBOARD_LAYOUT_KEY);
        mKeymapCustomPref = findPreference(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY);
        mKeymapSpacePowerPref = findPreference(Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY);
        mKeymapFnKeysPref = findPreference(Constants.KEYBOARD_KEYMAP_FNKEYS_KEY);
        mFastPollPref = findPreference(Constants.KEYBOARD_FASTPOLL_KEY);

        String value = FileUtils.readOneLine(Constants.KEYBOARD_LAYOUT_SYS_FILE);
        mLayoutPref.setValue(value.substring(0, 6));

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        doUpdateKeymapPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case Constants.KEYBOARD_LAYOUT_KEY:
                doUpdateLayoutPreference();
                break;
            case Constants.KEYBOARD_KEYMAP_CUSTOM_KEY:
            case Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY:
            case Constants.KEYBOARD_KEYMAP_FNKEYS_KEY:
                doUpdateKeymapPreferences();
                break;
            case Constants.KEYBOARD_FASTPOLL_KEY:
                doUpdateFastPollPreference();
                break;
        }
    }

    private void doUpdateLayoutPreference() {
        String value = mLayoutPref.getValue();
        SystemProperties.set(Constants.KEYBOARD_LAYOUT_PROPERTY, value);
    }

    private void doUpdateKeymapPreferences() {
        FileUtils.writeLine(Constants.KEYBOARD_LAYOUT_SYS_FILE, mLayoutPref.getValue());

        File customKeymapFile = new File(Constants.KEYBOARD_KEYMAP_CFG_FILE);
        if (customKeymapFile.exists()) {
            mKeymapCustomPref.setEnabled(true);

            if (mKeymapCustomPref.isChecked()) {
                mKeymapFnKeysPref.setEnabled(false);
                mKeymapSpacePowerPref.setEnabled(false);

                String text = readFile(Constants.KEYBOARD_KEYMAP_CFG_FILE);
                writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, text);
            }
            mKeymapCustomPref.setSummary(getResources().getString(
                    R.string.keyboard_keymap_custom_summary_enabled));
        } else {
            mKeymapCustomPref.setEnabled(false);
            mKeymapCustomPref.setChecked(false);
            mKeymapCustomPref.setSummary(getResources().getString(
                    R.string.keyboard_keymap_custom_summary_disabled));
            mKeymapFnKeysPref.setEnabled(true);
            mKeymapSpacePowerPref.setEnabled(true);

            int i;
            if (mKeymapSpacePowerPref.isChecked()) {
                for (i = 0; i < Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT.length; ++i) {
                    writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT[i] + "\n");
                }
            }
            if (mKeymapFnKeysPref.isChecked()) {
                for (i = 0; i < Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT.length; ++i) {
                    writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT[i] + "\n");
                }
            }
        }
    }

    private void doUpdateFastPollPreference() {
        int value = mFastPollPref.isChecked()
                    ? Constants.KEYBOARD_POLL_INTERVAL_FAST
                    : Constants.KEYBOARD_POLL_INTERVAL_SLOW;
        writeFile(Constants.KEYBOARD_POLL_INTERVAL_SYS_FILE, Integer.toString(value));
    }

    private String readFile(String filename) {
        String result = null;
        try {
            FileReader reader = new FileReader(filename);
            char[] buffer = new char[4096];
            reader.read(buffer);
            result = new String(buffer);
        }
        catch (Exception e) { /* Ignore */ }
        return result;
    }

    private boolean writeFile(String filename, String text) {
        boolean result = false;
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(text);
            writer.flush();
            result = true;
        }
        catch (Exception e) { /* Ignore */ }
        return result;
    }
}
