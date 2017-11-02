package com.task.aliessa.appsinnovatetask.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Muhammad on 11/2/2017
 */

public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    private static final int MAX_IMAGE_WIDTH = 500;

    // max folder size is 15MB
    private static final long MAX_TEMP_FOLDER_SIZE = 15000000L;

    public static Bitmap compressBitmap(Bitmap inputBitmap) {
        Log.d(TAG, "compressBitmap: inputBitmap" + inputBitmap.getByteCount());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        inputBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Bitmap outputBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        Log.d(TAG, "compressBitmap: outputBitmap" + outputBitmap.getByteCount());

        return outputBitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap inputBitmap) {
        return createScaledBitmap(inputBitmap, MAX_IMAGE_WIDTH);
    }

    public static Bitmap createScaledBitmap(Bitmap inputBitmap, int screenWidth) {

        if (inputBitmap == null) {
            return null;
        }

        Bitmap outputBitmap;

        int scalingRatio;
        int height;

        int width = inputBitmap.getWidth();

        if (width > screenWidth) {
            scalingRatio = width / screenWidth;

            width = screenWidth;
            height = inputBitmap.getHeight() / scalingRatio;

            outputBitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true);
        } else {
            outputBitmap = inputBitmap;
        }

        return outputBitmap;
    }

    public static Bitmap createScaledBitmap(String path) {

        if (path == null || path.isEmpty()) {
            return null;
        }

        int desiredWidth = MAX_IMAGE_WIDTH;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
        if (desiredWidth > srcWidth)
            desiredWidth = srcWidth;

        // Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2
        int inSampleSize = 1;
        while (srcWidth / 2 > desiredWidth) {
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }

        float desiredScale = (float) desiredWidth / srcWidth;

        // Decode with inSampleSize
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path, options);

        // Resize
        Matrix matrix = new Matrix();
        matrix.postScale(desiredScale, desiredScale);
        Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);

        return scaledBitmap;
    }

    public static File getBitmapFile(Bitmap inputBitmap) {
        File returnFile = null;
        File file = getNewTempFile(System.currentTimeMillis() + "");
        if (inputBitmap != null) {
            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                inputBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapData = bos.toByteArray();

                // Write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapData);
                fos.flush();
                fos.close();

                returnFile = file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnFile;
    }

    public static File getNewTempFile(String name) {

        File appFolder = new File(Environment.getExternalStorageDirectory(), BitmapUtils.class.getPackage().getName());

        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Log.e(TAG, "getNewTempFile: Failed to create directories(" + appFolder.getAbsolutePath() + ")");
                return null;
            }
        }

        if (appFolder.listFiles().length > 5) {
            if (getDirSize(appFolder) > MAX_TEMP_FOLDER_SIZE) {
                for (File file : appFolder.listFiles()) {
                    if (!file.delete()) {
                        Log.e(TAG, "getNewTempFile: Failed to delete file(" + file.getAbsolutePath() + ")");
                    }
                }
            }
        }

        File file = new File(appFolder, name);

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    Log.e(TAG, "getNewTempFile: Failed to create new file (" + file.getAbsolutePath() + ")");
                }
            } catch (IOException e) {
                Log.e(TAG, "getNewTempFile: Failed to create new file (" + file.getAbsolutePath() + ")");
                e.printStackTrace();
            }
        }

        return file;
    }

    private static long getDirSize(File file) {
        long size = 0;

        if (file.exists()) {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (File currentFile : fileList) {
                    // Recursive call if it's a directory
                    if (currentFile.isDirectory()) {
                        size += getDirSize(currentFile);
                    } else {
                        // Sum the file size in bytes
                        size += currentFile.length();
                    }
                }
            } else {
                size = file.length();
            }
        }

        return size;
    }
}