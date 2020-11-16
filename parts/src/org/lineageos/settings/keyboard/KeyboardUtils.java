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

package org.lineageos.settings.keyboard;

import android.content.SharedPreferences;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class KeyboardUtils {
    public static void setKeymap(SharedPreferences prefs) {
        if (!prefs.contains(KeyboardConstants.KEYBOARD_KEYMAP_CUSTOM_KEY)) {
            File file = new File(KeyboardConstants.KEYBOARD_KEYMAP_CFG_FILE);
            prefs.edit().putBoolean(KeyboardConstants.KEYBOARD_KEYMAP_CUSTOM_KEY,
                    file.exists()).commit();
        }
        if (prefs.getBoolean(KeyboardConstants.KEYBOARD_KEYMAP_CUSTOM_KEY, false)) {
            String text = readFile(KeyboardConstants.KEYBOARD_KEYMAP_CFG_FILE);
            writeFile(KeyboardConstants.KEYBOARD_KEYMAP_SYS_FILE, text);
        } else {
            if (prefs.getBoolean(KeyboardConstants.KEYBOARD_KEYMAP_SPACEPOWER_KEY, false)) {
                for (int i = 0; i < KeyboardConstants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT.length; ++i) {
                    writeFile(KeyboardConstants.KEYBOARD_KEYMAP_SYS_FILE,
                            KeyboardConstants.KEYBOARD_KEYMAP_SPACEPOWER_TEXT[i] + "\n");
                }
            }
            if (prefs.getBoolean(KeyboardConstants.KEYBOARD_KEYMAP_FNKEYS_KEY, false)) {
                for (int i = 0; i < KeyboardConstants.KEYBOARD_KEYMAP_FNKEYS_TEXT.length; ++i) {
                    writeFile(KeyboardConstants.KEYBOARD_KEYMAP_SYS_FILE,
                            KeyboardConstants.KEYBOARD_KEYMAP_FNKEYS_TEXT[i] + "\n");
                }
            }
        }
    }

    public static void setPollInterval(SharedPreferences prefs) {
        final int interval = prefs.getBoolean(KeyboardConstants.KEYBOARD_FASTPOLL_KEY, false)
                ? KeyboardConstants.KEYBOARD_POLL_INTERVAL_FAST
                : KeyboardConstants.KEYBOARD_POLL_INTERVAL_SLOW;
        writeFile(KeyboardConstants.KEYBOARD_POLL_INTERVAL_SYS_FILE, Integer.toString(interval));
    }

    private static String readFile(String filename) {
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

    private static boolean writeFile(String filename, String text) {
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
