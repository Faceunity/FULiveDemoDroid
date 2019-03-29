package com.faceunity.fulivedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.utils.FullScreenUtils;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面，区分了SDK各个功能，并且获取权限码验证证书是否能够使用该功能
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int[] home_function_type = {
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_NORMAL,
            Effect.EFFECT_TYPE_ANIMOJI,
            Effect.EFFECT_TYPE_NONE,
            Effect.EFFECT_TYPE_AR,
            Effect.EFFECT_TYPE_FACE_CHANGE,
            Effect.EFFECT_TYPE_POSTER_FACE,
            Effect.EFFECT_TYPE_EXPRESSION,
            Effect.EFFECT_TYPE_MUSIC_FILTER,
            Effect.EFFECT_TYPE_BACKGROUND,
            Effect.EFFECT_TYPE_GESTURE,
            Effect.EFFECT_TYPE_FACE_WARP,
            Effect.EFFECT_TYPE_PORTRAIT_DRIVE,
//            Effect.EFFECT_TYPE_LIVE_PHOTO
    };

    // 文档：http://confluence.faceunity.com/pages/viewpage.action?pageId=10453059
    private static final int[] home_function_permissions_code = {
            0x1,                    //美颜
            0x80000,                //美妆
            0x2 | 0x4,              //道具贴纸
            0x10,                   //Animoji
            0x100000,               //美发
            0x20 | 0x40,            //AR面具
            0x80,                   //换脸
            0x800000,               //海报换脸
            0x800,                  //表情识别
            0x20000,                //音乐滤镜
            0x100,                  //背景分割
            0x200,                  //手势识别
            0x10000,                //哈哈镜
            0x8000,                 //人像驱动
//            0x1000000                 //表情动图
    };

    private static final int[] home_function_name = {
            R.string.home_function_name_beauty,
            R.string.home_function_name_makeup,
            R.string.home_function_name_normal,
            R.string.home_function_name_animoji,
            R.string.home_function_name_hair,
            R.string.home_function_name_ar,
            R.string.home_function_name_face_change,
            R.string.home_function_name_poster_face,
            R.string.home_function_name_expression,
            R.string.home_function_name_music_filter,
            R.string.home_function_name_background,
            R.string.home_function_name_gesture,
            R.string.home_function_name_face_warp,
            R.string.home_function_name_portrait_drive,
//            R.string.home_function_name_live_photo
    };

    private static final int[] home_function_res = {
            R.drawable.main_beauty,
            R.drawable.main_makeup,
            R.drawable.main_effect,
            R.drawable.main_avatar,
            R.drawable.main_hair,
            R.drawable.main_ar_mask,
            R.drawable.main_change_face,
            R.drawable.main_poster_face,
            R.drawable.main_expression,
            R.drawable.main_music_fiter,
            R.drawable.main_background,
            R.drawable.main_gesture,
            R.drawable.main_face_warp,
            R.drawable.main_portrait_drive,
//            R.drawable.main_magic_photo
    };

    private List<Integer> hasFaceUnityPermissionsList = new ArrayList<>();
    private final boolean[] hasFaceUnityPermissions = new boolean[home_function_name.length];

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        FullScreenUtils.fullScreen(this);
        MiscUtil.checkPermission(this);

        int moduleCode = FURenderer.getModuleCode();
        Log.e(TAG, "ModuleCode " + moduleCode);
        for (int i = 0, count = 0; i < home_function_name.length; i++) {
            hasFaceUnityPermissions[i] = moduleCode == 0 || (home_function_permissions_code[i] & moduleCode) > 0;
            if (hasFaceUnityPermissions[i]) {
                hasFaceUnityPermissionsList.add(count++, i);
            } else {
                hasFaceUnityPermissionsList.add(i);
            }
        }

        mRecyclerView = findViewById(R.id.home_recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if (type == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter();
        mRecyclerView.setAdapter(homeRecyclerAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType > 0) {
                return new HomeRecyclerHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_main_recycler, parent, false));
            } else {
                return new TopHomeRecyclerHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_main_recycler_top, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int p) {
            if (viewHolder instanceof HomeRecyclerHolder) {
                HomeRecyclerHolder holder = (HomeRecyclerHolder) viewHolder;
                final int pos = p - 1;
                final int position = hasFaceUnityPermissionsList.get(pos);

                holder.homeFunctionImg.setImageResource(home_function_res[position]);
                holder.homeFunctionName.setText(home_function_name[position]);
                holder.homeFunctionName.setBackgroundResource(hasFaceUnityPermissions[position] ? R.drawable.main_recycler_item_text_background : R.drawable.main_recycler_item_text_background_unable);

                holder.itemView.setOnClickListener(new OnMultiClickListener() {
                    private long mLastClickTime;

                    @Override
                    public void onMultiClick(View v) {
                        if (!hasFaceUnityPermissions[position]) {
                            ToastUtil.showToast(MainActivity.this, R.string.sorry_no_permission);
                            return;
                        }

                        // 防止同时快速点击，因为启动相机非常慢
                        if (System.currentTimeMillis() - mLastClickTime < 300) {
                            return;
                        }

                        mLastClickTime = System.currentTimeMillis();

                        Intent intent;
                        if (home_function_res[position] == R.drawable.main_beauty) {
                            intent = new Intent(MainActivity.this, FUBeautyActivity.class);
                            startActivity(intent);
                        } else if (home_function_res[position] == R.drawable.main_makeup) {
                            intent = new Intent(MainActivity.this, FUMakeupActivity.class);
                            startActivity(intent);
                        } else if (home_function_res[position] == R.drawable.main_hair) {
                            intent = new Intent(MainActivity.this, FUHairActivity.class);
                            startActivity(intent);
                        } else if (home_function_res[position] == R.drawable.main_poster_face) {
                            intent = new Intent(MainActivity.this, PosterListActivity.class);
                            startActivity(intent);
                        } else if (home_function_res[position] == R.drawable.main_avatar) {
                            intent = new Intent(MainActivity.this, FUAnimojiActivity.class);
                            startActivity(intent);
//                        } else if (home_function_res[position] == R.drawable.main_magic_photo) {
//                            intent = new Intent(MainActivity.this, FUMagicDriveActivity.class);
//                            startActivity(intent);
                        } else {
                            intent = new Intent(MainActivity.this, FUEffectActivity.class);
                            intent.putExtra("EffectType", home_function_type[position]);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return hasFaceUnityPermissionsList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView homeFunctionImg;
            TextView homeFunctionName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                homeFunctionImg = (ImageView) itemView.findViewById(R.id.home_recycler_img);
                homeFunctionName = (TextView) itemView.findViewById(R.id.home_recycler_text);
            }
        }

        class TopHomeRecyclerHolder extends RecyclerView.ViewHolder {

            public TopHomeRecyclerHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
