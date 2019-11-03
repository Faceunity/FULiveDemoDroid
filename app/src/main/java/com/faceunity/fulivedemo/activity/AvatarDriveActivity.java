package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.entity.AvatarModel;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.database.DatabaseOpenHelper;
import com.faceunity.fulivedemo.entity.AvatarFaceAspect;
import com.faceunity.fulivedemo.entity.AvatarFaceHelper;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.ui.fragment.AvatarMakeFragment;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.ColorConstant;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Avatar 驱动页和生成页，使用 fragment 显示
 *
 * @author Richie on 2019.03.20
 */
public class AvatarDriveActivity extends FUBaseActivity implements FURenderer.OnBundleLoadCompleteListener {
    private static final double[] NORMAL_XYZ = new double[]{0, 0, 0};
    public static final String AVATAR_MODEL_LIST = "avatar_model_list";
    private static final int REQ_DELETE = 123;
    private static final String TAG = "AvatarDriveActivity";
    private AvatarModelAdapter mAvatarModelAdapter;
    private FrameLayout mFlFragment;
    private TextView mTvNewAvatar;
    private FrameLayout mflNewAvatar;
    private volatile boolean mInMakeMode;
    private volatile boolean mSnapShot;
    private AvatarModel mAvatarModel;
    private RecyclerView mRvAvatarModel;
    private AvatarMakeFragment mAvatarMakeFragment;
    // 稍后要设置的头发参数
    private Map<String, double[]> mPendingHairColors = new HashMap<>(8);
    // 选中的模型位置，默认从第一个开始
    private int mSelectPos = 1;
    private BitmapUtil.OnReadBitmapListener mSnapshotBitmapListener = new BitmapUtil.OnReadBitmapListener() {

        @Override
        public void onReadBitmapListener(Bitmap bitmap) {
            // 截取上面的一部分作为缩略图
            Bitmap cropped = BitmapUtil.clip(bitmap, 0, 0, bitmap.getWidth(), (int) (0.93f * bitmap.getWidth()), true);
            String path = MiscUtil.saveBitmap(cropped, FileUtils.getThumbnailDir(AvatarDriveActivity.this)
                    .getAbsolutePath(), FileUtils.getUUID32() + MiscUtil.IMAGE_FORMAT_JPG);
            AvatarModel clone = mAvatarModel.cloneIt();
            clone.setIconPath(path);
            clone.setParamJson(AvatarFaceHelper.array2Config());
            clone.setUiJson(AvatarFaceHelper.uiConfig2Array());
            Log.i(TAG, "onReadBitmapListener: save " + clone);
            try {
                DatabaseOpenHelper.getInstance().getAvatarModelDao().insertOrUpdate(clone);
                mFURenderer.recomputeFaceup();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.makeFineToast(AvatarDriveActivity.this, getString(R.string.avatar_save_succeed), R.drawable.icon_confirm).show();
                        resetAllData();
                        mAvatarModelAdapter.replaceAll(getAllAvatars());
                        boolean isCreate = (boolean) mTvNewAvatar.getTag();
                        int pos;
                        if (isCreate) {
                            pos = mAvatarModelAdapter.getItemCount() - 1;
                        } else {
                            pos = mSelectPos;
                        }
                        mSelectPos = pos;
                        mAvatarModel = mAvatarModelAdapter.getItem(pos);
                        mAvatarModelAdapter.setItemSelected(pos);
                        mRvAvatarModel.scrollToPosition(pos);
                        setButtonText(false);
                        showDrivePage();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "save avatar: ", e);
            }
        }
    };

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mvpMatrix, long timeStamp) {
        int fuTextureId = 0;
        if (mInMakeMode) {
            // 捏脸模式
            fuTextureId = mFURenderer.onDrawFrameAvatar(cameraNV21Byte, cameraWidth, cameraHeight);
            if (mSnapShot) {
                mSnapShot = false;
                BitmapUtil.glReadBitmap(fuTextureId, mvpMatrix, mCameraRenderer.getMvpMatrix(), cameraHeight,
                        cameraWidth, mSnapshotBitmapListener, false);
            }
        } else {
            // 驱动模式
            if (isDoubleInputType) {
                fuTextureId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
            } else if (cameraNV21Byte != null) {
                if (mFuNV21Byte == null || mFuNV21Byte.length != cameraNV21Byte.length) {
                    mFuNV21Byte = new byte[cameraNV21Byte.length];
                }
                System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
                fuTextureId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
            }
            sendRecordingData(fuTextureId, mvpMatrix, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
            checkPic(fuTextureId, mvpMatrix, cameraHeight, cameraWidth);
        }
        return fuTextureId;
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isMakeAvatarViewVisible()) {
                    if (!TextUtils.isEmpty(AvatarFaceHelper.sFaceHairBundlePath)) {
                        mFURenderer.loadAvatarHair(AvatarFaceHelper.sFaceHairBundlePath);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isMakeAvatarViewVisible()) {
            mAvatarMakeFragment.backPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void showDrivePage() {
        mFURenderer.unloadAvatarBackground();
        mFURenderer.setAvatarTranslate(NORMAL_XYZ);
        mFURenderer.setAvatarHairTranslate(NORMAL_XYZ);
        mFURenderer.setAvatarScale(AvatarMakeFragment.NORMAL_SCALE);
        mFURenderer.setAvatarHairScale(AvatarMakeFragment.NORMAL_SCALE);
        mFlFragment.setVisibility(View.INVISIBLE);
        mClOperationView.setVisibility(View.VISIBLE);
        AvatarModel avatarModel = mAvatarModelAdapter.getSelectedItems().valueAt(0);
        if (avatarModel != null) {
            int index = mAvatarModelAdapter.indexOf(avatarModel);
            if (index > 0) {
                setAvatarConfig(avatarModel);
            } else {
                // 禁用
                mFURenderer.onEffectSelected(EffectEnum.EffectNone.effect());
                mFURenderer.loadAvatarHair(null);
            }
        }
        mInMakeMode = false;
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }

    public void setSnapShot() {
        mSnapShot = true;
    }

    private boolean isMakeAvatarViewVisible() {
        return mAvatarMakeFragment != null && mFlFragment.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onCreate() {
        resetAllData();
        getWindow().setBackgroundDrawable(null);
        mFlFragment = findViewById(R.id.fl_fragment);
        mBottomViewStub.setLayoutResource(R.layout.activity_avatar_drive);
        mBottomViewStub.inflate();

        ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        btnParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        btnParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x200);
        mTakePicBtn.setLayoutParams(btnParams);

        mTvNewAvatar = findViewById(R.id.tv_new_avatar);
        // indicate whether creates model
        ViewClickListener viewClickListener = new ViewClickListener();
        mflNewAvatar = findViewById(R.id.fl_new_model);
        mflNewAvatar.setOnClickListener(viewClickListener);
        setButtonText(true);
        findViewById(R.id.fl_delete_model).setOnClickListener(viewClickListener);
        mRvAvatarModel = findViewById(R.id.rv_avatar);
        mRvAvatarModel.setHasFixedSize(true);
        mRvAvatarModel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) mRvAvatarModel.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvAvatarModel.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x10),
                getResources().getDimensionPixelSize(R.dimen.x19), getResources().getDimensionPixelSize(R.dimen.x24), 0));
        mAvatarModelAdapter = new AvatarModelAdapter(getAllAvatars());
        mAvatarModelAdapter.setOnItemClickListener(new AvatarModelClickListener());
        // 默认选中预置的模型
        mAvatarModelAdapter.setItemSelected(1);
        mAvatarModel = mAvatarModelAdapter.getItem(1);
        mRvAvatarModel.setAdapter(mAvatarModelAdapter);

        ColorConstant.init(this);
    }

    private List<AvatarModel> getAllAvatars() {
        List<AvatarModel> avatarModels = new ArrayList<>(16);
        List<AvatarModel> defaultAvatars = initDefaultAvatar();
        avatarModels.addAll(defaultAvatars);
        List<AvatarModel> dbAvatarModels = queryAvatarModel();
        avatarModels.addAll(dbAvatarModels);
        return avatarModels;
    }

    private List<AvatarModel> queryAvatarModel() {
        return DatabaseOpenHelper.getInstance().getAvatarModelDao().queryAll();
    }

    @Override
    protected FURenderer initFURenderer() {
        int frontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        return new FURenderer
                .Builder(this)
                .inputTextureType(1)
                .maxFaces(1)
                .inputImageOrientation(frontCameraOrientation)
                .defaultEffect(EffectEnum.AVATAR_HEAD.effect())
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setOnBundleLoadCompleteListener(this)
                .build();
    }

    private List<AvatarModel> initDefaultAvatar() {
        List<AvatarModel> avatarModels = new ArrayList<>(2);
        avatarModels.add(new AvatarModel(R.drawable.demo_avatar_icon_cancel, false));
        AvatarModel avatarMale = new AvatarModel(R.drawable.demo_icon_template_male, true);
        // 默认模型有个默认头发，鼻子修正过
        avatarMale.setParamJson(AvatarFaceHelper.array2Config());
        avatarModels.add(avatarMale);
        return avatarModels;
    }

    private void showFragment() {
        if (mFlFragment.getVisibility() != View.VISIBLE) {
            mFlFragment.setVisibility(View.VISIBLE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (mAvatarMakeFragment == null) {
            mAvatarMakeFragment = new AvatarMakeFragment();
            transaction.add(R.id.fl_fragment, mAvatarMakeFragment, AvatarMakeFragment.TAG);
        } else {
            transaction.show(mAvatarMakeFragment);
        }
        transaction.commit();

        mAvatarMakeFragment.setAvatarModel(mAvatarModel);
        mAvatarMakeFragment.startMakeAnimation();
    }

    @Override
    public void onBundleLoadComplete(int what) {
        Log.d(TAG, "onBundleLoadComplete() called with: what = [" + what + "]");
        if (what == FURenderer.ITEM_ARRAYS_EFFECT_INDEX) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isMakeAvatarViewVisible()) {
                        mAvatarMakeFragment.transformModelHead();
                        mFURenderer.enterFaceShape();
                        Set<Map.Entry<String, Float>> aspectEntries = AvatarFaceHelper.FACE_ASPECT_MAP.entrySet();
                        for (Map.Entry<String, Float> aspectEntry : aspectEntries) {
                            mFURenderer.fuItemSetParamFaceup(aspectEntry.getKey(), aspectEntry.getValue());
                        }
                        Set<Map.Entry<String, double[]>> avatarColorEntries = AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.entrySet();
                        for (Map.Entry<String, double[]> avatarColorEntry : avatarColorEntries) {
                            String key = avatarColorEntry.getKey();
                            key = key.substring(0, key.length() - 1);
                            mFURenderer.fuItemSetParamFaceColor(key, avatarColorEntry.getValue());
                        }
                        Set<Map.Entry<String, Float>> customEntries = AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.entrySet();
                        for (Map.Entry<String, Float> customEntry : customEntries) {
                            Float value = customEntry.getValue();
                            if (value != 0) {
                                mFURenderer.fuItemSetParamFaceup(customEntry.getKey(), value);
                            }
                        }
                    } else {
                        setAvatarConfig(mAvatarModelAdapter.getSelectedItems().valueAt(0));
                    }
                }
            });
        } else if (what == FURenderer.ITEM_ARRAYS_AVATAR_HAIR) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isMakeAvatarViewVisible()) {
                        mAvatarMakeFragment.showLoadingView(false);
                    }
                    // 如果选择页显示，那么改变 y 位置
                    mFURenderer.enterFaceShape();
                    if (isMakeAvatarViewVisible()) {
                        mAvatarMakeFragment.transformModelHair();
                        Set<Map.Entry<String, double[]>> avatarColorEntries = AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.entrySet();
                        for (Map.Entry<String, double[]> avatarColorEntry : avatarColorEntries) {
                            String key = avatarColorEntry.getKey();
                            key = key.substring(0, key.length() - 1);
                            mFURenderer.fuItemSetParamFaceColor(key, avatarColorEntry.getValue());
                        }
                    } else {
                        if (mPendingHairColors.size() > 0) {
                            Set<Map.Entry<String, double[]>> entries = mPendingHairColors.entrySet();
                            for (Map.Entry<String, double[]> entry : entries) {
                                mFURenderer.fuItemSetParamFaceColor(entry.getKey(), entry.getValue());
                            }
                            mPendingHairColors.clear();
                        }
                        mFURenderer.recomputeFaceup();
                    }
                }
            });
        } else if (what == FURenderer.ITEM_ARRAYS_AVATAR_BACKGROUND) {
            // 解决没有调整好的鼻子问题，其实可以放在 bundle 里的
            List<AvatarFaceAspect> defaultNose = AvatarFaceHelper.getDefaultNose();
            mFURenderer.enterFaceShape();
            for (AvatarFaceAspect avatarFaceAspect : defaultNose) {
                mFURenderer.fuItemSetParamFaceup(avatarFaceAspect.getName(), avatarFaceAspect.getLevel());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_DELETE) {
            mAvatarModelAdapter.replaceAll(getAllAvatars());
            int index = mAvatarModelAdapter.indexOf(mAvatarModel);
            if (index < 0) {
                mAvatarModelAdapter.setItemSelected(1);
                mRvAvatarModel.scrollToPosition(1);
                setButtonText(true);
            } else {
                mAvatarModelAdapter.setItemSelected(index);
                mRvAvatarModel.scrollToPosition(index);
                if (index > 1) {
                    setButtonText(false);
                } else {
                    setButtonText(true);
                }
            }
            mAvatarModel = mAvatarModelAdapter.getSelectedItems().valueAt(0);
        }
    }

    public void resetAllData() {
        AvatarFaceHelper.FACE_ASPECT_MAP.clear();
        AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.clear();
        AvatarFaceHelper.sFaceHairBundlePath = AvatarFaceHelper.DEFAULT_HAIR_PATH;
    }

    // 四步走 进入捏脸 --> 清除全部参数 --> 设置捏脸参数 --> 保存
    private void setAvatarConfig(AvatarModel avatarModel) {
        if (avatarModel == null) {
            return;
        }

        String configJson = avatarModel.getParamJson();
        Log.d(TAG, "setAvatarConfig: configJson " + configJson);
        if (configJson != null) {
            List<AvatarFaceAspect> avatarFaceAspects = AvatarFaceHelper.config2Array(configJson);
            if (avatarFaceAspects != null) {
                if (avatarModel.isDefault()) {
                    avatarFaceAspects.addAll(AvatarFaceHelper.getDefaultNose());
                }
                resetAllData();
                mFURenderer.enterFaceShape();
                mFURenderer.clearFaceShape();
                for (AvatarFaceAspect avatarFaceAspect : avatarFaceAspects) {
                    double[] color = avatarFaceAspect.getColor();
                    if (color != null) {
                        // 延迟设置参数，以免头发道具没加载完成导致没效果
                        if (color.length > 3) {
                            mPendingHairColors.put(avatarFaceAspect.getName(), color);
                        } else {
                            mFURenderer.fuItemSetParamFaceColor(avatarFaceAspect.getName(), color);
                        }
                    } else {
                        mFURenderer.fuItemSetParamFaceup(avatarFaceAspect.getName(), avatarFaceAspect.getLevel());
                    }
                    if (avatarFaceAspect.getBundlePath() != null) {
                        mFURenderer.loadAvatarHair(avatarFaceAspect.getBundlePath());
                    }
                }
                mFURenderer.recomputeFaceup();
            }
        } else {
            resetAllData();
            mFURenderer.enterFaceShape();
            mFURenderer.clearFaceShape();
            mFURenderer.recomputeFaceup();
        }
    }

    private void setButtonText(boolean isCreate) {
        mflNewAvatar.setVisibility(View.VISIBLE);
        mTvNewAvatar.setText(isCreate ? R.string.new_avatar_model : R.string.edit_avatar_model);
        mTvNewAvatar.setTag(isCreate);
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                // enter make up mode
                case R.id.fl_new_model: {
                    showFragment();
                    mClOperationView.setVisibility(View.INVISIBLE);
                    mInMakeMode = true;
                    mFURenderer.enterFaceShape();
                    // create or edit model
                    boolean isCreate = (boolean) mTvNewAvatar.getTag();
                    if (isCreate) {
                        mFURenderer.clearFaceShape();
                    }
                    mFURenderer.loadAvatarBackground();
                    if (!mFURenderer.isAvatarLoaded()) {
                        mFURenderer.onEffectSelected(EffectEnum.AVATAR_HEAD.effect());
                    }
                    if (!mFURenderer.isAvatarMakeupItemLoaded()) {
                        List<AvatarFaceAspect> avatarFaceAspects = AvatarFaceHelper.config2Array(mAvatarModel.getParamJson());
                        if (avatarFaceAspects != null) {
                            for (AvatarFaceAspect avatarFaceAspect : avatarFaceAspects) {
                                String bundlePath = avatarFaceAspect.getBundlePath();
                                if (bundlePath != null) {
                                    mFURenderer.loadAvatarHair(bundlePath);
                                    break;
                                }
                            }
                        } else {
                            mFURenderer.loadAvatarHair(AvatarFaceHelper.sFaceHairBundlePath);
                        }
                    }
                }
                break;
                case R.id.fl_delete_model: {
                    Intent intent = new Intent(AvatarDriveActivity.this, AvatarDeleteActivity.class);
                    intent.putParcelableArrayListExtra(AVATAR_MODEL_LIST, new ArrayList<>(queryAvatarModel()));
                    startActivityForResult(intent, REQ_DELETE);
                }
                break;
                default:
            }
        }
    }

    private class AvatarModelClickListener implements BaseRecyclerAdapter.OnItemClickListener<AvatarModel> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<AvatarModel> adapter, View view, int position) {
            mSelectPos = position;
            if (position == 0) {
                mflNewAvatar.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                setButtonText(true);
            } else {
                setButtonText(false);
            }

            if (position == 0) {
                mFURenderer.onEffectSelected(EffectEnum.EffectNone.effect());
                mFURenderer.loadAvatarHair(null);
                return;
            }

            AvatarModel adapterItem = adapter.getItem(position);
            mAvatarModel = adapterItem;
            if (mFURenderer.isAvatarLoaded()) {
                setAvatarConfig(adapterItem);
            } else {
                mFURenderer.onEffectSelected(EffectEnum.AVATAR_HEAD.effect());
            }
        }
    }

    private class AvatarModelAdapter extends BaseRecyclerAdapter<AvatarModel> {

        public AvatarModelAdapter(@NonNull List<AvatarModel> data) {
            super(data, R.layout.recycler_list_avatar);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, AvatarModel item) {
            int iconId = item.getIconId();
            String iconPath = item.getIconPath();
            if (!TextUtils.isEmpty(iconPath)) {
                viewHolder.setImageBitmap(R.id.iv_avatar_item_icon, BitmapUtil.decodeSampledBitmapFromFile(
                        iconPath, getResources().getDimensionPixelSize(R.dimen.x120), getResources().getDimensionPixelSize(R.dimen.x120)));
                ImageView imageView = viewHolder.getViewById(R.id.iv_avatar_item_icon);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x6)));
                Glide.with(AvatarDriveActivity.this).applyDefaultRequestOptions(requestOptions).load(iconPath).into(imageView);
            } else if (iconId != 0) {
                viewHolder.setImageResource(R.id.iv_avatar_item_icon, iconId);
            }
            viewHolder.setVisibility(R.id.iv_avatar_item_example, item.isDefault() ? android.view.View.VISIBLE : View.GONE);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, AvatarModel data, boolean selected) {
            viewHolder.setViewSelected(R.id.iv_avatar_item_bg, selected);
        }

        @Override
        public int indexOf(@NonNull AvatarModel data) {
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).equals(data)) {
                    return i;
                }
            }
            return -1;
        }
    }

}