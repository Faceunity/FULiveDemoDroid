package com.faceunity.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.entity.FunctionType;
import com.faceunity.app.entity.HomeFunctionModuleData;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.app.view.ActionRecognitionActivity;
import com.faceunity.app.view.AnimoActivity;
import com.faceunity.app.view.AvatarActivity;
import com.faceunity.app.view.BgSegGreenActivity;
import com.faceunity.app.view.BodyBeautyActivity;
import com.faceunity.app.view.FaceBeautyActivity;
import com.faceunity.app.view.FineStickerActivity;
import com.faceunity.app.view.HairBeautyActivity;
import com.faceunity.app.view.LightMakeupActivity;
import com.faceunity.app.view.MakeupActivity;
import com.faceunity.app.view.MusicFilterActivity;
import com.faceunity.app.view.PortraitSegmentActivity;
import com.faceunity.app.view.PosterListActivity;
import com.faceunity.app.view.PropActivity;
import com.faceunity.ui.base.BaseDelegate;
import com.faceunity.ui.base.BaseListAdapter;
import com.faceunity.ui.base.BaseViewHolder;
import com.faceunity.ui.dialog.ToastHelper;

import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class MainActivity extends BaseActivity {
    private ArrayList<HomeFunctionModuleData> mFunctions;
    private BaseListAdapter<HomeFunctionModuleData> mAdapter;
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};


    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }


    @Override
    public void initData() {
        mFunctions = HomeFunctionModuleData.buildData();
        mAdapter = new BaseListAdapter(mFunctions, new BaseDelegate<HomeFunctionModuleData>() {

            @Override
            public int getItemViewType(HomeFunctionModuleData data, int position) {
                if (data.type == FunctionType.Banner) {
                    return 0;
                } else if (data.type == FunctionType.Title) {
                    return 1;
                } else if (data.type == FunctionType.ModelLottie) {
                    return 3;
                } else {
                    return 2;
                }
            }

            @Override
            public void convert(int viewType, BaseViewHolder helper, HomeFunctionModuleData data, int position) {
                if (viewType == 1) {
                    helper.setText(R.id.tv_title, data.titleRes);
                } else if (viewType == 2) {
                    helper.setText(R.id.tv_module, data.titleRes);
                    helper.setImageResource(R.id.iv_module, data.iconRes);
                } else if (viewType == 3) {
                    helper.setText(R.id.home_recycler_text, data.titleRes);
                }
            }

            @Override
            public void onItemClickListener(View view, HomeFunctionModuleData data, int position) {
                if (data.type == FunctionType.Model || data.type == FunctionType.ModelLottie) {
                    onFunctionClick(data);
                }
            }
        }, R.layout.list_item_home_banner,
                R.layout.list_item_home_title,
                R.layout.list_item_home_module,
                R.layout.list_item_home_module_lottie);
    }


    @Override
    public void initView() {
         RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                HomeFunctionModuleData mModule = mFunctions.get(position);
                return (mModule.type == FunctionType.Model) ? 1 : gridLayoutManager.getSpanCount();
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void bindListener() {
        checkSelfPermission();
    }


    private void checkSelfPermission() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 10001);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Boolean hasPermissionDismiss = false; //有权限没有通过
        for (int element : grantResults) {
            if (element == -1) {
                hasPermissionDismiss = true;
            }
        }
        //如果有权限没有被允许
        if (hasPermissionDismiss) {
            ToastHelper.showNormalToast(this, "缺少必要权限，可能导致应用功能无法使用");
        }
    }

    private void onFunctionClick(HomeFunctionModuleData data) {
        switch (data.titleRes) {
            case R.string.home_function_name_beauty:
                startActivity(new Intent(this, FaceBeautyActivity.class));
                break;
            case R.string.home_function_name_makeup:
                startActivity(new Intent(this, MakeupActivity.class));
                break;
            case R.string.home_function_name_beauty_body:
                startActivity(new Intent(this, BodyBeautyActivity.class));
                break;
            case R.string.home_function_name_sticker:
                PropActivity.startActivity(this, FunctionEnum.STICKER);
                break;
            case R.string.home_function_name_animoji:
                startActivity(new Intent(this, AnimoActivity.class));
                break;
            case R.string.home_function_name_hair:
                startActivity(new Intent(this, HairBeautyActivity.class));
                break;
            case R.string.home_function_name_light_makeup:
                startActivity(new Intent(this, LightMakeupActivity.class));
                break;
            case R.string.home_function_name_ar:
                PropActivity.startActivity(this, FunctionEnum.AR_MASK);
                break;
            case R.string.home_function_name_big_head:
                PropActivity.startActivity(this, FunctionEnum.BIG_HEAD);
                break;
            case R.string.home_function_name_expression:
                PropActivity.startActivity(this, FunctionEnum.EXPRESSION_RECOGNITION);
                break;
            case R.string.home_function_name_music_filter:
                startActivity(new Intent(this, MusicFilterActivity.class));
                break;
            case R.string.home_function_name_face_warp:
                PropActivity.startActivity(this, FunctionEnum.FACE_WARP);
                break;
            case R.string.home_function_name_action_recognition:
                startActivity(new Intent(this, ActionRecognitionActivity.class));
                break;
            case R.string.home_function_name_portrait_segment:
                startActivity(new Intent(this, PortraitSegmentActivity.class));
                break;
            case R.string.home_function_name_gesture:
                PropActivity.startActivity(this, FunctionEnum.GESTURE_RECOGNITION);
                break;
            case R.string.home_function_name_poster_face:
                startActivity(new Intent(this, PosterListActivity.class));
                break;
            case R.string.home_function_name_green_curtain:
                startActivity(new Intent(this, BgSegGreenActivity.class));
                break;
            case R.string.home_function_name_fine_sticker:
                startActivity(new Intent(this, FineStickerActivity.class));
                break;
            case R.string.home_function_name_human_avatar:
                startActivity(new Intent(this, AvatarActivity.class));
                break;
            default:
                ToastHelper.showNormalToast(this, data.titleRes);
        }
    }


}