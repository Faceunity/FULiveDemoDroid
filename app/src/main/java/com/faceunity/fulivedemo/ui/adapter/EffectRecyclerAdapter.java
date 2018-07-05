package com.faceunity.fulivedemo.ui.adapter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tujh on 2018/6/29.
 */
public class EffectRecyclerAdapter extends RecyclerView.Adapter<EffectRecyclerAdapter.HomeRecyclerHolder> {
    private static final String TAG = EffectRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    private int mEffectType;
    private List<Effect> mEffects;
    private int mPositionSelect = 1;
    private OnFUControlListener mOnFUControlListener;
    private OnDescriptionChangeListener mOnDescriptionChangeListener;

    public EffectRecyclerAdapter(Context context, int effectType, OnFUControlListener onFUControlListener) {
        mContext = context;
        mEffectType = effectType;
        mEffects = EffectEnum.getEffectsByEffectType(mEffectType);
        mOnFUControlListener = onFUControlListener;
    }

    @Override

    public HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_effect_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeRecyclerHolder holder, final int position) {

        holder.effectImg.setImageResource(mEffects.get(position).resId());
        holder.effectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPositionSelect == position) {
                    return;
                }
                Effect click = mEffects.get(mPositionSelect = position);
                mOnFUControlListener.onEffectSelected(click);
                playMusic(click);
                notifyDataSetChanged();
                if (mOnDescriptionChangeListener != null)
                    mOnDescriptionChangeListener.onDescriptionChangeListener(click.description());
            }
        });
        if (mPositionSelect == position) {
            holder.effectImg.setBackgroundResource(R.drawable.effect_select);
        } else {
            holder.effectImg.setBackgroundResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return mEffects.size();
    }

    class HomeRecyclerHolder extends RecyclerView.ViewHolder {

        CircleImageView effectImg;

        public HomeRecyclerHolder(View itemView) {
            super(itemView);
            effectImg = (CircleImageView) itemView.findViewById(R.id.effect_recycler_img);
        }
    }

    public void onResume() {
        playMusic(mEffects.get(mPositionSelect));
    }

    public void onPause() {
        stopMusic();
    }

    public Effect getSelectEffect() {
        return mEffects.get(mPositionSelect);
    }

    private MediaPlayer mediaPlayer;
    private Handler mMusicHandler;
    private static final int MUSIC_TIME = 50;
    Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying())
                mOnFUControlListener.onMusicFilterTime(mediaPlayer.getCurrentPosition());

            mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
        }
    };

    public void stopMusic() {
        if (mEffectType != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mMusicHandler.removeCallbacks(mMusicRunnable);

        }
    }

    public void playMusic(Effect effect) {
        if (mEffectType != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        stopMusic();

        if (effect.effectType() != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        mediaPlayer = new MediaPlayer();
        mMusicHandler = new Handler();

        /**
         * mp3
         */
        try {
            AssetFileDescriptor descriptor = mContext.getAssets().openFd("musicfilter/" + effect.bundleName() + ".mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    //mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();

                    mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    public void setOnDescriptionChangeListener(OnDescriptionChangeListener onDescriptionChangeListener) {
        mOnDescriptionChangeListener = onDescriptionChangeListener;
    }

    public interface OnDescriptionChangeListener {
        void onDescriptionChangeListener(String description);
    }
}