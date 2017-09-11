package com.faceunity.fulivedemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Base Acitivity, 负责界面UI的处理
 * Created by lirui on 2017/1/19.
 */

public abstract class FUBaseUIActivity extends Activity implements View.OnClickListener {

    private final String TAG = "FUBaseUIActivity";

    private RecyclerView mEffectRecyclerView;
    private EffectAndFilterSelectAdapter mEffectRecyclerAdapter;
    private RecyclerView mFilterRecyclerView;
    private EffectAndFilterSelectAdapter mFilterRecyclerAdapter;

    private LinearLayout mBlurLevelSelect;
    private LinearLayout mColorLevelSelect;
    private LinearLayout mFaceShapeSelect;
    private LinearLayout mRedLevelSelect;


    private Button mChooseEffectBtn;
    private Button mChooseFilterBtn;
    private Button mChooseBlurLevelBtn;
    private Button mChooseColorLevelBtn;
    private Button mChooseFaceShapeBtn;
    private Button mChooseRedLevelBtn;

    private TextView[] mBlurLevels;
    private int[] BLUR_LEVEL_TV_ID = {R.id.blur_level0, R.id.blur_level1, R.id.blur_level2,
            R.id.blur_level3, R.id.blur_level4, R.id.blur_level5, R.id.blur_level6};

    private TextView mFaceShape0Nvshen;
    private TextView mFaceShape1Wanghong;
    private TextView mFaceShape2Ziran;
    private TextView mFaceShape3Default;

    protected ImageView mFaceTrackingStatusImageView;

