package com.faceunity.fulivedemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.faceunity.fulivedemo.entity.PosterTemplate;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;

import java.util.List;

/**
 * 海报换脸列表界面
 */
public class PosterListActivity extends AppCompatActivity implements View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener {
    private static final String TAG = "PosterListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_poster_list);
        findViewById(R.id.iv_poster_list_back).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_poster_template);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        int h = getResources().getDimensionPixelSize(R.dimen.x17);
        int v = getResources().getDimensionPixelSize(R.dimen.x19);
        recyclerView.addItemDecoration(new VHSpaceItemDecoration(v, h));
        PosterTempAdapter posterTempAdapter = new PosterTempAdapter(PosterTemplate.getPosterTemplates(this));
        posterTempAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(posterTempAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_poster_list_back) {
            finish();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
        String tempPath = ((PosterTemplate) adapter.getItem(position)).getPath();
        FUPosterTakeActivity.startSelfActivity(this, tempPath);
    }

    private class PosterTempAdapter extends BaseRecyclerAdapter<PosterTemplate> {

        PosterTempAdapter(@NonNull List<PosterTemplate> data) {
            super(data, R.layout.layout_poster_temp_grid);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, PosterTemplate item) {
            ImageView imageView = viewHolder.getViewById(R.id.iv_poster_temp);
            Glide.with(viewHolder.itemView.getContext()).load(item.getGridIconPath()).centerCrop().into(imageView);
        }
    }

}
