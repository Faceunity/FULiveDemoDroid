package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.SQLException;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.entity.LivePhoto;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.database.DatabaseOpenHelper;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 表情动图驱动页
 *
 * @author Richie on 2019.04.12
 */
public class LivePhotoDriveActivity extends FUBaseActivity {
    private static final String TAG = "LivePhotoDriveActivity";
    private static final String OPERATION_ADD = "add";
    private static final int REQ_PHOTO = 561; // 打开相册
    private static final int REQ_CREATE = 456; // 创建完成
    private static final int REQ_UPDATE = 789; // 编辑完成
    private RecyclerAdapter mAdapter;
    private RecyclerClickListener mOnItemClickListener;
    private Group mGroupOperation;
    private List<LivePhoto> mDefaultLivePhotos;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_livephoto_drive);
        View bottomView = mBottomViewStub.inflate();
        OnViewClickListener onViewClickListener = new OnViewClickListener();
        bottomView.findViewById(R.id.fl_edit_effect).setOnClickListener(onViewClickListener);
        bottomView.findViewById(R.id.fl_delete_effect).setOnClickListener(onViewClickListener);
        mGroupOperation = findViewById(R.id.group_live_photo_operation);
        mGroupOperation.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams btnParams = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        btnParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        btnParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x204);
        mTakePicBtn.setLayoutParams(btnParams);

        mRecyclerView = bottomView.findViewById(R.id.rv_live_photo);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mDefaultLivePhotos = LivePhoto.getDefaultLivePhotos(this);
        List<LivePhoto> livePhotos = queryLivePhotos();
        mAdapter = new RecyclerAdapter(livePhotos);
        mOnItemClickListener = new RecyclerClickListener();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemSelected(0);

        ToastUtil.makeNormalToast(this, R.string.live_photo_toast_action).show();
    }

    @Override
    protected boolean showAutoFocus() {
        return false;
    }

    @Override
    protected FURenderer initFURenderer() {
        int frontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        return new FURenderer
                .Builder(this)
                .inputTextureType(1)
                .inputImageOrientation(frontCameraOrientation)
                .maxFaces(1)
                .setNeedFaceBeauty(false)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int lastPosition = mOnItemClickListener.mLastPosition;
                LivePhoto livePhoto = mAdapter.getItem(lastPosition);
                if (livePhoto == null || OPERATION_ADD.equals(livePhoto.getTemplateImagePath())) {
                    livePhoto = mAdapter.getItem(0);
                    mOnItemClickListener.mLastPosition = 0;
                    lastPosition = 0;
                }
                mAdapter.setItemSelected(lastPosition);
                mRecyclerView.scrollToPosition(lastPosition);
                mFURenderer.setLivePhoto(livePhoto);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_PHOTO) {
                // 打开相册
                String filePath = MiscUtil.getFileAbsolutePath(this, data.getData());
                Intent intent = new Intent(this, LivePhotoMakeActivity.class);
                intent.putExtra(LivePhotoMakeActivity.MODEL_PATH, filePath);
                startActivityForResult(intent, REQ_CREATE);
            } else if (requestCode == REQ_CREATE || requestCode == REQ_UPDATE) {
                // 创建模型 编辑模型
                int selPos = mOnItemClickListener.mLastPosition;
                List<LivePhoto> livePhotos = queryLivePhotos();
                mAdapter.replaceAll(livePhotos);
                // 创建时选中最后一个，排除增加按钮。编辑时选中当前按钮
                if (requestCode == REQ_CREATE) {
                    mOnItemClickListener.mLastPosition = livePhotos.size() - 2;
                }
                mAdapter.setItemSelected(selPos);
                mRecyclerView.scrollToPosition(mOnItemClickListener.mLastPosition);
                mGroupOperation.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCameraChange(int cameraType, int cameraOrientation) {
        super.onCameraChange(cameraType, cameraOrientation);
        boolean isFront = cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT;
        mFURenderer.setIsFrontCamera(isFront);
    }

    private List<LivePhoto> queryLivePhotos() {
        List<LivePhoto> livePhotos = new ArrayList<>(8);
        LivePhoto addLivePhoto = new LivePhoto(0, 0, null, null, OPERATION_ADD, null, null, null);
        livePhotos.addAll(mDefaultLivePhotos);
        List<LivePhoto> dbLivePhotos = DatabaseOpenHelper.getInstance().getLivePhotoDao().queryAll();
        livePhotos.addAll(dbLivePhotos);
        livePhotos.add(addLivePhoto);
        return livePhotos;
    }

    private class OnViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.fl_edit_effect: {
                    SparseArray<LivePhoto> selectedItems = mAdapter.getSelectedItems();
                    if (selectedItems.size() > 0) {
                        LivePhoto livePhoto = selectedItems.valueAt(0);
                        Intent intent = new Intent(LivePhotoDriveActivity.this, LivePhotoMakeActivity.class);
                        intent.putExtra(LivePhotoMakeActivity.EDIT_LIVE_PHOTO, livePhoto);
                        intent.putExtra(LivePhotoMakeActivity.MODEL_PATH, livePhoto.getTemplateImagePath());
                        startActivityForResult(intent, REQ_UPDATE);
                    }
                }
                break;
                case R.id.fl_delete_effect: {
                    ConfirmDialogFragment.newInstance(getString(R.string.dialog_confirm_delete), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            SparseArray<LivePhoto> selectedItems = mAdapter.getSelectedItems();
                            if (selectedItems.size() > 0) {
                                LivePhoto livePhoto = selectedItems.valueAt(0);
                                try {
                                    DatabaseOpenHelper.getInstance().getLivePhotoDao().delete(livePhoto);
                                    FileUtils.deleteFile(new File(livePhoto.getTemplateImagePath()));
                                    mAdapter.remove(livePhoto);
                                    ToastUtil.makeNormalToast(LivePhotoDriveActivity.this, getString(R.string.toast_delete_succeed)).show();
                                    // 删除后选中第一个
                                    mAdapter.setItemSelected(0);
                                    mRecyclerView.scrollToPosition(0);
                                    mOnItemClickListener.onItemClick(mAdapter, null, 0);
                                } catch (SQLException e) {
                                    Log.e(TAG, "delete model error", e);
                                    ToastUtil.makeNormalToast(LivePhotoDriveActivity.this, getString(R.string.toast_delete_failed)).show();
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                        }
                    }).show(getSupportFragmentManager(), "ConfirmDialogFragment");
                }
                break;
                default:
            }
        }
    }

    private class RecyclerAdapter extends BaseRecyclerAdapter<LivePhoto> {

        RecyclerAdapter(@NonNull List<LivePhoto> data) {
            super(data, R.layout.layout_effect_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, LivePhoto item) {
            ImageView iv = viewHolder.getViewById(R.id.effect_recycler_img);
            String imagePath = item.getTemplateImagePath();
            if (OPERATION_ADD.equals(imagePath)) {
                Glide.with(LivePhotoDriveActivity.this)
                        .load(R.drawable.live_photo_add)
                        .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                        .into(iv);
            } else {
                Glide.with(LivePhotoDriveActivity.this)
                        .load(item.getTemplateImagePath())
                        .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                        .into(iv);
            }
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, LivePhoto data, boolean selected) {
            viewHolder.setBackground(R.id.effect_recycler_img, selected ? R.drawable.effect_select : 0);
        }
    }

    private class RecyclerClickListener implements BaseRecyclerAdapter.OnItemClickListener<LivePhoto> {
        private int mLastPosition = 0;

        @Override
        public void onItemClick(BaseRecyclerAdapter<LivePhoto> adapter, View view, int position) {
            if (mLastPosition == position) {
                return;
            }

            int itemCount = adapter.getItemCount();
            if (position == itemCount - 1) {
                // 打开相册
                Intent intentPhoto = new Intent();
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/*");
                intentPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(intentPhoto, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo == null) {
                    intentPhoto.setAction(Intent.ACTION_GET_CONTENT);
                    intentPhoto.removeCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(intentPhoto, REQ_PHOTO);
            } else {
                // 切换模型
                if (position < mDefaultLivePhotos.size()) {
                    // 预置的模型，不显示操作按钮
                    mGroupOperation.setVisibility(View.GONE);
                } else {
                    mGroupOperation.setVisibility(View.VISIBLE);
                }
                mLastPosition = position;
                LivePhoto livePhoto = adapter.getItem(position);
                mFURenderer.setLivePhoto(livePhoto);
            }
        }
    }

}
