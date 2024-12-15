package com.example.photoblogapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;

public class BitmapUtils {




    public static Bitmap resizeimg(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public static Bitmap scalQuarylity(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Bitmap decodeFromByteArray(byte[] bitmaparray) {
        return BitmapFactory.decodeByteArray(bitmaparray, 0, bitmaparray.length);
    }

    public static ByteBuffer getBytesFromBitmap(Bitmap bitmap) throws Exception {
        if (bitmap == null) throw new Exception("Bitmap is null");

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 根据颜色模式和位深决定数组长度
        ByteBuffer byteBuffer;
        switch (bitmap.getConfig()) {
            case ARGB_8888: // 假设是4字节每个像素
                byteBuffer = ByteBuffer.allocate(width * height * 4);
                break;
            default:
                throw new Exception("Unsupported bitmap configuration");
        }
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }
}
