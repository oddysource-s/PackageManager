/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Package Manager, a simple, yet powerful application
 * to manage other application installed on an android device.
 *
 */

package com.smartpack.packagemanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.smartpack.packagemanager.R;
import com.smartpack.packagemanager.utils.Common;
import com.smartpack.packagemanager.utils.FilePicker;
import com.smartpack.packagemanager.utils.Flavor;
import com.smartpack.packagemanager.utils.SplitAPKInstaller;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 12, 2021
 */
public class InstallerInstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installer_instructions);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mAdd = findViewById(R.id.add_button);
        SwitchMaterial mHideSwitch = findViewById(R.id.hide_switch);

        mHideSwitch.setChecked(sUtils.getBoolean("neverShow", false, this));

        mHideSwitch.setOnClickListener(v -> sUtils.saveBoolean("neverShow", !sUtils.getBoolean(
                "neverShow",false, this), this));

        mBack.setOnClickListener(v -> finish());

        mAdd.setOnClickListener(v -> {
            if (Flavor.isFullVersion()) {
                Common.getAppList().clear();
                Common.setPath(FilePicker.getLastDirPath(this));
                Intent filePicker = new Intent(this, FilePickerActivity.class);
                startActivity(filePicker);
                finish();
            } else {
                Intent installer = new Intent(Intent.ACTION_GET_CONTENT);
                installer.setType("*/*");
                installer.addCategory(Intent.CATEGORY_OPENABLE);
                installer.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(installer, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 0) {
                Uri uriFile = data.getData();

                if (data.getClipData() != null) {
                    SplitAPKInstaller.handleMultipleAPKs(data.getClipData(), this).execute();
                } else if (uriFile != null) {
                    SplitAPKInstaller.handleSingleInstallationEvent(uriFile, this).execute();
                }
                finish();
            }
        }
    }

}