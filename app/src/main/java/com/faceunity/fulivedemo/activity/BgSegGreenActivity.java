package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.ColorPickerView;
import com.faceunity.fulivedemo.ui.GestureTouchHandler;
import com.faceunity.fulivedemo.ui.control.BgSegGreenControlView;
import com.faceunity.fulivedemo.ui.dialog.PromptDialogFragment;
import com.faceunity.fulivedemo.utils.ColorPickerTouchEvent;

public class BgSegGreenActivity extends FUBaseActivity implements ColorPickerTouchEvent.OnTouchEventListener {
    public static final String TAG = "BgSegGreenActivity";
    private ColorPickerTouchEvent mColorPickerTouchEvent;
    private BgSegGreenControlView mBgSegGreenControlView;
    private GestureTouchHandler mGestureTouchHandler;
    private int mPickedColor;
    private boolean mIsShowColorPicker;

    @Override
    protected void onCreate() {
        mTvTrackStatus.setVisibility(View.GONE);
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_bg_seg_green);
        mBgSegGreenControlView = (BgSegGreenControlView) mBottomViewStub.inflate();
        mBgSegGreenControlView.setOnFUControlListener(mFURenderer);
        mBgSegGreenControlView.setOnColorPickerStateChangedListener(new BgSegGreenControlView.OnColorPickerStateChangedListener() {
            @Override
            public void onColorPickerStateChanged(boolean selected, int color) {
                mFURenderer.setRunBgSegGreen(!selected);
                mIsShowColorPicker = selected;
                ColorPickerView colorPickerView = mColorPickerTouchEvent.getColorPickerView();
                colorPickerView.setVisibility(selected ? View.VISIBLE : View.GONE);
                if (selected) {
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) colorPickerView.getLayoutParams();
                    layoutParams.leftMargin = (mClOperationView.getWidth() - colorPickerView.getWidth()) / 2;
                    layoutParams.topMargin = (mClOperationView.getHeight() - colorPickerView.getHeight()) / 2;
                    colorPickerView.setLayoutParams(layoutParams);
                }
                colorPickerView.setPickedColor(color);
            }
        });
        mBgSegGreenControlView.setOnBottomAnimatorChangeListener(new BgSegGreenControlView.OnBottomAnimatorChangeListener() {
            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
        mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - 0.265f)));
        mGlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !mIsShowColorPicker && mGestureTouchHandler.onTouchEvent(event);
            }
        });
        mGestureTouchHandler = new GestureTouchHandler(this);
        mGestureTouchHandler.setOnTouchResultListener(new GestureTouchHandler.OnTouchResultListener() {
            @Override
            public void onTransform(float x1, float y1, float x2, float y2) {
                boolean isFront = mCameraRenderer.getCameraFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT;
                mFURenderer.setTransform(isFront ? 1 - y2 : y1, 1 - x2, isFront ? 1 - y1 : y2, 1 - x1);
            }

            @Override
            public void onClick() {
                if (mBgSegGreenControlView.isShown()) {
                    mBgSegGreenControlView.hideBottomLayoutAnimator();
                }
            }
        });

        PromptDialogFragment promptDialogFragment = PromptDialogFragment.newInstance(R.string.dialog_guide_bg_seg_green);
        promptDialogFragment.show(getSupportFragmentManager(), "PromptDialogFragment");

        mColorPickerTouchEvent = new ColorPickerTouchEvent(this);
        mClOperationView.post(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.leftMargin = mClOperationView.getWidth() / 2;
                layoutParams.topMargin = mClOperationView.getHeight() / 2;
                ColorPickerView colorPickerView = mColorPickerTouchEvent.getColorPickerView();
                mClOperationView.addView(colorPickerView, layoutParams);
                colorPickerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        return mIsShowColorPicker && mColorPickerTouchEvent.handleTouchEvent(event, mGlSurfaceView,
                mCameraRenderer.getViewWidth(), mCameraRenderer.getViewHeight(), mCameraRenderer.getTexMatrix(),
                mCameraRenderer.getMvpMatrix(), mCameraRenderer.get2dTexture(), this);
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
        super.onSurfaceChanged(viewWidth, viewHeight);
        mGestureTouchHandler.setViewSize(viewWidth, viewHeight);
    }

    @Override
    protected boolean showAutoFocus() {
        return false;
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(1)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .defaultEffect(EffectEnum.BG_SEG_GREEN.effect())
                .inputImageOrientation(mFrontCameraOrientation)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return true;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(BgSegGreenActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, BgSegGreenActivity.TAG);
        startActivity(intent);
    }

    @Override
    public void onTrackStatusChanged(int type, int status) {
        // do nothing
    }

    @Override
    public void onReadRgba(int r, int g, int b, int a) {
        int argb = Color.argb(a, r, g, b);
        mBgSegGreenControlView.postSetPalettePickColor(argb);
        mPickedColor = argb;
    }

    @Override
    public void onActionUp() {
        mFURenderer.setRunBgSegGreen(true);
        int pickedColor = mPickedColor;
        setKeyColor(pickedColor);
        mIsShowColorPicker = false;
    }

    private void setKeyColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        mFURenderer.setKeyColor(new double[]{red, green, blue});
    }

}