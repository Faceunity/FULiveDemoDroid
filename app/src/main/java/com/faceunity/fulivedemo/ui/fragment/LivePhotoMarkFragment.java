package com.faceunity.fulivedemo.ui.fragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.entity.LivePhoto;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.activity.LivePhotoMakeActivity;
import com.faceunity.fulivedemo.activity.LivePhotoPortraitType;
import com.faceunity.fulivedemo.activity.LivePhotoSticker;
import com.faceunity.fulivedemo.activity.LivePhotoStickerEnum;
import com.faceunity.fulivedemo.renderer.LivePhotoRenderer;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.sticker.Sticker;
import com.faceunity.fulivedemo.ui.sticker.StickerLayout;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.PointUtils;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.greendao.GreenDaoUtils;
import com.faceunity.greendao.LivePhotoDao;
import com.faceunity.utils.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 表情动图 五官标记页面
 *
 * @author LiuQiang on 2018.12.17
 */
public class LivePhotoMarkFragment extends Fragment {
    public static final String TAG = "LivePhotoMarkFragment";
    private View mFlAdjust;
    private StickerLayout mStickerLayout;
    private LivePhotoMakeActivity mActivity;
    private LivePhotoRenderer mLivePhotoRenderer;
    private boolean mIsSaving;
    private OrganStickerAdapter mOrganStickerAdapter;
    private RecyclerView mRvOrganSticker;
    private LivePhoto mLivePhoto;
    private int mEditStickerCount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = ((LivePhotoMakeActivity) getActivity());
        mLivePhotoRenderer = mActivity.getLivePhotoRenderer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_mark, container, false);
        ViewClickListener viewClickListener = new ViewClickListener();
        view.findViewById(R.id.iv_livephoto_back).setOnClickListener(viewClickListener);
        view.findViewById(R.id.iv_livephoto_save).setOnClickListener(viewClickListener);
        mFlAdjust = view.findViewById(R.id.fl_livephoto_adjust);
        mFlAdjust.setOnClickListener(viewClickListener);
        mStickerLayout = view.findViewById(R.id.sticker_layout);
        mStickerLayout.setIncreasePath("image/live_photo_increase.png");
        mStickerLayout.setRemovePath("image/live_photo_close.png");
        mStickerLayout.setRotatePath("image/live_photo_rotate.png");
        mStickerLayout.setOnChildViewStatusListener(new OnStickerChildViewStatusListener());

        // 人像分类列表
        RecyclerView rvPortraitType = view.findViewById(R.id.rv_live_photo_type);
        rvPortraitType.setHasFixedSize(true);
        rvPortraitType.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) rvPortraitType.getItemAnimator()).setSupportsChangeAnimations(false);
        PortraitTypeAdapter portraitTypeAdapter = new PortraitTypeAdapter(LivePhotoPortraitType.getAll());
        portraitTypeAdapter.setOnItemClickListener(new PortraitTypeClickListener());
        rvPortraitType.setAdapter(portraitTypeAdapter);
        portraitTypeAdapter.setItemSelected(0);

        // 五官列表
        mRvOrganSticker = view.findViewById(R.id.rv_live_photo_organ);
        mRvOrganSticker.setHasFixedSize(true);
        mRvOrganSticker.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) mRvOrganSticker.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvOrganSticker.addItemDecoration(new VHSpaceItemDecoration(0, 0) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    outRect.left += getResources().getDimensionPixelSize(R.dimen.x10);
                } else if (childAdapterPosition == parent.getChildCount() - 1) {
                    outRect.right += getResources().getDimensionPixelSize(R.dimen.x10);
                }
            }
        });
        List<LivePhotoSticker> stickers = LivePhotoStickerEnum.getStickerByPortraitType(portraitTypeAdapter.getSelectedItems().valueAt(0).getType());
        mOrganStickerAdapter = new OrganStickerAdapter(new ArrayList<>(stickers));
        mOrganStickerAdapter.setOnItemClickListener(new StickerClickListener());
        mRvOrganSticker.setAdapter(mOrganStickerAdapter);

        showEditSticker();
        return view;
    }

    // 编辑五官贴纸
    private void showEditSticker() {
        LivePhoto editableLivePhoto = mActivity.getEditableLivePhoto();
        if (editableLivePhoto != null) {
            mLivePhoto = editableLivePhoto;
            // 拆包
            double[] groupType = editableLivePhoto.getGroupType();
            float[] adjustPointsF = editableLivePhoto.getAdjustPointsF();
            int offset = 0;
            LivePhotoSticker livePhotoSticker = new LivePhotoSticker();
            livePhotoSticker.setImagePath(editableLivePhoto.getImagePath());
            String[] stickerImagePaths = editableLivePhoto.getStickerImagePaths();
            float[] matrixF = editableLivePhoto.getMatrixF();
            float[] stickerMatrix;
            float[] adjustPoints;
            mEditStickerCount = groupType.length;
            for (int i = 0; i < groupType.length; i++) {
                int type = (int) groupType[i];
                livePhotoSticker.setOrganType(type);
                int pointsLength = LivePhotoSticker.getPointsLength(type);
                adjustPoints = new float[pointsLength];
                System.arraycopy(adjustPointsF, offset, adjustPoints, 0, pointsLength);
                offset += pointsLength;
                stickerMatrix = new float[9];
                System.arraycopy(matrixF, stickerMatrix.length * i, stickerMatrix, 0, stickerMatrix.length);

                livePhotoSticker.setPoints(adjustPoints);
                mStickerLayout.addSticker(type, stickerImagePaths[i], adjustPoints, false, stickerMatrix);
            }
            setAdjustViewVisibility(groupType.length);
        }
    }

    public void setAdjustPoints(float[] pointsOfLandmark, float[] pointsOfView) {
        mStickerLayout.setAdjustPoints(pointsOfLandmark, pointsOfView);
    }

    private void setAdjustViewVisibility(int visibleChildCount) {
        if (visibleChildCount > 0) {
            mFlAdjust.setVisibility(View.VISIBLE);
        } else {
            mFlAdjust.setVisibility(View.GONE);
        }
    }

    public boolean shouldShowConfirmDialog() {
        if (mEditStickerCount > 0) {
            return mStickerLayout.getStickerCount() >= 0;
        } else {
            return mStickerLayout.getStickerCount() > 0;
        }
    }

    public void hideStickerLayout() {
        mStickerLayout.setVisibility(View.INVISIBLE);
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.fl_livephoto_adjust: {
                    // 进入精细调整
                    float[] mappedPoints = mStickerLayout.getMappedPoints();
                    float[] mappedBorders = mStickerLayout.getMappedBorders();
                    if (mappedPoints != null && mappedBorders != null) {
                        LivePhotoAdjustFragment fragment = (LivePhotoAdjustFragment) mActivity.showFragment(LivePhotoAdjustFragment.TAG);
                        fragment.setViewPoints(mappedPoints, mappedBorders);
                    } else {
                        ToastUtil.makeNormalToast(mActivity, "请先选择五官").show();
                    }
                }
                break;
                case R.id.iv_livephoto_back: {
                    mActivity.onBackPressed();
                }
                break;
                case R.id.iv_livephoto_save: {
                    if (mIsSaving) {
                        return;
                    }
                    // 保存操作
                    mStickerLayout.setUnEditable();
                    final int stickerCount = mStickerLayout.getStickerCount();
                    if (stickerCount > 0) {
                        mIsSaving = true;
                        // 截取屏幕快照，图片是 1280*720 像素
                        mActivity.setNeedTakePic(true, new LivePhotoMakeActivity.OnCheckPicListener() {
                            // run on UI thread
                            @Override
                            public void onPhotoChecked(String path) {
                                File file = new File(path);
                                List<Sticker> stickers = mStickerLayout.getStickers();
                                float[] mappedPoints;
                                List<Integer> gpPoints = new ArrayList<>(64);
                                List<Integer> adjustPoints = new ArrayList<>(64);
                                double[] groupType = new double[stickerCount];
                                String[] stickerImagePaths = new String[stickerCount];
                                float[] matrix = new float[9 * stickerCount];
                                float[] mtxVal;
                                for (int i = 0; i < stickerCount; i++) {
                                    Sticker sticker = stickers.get(i);
                                    stickerImagePaths[i] = sticker.getImagePath();
                                    groupType[i] = sticker.getType();
                                    mtxVal = new float[9];
                                    sticker.getMatrix().getValues(mtxVal);
                                    System.arraycopy(mtxVal, 0, matrix, i * 9, mtxVal.length);
                                    float[] landmarkPoints = sticker.getLandmarkPoints();
                                    float[] points = sticker.getPoints();
                                    for (float point : points) {
                                        adjustPoints.add((int) point);
                                    }
                                    // 最终保存的是 landmark 坐标系的点位坐标
                                    if (landmarkPoints == null) {
                                        // 没有进行精细调整
                                        mappedPoints = new float[points.length];
                                        PointUtils.getMappedPoints(points, mappedPoints, sticker.getMatrix());
                                        for (int j = 0; j < mappedPoints.length; j += 2) {
                                            PointF pointFLand = mLivePhotoRenderer.changeViewToLandmark(mappedPoints[j], mappedPoints[j + 1]);
                                            mappedPoints[j] = pointFLand.x;
                                            mappedPoints[j + 1] = pointFLand.y;
                                        }
                                        for (float p : mappedPoints) {
                                            gpPoints.add((int) p);
                                        }
                                    } else {
                                        // 经过精细调整
                                        for (float p : landmarkPoints) {
                                            gpPoints.add((int) p);
                                        }
                                    }
                                }
                                // convert list to array
                                double[] groupPoints = new double[gpPoints.size()];
                                for (int i = 0, j = gpPoints.size(); i < j; i++) {
                                    groupPoints[i] = gpPoints.get(i);
                                }
                                float[] adjustPointsF = new float[adjustPoints.size()];
                                for (int i = 0; i < adjustPoints.size(); i++) {
                                    adjustPointsF[i] = adjustPoints.get(i);
                                }
                                String imagePath = file.getAbsolutePath();
                                Point bitmapSize = BitmapUtil.getBitmapSize(imagePath);
                                if (mLivePhoto == null) {
                                    mLivePhoto = new LivePhoto(bitmapSize.x, bitmapSize.y, groupPoints,
                                            groupType, imagePath, stickerImagePaths, matrix, adjustPointsF);
                                } else {
                                    mLivePhoto.setGroupType(groupType);
                                    mLivePhoto.setGroupPoints(groupPoints);
                                    mLivePhoto.setStickerImagePath(stickerImagePaths);
                                    mLivePhoto.setMatrixF(matrix);
                                    mLivePhoto.setAdjustPointsF(adjustPointsF);
                                }
                                Log.i(TAG, "onPhotoChecked: insertOrReplace LivePhoto " + mLivePhoto);
                                saveLivePhoto(mLivePhoto);
                            }

                            private void saveLivePhoto(LivePhoto livePhoto) {
                                try {
                                    LivePhotoDao livePhotoDao = GreenDaoUtils.getInstance().getDaoSession().getLivePhotoDao();
                                    livePhotoDao.insertOrReplace(livePhoto);
                                    mActivity.setSavedModel(true);
                                    ToastUtil.makeFineToast(mActivity, getString(R.string.live_photo_save_succeed), R.drawable.icon_confirm).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mIsSaving = false;
                                            mActivity.onBackPressed();
                                        }
                                    }, LivePhotoMakeActivity.TOAST_DELAY);
                                } catch (Exception e) {
                                    ToastUtil.makeFineToast(mActivity, getString(R.string.live_photo_save_failed), R.drawable.icon_fail).show();
                                    Log.e(TAG, "LivePhotoDao insert failed", e);
                                }
                            }
                        });
                    } else {
                        ConfirmDialogFragment.newInstance(
                                getString(R.string.live_photo_not_add_sticker), getString(R.string.live_photo_make_confirm)
                                , getString(R.string.live_photo_make_cancel), new BaseDialogFragment.OnClickListener() {
                                    @Override
                                    public void onConfirm() {

                                    }

                                    @Override
                                    public void onCancel() {
                                        mActivity.onBackPressed();
                                    }
                                }
                        ).show(mActivity.getSupportFragmentManager(), "ConfirmDialogFragment");
                    }
                }
                break;
                default:
            }
        }
    }

    private class OnStickerChildViewStatusListener implements StickerLayout.OnChildViewStatusListener {

        @Override
        public void onViewAdded(int visibleChildCount) {
            setAdjustViewVisibility(visibleChildCount);
        }

        @Override
        public void onViewRemoved(int visibleChildCount) {
            setAdjustViewVisibility(visibleChildCount);
        }
    }

    // 人像类型点击事件
    private class PortraitTypeClickListener implements BaseRecyclerAdapter.OnItemClickListener<LivePhotoPortraitType> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<LivePhotoPortraitType> adapter, View view, int position) {
            LivePhotoPortraitType item = adapter.getItem(position);
            List<LivePhotoSticker> stickerByPortraitType = LivePhotoStickerEnum.getStickerByPortraitType(item.getType());
            mOrganStickerAdapter.replaceAll(stickerByPortraitType);
            mRvOrganSticker.scrollToPosition(0);
        }
    }

    // 五官贴纸点击事件
    private class StickerClickListener implements BaseRecyclerAdapter.OnItemClickListener<LivePhotoSticker> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<LivePhotoSticker> adapter, View view, int position) {
            LivePhotoSticker livePhotoSticker = adapter.getItem(position);
            mStickerLayout.addSticker(livePhotoSticker.getOrganType(), livePhotoSticker.getImagePath(), livePhotoSticker.getPoints(), true, null);
            setAdjustViewVisibility(mStickerLayout.getStickerCount());
        }
    }

    private class PortraitTypeAdapter extends BaseRecyclerAdapter<LivePhotoPortraitType> {

        PortraitTypeAdapter(@NonNull List<LivePhotoPortraitType> data) {
            super(data, R.layout.recycler_live_photo_type);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, LivePhotoPortraitType item) {
            viewHolder.setText(R.id.tv_name, getString(item.getNameId()));
        }
    }

    private class OrganStickerAdapter extends BaseRecyclerAdapter<LivePhotoSticker> {

        OrganStickerAdapter(@NonNull List<LivePhotoSticker> data) {
            super(data, R.layout.recycler_live_photo_organ);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, LivePhotoSticker item) {
            viewHolder.setImageResource(R.id.iv_live_photo_item, item.getIconId())
                    .setText(R.id.tv_live_photo_item, getResources().getString(item.getNameId()));
        }
    }

}
