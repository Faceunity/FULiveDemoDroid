package com.faceunity.app.view;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.data.source.PosterChangeFaceSource;
import com.faceunity.ui.base.BaseDelegate;
import com.faceunity.ui.base.BaseListAdapter;
import com.faceunity.ui.base.BaseViewHolder;
import com.faceunity.ui.entity.PosterBean;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class PosterListActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_poster_list;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.home_function_name_poster_face);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x24), getResources().getDimensionPixelSize(R.dimen.x12)));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void bindListener() {

        BaseListAdapter posterAdapter = new BaseListAdapter(PosterChangeFaceSource.buildPoster(), new BaseDelegate<PosterBean>() {
            @Override
            public void convert(int viewType, BaseViewHolder helper, PosterBean data, int position) {
                Glide.with(PosterListActivity.this).load(data.getGridIconPath())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x10))))
                        .apply(RequestOptions.centerCropTransform()).into(((ImageView) helper.getView(R.id.iv_poster)));
            }

            @Override
            public void onItemClickListener(View view, PosterBean data, int position) {
                PosterFaceAcquisitionActivity.startActivity(PosterListActivity.this, data.getPath(), data.getWarpIntensity());
            }
        }, R.layout.list_item_poster);
        mRecyclerView.setAdapter(posterAdapter);
        findViewById(R.id.iv_back).setOnClickListener((view) -> onBackPressed());
    }


    private static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int height;
        private final int width;

        SpaceItemDecoration(int height, int width) {
            this.height = height;
            this.width = width;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = height;
            if (parent.getChildLayoutPosition(view) % 2 == 0) {
                outRect.left = 0;
                outRect.right = width;
            } else {
                outRect.left = width;
                outRect.right = 0;
            }
        }
    }


}
