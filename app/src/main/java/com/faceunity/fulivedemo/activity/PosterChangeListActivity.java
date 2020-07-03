package com.faceunity.fulivedemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.PosterChangeFaceTemplate;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ScreenUtils;

import java.util.List;

/**
 * 海报换脸列表界面
 *
 * @author Richie
 */
public class PosterChangeListActivity extends AppCompatActivity implements BaseRecyclerAdapter.OnItemClickListener<PosterChangeFaceTemplate> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.fullScreen(this);
        setContentView(R.layout.activity_poster_list);
        findViewById(R.id.iv_poster_list_back).setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                finish();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_poster_template);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        int h = getResources().getDimensionPixelSize(R.dimen.x17);
        int v = getResources().getDimensionPixelSize(R.dimen.x19);
        recyclerView.addItemDecoration(new SpaceItemDecoration(h, v));
        PosterTempAdapter posterTempAdapter = new PosterTempAdapter(PosterChangeFaceTemplate.getPosterTemplates(this));
        posterTempAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(posterTempAdapter);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter<PosterChangeFaceTemplate> adapter, View view, int position) {
        String tempPath = adapter.getItem(position).getPath();
        PosterChangeFaceCameraActivity.startSelfActivity(this, tempPath);
    }

    private class PosterTempAdapter extends BaseRecyclerAdapter<PosterChangeFaceTemplate> {

        PosterTempAdapter(@NonNull List<PosterChangeFaceTemplate> data) {
            super(data, R.layout.layout_poster_temp_grid);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, PosterChangeFaceTemplate item) {
            ImageView imageView = viewHolder.getViewById(R.id.iv_poster_temp);
            Glide.with(viewHolder.itemView.getContext()).load(item.getGridIconPath())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x10))))
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageView);
        }
    }

}
