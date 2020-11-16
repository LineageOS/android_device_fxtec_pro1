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
import android.preference.PreferenceManager;
import android.util.Log;
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

    private SharedPreferences mPrefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.keyboard_panel);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        doUpdateLayoutPreference();
        doUpdateKeymapPreference();
        doUpdateFastPollPreference();
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
            case Constants.KEYBOARD_KEYMAP_CUSTOM_KEY:
            case Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY:
            case Constants.KEYBOARD_KEYMAP_FNKEYS_KEY:
                doUpdateLayoutPreference();
                doUpdateKeymapPreference();
                break;
            case Constants.KEYBOARD_FASTPOLL_KEY:
                doUpdateFastPollPreference();
                break;
        }
    }

    private void doUpdateLayoutPreference() {
        ListPreference layoutPref = findPreference(Constants.KEYBOARD_LAYOUT_KEY);
        String value = layoutPref.getValue();
        writeFile(Constants.KEYBOARD_LAYOUT_SYS_FILE, value);
        writeFile(Constants.KEYBOARD_LAYOUT_CFG_FILE, value);
    }

    private void doUpdateKeymapPreference() {
        SwitchPreference customKeymapPref = findPreference(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY);
        SwitchPreference remapFnKeysPref = findPreference(Constants.KEYBOARD_KEYMAP_FNKEYS_KEY);
        SwitchPreference remapFnSpacePowerPref =
                findPreference(Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY);

        if (customKeymapPref.isChecked()) {
            String text = readFile(Constants.KEYBOARD_KEYMAP_CFG_FILE);
            writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, text);
        }
        else {
            int i;
            if (remapFnSpacePowerPref.isChecked()) {
                for (i = 0; i < Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT.length; ++i) {
                    writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT[i] + "\n");
                }
            }
            if (remapFnKeysPref.isChecked()) {
                for (i = 0; i < Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT.length; ++i) {
                    writeFile(Constants.KEYBOARD_KEYMAP_SYS_FILE, Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT[i] + "\n");
                }
            }
        }
    }

    private void doUpdateFastPollPreference() {
        SwitchPreference fastPollPref = findPreference(Constants.KEYBOARD_FASTPOLL_KEY);
        int value = fastPollPref.isChecked()
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
