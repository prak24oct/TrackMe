package com.animuscyberspace.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;



/*BroadCast Reciever To Recieve intent when app is installed from playstore*/

public class ReferrerReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER";


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

       /* Feteching params at runtime and sending in body of post request*/
        HashMap<String,String> params = new HashMap<>();
        String trackValue="";
        if (extras != null) {
            for (String key : extras.keySet()) {
                String value = extras.getString(key);
                Log.d("prak24", "Key: " + key + " Value: " + value);
                params.put(key,value);
                trackValue=trackValue+" "+ key+ " "+ value;
            }
        }
        Toast.makeText(context,trackValue,Toast.LENGTH_LONG).show();

        /*Performing Http Post Call */
         new PerformNetworkCall().execute(params,context);
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
            writer.write(getPostDataString(postDataParams));

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

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        String  result = (String) new JSONObject(params).toString();
//        boolean first = true;
//        for(Map.Entry<String, String> entry : params.entrySet()){
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//        }


        return result;
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
