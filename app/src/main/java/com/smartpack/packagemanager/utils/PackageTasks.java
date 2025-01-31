/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Package Manager, a simple, yet powerful application
 * to manage other application installed on an android device.
 *
 */

package com.smartpack.packagemanager.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import com.smartpack.packagemanager.R;
import com.smartpack.packagemanager.activities.PackageTasksActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.sCommon.Utils.sAPKUtils;
import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sPackageUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 12, 2020
 */
public class PackageTasks {

    private static RootShell mRootShell = null;
    private static ShizukuShell mShizukuShell = null;

    public static void batchDisableTask(Activity activity) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                Common.isRunning(true);
                Common.getOutput().setLength(0);
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_processing_initialized)).append("...\n\n");
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_list_summary)).append(PackageData.showBatchList()).append("\n\n");
                Intent turnOffIntent = new Intent(activity, PackageTasksActivity.class);
                turnOffIntent.putExtra(PackageTasksActivity.TITLE_START, activity.getString(R.string.batch_processing));
                turnOffIntent.putExtra(PackageTasksActivity.TITLE_FINISH, activity.getString(R.string.batch_processing_finished));
                activity.startActivity(turnOffIntent);
                if (mRootShell == null) {
                    mRootShell = new RootShell();
                }
                if (mShizukuShell == null) {
                    mShizukuShell = new ShizukuShell();
                }
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void doInBackground() {
                for (String packageID : Common.getBatchList()) {
                    if (packageID.contains(".")) {
                        if (packageID.equals(activity.getPackageName())) {
                            Common.getOutput().append("** ").append(activity.getString(R.string.disabling, PackageData.getAppName(packageID, activity)));
                            Common.getOutput().append(": ").append(activity.getString(R.string.uninstall_nope)).append(" *\n\n");
                        } else {
                            Common.getOutput().append(sPackageUtils.isEnabled(packageID, activity) ? "** " +
                                    activity.getString(R.string.disabling, PackageData.getAppName(packageID, activity)) :
                                    "** " + activity.getString(R.string.enabling, PackageData.getAppName(packageID, activity)));
                            String result;
                            if (mRootShell.rootAccess()) {
                                result = mRootShell.runAndGetError((sPackageUtils.isEnabled(packageID, activity) ? "pm disable " : "pm enable ") + packageID);
                            } else {
                                result = mShizukuShell.runAndGetOutput((sPackageUtils.isEnabled(packageID, activity) ? "pm disable " : "pm enable ") + packageID);
                            }
                            Common.getOutput().append(": ").append(activity.getString(result != null && (result.contains("new state: disabled")
                                    || result.contains("new state: enabled")) ? R.string.done : R.string.failed)).append(" *\n\n");
                        }
                        sUtils.sleep(1);
                    }
                }
            }

            @Override
            public void onPostExecute() {
                Common.getOutput().append("** ").append(activity.getString(R.string.everything_done)).append(" *");
                Common.isRunning(false);
                Common.reloadPage(true);
            }
        }.execute();
    }

    public static void batchResetTask(Activity activity) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                Common.isRunning(true);
                Common.getOutput().setLength(0);
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_processing_initialized)).append("...\n\n");
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_list_summary)).append(PackageData.showBatchList()).append("\n\n");
                Intent removeIntent = new Intent(activity, PackageTasksActivity.class);
                removeIntent.putExtra(PackageTasksActivity.TITLE_START, activity.getString(R.string.batch_processing));
                removeIntent.putExtra(PackageTasksActivity.TITLE_FINISH, activity.getString(R.string.batch_processing_finished));
                activity.startActivity(removeIntent);
                if (mRootShell == null) {
                    mRootShell = new RootShell();
                }
                if (mShizukuShell == null) {
                    mShizukuShell = new ShizukuShell();
                }
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void doInBackground() {
                for (String packageID : Common.getBatchList()) {
                    if (packageID.contains(".") && sPackageUtils.isPackageInstalled(packageID, activity)) {
                        if (packageID.equals(activity.getPackageName())) {
                            Common.getOutput().append("** ").append(activity.getString(R.string.reset_summary, PackageData.getAppName(packageID, activity)));
                            Common.getOutput().append(": ").append(activity.getString(R.string.uninstall_nope)).append(" *\n\n");
                        } else {
                            Common.getOutput().append("** ").append(activity.getString(R.string.reset_summary, PackageData.getAppName(packageID, activity)));
                            if (mRootShell.rootAccess()) {
                                mRootShell.runCommand("pm clear " + packageID);
                            } else {
                                mShizukuShell.runCommand("pm clear " + packageID);
                            }
                            Common.getOutput().append(": ").append(activity.getString(R.string.done)).append(" *\n\n");
                        }
                        sUtils.sleep(1);
                    }
                }
            }

            @Override
            public void onPostExecute() {
                Common.getOutput().append("** ").append(activity.getString(R.string.everything_done)).append(" *");
                Common.isRunning(false);
            }
        }.execute();
    }

    public static void batchExportTask(Activity activity) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                Common.isRunning(true);
                Common.getOutput().setLength(0);
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_processing_initialized)).append("...\n\n");
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_list_summary)).append(PackageData.showBatchList()).append("\n\n");
                Intent removeIntent = new Intent(activity, PackageTasksActivity.class);
                removeIntent.putExtra(PackageTasksActivity.TITLE_START, activity.getString(R.string.batch_processing));
                removeIntent.putExtra(PackageTasksActivity.TITLE_FINISH, activity.getString(R.string.batch_processing_finished));
                activity.startActivity(removeIntent);
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void doInBackground() {
                if (!PackageData.getPackageDir(activity).exists()) {
                    PackageData.getPackageDir(activity).mkdirs();
                }
                for (String packageID : Common.getBatchList()) {
                    if (packageID.contains(".") && sPackageUtils.isPackageInstalled(packageID, activity)) {
                        if (SplitAPKInstaller.isAppBundle(sPackageUtils.getParentDir(packageID, activity))) {
                            Common.getOutput().append("** ").append(activity.getString(R.string.exporting_bundle, PackageData.getAppName(packageID, activity)));
                            List<File> mFiles = new ArrayList<>();
                            for (final String splitApps : SplitAPKInstaller.splitApks(sPackageUtils.getParentDir(packageID, activity))) {
                                mFiles.add(new File(sPackageUtils.getParentDir(packageID, activity) + "/" + splitApps));
                            }
                            Utils.zip(PackageData.getPackageDir(activity) + "/" + PackageData.getFileName(packageID, activity) + "_" +
                                    sAPKUtils.getVersionCode(sPackageUtils.getSourceDir(packageID, activity), activity) + ".apkm", mFiles);
                        } else {
                            Common.getOutput().append("** ").append(activity.getString(R.string.exporting, PackageData.getAppName(packageID, activity)));
                            sUtils.copy(new File(sPackageUtils.getSourceDir(packageID, activity)), new File(PackageData.getPackageDir(activity), PackageData.getFileName(packageID, activity) + "_" +
                                    sAPKUtils.getVersionCode(sPackageUtils.getSourceDir(packageID, activity), activity) + ".apk"));
                        }
                        Common.getOutput().append(": ").append(activity.getString(R.string.done)).append(" *\n\n");
                        sUtils.sleep(1);
                    }
                }
            }

            @Override
            public void onPostExecute() {
                Common.getOutput().append("** ").append(activity.getString(R.string.everything_done)).append(" *");
                Common.isRunning(false);
            }
        }.execute();
    }

    public static void batchUninstallTask(Activity activity) {
        new sExecutor() {

            @Override
            public void onPreExecute() {
                Common.isRunning(true);
                Common.getOutput().setLength(0);
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_processing_initialized)).append("...\n\n");
                Common.getOutput().append("** ").append(activity.getString(R.string.batch_list_summary)).append(PackageData.showBatchList()).append("\n\n");
                Intent removeIntent = new Intent(activity, PackageTasksActivity.class);
                removeIntent.putExtra(PackageTasksActivity.TITLE_START, activity.getString(R.string.batch_processing));
                removeIntent.putExtra(PackageTasksActivity.TITLE_FINISH, activity.getString(R.string.batch_processing_finished));
                activity.startActivity(removeIntent);
                if (mRootShell == null) {
                    mRootShell = new RootShell();
                }
                if (mShizukuShell == null) {
                    mShizukuShell = new ShizukuShell();
                }
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void doInBackground() {
                for (String packageID : Common.getBatchList()) {
                    if (packageID.contains(".") && sPackageUtils.isPackageInstalled(packageID, activity)) {
                        if (packageID.equals(activity.getPackageName())) {
                            Common.getOutput().append("** ").append(activity.getString(R.string.uninstall_summary, PackageData.getAppName(packageID, activity)));
                            Common.getOutput().append(": ").append(activity.getString(R.string.uninstall_nope)).append(" *\n\n");
                        } else {
                            Common.getOutput().append("** ").append(activity.getString(R.string.uninstall_summary, PackageData.getAppName(packageID, activity)));
                            if (mRootShell.rootAccess()) {
                                mRootShell.runCommand("pm uninstall --user 0 " + packageID);
                            } else {
                                mShizukuShell.runCommand("pm uninstall --user 0 " + packageID);
                            }
                            Common.getOutput().append(sPackageUtils.isPackageInstalled(packageID, activity) ? ": " +
                                    activity.getString(R.string.failed) + " *\n\n" : ": " + activity.getString(R.string.done) + " *\n\n");
                        }
                        sUtils.sleep(1);
                    }
                }
            }

            @Override
            public void onPostExecute() {
                PackageData.setRawData(activity);
                Common.getOutput().append("** ").append(activity.getString(R.string.everything_done)).append(" *");
                Common.isRunning(false);
                Common.reloadPage(true);
            }
        }.execute();
    }

}