/*
 * Copyright (C) 2020 The LineageOS Project
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

import android.content.SharedPreferences;

import org.lineageos.settings.utils.FileUtils;

public class KeyboardUtils {
    public static void setLayout(SharedPreferences prefs) {
        if (!prefs.contains(Constants.KEYBOARD_LAYOUT_KEY)) {
            String text = FileUtils.readOneLine(Constants.KEYBOARD_LAYOUT_CFG_FILE);
            if (text == null) {
                text = Constants.KEYBOARD_LAYOUT_DEFAULT;
            }
            prefs.edit().putString(Constants.KEYBOARD_LAYOUT_KEY, text).commit();
        }
        String layout = prefs.getString(Constants.KEYBOARD_LAYOUT_KEY,
                Constants.KEYBOARD_LAYOUT_DEFAULT);
        FileUtils.writeLine(Constants.KEYBOARD_LAYOUT_CFG_FILE, layout);
        FileUtils.writeLine(Constants.KEYBOARD_LAYOUT_SYS_FILE, layout);
    }

    public static void setKeymap(SharedPreferences prefs) {
        if (!prefs.contains(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY)) {
            prefs.edit().putBoolean(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY,
                    FileUtils.fileExists(Constants.KEYBOARD_KEYMAP_CFG_FILE)).commit();
        }
        if (prefs.getBoolean(Constants.KEYBOARD_KEYMAP_CUSTOM_KEY, false)) {
            String text = FileUtils.readFile(Constants.KEYBOARD_KEYMAP_CFG_FILE);
            FileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE, text);
        } else {
            if (prefs.getBoolean(Constants.KEYBOARD_KEYMAP_SPACEPOWER_KEY, false)) {
                for (int i = 0; i < Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT.length; ++i) {
                    FileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE,
                            Constants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT[i] + "\n");
                }
            }
            if (prefs.getBoolean(Constants.KEYBOARD_KEYMAP_FNKEYS_KEY, false)) {
                for (int i = 0; i < Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT.length; ++i) {
                    FileUtils.writeLine(Constants.KEYBOARD_KEYMAP_SYS_FILE,
                            Constants.KEYBOARD_KEYMAP_FNKEYS_TEXT[i] + "\n");
                }
            }
        }
    }

    public static void setPollInterval(SharedPreferences prefs) {
        int value = prefs.getBoolean(Constants.KEYBOARD_FASTPOLL_KEY, false) ?
                Constants.KEYBOARD_POLL_INTERVAL_FAST : Constants.KEYBOARD_POLL_INTERVAL_SLOW;
        FileUtils.writeLine(Constants.KEYBOARD_POLL_INTERVAL_SYS_FILE, Integer.toString(value));
    }
}
