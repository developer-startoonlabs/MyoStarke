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
    int mX=50, mY=50;
    int radius = 190;
    Paint mPaint ;
    RectF oval = new RectF(mX - radius, mY - radius, mX + radius, mY + radius);
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

    private void init(@Nullable AttributeSet set){
        mPaint = new Paint();

        if(set==null){
            return;
        }
        TypedArray ta  = getContext().obtainStyledAttributes(R.styleable.ArcView);
        range_color = ta.getColor(R.styleable.ArcView_arc_color,Color.parseColor("#00B386"));
        radius = ta.getDimensionPixelSize(R.styleable.ArcView_arc_radius,radius);
        setRangeColor(range_color);
        setRadius(radius);


        ta.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mX = canvas.getWidth()/2;
        mY = canvas.getHeight()/2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        // Setting the color of the circle
        mPaint.setColor(Color.GRAY);
        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
        mPaint.setAntiAlias(true);
        float padding = 30;
        float size = getWidth();
        float width = size - (2 * padding);
        float height = size - (2 * padding);
        oval.left =padding;
        oval.top = padding;
        oval.right = width;
        oval.bottom = width;
        canvas.drawArc(oval, 180, 180, false, mPaint);
        mPaint.setColor(range_color);
        canvas.drawArc(oval, -min_angle,-(max_angle-min_angle) , false, mPaint);
    }

    public void setMinAngle(int min_angle){
        this.min_angle = min_angle;
        invalidate();
        postInvalidate();
    }

    public void setMaxAngle(int max_angle){
        this.max_angle = max_angle;
        invalidate();
        postInvalidate();
    }

    public void setRangeColor(int color){
        range_color = color;
        invalidate();
        postInvalidate();
    }

    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
        postInvalidate();
    }
}
