package com.chartiq.demo.ui.chart;

import android.os.AsyncTask;
import android.util.Log;

import com.chartiq.sdk.model.OHLCParams;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TemproraryAsyncTask extends AsyncTask<Void, Void, String> {
    String url;
    Callback callback;

    public TemproraryAsyncTask(String url, Callback call) {
        this.url = url;
        callback = call;
    }

    @Override
    protected String doInBackground(Void... params) {
        String body = "";
        try {
            URL connectionUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            int code = connection.getResponseCode();

            InputStream is;
            StringBuilder builder;
            if (code >= 200 && code < 400) {
                builder = new StringBuilder();
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
                builder = new StringBuilder("Error(" + code + "): ");
            }
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                body = builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    @Override
    protected void onPostExecute(String body) {
        if (body.startsWith("Error")) {
            Log.d("ERROR", "ERROR");
        } else if ("invalid symbol".equals(body)) {
            Log.d("ERROR", "invalid SYMBOL");

        } else {
            callback.setOHLCChartData(new Gson().fromJson(body, OHLCParams[].class));
        }
    }
}