    private Button mRecordingBtn;
    private int mRecordStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);

        mEffectRecyclerView = (RecyclerView) findViewById(R.id.effect_recycle_view);
        mEffectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mEffectRecyclerAdapter = new EffectAndFilterSelectAdapter(mEffectRecyclerView, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT);
        mEffectRecyclerAdapter.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int itemPosition) {
                Log.d(TAG, "effect item selected " + itemPosition);
                onEffectItemSelected(EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[itemPosition]);
            }
        });
        mEffectRecyclerView.setAdapter(mEffectRecyclerAdapter);

        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerAdapter = new EffectAndFilterSelectAdapter(mFilterRecyclerView, EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_FILTER);
        mFilterRecyclerAdapter.setOnItemSelectedListener(new EffectAndFilterSelectAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int itemPosition) {
                Log.d(TAG, "filter item selected " + itemPosition);
                onFilterSelected(EffectAndFilterSelectAdapter.FILTERS_NAME[itemPosition]);
            }
        });
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter);

        mChooseEffectBtn = (Button) findViewById(R.id.btn_choose_effect);
        mChooseFilterBtn = (Button) findViewById(R.id.btn_choose_filter);
        mChooseBlurLevelBtn = (Button) findViewById(R.id.btn_choose_blur_level);
        mChooseColorLevelBtn = (Button) findViewById(R.id.btn_choose_color_level);
        mChooseFaceShapeBtn = (Button) findViewById(R.id.btn_choose_face_shape);
        mChooseRedLevelBtn = (Button) findViewById(R.id.btn_choose_red_level);

        mFaceShape0Nvshen = (TextView) findViewById(R.id.face_shape_0_nvshen);
        mFaceShape1Wanghong = (TextView) findViewById(R.id.face_shape_1_wanghong);
        mFaceShape2Ziran = (TextView) findViewById(R.id.face_shape_2_ziran);
        mFaceShape3Default = (TextView) findViewById(R.id.face_shape_3_default);

        mBlurLevelSelect = (LinearLayout) findViewById(R.id.blur_level_select_block);
        mColorLevelSelect = (LinearLayout) findViewById(R.id.color_level_select_block);
        mFaceShapeSelect = (LinearLayout) findViewById(R.id.lin_face_shape);
        mRedLevelSelect = (LinearLayout) findViewById(R.id.red_level_select_block);

        mBlurLevels = new TextView[BLUR_LEVEL_TV_ID.length];
        for (int i = 0; i < BLUR_LEVEL_TV_ID.length; i++) {
            final int level = i;
            mBlurLevels[i] = (TextView) findViewById(BLUR_LEVEL_TV_ID[i]);
            mBlurLevels[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBlurLevelTextBackground(mBlurLevels[level]);
                    onBlurLevelSelected(level);
                }
            });
        }

        DiscreteSeekBar colorLevelSeekbar = (DiscreteSeekBar) findViewById(R.id.color_level_seekbar);
        colorLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onColorLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar cheekThinSeekbar = (DiscreteSeekBar) findViewById(R.id.cheekthin_level_seekbar);
        cheekThinSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onCheekThinSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar enlargeEyeSeekbar = (DiscreteSeekBar) findViewById(R.id.enlarge_eye_level_seekbar);
        enlargeEyeSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onEnlargeEyeSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar faceShapeLevelSeekbar = (DiscreteSeekBar) findViewById(R.id.face_shape_seekbar);
        faceShapeLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onFaceShapeLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        DiscreteSeekBar redLevelShapeLevelSeekbar = (DiscreteSeekBar) findViewById(R.id.red_level_seekbar);
        redLevelShapeLevelSeekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onRedLevelSelected(value, 100);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        mFaceTrackingStatusImageView = (ImageView) findViewById(R.id.iv_face_detect);
        mRecordingBtn = (Button) findViewById(R.id.btn_recording);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_effect:
                setEffectFilterBeautyChooseBtnTextColor(mChooseEffectBtn);
                setEffectFilterBeautyChooseBlock(mEffectRecyclerView);
                break;
            case R.id.btn_choose_filter:
                setEffectFilterBeautyChooseBtnTextColor(mChooseFilterBtn);
                setEffectFilterBeautyChooseBlock(mFilterRecyclerView);
                break;
            case R.id.btn_choose_blur_level:
                setEffectFilterBeautyChooseBtnTextColor(mChooseBlurLevelBtn);
                setEffectFilterBeautyChooseBlock(mBlurLevelSelect);
                break;
            case R.id.btn_choose_color_level:
                setEffectFilterBeautyChooseBtnTextColor(mChooseColorLevelBtn);
                setEffectFilterBeautyChooseBlock(mColorLevelSelect);
                break;
            case R.id.btn_choose_face_shape:
                setEffectFilterBeautyChooseBtnTextColor(mChooseFaceShapeBtn);
                setEffectFilterBeautyChooseBlock(mFaceShapeSelect);
                break;
            case R.id.btn_choose_red_level:
                setEffectFilterBeautyChooseBtnTextColor(mChooseRedLevelBtn);
                setEffectFilterBeautyChooseBlock(mRedLevelSelect);
                break;
            case R.id.btn_choose_camera:
                onCameraChange();
                break;
            case R.id.btn_recording:
                if (mRecordStatus == 0) {
                    mRecordingBtn.setText("停止录制");
                    onStartRecording();
                    mRecordStatus ^= 1;
                } else {
                    mRecordingBtn.setText("开始录制");
                    onStopRecording();
                    mRecordStatus ^= 1;
                }
                break;
            case R.id.face_shape_0_nvshen:
                setFaceShapeBackground(mFaceShape0Nvshen);
                onFaceShapeSelected(0);
                break;
            case R.id.face_shape_1_wanghong:
                setFaceShapeBackground(mFaceShape1Wanghong);
                onFaceShapeSelected(1);
                break;
            case R.id.face_shape_2_ziran:
                setFaceShapeBackground(mFaceShape2Ziran);
                onFaceShapeSelected(2);
                break;
            case R.id.face_shape_3_default:
                setFaceShapeBackground(mFaceShape3Default);
                onFaceShapeSelected(3);
                break;
        }
    }

    private void setBlurLevelTextBackground(TextView tv) {
        mBlurLevels[0].setBackground(getResources().getDrawable(R.drawable.zero_blur_level_item_unselected));
        for (int i = 1; i < BLUR_LEVEL_TV_ID.length; i++) {
            mBlurLevels[i].setBackground(getResources().getDrawable(R.drawable.blur_level_item_unselected));
        }
        if (tv == mBlurLevels[0]) {
            tv.setBackground(getResources().getDrawable(R.drawable.zero_blur_level_item_selected));
        } else {
            tv.setBackground(getResources().getDrawable(R.drawable.blur_level_item_selected));
        }
    }

    private void setFaceShapeBackground(TextView tv) {
        mFaceShape0Nvshen.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape1Wanghong.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape2Ziran.setBackground(getResources().getDrawable(R.color.unselect_gray));
        mFaceShape3Default.setBackground(getResources().getDrawable(R.color.unselect_gray));
        tv.setBackground(getResources().getDrawable(R.color.faceunityYellow));
    }

    private void setEffectFilterBeautyChooseBlock(View v) {
        mEffectRecyclerView.setVisibility(View.INVISIBLE);
        mFilterRecyclerView.setVisibility(View.INVISIBLE);
        mFaceShapeSelect.setVisibility(View.INVISIBLE);
        mBlurLevelSelect.setVisibility(View.INVISIBLE);
        mColorLevelSelect.setVisibility(View.INVISIBLE);
        mRedLevelSelect.setVisibility(View.INVISIBLE);
        v.setVisibility(View.VISIBLE);
    }

    private void setEffectFilterBeautyChooseBtnTextColor(Button selectedBtn) {
        mChooseEffectBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseColorLevelBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseBlurLevelBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseFilterBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseFaceShapeBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        mChooseRedLevelBtn.setTextColor(getResources().getColor(R.color.white));
        selectedBtn.setTextColor(getResources().getColor(R.color.faceunityYellow));
    }

    /**
     * 道具贴纸选择
     *
     * @param effectItemName 道具贴纸文件名
     */
    abstract protected void onEffectItemSelected(String effectItemName);

    /**
     * 滤镜选择
     *
     * @param filterName 滤镜名称
     */
    abstract protected void onFilterSelected(String filterName);

    /**
     * 磨皮选择
     *
     * @param level 磨皮level
     */
    abstract protected void onBlurLevelSelected(int level);

    /**
     * 美白选择
     *
     * @param progress 美白滑动条进度
     * @param max      美白滑动条最大值
     */
    abstract protected void onColorLevelSelected(int progress, int max);

    /**
     * 瘦脸选择
     *
     * @param progress 瘦脸滑动进度
     * @param max      瘦脸滑动条最大值
     */
    abstract protected void onCheekThinSelected(int progress, int max);

    /**
     * 大眼选择
     *
     * @param progress 大眼滑动进度
     * @param max      大眼滑动条最大值
     */
    abstract protected void onEnlargeEyeSelected(int progress, int max);

    /**
     * 相机切换
     */
    abstract protected void onCameraChange();

    /**
     * 开始录制
     */
    abstract protected void onStartRecording();

    /**
     * 停止录制
     */
    abstract protected void onStopRecording();

    /**
     * 脸型选择
     */
    abstract protected void onFaceShapeSelected(int faceShape);

    /**
     * 美型程度选择
     */
    abstract protected void onFaceShapeLevelSelected(int progress, int max);

    /**
     * 美白程度选择
     */
    abstract protected void onRedLevelSelected(int progress, int max);
}
