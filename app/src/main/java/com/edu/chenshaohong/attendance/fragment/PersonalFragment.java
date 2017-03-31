package com.edu.chenshaohong.attendance.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.chenshaohong.attendance.MyApplication;
import com.edu.chenshaohong.attendance.R;
import com.edu.chenshaohong.attendance.adapter.SetUserIconAdapter;
import com.edu.chenshaohong.attendance.entity.AllUserDataEntity;
import com.edu.chenshaohong.attendance.entity.BaseEntity;
import com.edu.chenshaohong.attendance.entity.MessageEntity;
import com.edu.chenshaohong.attendance.entity.UserDataEntity;
import com.edu.chenshaohong.attendance.nextwork.AttendanceApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaohong on 2017-3-21.
 * 修改个人信息界面
 */

public class PersonalFragment extends DialogFragment {
    public static final int IMAGE_REQUEST_CODE = 0, CAMERA_REQUEST_CODE = 1, RESULT_REQUEST_CODE = 2;
    public static final String IMAGE_FILE_NAME = "faceImage.jpg", SAVE_AVATORNAME = "tempImage.jpg";
    @BindView(R.id.userIcon)
    ImageView mUserIcon;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.department)
    EditText mDepartment;
    @BindView(R.id.phoneNumber)
    TextView mPhoneNumber;
    @BindView(R.id.mail)
    EditText mMail;
    @BindView(R.id.setInformation)
    TextView mSetInformation;
    private View mView;
    private Unbinder mBind;
    private String userId;
    private AttendanceApi.Api mApi;
    private PopupWindow mPopupWindow;
    private SetUserIconAdapter mSetUserIconAdapter;
    private String[] items = new String[]{"    选择本地图片", "    打开相机拍照"};/* 请求码*/
    private String path;
    private List<UserDataEntity> mList;
    private String mUsermessage;
    private StringBuffer mBuffer;
    private Bitmap mPhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_personal, container, false);
        }

        mBind = ButterKnife.bind(this, mView);
        initData();
        return mView;
    }

    private void initData() {
        mList = new ArrayList<>();
        mApi = AttendanceApi.createApi();
        SharedPreferences ff = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        final Gson gson1 = new Gson();
        final List<MessageEntity> list1 = gson1.fromJson(ff.getString("messagelist", "[]"),
                new TypeToken<List<MessageEntity>>() {
                }.getType());
//
        SharedPreferences sf = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        mUsermessage = sf.getString("usermessage", "[]");
        int listnumber = sf.getInt("listnumber", 0);

        mApi.getAllUserData(MyApplication.getInstance().getAppId(),
                MyApplication.getInstance().getPhoneNumbere(),
                list1.get(listnumber).getMachineId()).enqueue(new Callback<AllUserDataEntity>() {
            @Override
            public void onResponse(Call<AllUserDataEntity> call, Response<AllUserDataEntity> response) {
                if (response.body().getStatus().equals("1")) {
                    mList = response.body().getMessage();
                    Log.e("onResponse---list", mList.size() + "");
                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).getPhonenumber().equals(MyApplication.getInstance().getPhoneNumbere())) {
                            String username = mList.get(i).getUsername();
                            String email = mList.get(i).getEmail();
                            String department = mList.get(i).getDepartment();
                            String phonenumber = mList.get(i).getPhonenumber();
                            userId = mList.get(i).getId();
                            mUserName.setText(username);
                            mDepartment.setText(department);
                            mPhoneNumber.setText(phonenumber);
                            mMail.setText(email);
                            String[] split = mList.get(i).getHeadurl().split("\"");
                            mBuffer = new StringBuffer();
                            for (String ss : split) {
                                mBuffer.append(ss);
                            }
                            Picasso.with(getContext()).load("http://121.40.70.170" +
                                    mBuffer).placeholder(R.mipmap.userportrait).into(mUserIcon);
                            if (mPhoto != null) {
                                mUserIcon.setImageBitmap(mPhoto);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AllUserDataEntity> call, Throwable t) {

            }
        });


    }

    @OnClick(R.id.userIcon)
    public void setUserIcon() {
        new AlertDialog.Builder(getActivity())
                .setTitle("设置头像方式")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //到相册
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                                    IMAGE_FILE_NAME)));
                                }
                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_CANCELED)
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        setImageToView(data);
                    }
                    break;
            }
    }

    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    //保存裁剪之后的图片数据
    private void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        mPhoto = null;
        if (extras != null) {
            mPhoto = extras.getParcelable("data");
        } else {
            Uri uri = data.getData();
            try {
                mPhoto = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (mPhoto != null) {
            path = saveMyBitmap(mPhoto);//保存图片操作
            updateUserHead();
            mUserIcon.setImageBitmap(mPhoto);
        }
    }

    private String saveMyBitmap(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/head_back/");
        if (!dir.exists())
            dir.mkdir();
        File f = new File(dir.getPath() + SAVE_AVATORNAME);
        try {
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateUserHead() {
        if (path == null)
            return;
        File file = new File(path);//filePath 图片地址

        Log.e("filename", file.getName());

        String phone = MyApplication.getInstance().getPhoneNumbere();
        RequestBody requestFile  = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("phone", phone);
        builder.addFormDataPart("head_back", file.getName(), requestFile);
        List<MultipartBody.Part> parts = builder.build().parts();

        mApi = AttendanceApi.createApi();
        mApi.updateUserHead(parts)
                .enqueue(new Callback<BaseEntity>() {
                    @Override
                    public void onResponse(Call<BaseEntity> call, Response<BaseEntity> response) {
                        if (response.body().getMess().equals("1")) {
                            Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();

                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(
                                    new Intent(CompanyFragment.USER_DATA)
                                            .putExtra("icon", mPhoto)
                            );
                        } else {
                            Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseEntity> call, Throwable t) {
                        Log.e("Throwable", t.getMessage());
                        Toast.makeText(getContext(), "上传失败，访问网络异常", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick(R.id.setInformation)
    public void setInformation() {
        mApi = AttendanceApi.createApi();
        mApi.updateUserData(userId, mDepartment.getText().toString().trim(),
                mMail.getText().toString().trim()).enqueue(new Callback<BaseEntity>() {
            @Override
            public void onResponse(Call<BaseEntity> call, Response<BaseEntity> response) {
                if (response.body().getStatus().equals("1")) {
                    Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(
                            new Intent(CompanyFragment.USER_DATA)
                                    .putExtra("department", mDepartment.getText().toString().trim())
                                    .putExtra("username", mUserName.getText().toString())
                                    .putExtra("mail", mMail.getText().toString().trim()));
                } else {
                    Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseEntity> call, Throwable t) {
                Toast.makeText(getContext(), "访问网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.back)
    public void setback() {
        getDialog().dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }

}
