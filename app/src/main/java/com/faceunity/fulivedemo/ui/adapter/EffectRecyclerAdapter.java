package com.faceunity.fulivedemo.ui.adapter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by tujh on 2018/6/29.
 */
public class EffectRecyclerAdapter extends RecyclerView.Adapter<EffectRecyclerAdapter.HomeRecyclerHolder> {
    private static final String TAG = "EffectRecyclerAdapter";
    private Context mContext;
    private int mEffectType;
    private List<Effect> mEffects;
    private int mPositionSelect = 1;
    private OnFUControlListener mOnFUControlListener;
    private OnDescriptionChangeListener mOnDescriptionChangeListener;
    private OnEffectSelectedListener mOnEffectSelectedListener;

    public EffectRecyclerAdapter(Context context, int effectType, OnFUControlListener onFUControlListener, List<Effect> effects) {
        mContext = context;
        mEffectType = effectType;
        mEffects = effects;
        mOnFUControlListener = onFUControlListener;
    }

    public void setPositionSelect(int positionSelect) {
        mPositionSelect = positionSelect;
    }

    @Override

    public HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_effect_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeRecyclerHolder holder, final int position) {
        holder.effectImg.setImageResource(mEffects.get(position).getIconId());
        holder.effectImg.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                if (mPositionSelect == position) {
                    return;
                }
                Effect effect = mEffects.get(mPositionSelect = position);
                if (mOnEffectSelectedListener != null) {
                    mOnEffectSelectedListener.onEffectSelected(effect);
                }
                playMusic(effect);
                notifyDataSetChanged();
                if (mOnDescriptionChangeListener != null) {
                    mOnDescriptionChangeListener.onDescriptionChangeListener(effect.getDescId());
                }
            }
        });
        if (mPositionSelect == position) {
            holder.effectImg.setBackgroundResource(R.drawable.effect_select);
        } else {
            holder.effectImg.setBackgroundResource(R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return mEffects.size();
    }

    static class HomeRecyclerHolder extends RecyclerView.ViewHolder {
        CircleImageView effectImg;

        HomeRecyclerHolder(View itemView) {
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
    private Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mOnFUControlListener.onMusicFilterTime(mediaPlayer.getCurrentPosition());
            }
            mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
        }
    };

    public void stopMusic() {
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

        if (effect.getType() != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        mediaPlayer = new MediaPlayer();
        mMusicHandler = new Handler();

        /**
         * mp3
         */
        try {
            AssetFileDescriptor descriptor = mContext.getAssets().openFd("effect/musicfilter/" + effect.getBundleName() + ".mp3");
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
                    mediaPlayer.start();
                    mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "playMusic: ", e);
            mediaPlayer = null;
        }
    }

    public void setOnDescriptionChangeListener(OnDescriptionChangeListener onDescriptionChangeListener) {
        mOnDescriptionChangeListener = onDescriptionChangeListener;
    }

    public interface OnDescriptionChangeListener {
        void onDescriptionChangeListener(int description);
    }

    public void setOnEffectSelectedListener(OnEffectSelectedListener onEffectSelectedListener) {
        mOnEffectSelectedListener = onEffectSelectedListener;
    }

    public interface OnEffectSelectedListener {
        void onEffectSelected(Effect effect);
    }
}