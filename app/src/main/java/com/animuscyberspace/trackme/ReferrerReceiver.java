package com.animuscyberspace.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ReferrerReceiver extends BroadcastReceiver {
    public static final String ACTION_UPDATE_DATA = "ACTION_UPDATE_DATA";
    private static final String ACTION_INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER";
    private static final String KEY_REFERRER = "referrer";

    public ReferrerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.e("ReferrerReceiver", "Intent is null");
            return;
        }
        if (!ACTION_INSTALL_REFERRER.equals(intent.getAction())) {
            Log.e("ReferrerReceiver", "Wrong action! Expected: " + ACTION_INSTALL_REFERRER + " but was: " + intent.getAction());
            return;
        }
        Bundle extras = intent.getExtras();
        if (intent.getExtras() == null) {
            Log.e("ReferrerReceiver", "No data in intent");
            return;
        }


        Toast.makeText(context,(String) extras.get(KEY_REFERRER),Toast.LENGTH_LONG).show();
         String value = (String) extras.get(KEY_REFERRER);

        String[] arrOfStr = value.split("&", 2);

        for (String a : arrOfStr)
            System.out.println("prak24"+a);

        String[] utm_source=arrOfStr[0].split("=");
        String utm_source_value = utm_source[1];
        System.out.println("prak24 utm_source_value : "+utm_source_value);


        String[] tracking_id=arrOfStr[1].split("=");
        String tracking_id_value = tracking_id[1];
        System.out.println("prak24 tracking_id_value : "+tracking_id_value);

        HashMap<String,String> params = new HashMap<>();
        params.put("utm_source",utm_source_value);
        params.put("tracking_id",tracking_id_value);


        new PerformNetworkCall().execute(params,context);

        Application.setReferrerDate(context.getApplicationContext(), new Date().getTime());
        Application.setReferrerData(context.getApplicationContext(), (String) extras.get(KEY_REFERRER));

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_UPDATE_DATA));
    }



    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams,Context context) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams,context));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params,Context context) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    class PerformNetworkCall extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            HashMap<String ,String> a = (HashMap<String, String>) params[0];
            Context t = (Context)params[1];

            performPostCall("https://d0a1ea6849d0eae68bd5850427816c40.m.pipedream.net",a,t);
            return null;
        }
    }
}
