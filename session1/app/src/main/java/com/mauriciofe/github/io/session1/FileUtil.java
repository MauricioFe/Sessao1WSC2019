package com.mauriciofe.github.io.session1;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class FileUtil {
    public static byte[] convertBitmapToArrayByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
