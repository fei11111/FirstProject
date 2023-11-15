/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.crash;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.common.R;
import com.common.base.BaseActivity;
import com.common.databinding.CustomactivityoncrashDefaultErrorActivityBinding;
import com.common.viewmodel.EmptyViewModel;


public final class DefaultErrorActivity extends BaseActivity<EmptyViewModel, CustomactivityoncrashDefaultErrorActivityBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is needed to avoid a crash if the developer has not specified
        //an app-level theme that extends Theme.AppCompat
        TypedArray a = obtainStyledAttributes(androidx.appcompat.R.styleable.AppCompatTheme);
        if (!a.hasValue(androidx.appcompat.R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_DarkActionBar);
        }
        a.recycle();
    }

    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent());

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        //Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close and just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            mBinding.customactivityoncrashErrorActivityRestartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
            mBinding.customactivityoncrashErrorActivityRestartButton.setOnClickListener(v -> CustomActivityOnCrash.restartApplication(DefaultErrorActivity.this, config));
        } else {
            mBinding.customactivityoncrashErrorActivityRestartButton.setOnClickListener(v -> CustomActivityOnCrash.closeApplication(DefaultErrorActivity.this, config));
        }


        if (config.isShowErrorDetails()) {
            mBinding.customactivityoncrashErrorActivityMoreInfoButton.setOnClickListener(v -> {
                //We retrieve all the error data and show it

                AlertDialog dialog = new AlertDialog.Builder(DefaultErrorActivity.this)
                        .setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
                        .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(DefaultErrorActivity.this, getIntent()))
                        .setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
                        .setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy,
                                (dialog1, which) -> {
                                    copyErrorToClipboard();
                                })
                        .show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size));
            });
        } else {
            mBinding.customactivityoncrashErrorActivityMoreInfoButton.setVisibility(View.GONE);
        }

        Integer defaultErrorActivityDrawableId = config.getErrorDrawable();

        if (defaultErrorActivityDrawableId != null) {
            mBinding.customactivityoncrashErrorActivityImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId, getTheme()));
        }
    }
}
