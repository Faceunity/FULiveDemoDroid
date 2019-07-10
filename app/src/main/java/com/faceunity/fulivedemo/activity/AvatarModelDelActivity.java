package com.faceunity.fulivedemo.activity;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.entity.AvatarModel;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.database.DatabaseOpenHelper;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Avatar 模型删除页
 *
 * @author Richie on 2019.03.20
 */
public class AvatarModelDelActivity extends AppCompatActivity {
    private Button mBtnDelete;
    private Button mBtnAll;
    private boolean mIsDeleted;
    private DeleteAvatarAdapter mDeleteAvatarAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_live_photo_delete);
        ViewClickListener viewClickListener = new ViewClickListener();
        findViewById(R.id.iv_delete_back).setOnClickListener(viewClickListener);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.delete_avatar_model);
        TextView tvEmpty = findViewById(R.id.tv_empty_message);
        tvEmpty.setText(R.string.model_empty_tip);
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
        ArrayList<AvatarModel> avatarModels = getIntent().getParcelableArrayListExtra(AvatarDriveActivity.AVATAR_MODEL_LIST);
        mDeleteAvatarAdapter = new DeleteAvatarAdapter(avatarModels);
        recyclerView.setAdapter(mDeleteAvatarAdapter);
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

    private void checkEmpty() {
        if (mDeleteAvatarAdapter.getItemCount() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.iv_delete_back: {
                    // 回退
                    onBackPressed();
                }
                break;
                case R.id.btn_delete_all: {
                    // 全选
                    int size = mDeleteAvatarAdapter.mSelectedEntities.size();
                    if (size == mDeleteAvatarAdapter.getItemCount()) {
                        mBtnAll.setText(R.string.live_photo__delete_all);
                        mBtnDelete.setEnabled(false);
                        mBtnDelete.setText(getResources().getString(R.string.live_photo_btn_delete));
                        mDeleteAvatarAdapter.mSelectedEntities.clear();
                        mDeleteAvatarAdapter.notifyDataSetChanged();
                    } else {
                        mBtnAll.setText(R.string.live_photo_btn_cancel);
                        mBtnDelete.setEnabled(true);
                        mBtnDelete.setText(getResources().getString(R.string.live_photo_btn_delete_, mDeleteAvatarAdapter.getItemCount()));
                        mDeleteAvatarAdapter.mSelectedEntities.addAll(mDeleteAvatarAdapter.mAvatarModels);
                        mDeleteAvatarAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case R.id.btn_delete_bottom: {
                    // 删除
                    ConfirmDialogFragment.newInstance(getString(R.string.dialog_confirm_delete), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            Set<AvatarModel> selectedEntities = mDeleteAvatarAdapter.mSelectedEntities;
                            List<AvatarModel> toDelete = new ArrayList<>(selectedEntities.size());
                            toDelete.addAll(selectedEntities);
                            mBtnAll.setText(R.string.live_photo__delete_all);
                            mBtnDelete.setEnabled(false);
                            mBtnDelete.setText(getResources().getString(R.string.live_photo_btn_delete));
                            List<AvatarModel> failedList = DatabaseOpenHelper.getInstance().getAvatarModelDao().delete(toDelete);
                            if (failedList.size() > 0) {
                                ToastUtil.makeNormalToast(AvatarModelDelActivity.this, getString(R.string.toast_delete_failed)).show();
                            } else {
                                mDeleteAvatarAdapter.mSelectedEntities.clear();
                                for (AvatarModel avatarModel : toDelete) {
                                    mDeleteAvatarAdapter.mAvatarModels.remove(avatarModel);
                                }
                                mDeleteAvatarAdapter.notifyDataSetChanged();
                                checkEmpty();
                                ToastUtil.makeNormalToast(AvatarModelDelActivity.this, getString(R.string.toast_delete_succeed)).show();
                                mIsDeleted = true;
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

    private class DeleteAvatarAdapter extends RecyclerView.Adapter<DeleteAvatarAdapter.VH> {
        private List<AvatarModel> mAvatarModels;
        private Set<AvatarModel> mSelectedEntities;

        DeleteAvatarAdapter(@NonNull List<AvatarModel> avatarModels) {
            mAvatarModels = avatarModels;
            mSelectedEntities = new HashSet<>(mAvatarModels.size());
        }

        @NonNull
        @Override
        public DeleteAvatarAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_live_photo_delete, parent, false);
            final VH vh = new VH(view);
            vh.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    AvatarModel avatarModel = (AvatarModel) vh.itemView.getTag();
                    boolean selected = mSelectedEntities.contains(avatarModel);
                    if (selected) {
                        mSelectedEntities.remove(avatarModel);
                        vh.mIvMask.setVisibility(View.INVISIBLE);
                    } else {
                        vh.mIvMask.setVisibility(View.VISIBLE);
                        mSelectedEntities.add(avatarModel);
                    }
                    int size = mSelectedEntities.size();
                    if (size > 0) {
                        mBtnDelete.setEnabled(true);
                        mBtnDelete.setText(getResources().getString(R.string.live_photo_btn_delete_, size));
                    } else {
                        mBtnDelete.setEnabled(false);
                        mBtnDelete.setText(getResources().getString(R.string.live_photo_btn_delete));
                    }
                    if (size == getItemCount()) {
                        mBtnAll.setText(R.string.live_photo_btn_cancel);
                    } else {
                        mBtnAll.setText(R.string.live_photo__delete_all);
                    }
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DeleteAvatarAdapter.VH holder, int position) {
            AvatarModel avatarModel = mAvatarModels.get(position);
            holder.itemView.setTag(avatarModel);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x6)));
            Glide.with(AvatarModelDelActivity.this)
                    .load((avatarModel).getIconPath())
                    .apply(requestOptions).into(holder.mIvIcon);
            if (mSelectedEntities.contains(avatarModel)) {
                holder.mIvMask.setVisibility(View.VISIBLE);
            } else {
                holder.mIvMask.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mAvatarModels.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView mIvIcon;
            ImageView mIvMask;

            VH(View itemView) {
                super(itemView);
                mIvIcon = itemView.findViewById(R.id.iv_live_photo_photo);
                mIvMask = itemView.findViewById(R.id.iv_live_photo_mask);
            }
        }
    }
}
