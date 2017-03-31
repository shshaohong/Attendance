package com.edu.chenshaohong.attendance.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.edu.chenshaohong.attendance.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shaohong on 2017-3-25.
 */

public class FragmentDialog extends DialogFragment {

    public static final int IMAGE_REQUEST_CODE = 0, CAMERA_REQUEST_CODE = 1, RESULT_REQUEST_CODE = 2;
    public static final String IMAGE_FILE_NAME = "faceImage.jpg", SAVE_AVATORNAME = "tempImage.jpg";
    @BindView(R.id.btn_gallery)
    Button mBtnGallery;
    @BindView(R.id.btn_camera)
    Button mBtnCamera;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    public String path;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.window_select_icon, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_gallery)
    public void setBtnGallery() {
        //到相册
        Intent intent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
        getDialog().cancel();
    }

    @OnClick(R.id.btn_camera)
    public void setBtnCamera() {
        //到相机
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
        getDialog().cancel();
    }

    @OnClick(R.id.btn_cancel)
    public void setBtnCancel() {
        getDialog().cancel();
    }
}
