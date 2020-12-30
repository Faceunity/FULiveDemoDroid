package com.faceunity.fulivedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.activity.BeautifyBodyActivity;
import com.faceunity.fulivedemo.activity.BgSegGreenActivity;
import com.faceunity.fulivedemo.activity.FUAnimojiActivity;
import com.faceunity.fulivedemo.activity.FUBeautyActivity;
import com.faceunity.fulivedemo.activity.FUEffectActivity;
import com.faceunity.fulivedemo.activity.FUHairActivity;
import com.faceunity.fulivedemo.activity.FUMakeupActivity;
import com.faceunity.fulivedemo.activity.LightMakeupActivity;
import com.faceunity.fulivedemo.activity.PosterChangeListActivity;
import com.faceunity.fulivedemo.activity.PtaActivity;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.PermissionUtil;
import com.faceunity.fulivedemo.utils.ScreenUtils;
import com.faceunity.fulivedemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Richie on 2020.06.02
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.fullScreen(this);
        setContentView(R.layout.activity_main);
        PermissionUtil.checkPermissions(this);

        RecyclerView recyclerView = findViewById(R.id.rv_main_list);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        List<ModuleEntity> moduleEntities = initModuleEntity();
        filterByModuleCode(moduleEntities);
        final MainModuleAdapter mainModuleAdapter = new MainModuleAdapter(new ArrayList<>(moduleEntities));
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mainModuleAdapter.getItemViewType(position);
                if (itemViewType == ModuleEntity.UI_TYPE_BANNER
                        || itemViewType == ModuleEntity.UI_TYPE_CLASSIFICATION) {
                    return layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainModuleAdapter);
    }

    /**
     * 各个功能模块
     *
     * @return
     */
    private static List<ModuleEntity> initModuleEntity() {
        List<ModuleEntity> moduleEntities = new ArrayList<>();
        moduleEntities.add(new ModuleEntity(ModuleEntity.UI_TYPE_BANNER));
        moduleEntities.add(new ModuleEntity(R.string.main_classification_face, ModuleEntity.UI_TYPE_CLASSIFICATION));
        moduleEntities.add(new ModuleEntity(R.drawable.main_beauty, R.string.home_function_name_beauty, "1-0", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_makeup, R.string.home_function_name_makeup, "524288-0", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_effect, R.string.home_function_name_sticker, "110-0", Effect.EFFECT_TYPE_STICKER, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_animoji, R.string.home_function_name_animoji, "16-0", Effect.EFFECT_TYPE_ANIMOJI, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_hair, R.string.home_function_name_hair, "1048576-0", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_texture_beauty, R.string.home_function_name_light_makeup, "0-8", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_ar_mask, R.string.home_function_name_ar, "96-0", Effect.EFFECT_TYPE_AR_MASK, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_photo_sticker, R.string.home_function_name_big_head, "0-32768", Effect.EFFECT_TYPE_BIG_HEAD, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_poster_face, R.string.home_function_name_poster_face, "8388608-0", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_expression, R.string.home_function_name_expression, "2058-0", Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_music_fiter, R.string.home_function_name_music_filter, "131072-0", Effect.EFFECT_TYPE_MUSIC_FILTER, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_face_warp, R.string.home_function_name_face_warp, "65536-0", Effect.EFFECT_TYPE_FACE_WARP, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.string.main_classification_human, ModuleEntity.UI_TYPE_CLASSIFICATION));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_body, R.string.home_function_name_beauty_body, "0-32", Effect.EFFECT_TYPE_NONE, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_whole_body, R.string.home_function_name_human_avatar, "0-448", Effect.EFFECT_TYPE_PTA, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_action, R.string.home_function_name_action_recognition, "2-65536", Effect.EFFECT_TYPE_ACTION_RECOGNITION, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_background, R.string.home_function_name_portrait_segment, "256-0", Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.main_gesture, R.string.home_function_name_gesture, "512-0", Effect.EFFECT_TYPE_GESTURE_RECOGNITION, ModuleEntity.UI_TYPE_MODULE));
        moduleEntities.add(new ModuleEntity(R.drawable.demo_icon_green_curtain, R.string.home_function_name_bg_seg_green, "0-512", Effect.EFFECT_TYPE_BG_SEG_GREEN, ModuleEntity.UI_TYPE_MODULE));
        return Collections.unmodifiableList(moduleEntities);
    }

    private static void filterByModuleCode(List<ModuleEntity> moduleEntities) {
        int moduleCode0 = FURenderer.getModuleCode(0);
        int moduleCode1 = FURenderer.getModuleCode(1);
        for (ModuleEntity moduleEntity : moduleEntities) {
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

    private class MainModuleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ModuleEntity> mModuleEntities;

        MainModuleAdapter(List<ModuleEntity> moduleEntities) {
            mModuleEntities = moduleEntities;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            if (viewType == ModuleEntity.UI_TYPE_MODULE) {
                View view = layoutInflater.inflate(R.layout.layout_main_recycler_module, parent, false);
                ModuleViewHolder moduleViewHolder = new ModuleViewHolder(view);
                view.setOnClickListener(new ViewClickListener(moduleViewHolder));
                return moduleViewHolder;
            } else if (viewType == ModuleEntity.UI_TYPE_CLASSIFICATION) {
                View view = layoutInflater.inflate(R.layout.recycler_main_classification, parent, false);
                return new ClassificationViewHolder(view);
            } else {
                View view = layoutInflater.inflate(R.layout.layout_main_recycler_banner, parent, false);
                return new BannerViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            ModuleEntity moduleEntity = mModuleEntities.get(position);
            if (itemViewType == ModuleEntity.UI_TYPE_MODULE) {
                ModuleViewHolder viewHolder = (ModuleViewHolder) holder;
                viewHolder.ivModuleIcon.setImageResource(moduleEntity.iconId);
                viewHolder.tvModuleName.setText(moduleEntity.nameId);
                viewHolder.tvModuleName.setEnabled(moduleEntity.enable);
            } else if (itemViewType == ModuleEntity.UI_TYPE_CLASSIFICATION) {
                ClassificationViewHolder viewHolder = (ClassificationViewHolder) holder;
                viewHolder.tvClassificationName.setText(moduleEntity.nameId);
            }
        }

        @Override
        public int getItemCount() {
            return mModuleEntities.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mModuleEntities.get(position).uiType;
        }

        private class ViewClickListener extends OnMultiClickListener {
            private ModuleViewHolder mModuleViewHolder;

            ViewClickListener(ModuleViewHolder moduleViewHolder) {
                mModuleViewHolder = moduleViewHolder;
            }

            @Override
            protected void onMultiClick(View v) {
                int position = mModuleViewHolder.getAdapterPosition();
                ModuleEntity moduleEntity = mModuleEntities.get(position);
                if (!moduleEntity.enable) {
                    ToastUtil.showNormalToast(MainActivity.this, R.string.sorry_no_permission);
                    return;
                }
                Intent intent = null;
                switch (moduleEntity.nameId) {
                    case R.string.home_function_name_beauty: {
                        intent = new Intent(MainActivity.this, FUBeautyActivity.class);
                    }
                    break;
                    case R.string.home_function_name_makeup: {
                        intent = new Intent(MainActivity.this, FUMakeupActivity.class);
                    }
                    break;
                    case R.string.home_function_name_light_makeup: {
                        intent = new Intent(MainActivity.this, LightMakeupActivity.class);
                    }
                    break;
                    case R.string.home_function_name_hair: {
                        intent = new Intent(MainActivity.this, FUHairActivity.class);
                    }
                    break;
                    case R.string.home_function_name_poster_face: {
                        intent = new Intent(MainActivity.this, PosterChangeListActivity.class);
                    }
                    break;
                    case R.string.home_function_name_animoji: {
                        intent = new Intent(MainActivity.this, FUAnimojiActivity.class);
                    }
                    break;
                    case R.string.home_function_name_beauty_body: {
                        intent = new Intent(MainActivity.this, BeautifyBodyActivity.class);
                    }
                    break;
                    case R.string.home_function_name_human_avatar: {
                        intent = new Intent(MainActivity.this, PtaActivity.class);
                        intent.putExtra(FUEffectActivity.EFFECT_TYPE, moduleEntity.effectType);
                    }
                    break;
                    case R.string.home_function_name_sticker:
                    case R.string.home_function_name_ar:
                    case R.string.home_function_name_expression:
                    case R.string.home_function_name_music_filter:
                    case R.string.home_function_name_face_warp:
                    case R.string.home_function_name_portrait_segment:
                    case R.string.home_function_name_action_recognition:
                    case R.string.home_function_name_big_head:
                    case R.string.home_function_name_gesture: {
                        intent = new Intent(MainActivity.this, FUEffectActivity.class);
                        intent.putExtra(FUEffectActivity.EFFECT_TYPE, moduleEntity.effectType);
                    }
                    break;
                    case R.string.home_function_name_bg_seg_green: {
                        intent = new Intent(MainActivity.this, BgSegGreenActivity.class);
                    }
                    break;
                    default:
                }
                if (intent != null) {
                    MainActivity.this.startActivity(intent);
                }
            }
        }

        class ClassificationViewHolder extends RecyclerView.ViewHolder {
            TextView tvClassificationName;

            ClassificationViewHolder(View itemView) {
                super(itemView);
                tvClassificationName = itemView.findViewById(R.id.tv_classification_name);
            }
        }

        class ModuleViewHolder extends RecyclerView.ViewHolder {
            ImageView ivModuleIcon;
            TextView tvModuleName;

            ModuleViewHolder(View itemView) {
                super(itemView);
                ivModuleIcon = itemView.findViewById(R.id.home_recycler_img);
                tvModuleName = itemView.findViewById(R.id.home_recycler_text);
            }
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            BannerViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private static class ModuleEntity {
        private static final int UI_TYPE_BANNER = 147;
        private static final int UI_TYPE_MODULE = 201;
        private static final int UI_TYPE_CLASSIFICATION = 430;

        private int iconId;
        private int nameId;
        private String authCode;
        private int effectType;
        private int uiType;
        private boolean enable;

        ModuleEntity(int uiType) {
            this.uiType = uiType;
        }

        ModuleEntity(int nameId, int uiType) {
            this.nameId = nameId;
            this.uiType = uiType;
        }

        ModuleEntity(int iconId, int nameId, String authCode, int effectType, int uiType) {
            this.iconId = iconId;
            this.nameId = nameId;
            this.authCode = authCode;
            this.effectType = effectType;
            this.uiType = uiType;
        }
    }

}