package com.mauriciofe.github.io.session1;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAsyncTask {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final static Handler handler = new Handler(Looper.getMainLooper());
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    public static void requestApi(String uri, String method, String jsonBody, Callback<String> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String result = null;
                if (method.equals(METHOD_POST))
                    result = postRequest(uri, jsonBody);
                else if (method.equals(METHOD_GET))
                    result = getRequest(uri);

                String finalResult = result;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(finalResult);
                    }
                });
            }
        });
    }

    public static void openNewThreadSync(Bitmap result, Callback<Bitmap> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(result);
                    }
                });
            }
        });
    }

    private static String getRequest(String uri) {
        BufferedReader reader;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json;");
            conn.setDoOutput(false);
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String postRequest(String uri, String jsonBody) {
        BufferedReader reader;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
