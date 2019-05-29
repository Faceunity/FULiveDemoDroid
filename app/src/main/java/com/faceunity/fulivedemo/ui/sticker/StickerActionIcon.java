package com.faceunity.fulivedemo.ui.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.faceunity.fulivedemo.R;
import com.faceunity.utils.BitmapUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
class StickerActionIcon {
    private static final boolean SHOW_AREA = false;
    private Context context;
    // 资源缩放图片的位图
    private Bitmap srcIcon;
    private Rect rect;
    private Paint bmpPaint;
    private Paint shadowPaint;
    private Paint mRectPaint;
    private boolean iconEnable = true;
    // 增加交互区域
    private int padding;
    private int enlargedSize;

    StickerActionIcon(Context context) {
        this.context = context;
        rect = new Rect();
        bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.BLACK);
        // 40% black
        shadowPaint.setShadowLayer(context.getResources().getDimensionPixelSize(R.dimen.x4), 0, 0, Color.parseColor("#66000000"));
        // 热区 72px
        padding = context.getResources().getDimensionPixelSize(R.dimen.x12);
        if (SHOW_AREA) {
            mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRectPaint.setStyle(Paint.Style.STROKE);
            mRectPaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.x2));
            mRectPaint.setColor(Color.RED);
        }
    }

    public void setAlpha(int alpha) {
        bmpPaint.setAlpha(alpha);
    }

    public void setEnlargedSize(int enlargedSize) {
        this.enlargedSize = enlargedSize;
    }

    public boolean setIconEnable(boolean iconEnable) {
        if (this.iconEnable != iconEnable) {
            this.iconEnable = iconEnable;
            if (!iconEnable) {
                // 70% == 178
                bmpPaint.setAlpha(178);
            } else {
                bmpPaint.setAlpha(255);
            }
            return true;
        }
        return false;
    }

    public void setSrcIcon(String assetsPath) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.x48);
        try {
            InputStream isBounds = context.getAssets().open(assetsPath);
            InputStream isData = context.getAssets().open(assetsPath);
            srcIcon = BitmapUtil.decodeSampledBitmapFromStream(isBounds, isData, size, size);
            isBounds.close();
            isData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Canvas canvas, float x, float y) {
        // 画顶点缩放图片
        rect.left = (int) (x - srcIcon.getWidth() / 2) - enlargedSize;
        rect.right = (int) (x + srcIcon.getWidth() / 2) + enlargedSize;
        rect.top = (int) (y - srcIcon.getHeight() / 2) - enlargedSize;
        rect.bottom = (int) (y + srcIcon.getHeight() / 2) + enlargedSize;
        canvas.drawCircle(rect.centerX(), rect.centerY(), (float) rect.width() / 2, shadowPaint);
        canvas.drawBitmap(srcIcon, null, rect, bmpPaint);

        // 绘制热区
        if (SHOW_AREA) {
            RectF rectF = new RectF(rect.left - padding, rect.top - padding, rect.right + padding, rect.bottom + padding);
            canvas.drawRect(rectF, mRectPaint);
        }
    }

    /**
     * 判断手指触摸的区域是否在顶点的操作按钮内，热区 72px
     *
     * @param event
     * @return
     */
    public boolean isInActionCheck(MotionEvent event) {
        RectF rectF = new RectF(rect.left - padding, rect.top - padding, rect.right + padding, rect.bottom + padding);
        return rectF.contains(event.getX(), event.getY());
    }
}
