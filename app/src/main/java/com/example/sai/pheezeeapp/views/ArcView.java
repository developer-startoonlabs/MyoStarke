package com.example.sai.pheezeeapp.views;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.sai.pheezeeapp.R;

public class ArcView extends View {

    int min_angle=0, max_angle=180;
    int range_color = Color.BLUE;

    public ArcView(Context context) {
        super(context);
        init(null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet set){
        if(set==null){
            return;
        }
        Log.i("present","present");
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint mPaint = new Paint();
        int mX = 235,mY=300, radius;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        // Setting the color of the circle
        mPaint.setColor(Color.BLUE);

        // Draw the circle at (x,y) with radius 200
        radius = 190;
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            mX=400;
        }


        mPaint.setColor(Color.GRAY);
        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
        mPaint.setAntiAlias(true);

        RectF oval = new RectF(mX - radius, mY - radius, mX + radius, mY + radius);
        canvas.drawArc(oval, 180, 180, false, mPaint);
        mPaint.setColor(range_color);

        canvas.drawArc(oval, -min_angle,-(max_angle-min_angle) , false, mPaint);


        // Redraw the canvas
        invalidate();
    }

    public void setMinAngle(int min_angle){
        this.min_angle = min_angle;
        invalidate();
    }

    public void setMaxAngle(int max_angle){
        this.max_angle = max_angle;
        invalidate();
    }

    public void setRangeColor(int color){
        range_color = color;
        invalidate();
    }
}
