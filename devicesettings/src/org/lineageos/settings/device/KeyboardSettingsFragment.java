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

import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.device.R;
import org.lineageos.settings.utils.FileUtils;

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
        KeyboardUtils.setLayout(mPrefs);
    }

    private void doUpdateKeymapPreference() {
        SwitchPreference customKeymapPref = findPreference(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY);
        SwitchPreference remapFnKeysPref = findPreference(Constants.KEYBOARD_KEYMAP_FNKEYS_KEY);
        SwitchPreference remapFnSpacePowerPref =
                findPreference(Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY);

        if (FileUtils.fileExists(Constants.KEYBOARD_KEYMAP_CFG_FILE)) {
            customKeymapPref.setEnabled(true);

            if (customKeymapPref.isChecked()) {
                remapFnKeysPref.setEnabled(false);
                remapFnSpacePowerPref.setEnabled(false);
            }
        } else {
            customKeymapPref.setEnabled(false);
            customKeymapPref.setChecked(false);
            remapFnKeysPref.setEnabled(true);
            remapFnSpacePowerPref.setEnabled(true);
        }

        KeyboardUtils.setKeymap(mPrefs);
    }

    private void doUpdateFastPollPreference() {
        KeyboardUtils.setPollInterval(mPrefs);
    }
}
