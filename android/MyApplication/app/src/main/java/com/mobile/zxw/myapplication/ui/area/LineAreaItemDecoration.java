package com.mobile.zxw.myapplication.ui.area;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.zxw.myapplication.R;


/**
 * Description :
 * 
 * @author WSoBan
 * @date 2018/05/03
 */
public class LineAreaItemDecoration extends RecyclerView.ItemDecoration {

    private float spaceBottom;
    private Paint linePaint;

    public LineAreaItemDecoration(Context context, float interval) {
        spaceBottom = interval;
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(context.getResources().getColor(R.color.color_f3f4f5));
        linePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int itemCount = parent.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View view = parent.getChildAt(i);
            c.drawRect(view.getLeft(), view.getBottom(), view.getRight(), view.getBottom() + spaceBottom, linePaint);
            c.drawRect(view.getRight(), view.getTop(), view.getRight() + spaceBottom / 2, view.getBottom(), linePaint);
            c.drawRect(view.getLeft(), view.getTop(), view.getLeft() + spaceBottom / 2, view.getBottom(), linePaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) spaceBottom;
        outRect.right = (int) spaceBottom / 2;
        outRect.left = (int) spaceBottom / 2;
    }
}
