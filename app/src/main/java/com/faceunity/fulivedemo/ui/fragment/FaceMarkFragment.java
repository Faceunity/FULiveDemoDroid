package com.faceunity.fulivedemo.ui.fragment;

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

import com.faceunity.entity.MagicPhotoEntity;
import com.faceunity.fulivedemo.FUMagicGenActivity;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.MagicFloatingEntity;
import com.faceunity.fulivedemo.renderer.MagicPhotoRenderer;
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
import com.faceunity.greendao.MagicPhotoEntityDao;
import com.faceunity.utils.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LiuQiang on 2018.12.17
 * 五官标记页面
 */
public class FaceMarkFragment extends Fragment implements BaseRecyclerAdapter.OnItemClickListener<MagicFloatingEntity> {
    public static final String TAG = "FaceMarkFragment";
    private View mFlAdjust;
    private StickerLayout mStickerLayout;
    private FUMagicGenActivity mActivity;
    private MagicPhotoRenderer mMagicPhotoRenderer;
    private boolean mIsSaving;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = ((FUMagicGenActivity) getActivity());
        mMagicPhotoRenderer = ((FUMagicGenActivity) getActivity()).getMagicPhotoRenderer();
        View view = inflater.inflate(R.layout.fragment_face_mark, container, false);
        ViewClickListener viewClickListener = new ViewClickListener();
        view.findViewById(R.id.iv_magic_back).setOnClickListener(viewClickListener);
        view.findViewById(R.id.iv_magic_save).setOnClickListener(viewClickListener);
        mFlAdjust = view.findViewById(R.id.fl_adjust);
        mFlAdjust.setOnClickListener(viewClickListener);
        mStickerLayout = view.findViewById(R.id.sticker_layout);
        mStickerLayout.setIncreaseRes(R.drawable.ic_magic_increase);
        mStickerLayout.setRemoveRes(R.drawable.ic_magic_close);
        mStickerLayout.setRotateRes(R.drawable.ic_magic_rotate);
        mStickerLayout.setOnChildViewStatusListener(new OnStickerChildViewStatusListener());

