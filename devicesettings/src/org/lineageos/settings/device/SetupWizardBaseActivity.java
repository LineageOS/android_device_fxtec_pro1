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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.lineageos.settings.device.R;

public class SetupWizardBaseActivity extends Activity implements View.OnClickListener {

    private static final int WINDOW_FLAGS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                          | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

    private Button mButtonBack;
    private Button mButtonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make it fullscreen, hide the navbar
        getWindow().getDecorView().setSystemUiVisibility(WINDOW_FLAGS);

        initLayout();
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

    private void initLayout() {
        if (getLayoutResId() != -1) {
            setContentView(getLayoutResId());
        }
        if (getTitleResId() != -1) {
            TextView title = (TextView) findViewById(android.R.id.title);
            title.setText(getTitleResId());
        }
        if (getIconResId() != -1) {
            ImageView icon = (ImageView) findViewById(R.id.header_icon);
            icon.setImageResource(getIconResId());
            icon.setVisibility(View.VISIBLE);
        }

        mButtonBack = findViewById(R.id.btn_back);
        mButtonBack.setOnClickListener(this);
        mButtonNext = findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);

        setupPage();
    }

    protected int getLayoutResId() {
        return -1;
    }

    protected int getTitleResId() {
        return -1;
    }

    protected int getIconResId() {
        return -1;
    }

    protected void setupPage() {
        return;
    }
}
