/*
 * Copyright (C) 2021 The LineageOS Project
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

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import org.lineageos.internal.util.FileUtils;

import org.lineageos.settings.device.R;

public class PhysicalLayoutDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] choices = getResources().getStringArray(R.array.keyboard_layout_titles);

        AlertDialog.Builder builder = new AlertDialog.Builder(PhysicalLayoutDialog.this);
        builder.setTitle(R.string.keyboard_layout_dialog_title);
        builder.setSingleChoiceItems(choices, getCurrentLayoutIndex(),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] values = getResources().getStringArray(R.array.keyboard_layout_values);
                if (which < values.length) {
                    SystemProperties.set(Constants.KEYBOARD_LAYOUT_PROPERTY, values[which]);
                }
                dialog.dismiss();
                finish();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private int getCurrentLayoutIndex() {
        String value = FileUtils.readOneLine(Constants.KEYBOARD_LAYOUT_SYS_FILE);
        value = value.substring(0, 6);
        String[] values = getResources().getStringArray(R.array.keyboard_layout_values);
        int i;
        for (i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}
