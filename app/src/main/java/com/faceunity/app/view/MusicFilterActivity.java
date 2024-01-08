package com.faceunity.app.view;

import android.os.Handler;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.MusicFilterDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.media.midea.MediaPlayerHelper;
import com.faceunity.core.model.musicFilter.MusicFilter;
import com.faceunity.ui.control.MusicFilterControlView;
import com.faceunity.ui.entity.MusicFilterBean;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class MusicFilterActivity extends BaseFaceUnityActivity {


    private MusicFilterControlView mMusicFilterControlView;
    private MusicFilterDataFactory mMusicFilterDataFactory;
    private MediaPlayerHelper mediaPlayerHelper;

    private volatile boolean isMusicPlaying = false;

    private Handler mHandler;
    private final Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMusicPlaying) {
                MusicFilter musicFilter = mFURenderKit.getMusicFilter();
                if (musicFilter!=null){
                    musicFilter.setMusicTime(mediaPlayerHelper.getMusicCurrentPosition());
                }
                mHandler.postDelayed(this, 50L);
            }
        }
    };


    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_music_filter;
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mMusicFilterDataFactory.bindCurrentRenderer();
    }

    @Override
    public void initData() {
        super.initData();
        mHandler = new Handler();
        mMusicFilterDataFactory = new MusicFilterDataFactory(1, mMusicFilterListener);
        mediaPlayerHelper = new MediaPlayerHelper(this, mediaPlayerListener);
    }

    @Override
    public void initView() {
        super.initView();
        mMusicFilterControlView = (MusicFilterControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x212));
    }


    @Override
    public void bindListener() {
        super.bindListener();
        mMusicFilterControlView.bindDataFactory(mMusicFilterDataFactory);
    }


    @Override
    public void onPause() {
        if (isMusicPlaying)
            mediaPlayerHelper.pausePlay();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mediaPlayerHelper.release();
        super.onDestroy();
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.MUSIC_FILTER;
    }


    private final MediaPlayerHelper.MediaPlayerListener mediaPlayerListener = new MediaPlayerHelper.MediaPlayerListener() {
        @Override
        public void onStart() {
            isMusicPlaying = true;
            mHandler.post(mMusicRunnable);
        }

        @Override
        public void onPause() {
            isMusicPlaying = false;
        }

        @Override
        public void onStop() {
            isMusicPlaying = false;
        }

        @Override
        public void onCompletion() {
            isMusicPlaying = false;
        }
    };

    private final MusicFilterDataFactory.MusicFilterListener mMusicFilterListener = new MusicFilterDataFactory.MusicFilterListener() {

        @Override
        public void onMusicFilterSelected(MusicFilterBean data) {
            String path = data.getMusic();
            if (path != null) {
                mediaPlayerHelper.playMusic(path, true);
            } else {
                mediaPlayerHelper.stopPlay();
            }
        }
    };
}
