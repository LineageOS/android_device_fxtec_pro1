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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.lineageos.settings.device.R;
import org.lineageos.settings.utils.LocalFileUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        setKeyboardKeymap(context);
        setKeyboardPollInterval(context);
        setTouchscreenMargin(context);
    }

    private void setKeyboardKeymap(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.contains(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY)) {
            prefs.edit().putBoolean(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY,
                    LocalFileUtils.fileExists(Constants.KEYBOARD_KEYMAP_CFG_FILE)).commit();
        }
        boolean custom = prefs.getBoolean(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY, false);
        if (custom) {
            String text = LocalFileUtils.readFile(Constants.KEYBOARD_KEYMAP_CFG_FILE);
            LocalFileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE, text);
        }
        else {
            boolean value;
            value = prefs.getBoolean(Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY, false);
            if (value) {
                for (int i = 0; i < Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT.length; ++i) {
                    LocalFileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE,
                            Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT[i]);
                }
            }
            value = prefs.getBoolean(Constants.KEYBOARD_KEYMAP_FNKEYS_KEY, false);
            if (value) {
                for (int i = 0; i < Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT.length; ++i) {
                    LocalFileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE,
                            Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT[i]);
                }
            }
        }
    }

    private void setKeyboardPollInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int value = prefs.getBoolean(Constants.KEYBOARD_FASTPOLL_KEY, false)
                ? Constants.KEYBOARD_POLL_INTERVAL_FAST
                : Constants.KEYBOARD_POLL_INTERVAL_SLOW;
        LocalFileUtils.writeLine(Constants.KEYBOARD_POLL_INTERVAL_SYS_FILE,
                Integer.toString(value));
    }

    private void setTouchscreenMargin(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int value = Constants.TOUCHSCREEN_MARGIN_STEP * prefs.getInt(Constants.TOUCHSCREEN_MARGIN_KEY,
                context.getResources().getInteger(R.integer.touchscreen_margin_default));
        LocalFileUtils.writeLine(Constants.TOUCHSCREEN_MARGIN_SYS_FILE, Integer.toString(value));
    }
}
