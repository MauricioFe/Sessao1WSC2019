package com.mauriciofe.github.io.session1;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigConsumeApi {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final static Handler handler = new Handler(Looper.getMainLooper());

    public static void requestApi(String uri, Callback<String> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String result = getRequest(uri);
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
        BufferedReader reader = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
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
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
