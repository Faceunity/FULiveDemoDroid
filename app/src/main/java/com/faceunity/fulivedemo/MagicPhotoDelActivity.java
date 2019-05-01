package com.faceunity.fulivedemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.entity.MagicPhotoEntity;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.greendao.GreenDaoUtils;
import com.faceunity.greendao.MagicPhotoEntityDao;
import com.faceunity.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 表情动图道具删除页
 */
public class MagicPhotoDelActivity extends AppCompatActivity {
    private static final String TAG = "MagicPhotoDelActivity";
    private Button mBtnDelete;
    private Button mBtnAll;
    private boolean mIsDeleted;
    private DeleteMagicAdapter mDeleteMagicAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_photo_delete);
        ViewClickListener viewClickListener = new ViewClickListener();
        findViewById(R.id.iv_delete_back).setOnClickListener(viewClickListener);
        mEmptyView = findViewById(R.id.ll_empty_view);
        mBtnAll = findViewById(R.id.btn_delete_all);
        mBtnAll.setOnClickListener(viewClickListener);
        mBtnDelete = findViewById(R.id.btn_delete_bottom);
        mBtnDelete.setOnClickListener(viewClickListener);
        RecyclerView recyclerView = findViewById(R.id.rcv_delete_effect);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        int padding = getResources().getDimensionPixelSize(R.dimen.x19);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.addItemDecoration(new VHSpaceItemDecoration(padding, padding));
        final ArrayList<MagicPhotoEntity> magicPhotoEntities = getIntent().getParcelableArrayListExtra(FUMagicDriveActivity.MAGIC_LIST);
        mDeleteMagicAdapter = new DeleteMagicAdapter(magicPhotoEntities);
        recyclerView.setAdapter(mDeleteMagicAdapter);
        mBtnDelete.setEnabled(false);
        checkEmpty();
    }

    @Override
    public void onBackPressed() {
        if (mIsDeleted) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.iv_delete_back: {
                    onBackPressed();
                }
                break;
                case R.id.btn_delete_all: {
                    // 全选
                    int size = mDeleteMagicAdapter.mSelectedEntities.size();
                    if (size == mDeleteMagicAdapter.getItemCount()) {
                        mBtnAll.setText(R.string.magic_delete_all);
                        mBtnDelete.setEnabled(false);
                        mBtnDelete.setText(getResources().getString(R.string.magic_btn_delete));
                        mDeleteMagicAdapter.mSelectedEntities.clear();
                        mDeleteMagicAdapter.notifyDataSetChanged();
                    } else {
                        mBtnAll.setText(R.string.magic_btn_cancel);
                        mBtnDelete.setEnabled(true);
                        mBtnDelete.setText(getResources().getString(R.string.magic_btn_delete_, mDeleteMagicAdapter.getItemCount()));
                        mDeleteMagicAdapter.mSelectedEntities.addAll(mDeleteMagicAdapter.mMagicPhotoEntities);
                        mDeleteMagicAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case R.id.btn_delete_bottom: {
                    // 删除
                    ConfirmDialogFragment.newInstance(getString(R.string.dialog_confirm_delete), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            Set<MagicPhotoEntity> selectedEntities = mDeleteMagicAdapter.mSelectedEntities;
                            List<MagicPhotoEntity> toDelete = new ArrayList<>(selectedEntities.size());
                            toDelete.addAll(selectedEntities);
                            mBtnAll.setText(R.string.magic_delete_all);
                            mBtnDelete.setEnabled(false);
                            mBtnDelete.setText(getResources().getString(R.string.magic_btn_delete));
                            try {
                                MagicPhotoEntityDao magicPhotoEntityDao = GreenDaoUtils.getInstance().getDaoSession().getMagicPhotoEntityDao();
                                magicPhotoEntityDao.deleteInTx(toDelete);
                                for (MagicPhotoEntity magicPhotoEntity : toDelete) {
                                    FileUtils.deleteFile(new File(magicPhotoEntity.getImagePath()));
                                }
                                ToastUtil.makeNormalToast(MagicPhotoDelActivity.this, getString(R.string.toast_delete_succeed)).show();
                                mDeleteMagicAdapter.mSelectedEntities.clear();
                                for (MagicPhotoEntity magicPhotoEntity : toDelete) {
                                    mDeleteMagicAdapter.mMagicPhotoEntities.remove(magicPhotoEntity);
                                }
                                mDeleteMagicAdapter.notifyDataSetChanged();
                                checkEmpty();
                                mIsDeleted = true;
                            } catch (Exception e) {
                                Log.e(TAG, "delete photo:", e);
                                ToastUtil.makeNormalToast(MagicPhotoDelActivity.this, getString(R.string.toast_delete_failed)).show();
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

    private void checkEmpty() {
        if (mDeleteMagicAdapter.getItemCount() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private class DeleteMagicAdapter extends RecyclerView.Adapter<DeleteMagicAdapter.VH> {
        private List<MagicPhotoEntity> mMagicPhotoEntities;
        private Set<MagicPhotoEntity> mSelectedEntities;

        DeleteMagicAdapter(@NonNull List<MagicPhotoEntity> magicPhotoEntities) {
            mMagicPhotoEntities = magicPhotoEntities;
            mSelectedEntities = new HashSet<>(mMagicPhotoEntities.size());
        }

        @NonNull
        @Override
        public DeleteMagicAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_magic_delete, parent, false);
            final VH vh = new VH(view);
            vh.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    MagicPhotoEntity magicPhotoEntity = (MagicPhotoEntity) vh.itemView.getTag();
                    boolean selected = mSelectedEntities.contains(magicPhotoEntity);
                    if (selected) {
                        mSelectedEntities.remove(magicPhotoEntity);
                        vh.mIvMask.setVisibility(View.INVISIBLE);
                    } else {
                        vh.mIvMask.setVisibility(View.VISIBLE);
                        mSelectedEntities.add(magicPhotoEntity);
                    }
                    int size = mSelectedEntities.size();
                    if (size > 0) {
                        mBtnDelete.setEnabled(true);
                        mBtnDelete.setText(getResources().getString(R.string.magic_btn_delete_, size));
                    } else {
                        mBtnDelete.setEnabled(false);
                        mBtnDelete.setText(getResources().getString(R.string.magic_btn_delete));
                    }
                    if (size == getItemCount()) {
                        mBtnAll.setText(R.string.magic_btn_cancel);
                    } else {
                        mBtnAll.setText(R.string.magic_delete_all);
                    }
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DeleteMagicAdapter.VH holder, int position) {
            MagicPhotoEntity magicPhotoEntity = mMagicPhotoEntities.get(position);
            holder.itemView.setTag(magicPhotoEntity);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x8)));
            Glide.with(MagicPhotoDelActivity.this)
                    .load(magicPhotoEntity.getImagePath())
                    .apply(requestOptions).into(holder.mIvIcon);
            if (mSelectedEntities.contains(magicPhotoEntity)) {
                holder.mIvMask.setVisibility(View.VISIBLE);
            } else {
                holder.mIvMask.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return mMagicPhotoEntities.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView mIvIcon;
            ImageView mIvMask;

            public VH(View itemView) {
                super(itemView);
                mIvIcon = itemView.findViewById(R.id.iv_magic_photo);
                mIvMask = itemView.findViewById(R.id.iv_magic_mask);
            }
        }
    }
}
