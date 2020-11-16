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

import android.content.Context;
import android.content.SharedPreferences;

import org.lineageos.settings.device.R;
import org.lineageos.settings.utils.FileUtils;

public class TouchscreenUtils {
    public static void setMargin(Context context, SharedPreferences prefs) {
        int margin = Constants.TOUCHSCREEN_MARGIN_STEP *
                prefs.getInt(Constants.TOUCHSCREEN_MARGIN_KEY,
                        context.getResources().getInteger(R.integer.touchscreen_margin_default));
        FileUtils.writeLine(Constants.TOUCHSCREEN_MARGIN_SYS_FILE, Integer.toString(margin));
    }
}
