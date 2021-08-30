/*
 * Copyright (C) 2018-2021 The LineageOS Project
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

import android.util.Log;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomKeymap {

    private static final String TAG = CustomKeymap.class.getSimpleName();
    private static final boolean DEBUG = false;

    private CustomKeymap() {
        // This class is not supposed to be instantiated
    }

    public static boolean install() {
        if (DEBUG) Log.d(TAG, "Installing custom keymap");
        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(
                        new File(Constants.KEYBOARD_KEYMAP_CFG_FILE)
                    )
                )
            );
            if (DEBUG) Log.d(TAG, "Opened input: " + Constants.KEYBOARD_KEYMAP_CFG_FILE);
            out = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        new File(Constants.KEYBOARD_KEYMAP_SYS_FILE)
                    )
                )
            );
            if (DEBUG) Log.d(TAG, "Opened output: " + Constants.KEYBOARD_KEYMAP_SYS_FILE);
            for ( String line; ( line = in.readLine() ) != null; ) {
                out.write(line);
                out.newLine();
            }

        } catch (FileNotFoundException e) {
            Log.w(TAG, "FileNotFoundException: ", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException: ", e);
            }
        }

        if (DEBUG) Log.d(TAG, "Wrote custom keymap to kernel");
        return true;
    }

}