        RecyclerView recyclerView = view.findViewById(R.id.rv_magic_photo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.addItemDecoration(new VHSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.x22)) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    outRect.left += getResources().getDimensionPixelSize(R.dimen.x12);
                } else if (childAdapterPosition == parent.getChildCount() - 1) {
                    outRect.right += getResources().getDimensionPixelSize(R.dimen.x12);
                }
            }
        });
        RecyclerAdapter adapter = new RecyclerAdapter(MagicFloatingEntity.getDefaultEntities());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter<MagicFloatingEntity> adapter, View view, int position) {
        MagicFloatingEntity item = adapter.getItem(position);
        mStickerLayout.addSticker(item.getType(), item.getImageAssetsPath(), item.getDots());
        setAdjustViewVisibility(mStickerLayout.getStickerCount());
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.fl_adjust: {
                    // 点击跳转精细调整
                    float[] mappedPoints = mStickerLayout.getMappedPoints();
                    if (mappedPoints != null) {
                        setAdjustViewVisibility(1);
                        FaceAdjustFragment fragment = (FaceAdjustFragment) mActivity.showFragment(FaceAdjustFragment.TAG);
                        fragment.setViewPoints(mappedPoints);
                    } else {
                        ToastUtil.makeNormalToast(mActivity, "请先选择五官").show();
                    }
                }
                break;
                case R.id.iv_magic_back: {
                    mActivity.onBackPressed();
                }
                break;
                case R.id.iv_magic_save: {
                    if (mIsSaving) {
                        return;
                    }
                    mStickerLayout.setUnEditable();
                    final int stickerCount = mStickerLayout.getStickerCount();
                    if (stickerCount > 0) {
                        mIsSaving = true;
                        // 截取屏幕快照，图片是 1280*720 像素
                        mActivity.setNeedTakePic(true, new FUMagicGenActivity.OnCheckPicListener() {
                            @Override
                            public void onPhotoChecked(String path) {
                                File file = new File(path);
                                List<Sticker> stickers = mStickerLayout.getStickers();
                                float[] mappedPoints;
                                List<Integer> gpPoints = new ArrayList<>(64);
                                double[] groupType = new double[stickerCount];
                                for (int i = 0; i < stickerCount; i++) {
                                    Sticker sticker = stickers.get(i);
                                    groupType[i] = sticker.getType();
                                    float[] landmarkPoints = sticker.getLandmarkPoints();
                                    if (landmarkPoints == null) {
                                        // view 坐标系
                                        float[] points = sticker.getPoints();
                                        mappedPoints = new float[points.length];
                                        PointUtils.getMappedPoints(points, mappedPoints, sticker.getMatrix());
                                        for (int j = 0; j < mappedPoints.length; j += 2) {
                                            PointF pointFLand = mMagicPhotoRenderer.changeViewToLandmark(mappedPoints[j], mappedPoints[j + 1]);
                                            mappedPoints[j] = pointFLand.x;
                                            mappedPoints[j + 1] = pointFLand.y;
                                        }
                                        for (float p : mappedPoints) {
                                            gpPoints.add((int) p);
                                        }
                                    } else {
                                        for (float p : landmarkPoints) {
                                            gpPoints.add((int) p);
                                        }
                                    }
                                }
                                double[] groupPoints = new double[gpPoints.size()];
                                for (int i = 0, j = gpPoints.size(); i < j; i++) {
                                    groupPoints[i] = gpPoints.get(i);
                                }
                                String imagePath = file.getAbsolutePath();
                                Point bitmapSize = BitmapUtil.getBitmapSize(imagePath);
                                MagicPhotoEntity magicPhotoEntity = new MagicPhotoEntity(bitmapSize.x, bitmapSize.y, groupPoints, groupType, imagePath);
                                try {
                                    MagicPhotoEntityDao magicPhotoEntityDao = GreenDaoUtils.getInstance().getDaoSession().getMagicPhotoEntityDao();
                                    magicPhotoEntityDao.insert(magicPhotoEntity);
                                    mActivity.setSavedModel(true);
                                    ToastUtil.makeFineToast(mActivity, getString(R.string.magic_save_succeed), R.drawable.icon_confirm).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mIsSaving = false;
                                            mActivity.onBackPressed();
                                        }
                                    }, 1500);
                                } catch (Exception e) {
                                    ToastUtil.makeFineToast(mActivity, getString(R.string.magic_save_failed), R.drawable.icon_fail).show();
                                    Log.e(TAG, "MagicPhotoEntityDao insert failed", e);
                                }
                            }
                        });
                    } else {
                        ConfirmDialogFragment.newInstance(
                                getString(R.string.magic_not_add_sticker), getString(R.string.magic_not_add_sticker_confirm)
                                , getString(R.string.magic_not_add_sticker_cancel), new BaseDialogFragment.OnClickListener() {
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

    public void setFixedLandmarkPoints(float[] points) {
        mStickerLayout.setFixedLandmarkPoints(points);
    }

    private void setAdjustViewVisibility(int visibleChildCount) {
        if (visibleChildCount > 0) {
            mFlAdjust.setVisibility(View.VISIBLE);
        } else {
            mFlAdjust.setVisibility(View.GONE);
        }
    }

    // 先缩放再平移, points 是 view 坐标系，变化之后的
    private PointF getLandTranslation(float[] points) {
        float[] tempX = new float[points.length / 2];
        float[] tempY = new float[points.length / 2];
        for (int i = 0, j = 0, k = 0, length = points.length; i < length; i++) {
            if (i % 2 == 0) {
                tempX[j++] = points[i];
            } else {
                tempY[k++] = points[i];
            }
        }
        Arrays.sort(tempX);
        Arrays.sort(tempY);
        int width = mMagicPhotoRenderer.getViewWidth();
        int height = mMagicPhotoRenderer.getViewHeight();
        // 缩放至屏幕的 1/3 宽度
        float scale = width / 3 / (tempX[tempX.length - 1] - tempX[0]);
        float pivotX = (tempX[tempX.length - 1] + tempX[0]) / 2;
        float pivotY = (tempY[tempY.length - 1] + tempY[0]) / 2;
        float transX = (width - (tempX[tempX.length - 1] + tempX[0])) / 2;
        float transY = (height - (tempY[tempY.length - 1] + tempY[0])) / 2;
        Log.d(TAG, "getLandTranslation: x:" + transX + ", y:" + transY + ", scale:" + scale
                + ", pivotX:" + pivotX + ", pivotY:" + pivotY);
        return new PointF(transX, transY);
    }

    public int getStickerCount() {
        return mStickerLayout.getStickerCount();
    }

    public void hideStickerLayout() {
        mStickerLayout.setVisibility(View.INVISIBLE);
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

    private class RecyclerAdapter extends BaseRecyclerAdapter<MagicFloatingEntity> {

        RecyclerAdapter(@NonNull List<MagicFloatingEntity> data) {
            super(data, R.layout.rv_item_magic);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, MagicFloatingEntity item) {
            viewHolder.setImageResource(R.id.iv_magic_item, item.getIconId())
                    .setText(R.id.tv_magic_item, getResources().getString(item.getNameId()));
        }
    }

}
