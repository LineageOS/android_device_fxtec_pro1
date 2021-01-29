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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.lineageos.internal.util.FileUtils;

import org.lineageos.settings.device.R;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetupWizardActivity extends Activity implements View.OnClickListener {

    private static final int IMMERSIVE_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                             | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                             | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    private static final String KEY_DISPLAYNAME = "name";

    private Button mButtonBack;
    private Button mButtonNext;
    private View mKeylayoutItem;
    private TextView mKeyLayoutSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupwizard);

        // Make it fullscreen, hide the navbar
        getWindow().getDecorView().setSystemUiVisibility(IMMERSIVE_FLAGS);

        mButtonBack = findViewById(R.id.btn_back);
        mButtonBack.setOnClickListener(this);
        mButtonNext = findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);

        // override this method for your own page
        setupPage();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Keep these for navigating purposes
            case R.id.btn_back:
                setResult(RESULT_CANCELED, null);
                finish();
                break;

            case R.id.btn_next:
                setResult(RESULT_OK, null);
                finish();
                break;
        }
    }
    
    private void setupPage() {
        mKeylayoutItem = findViewById(R.id.keylayout_item);
        mKeylayoutItem.setOnClickListener((v) -> showKeylayoutChoices());
        mKeyLayoutSummary = findViewById(R.id.keylayout_summary);

        int currentIndex = getSelectedIndex();
        setSummary(currentIndex);

        setupSpinner(currentIndex);
    }

    private void setupSpinner(int currentIndex) {
        final Spinner spinner = (Spinner) findViewById(R.id.kbd_layout_list);
        final SimpleAdapter adapter = constructKeylayoutAdapter();
        spinner.setAdapter(adapter);
        spinner.setSelection(currentIndex);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] values = getResources().getStringArray(R.array.keyboard_layout_values);
                SystemProperties.set(Constants.KEYBOARD_LAYOUT_PROPERTY, values[position]);
                setSummary(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        
        /*final ArrayAdapter adapter = constructKeylayoutAdapter2();
        spinner.setAdapter(adapter);*/
    }

    /* Alert */

    private void showKeylayoutChoices() {
        String[] choices = getResources().getStringArray(R.array.keyboard_layout_titles);

        int selectedIndex = getSelectedIndex();
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardActivity.this);
        builder.setSingleChoiceItems(choices, selectedIndex,
                (DialogInterface dialog, int which) -> {
                    String[] values = getResources().getStringArray(R.array.keyboard_layout_values);
                    if (which < values.length) {
                        SystemProperties.set(Constants.KEYBOARD_LAYOUT_PROPERTY, values[which]);
                        setSummary(which);
                        final Spinner spinner = (Spinner) findViewById(R.id.kbd_layout_list);
                        spinner.setSelection(which);
                    }
                    dialog.dismiss();
                }
        );
        builder.show();
    }

    private void setSummary(int index) {
        String[] choices = getResources().getStringArray(R.array.keyboard_layout_titles);
        mKeyLayoutSummary.setText(choices[index]);
    }

    /* End alert */

    private SimpleAdapter constructKeylayoutAdapter() {
        String[] choices = getResources().getStringArray(R.array.keyboard_layout_titles);
        final String[] from = new String[] { KEY_DISPLAYNAME };
        final int[] to = new int[] {android.R.id.text1};

        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
        for (int i = 0 ; i < choices.length; i++) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put(KEY_DISPLAYNAME, choices[i]);
            arrayList.add(hashMap);
        }

        final SimpleAdapter adapter = new SimpleAdapter(this,
                arrayList,
                R.layout.kbd_layout_list_selection,
                from,
                to);

        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        return adapter;
    }

    private ArrayAdapter constructKeylayoutAdapter2() {
        String[] choices = getResources().getStringArray(R.array.keyboard_layout_titles);
        List<String> choicesList = Arrays.asList(choices);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                choicesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private int getSelectedIndex() {
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
