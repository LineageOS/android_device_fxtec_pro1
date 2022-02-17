/*
 * Copyright (C) 2020-2022 The LineageOS Project
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

package org.lineageos.settings.device.touchscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.preference.PreferenceFragment;

import org.lineageos.internal.util.FileUtils;
import org.lineageos.settings.device.R;
import org.lineageos.settings.device.widget.SeekBarPreference;

public class TouchscreenSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = TouchscreenSettingsFragment.class.getSimpleName();

    private SeekBarPreference mMarginSeekBar;
    private SharedPreferences mPrefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.touchscreen_panel);

        mMarginSeekBar = findPreference(Constants.TOUCHSCREEN_MARGIN_KEY);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        doUpdateMarginPreference();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        if (Constants.TOUCHSCREEN_MARGIN_KEY.equals(key)) {
            doUpdateMarginPreference();
        }
    }

    private void doUpdateMarginPreference() {
        final Context context = getContext();
        final int margin = Constants.TOUCHSCREEN_MARGIN_STEP *
                mPrefs.getInt(Constants.TOUCHSCREEN_MARGIN_KEY,
                        context.getResources().getInteger(R.integer.touchscreen_margin_default));

        FileUtils.writeLine(Constants.TOUCHSCREEN_MARGIN_SYS_FILE, Integer.toString(margin));
        mMarginSeekBar.setSummary(getString(R.string.touchscreen_margin_summary, margin));
    }
}
