package com.faceunity.fulivedemo.ui.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.faceunity.fulivedemo.R;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
class StickerActionIcon {

    private Context context;
    // 资源缩放图片的位图
    private Bitmap srcIcon;
    private Rect rect;
    private Paint bmpPaint;
    private Paint shadowPaint;
    private boolean iconEnable = true;
    // 增加交互区域
    private int padding;
    private int enlargedSize;

    public StickerActionIcon(Context context) {
        this.context = context;
        rect = new Rect();
        bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.BLACK);
        // 40% black
        shadowPaint.setShadowLayer(context.getResources().getDimensionPixelSize(R.dimen.x4), 0, 0, Color.parseColor("#66000000"));
        padding = context.getResources().getDimensionPixelSize(R.dimen.x20);
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

    public void setSrcIcon(int resource) {
        srcIcon = BitmapFactory.decodeResource(context.getResources(), resource);
    }

    public void draw(Canvas canvas, float x, float y) {
        // 画顶点缩放图片
        rect.left = (int) (x - srcIcon.getWidth() / 2) - enlargedSize;
        rect.right = (int) (x + srcIcon.getWidth() / 2) + enlargedSize;
        rect.top = (int) (y - srcIcon.getHeight() / 2) - enlargedSize;
        rect.bottom = (int) (y + srcIcon.getHeight() / 2) + enlargedSize;
        canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2, shadowPaint);
        canvas.drawBitmap(srcIcon, null, rect, bmpPaint);
    }

    /**
     * 判断手指触摸的区域是否在顶点的操作按钮内
     *
     * @param event
     * @return
     */
    public boolean isInActionCheck(MotionEvent event) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left - padding && event.getX(0) <= right + padding
                && event.getY(0) >= top - padding && event.getY(0) <= bottom + padding;
    }
}
