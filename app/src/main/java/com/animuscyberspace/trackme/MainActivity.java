package com.animuscyberspace.trackme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private TextView title;
    private TextView content;

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.app_name_and_version);
        content = (TextView) findViewById(R.id.content);
        initViews();
        updateData();


    }
    private void updateData() {
        boolean isReferrerDetected = Application.isReferrerDetected(getApplicationContext());
        String firstLaunch = Application.getFirstLaunch(getApplicationContext());
        String referrerDate = Application.getReferrerDate(getApplicationContext());
        String referrerDataRaw = Application.getReferrerDataRaw(getApplicationContext());
        String referrerDataDecoded = Application.getReferrerDataDecoded(getApplicationContext());

        StringBuilder sb = new StringBuilder();
        sb.append("<b>First launch:</b>")
                .append("<br/>")
                .append(firstLaunch)
                .append("<br/><br/>")
                .append("<b>Referrer detection:</b>")
                .append("<br/>")
                .append(referrerDate);
        if (isReferrerDetected) {
            sb.append("<br/><br/>")
                    .append("<b>Raw referrer:</b>")
                    .append("<br/>")
                    .append(referrerDataRaw);

            if (referrerDataDecoded != null) {
                sb.append("<br/><br/>")
                        .append("<b>Decoded referrer:</b>")
                        .append("<br/>")
                        .append(referrerDataDecoded);
            }
        }
        content.setText(Html.fromHtml(sb.toString()));
        content.setMovementMethod(new LinkMovementMethod());
    }
    private void initViews() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = getString(R.string.version_unavailable);
        }

        title.setText(Html.fromHtml(getString(R.string.app_name_and_version, versionName)));
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver, new IntentFilter(ReferrerReceiver.ACTION_UPDATE_DATA));
        super.onResume();
    }

}