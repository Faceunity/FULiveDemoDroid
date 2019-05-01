package com.faceunity.fulivedemo;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.entity.MagicPhotoEntity;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.greendao.GreenDaoUtils;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 异图驱动页
 */
public class FUMagicDriveActivity extends FUBaseActivity {
    private static final int REQ_PHOTO = 561;
    public static final String MAGIC_LIST = "magic_list";
    private static final int REQ_DELETE = 123;
    private static final int REQ_CREATE = 456;
    private RecyclerAdapter mAdapter;
    private int mDefaultModelCount;
    private RecyclerClickListener mOnItemClickListener;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        View bottomView = mBottomViewStub.inflate();
        RecyclerView recyclerView = bottomView.findViewById(R.id.fu_effect_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new RecyclerAdapter(queryDatabase());
        mOnItemClickListener = new RecyclerClickListener();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setItemSelected(0);
        ToastUtil.makeNormalToast(this, R.string.magic_toast_action).show();
        mDefaultModelCount = FileUtils.getDefaultMagicPhotoCount(this);
    }

    @Override
    protected boolean showAutoFocus() {
        return false;
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .inputTextureType(1)
                .maxFaces(1)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int lastPosition = mOnItemClickListener.mLastPosition;
                MagicPhotoEntity magicPhotoEntity = mAdapter.getItem(lastPosition);
                if (magicPhotoEntity == null || MagicPhotoEntity.OPERATION_DELETE.equals(magicPhotoEntity.getImagePath())
                        || MagicPhotoEntity.OPERATION_ADD.equals(magicPhotoEntity.getImagePath())) {
                    magicPhotoEntity = mAdapter.getItem(0);
                    mOnItemClickListener.mLastPosition = 0;
                    lastPosition = 0;
                }
                mAdapter.setItemSelected(lastPosition);
                mFURenderer.setMagicPhoto(magicPhotoEntity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_PHOTO) {
                String filePath = MiscUtil.getFileAbsolutePath(this, data.getData());
                Intent intent = new Intent(this, FUMagicGenActivity.class);
                intent.putExtra(FUMagicGenActivity.MODEL_PATH, filePath);
                startActivityForResult(intent, REQ_CREATE);
            } else if (requestCode == REQ_CREATE || requestCode == REQ_DELETE) {
                List<MagicPhotoEntity> magicPhotoEntities = queryDatabase();
                mAdapter.replaceAll(magicPhotoEntities);
                if (requestCode == REQ_CREATE) {
                    mOnItemClickListener.mLastPosition = magicPhotoEntities.size() - 3;
                }
            }
        }
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        super.onCameraChange(currentCameraType, cameraOrientation);
        boolean isFront = currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT;
        mFURenderer.setIsFrontCamera(isFront);
    }

    private List<MagicPhotoEntity> queryDatabase() {
        List<MagicPhotoEntity> magicPhotoEntities = GreenDaoUtils.getInstance().getDaoSession().getMagicPhotoEntityDao().loadAll();
        MagicPhotoEntity magicPhotoEntityAdd = new MagicPhotoEntity(0, 0, null, null, MagicPhotoEntity.OPERATION_ADD);
        MagicPhotoEntity magicPhotoEntityDelete = new MagicPhotoEntity(0, 0, null, null, MagicPhotoEntity.OPERATION_DELETE);
        magicPhotoEntities.add(magicPhotoEntityAdd);
        magicPhotoEntities.add(magicPhotoEntityDelete);
        return magicPhotoEntities;
    }

    private class RecyclerAdapter extends BaseRecyclerAdapter<MagicPhotoEntity> {

        RecyclerAdapter(@NonNull List<MagicPhotoEntity> data) {
            super(data, R.layout.layout_effect_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, MagicPhotoEntity item) {
            ImageView iv = viewHolder.getViewById(R.id.effect_recycler_img);
            String imagePath = item.getImagePath();
            if (MagicPhotoEntity.OPERATION_ADD.equals(imagePath)) {
                Glide.with(FUMagicDriveActivity.this)
                        .load(R.drawable.magic_photo_add)
                        .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                        .into(iv);
            } else if (MagicPhotoEntity.OPERATION_DELETE.equals(imagePath)) {
                Glide.with(FUMagicDriveActivity.this)
                        .load(R.drawable.magic_photo_delete)
                        .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                        .into(iv);
            } else {
                Glide.with(FUMagicDriveActivity.this)
                        .load(item.getImagePath())
                        .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                        .into(iv);
            }
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, MagicPhotoEntity data, boolean selected) {
            if (selected) {
                viewHolder.setBackground(R.id.effect_recycler_img, R.drawable.effect_select);
            } else {
                viewHolder.setBackground(R.id.effect_recycler_img, 0);
            }
        }
    }

    private class RecyclerClickListener implements BaseRecyclerAdapter.OnItemClickListener<MagicPhotoEntity> {
        private int mLastPosition = 0;

        @Override
        public void onItemClick(BaseRecyclerAdapter<MagicPhotoEntity> adapter, View view, int position) {
            if (mLastPosition == position) {
                return;
            }
            int itemCount = adapter.getItemCount();
            if (position == itemCount - 2) {
                // 打开相册
                Intent intentPhoto = new Intent();
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/*");
                intentPhoto.setAction(Build.VERSION.SDK_INT < 19 ? Intent.ACTION_GET_CONTENT : Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intentPhoto, REQ_PHOTO);
            } else if (position == itemCount - 1) {
                // 删除模型
                Intent intent = new Intent(FUMagicDriveActivity.this, MagicPhotoDelActivity.class);
                ArrayList<MagicPhotoEntity> magicPhotoEntities = new ArrayList<>();
                int size = mAdapter.getData().size() - 2;
                // 排除新加和删除按钮，预置的不出现
                for (int i = mDefaultModelCount; i < size; i++) {
                    magicPhotoEntities.add(mAdapter.getData().get(i));
                }
                intent.putExtra(MAGIC_LIST, magicPhotoEntities);
                FUMagicDriveActivity.this.startActivityForResult(intent, REQ_DELETE);
            } else {
                // 切换模型
                mLastPosition = position;
                MagicPhotoEntity magicPhotoEntity = adapter.getItem(position);
                mFURenderer.setMagicPhoto(magicPhotoEntity);
            }
        }
    }

}
