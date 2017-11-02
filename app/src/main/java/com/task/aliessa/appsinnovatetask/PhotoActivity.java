package com.task.aliessa.appsinnovatetask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.task.aliessa.appsinnovatetask.utils.BitmapUtils;
import com.task.aliessa.appsinnovatetask.utils.ImageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoActivity extends ImageActivity implements View.OnClickListener {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.Btn_add_photo)
    Button Btn_add_photo;
    @BindView(R.id.fb_share_button)
    ShareButton shareButton;

    Bitmap picture;
    SharePhotoContent content;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();

        Btn_add_photo.setOnClickListener(this);
        shareButton.setOnClickListener(this);

    }

    @Override
    protected void onFetchBitmap(@Nullable Bitmap bitmap) {
        this.picture = bitmap;
        imageView.setImageBitmap(BitmapFactory.decodeFile(BitmapUtils.getBitmapFile(bitmap).getAbsolutePath()));
    }

    @Override
    public void onClick(View v) {
        if (v == shareButton) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(picture)
                    .build();
            content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            final ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(PhotoActivity.this, "your post shared successful", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        } else {
            getImageFromGallery();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
