package com.faceunity.fulivedemo;

import android.animation.ValueAnimator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.faceunity.FURenderer;
import com.faceunity.entity.Makeup;
import com.faceunity.fulivedemo.entity.MakeupEnum;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sMakeupLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sMakeups;

/**
 * 美妆界面
 * Created by tujh on 2018/1/31.
 */

public class FUMakeupActivity extends FUBaseActivity {
    public final static String TAG = FUMakeupActivity.class.getSimpleName();

    private ConstraintLayout mConstraintLayout;
    private ImageView mMakeupNone;
    private MakeupAdapter mMakeupAdapter;
    private DiscreteSeekBar mBeautySeekBar;
    private boolean isShown;
    private CheckGroup mBottomCheckGroup;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_makeup);
        mBottomViewStub.inflate();

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.fu_makeup_layout);

        mConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

//        mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBottomCheckGroup.check(View.NO_ID);
//            }
//        });

        mBottomCheckGroup = (CheckGroup) findViewById(R.id.makeup_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedId_old = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.makeup_radio_lipstick:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_LIPSTICK);
                        break;
                    case R.id.makeup_radio_blusher:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_BLUSHER);
                        break;
                    case R.id.makeup_radio_eyebrow:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_EYEBROW);
                        break;
                    case R.id.makeup_radio_eye_shadow:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_EYE_SHADOW);
                        break;
                    case R.id.makeup_radio_eye_liner:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_EYE_LINER);
                        break;
                    case R.id.makeup_radio_eyelash:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_EYELASH);
                        break;
                    case R.id.makeup_radio_contact_lens:
                        mMakeupAdapter.setMakeupType(Makeup.MAKEUP_TYPE_CONTACT_LENS);
                        break;
                    default:
                }

                if ((checkedId == View.NO_ID || checkedId == checkedId_old) && checkedId_old != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    int startHeight = mConstraintLayout.getHeight();
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedId_old == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x98);
                    int endHeight = (int) getResources().getDimension(R.dimen.x366);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = true;
                }
                checkedId_old = checkedId;
            }
        });

        RecyclerView makeupMidRecycler = (RecyclerView) findViewById(R.id.makeup_mid_recycler);
        makeupMidRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        makeupMidRecycler.setAdapter(mMakeupAdapter = new MakeupAdapter());
        ((SimpleItemAnimator) makeupMidRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mMakeupNone = (ImageView) findViewById(R.id.makeup_none);
        mMakeupNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMakeupAdapter.clickPosition(-1);
            }
        });

        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.makeup_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                mMakeupAdapter.setMakeupLevel(1.0f * value / 100);
            }
        });
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(null)
                .setNeedFaceBeauty(true)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isShown) {
            mBottomCheckGroup.check(View.NO_ID);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        for (int i = 0; i < sMakeups.length; i++) {
            mMakeupAdapter.selectPos[i] = MakeupEnum.getMakeupsByMakeupType(i).lastIndexOf(sMakeups[i]);
            sMakeups[i].setLevel(mMakeupAdapter.getMakeupLevel(sMakeups[i].bundleName()));
            mFURenderer.onMakeupSelected(sMakeups[i]);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMakeupAdapter.notifyDataSetChanged();
            }
        });
    }

    private ValueAnimator mBottomLayoutAnimator;

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mConstraintLayout.getLayoutParams();
                if (params == null) return;
                params.height = height;
                mConstraintLayout.setLayoutParams(params);
                float s = 1.0f * (height - startHeight) / (endHeight - startHeight);
                float showRate = startHeight > endHeight ? 1 - s : s;
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
        mBottomLayoutAnimator.start();
    }

    class MakeupAdapter extends RecyclerView.Adapter<MakeupAdapter.MakeupHolder> {

        private int[] selectPos = {-1, -1, -1, -1, -1, -1, -1};
        private int selectMakeupType = -1;

        @Override
        public MakeupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MakeupAdapter.MakeupHolder(LayoutInflater.from(FUMakeupActivity.this).inflate(R.layout.layout_makeup_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(MakeupAdapter.MakeupHolder holder, final int position) {
            final List<Makeup> makeups = getItems();
            holder.makeupImg.setImageResource(makeups.get(position).resId());
            if (selectMakeupType >= 0 && selectPos[selectMakeupType] == position) {
                holder.makeupImg.setBackgroundResource(R.drawable.control_filter_select);
            } else {
                holder.makeupImg.setBackgroundResource(0);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPosition(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return getItems().size();
        }

        public void setMakeupType(int makeupType) {
            this.selectMakeupType = makeupType;
            notifyDataSetChanged();
            mMakeupNone.setImageResource(selectPos[selectMakeupType] >= 0 ? R.drawable.makeup_none_normal : R.drawable.makeup_none_checked);
            setMakeupProgress();
        }

        private void clickPosition(int position) {
            selectPos[selectMakeupType] = position;
            Makeup select;
            if (position >= 0) {
                select = getItems().get(position);
                select.setLevel(getMakeupLevel(select.bundleName()));
                mFURenderer.onMakeupSelected(select);
            } else {
                select = MakeupEnum.MakeupNone.makeup();
                select.setMakeupType(selectMakeupType);
                mFURenderer.onMakeupSelected(select);
            }
            sMakeups[selectMakeupType] = select;
            setMakeupProgress();
            notifyDataSetChanged();
            mMakeupNone.setImageResource(position >= 0 ? R.drawable.makeup_none_normal : R.drawable.makeup_none_checked);
        }

        public void setMakeupProgress() {
            if (selectMakeupType == -1 || selectPos[selectMakeupType] == -1) {
                mBeautySeekBar.setVisibility(View.GONE);
            } else {
                mBeautySeekBar.setVisibility(View.VISIBLE);
                mBeautySeekBar.setProgress((int) (100 * getMakeupLevel(getItems().get(selectPos[selectMakeupType]).bundleName())));
            }
        }

        private List<Makeup> getItems() {
            return MakeupEnum.getMakeupsByMakeupType(selectMakeupType);
        }

        public float getMakeupLevel(String makeupName) {
            Float level = sMakeupLevel.get(makeupName);
            float l = level == null ? 0.5f : level;
            return l;
        }

        public void setMakeupLevel(float makeupLevel) {
            String makeupName = getItems().get(selectPos[selectMakeupType]).bundleName();
            sMakeupLevel.put(makeupName, makeupLevel);
            mFURenderer.onMakeupLevelSelected(selectMakeupType, makeupLevel);
        }

        class MakeupHolder extends RecyclerView.ViewHolder {

            ImageView makeupImg;

            public MakeupHolder(View itemView) {
                super(itemView);
                makeupImg = (ImageView) itemView.findViewById(R.id.makeup_recycler_img);
            }
        }
    }
}
