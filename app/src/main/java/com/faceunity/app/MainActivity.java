package com.faceunity.app;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.entity.FunctionType;
import com.faceunity.app.entity.HomeFunctionModuleData;
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
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.ui.base.BaseDelegate;
import com.faceunity.ui.base.BaseListAdapter;
import com.faceunity.ui.base.BaseViewHolder;
import com.faceunity.ui.dialog.ToastHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class MainActivity extends BaseActivity {
    private ArrayList<HomeFunctionModuleData> mFunctions;
    private BaseListAdapter<HomeFunctionModuleData> mAdapter;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }


    @Override
    public void initData() {
        mFunctions = HomeFunctionModuleData.buildData();
        //根据权限码判断是否开启对应功能
        filterByModuleCode(mFunctions);
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
                    helper.getView(R.id.tv_module).setEnabled(data.enable);
                } else if (viewType == 3) {
                    helper.setText(R.id.home_recycler_text, data.titleRes);
                }
            }

            @Override
            public void onItemClickListener(View view, HomeFunctionModuleData data, int position) {
                if (data.type == FunctionType.Model || data.type == FunctionType.ModelLottie) {
                    if (!data.enable && data.type == FunctionType.Model) {
                        ToastHelper.showNormalToast(MainActivity.this, R.string.sorry_no_permission);
                        return;
                    }
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
        checkSelfPermission(permissions);
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

    private static void filterByModuleCode(List<HomeFunctionModuleData> homeFunctionModuleData) {
        int moduleCode0 = FURenderKit.getInstance().getModuleCode(0);
        int moduleCode1 = FURenderKit.getInstance().getModuleCode(1);
        for (HomeFunctionModuleData moduleEntity : homeFunctionModuleData) {
            if (moduleEntity.authCode != null) {
                String[] codeStr = moduleEntity.authCode.split("-");
                if (codeStr.length == 2) {
                    int code0 = Integer.parseInt(codeStr[0]);
                    int code1 = Integer.parseInt(codeStr[1]);
                    moduleEntity.enable = (moduleCode0 == 0 && moduleCode1 == 0) || ((code0 & moduleCode0) > 0 || (code1 & moduleCode1) > 0);
                }
            }
        }
    }

    //鉴权
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    @Override
    public void checkPermissionResult(boolean permissionResult) {
        if (!permissionResult) showToast("缺少必要权限，可能导致应用功能无法使用");
    }
}