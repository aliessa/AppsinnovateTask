package com.task.aliessa.appsinnovatetask.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Muhammad on 11/2/2017
 */

public abstract class ImageActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 300;
    public static final int GALLERY_REQUEST_CODE = 303;
    private final String TAG = "ImageActivity";
    private Uri newImageURI;

    protected void getImageFromGallery() {
        if (hasPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), GALLERY_REQUEST_CODE);
        }
    }

    protected void getImageFromCamera() {
        if (hasPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE)) {
            File tempFile = BitmapUtils.getNewTempFile(System.currentTimeMillis() + "");
            tempFile.delete();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                newImageURI = FileProvider.getUriForFile(this, ".provider", tempFile);
            } else {
                newImageURI = Uri.fromFile(tempFile);
            }
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newImageURI);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:

                    String path = "";
                    if (data == null) {
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                //In case you need image's absolute path
                                path = getRealPathFromURI(uri);
                                Log.d(TAG, "onActivityResult: path:" + path);
                            }
                        } else {
                            Uri dataUri = data.getData();
                            path = dataUri.getPath();
                            if (!path.startsWith("/storage/")) {
                                path = getRealPathFromURI(data.getData());
                            }
                        }
                    } else {
                        Uri dataUri = data.getData();
                        path = dataUri.getPath();
                        if (!path.startsWith("/storage/")) {
                            path = getRealPathFromURI(data.getData());
                        }
                    }

                    onFetchBitmap(BitmapFactory.decodeFile(path));
                    break;
                case CAMERA_REQUEST_CODE:

                    this.getContentResolver().notifyChange(newImageURI, null);
                    ContentResolver cr = this.getContentResolver();
                    Bitmap photo = null;
                    try {
                        photo = MediaStore.Images.Media.getBitmap(cr, newImageURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onActivityResult: ", e);
                    }

                    onFetchBitmap(photo);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                getImageFromGallery();
                break;
            case CAMERA_REQUEST_CODE:
                getImageFromCamera();
                break;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String result;
        try {
            Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            result = getRealPathFromURI(this.getApplicationContext(), contentUri);
        }
        return result;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(contentUri);
            // Split at colon, use second item in the array
            /* TODO: crash index out of bounds length is 1 and index is 1
             * dat=content://com.android.providers.downloads.documents/document/1287
             */
            String[] split = wholeID.split(":");
            String id = split[split.length - 1];
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, sel, new String[]{id}, null);
        } else {
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        }

        try {
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean hasPermissions(String[] permissions, int requestCode) {
        boolean hasPermission = true;
        for (int i = 0; hasPermission && i < permissions.length; i++) {
            String permission = permissions[i];
            hasPermission = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }

        return hasPermission;
    }

    protected abstract void onFetchBitmap(@Nullable Bitmap bitmap);
}

