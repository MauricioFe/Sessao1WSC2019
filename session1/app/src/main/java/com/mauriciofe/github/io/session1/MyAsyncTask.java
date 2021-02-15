package com.mauriciofe.github.io.session1;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAsyncTask {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final static Handler handler = new Handler(Looper.getMainLooper());
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";

    public static void requestApi(String uri, String method, String jsonBody, Callback<String> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String result = null;
                if (method.equals(METHOD_GET))
                    result = getRequest(uri);
                else
                    result = requestPostPut(uri, jsonBody, method);
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

//    public static void requestApiMultipartForm(String uri, int assetId, Bitmap bitmap, Callback<String> callback) {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                String result = postMultipartRequest(uri, assetId, bitmap);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onComplete(result);
//                    }
//                });
//            }
//        });
//    }

//    private static String postMultipartRequest(String uri, int assetID, Bitmap bitmap) {
//        BufferedReader reader;
//        try {
//            URL url = new URL(uri);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            String boundary = UUID.randomUUID().toString();
//            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setDoOutput(true);
//            DataOutputStream request = new DataOutputStream(conn.getOutputStream());
//
//            request.writeBytes("--" + boundary + "\r\n");
//            request.writeBytes("Content-Disposition: form-data; name=\"assetId\"\r\n\r\n");
//            request.writeBytes(assetID + "\r\n");
//
//            request.writeBytes("--" + boundary + "\r\n");
//            request.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\\\"\" + file.fileName + \"\\\"\\r\\n\\r\\n");
//            byte[] bacon = FileUtil.convertBitmapToArrayByte(bitmap);
//            request.write(FileUtil.convertBitmapToArrayByte(bitmap));
//            request.writeBytes("\r\n");
//
//            request.writeBytes("--" + boundary + "--\r\n");
//            request.flush();
//            int respCode = conn.getResponseCode();
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            return stringBuilder.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

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

    private static String requestPostPut(String uri, String jsonBody, String method) {
        BufferedReader reader;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");

            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonBody);
            writer.flush();
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
