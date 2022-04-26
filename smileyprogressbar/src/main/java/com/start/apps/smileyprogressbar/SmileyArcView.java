package com.start.apps.smileyprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import start.apps.smileyprogressbar.R;

/**
 * Custom circle arc with max and min angle which is used in monitor and session summary.
 */
public class SmileyArcView extends View {
    Context context;
    int progress=20;
    int range_color = Color.BLUE;
    int mX=50, mY=50;
    int radius = 100, radius_smile=40;
    Paint mPaint, image_paint, eyes_color ;
    RectF face_container, image_container, left_eye_container, right_eye_container;
    public SmileyArcView(Context context) {
        super(context);
        init(null);
    }

    public SmileyArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public SmileyArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmileyArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        mPaint = new Paint();
        image_paint = new Paint();
        eyes_color = new Paint();
        eyes_color.setColor(Color.BLACK);
        image_paint.setColor(getResources().getColor(R.color.home_orange));
        if(set==null){
            return;
        }
        TypedArray ta  = getContext().obtainStyledAttributes(R.styleable.ArcView);
        range_color = ta.getColor(R.styleable.ArcView_arc_color, ContextCompat.getColor(context,R.color.pitch_black));
        radius = ta.getDimensionPixelSize(R.styleable.ArcView_arc_radius,radius);
        setRangeColor(range_color);
        setRadius(radius);


        ta.recycle();
    }

    /**
     *
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        radius = Math.min(getHeight()/2,getWidth()/2);

        mX = getWidth()/2;
        mY = getHeight()/2;
        radius = Math.min(getWidth(), getHeight()) / 2;
        radius_smile=radius-60;
//        mY = radius+20;
        float left = mX-radius_smile, top = mY-radius_smile, right = mX+radius_smile, bottom = mY+radius_smile;
        float left_radius = mX-radius, top_radius = mY-radius, right_radius = mX+radius, bottom_radius = mY+radius;
        face_container = new RectF(left, top, right, bottom);
        image_container = new RectF(left_radius, top_radius, right_radius, bottom_radius);
        left_eye_container = new RectF(face_container.centerX(), face_container.centerY()-radius, face_container.centerX()+radius, face_container.centerY());
        right_eye_container = new RectF(face_container.centerX(), face_container.centerY()-radius, face_container.centerX()-radius, face_container.centerY());

        canvas.drawCircle(face_container.centerX(), face_container.centerY(),radius,image_paint);
        canvas.drawCircle(left_eye_container.centerX()-radius/6, left_eye_container.centerY()+radius/6,radius/6,eyes_color);
        canvas.drawCircle(right_eye_container.centerX()+radius/6, right_eye_container.centerY()+radius/6,radius/6,eyes_color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(radius_smile/10);
        //Setting the color of the circle
        mPaint.setColor(getResources().getColor(R.color.home_orange));
        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
        mPaint.setAntiAlias(true);
        canvas.drawArc(face_container, 180, -180, false, mPaint);
        mPaint.setColor(range_color);
        canvas.drawArc(face_container, 90,-(progress/2) , false, mPaint);
        canvas.drawArc(face_container, 90,(progress/2) , false, mPaint);
        Paint paint = new Paint();
        paint.setTextSize(radius_smile/6);
        paint.setColor(range_color);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(radius_smile/2);
        Paint paint_angle = new Paint();
        paint_angle.setTextSize(radius_smile/5);
        paint_angle.setColor(Color.WHITE);

        canvas.save();
        canvas.restore();
        invalidate();
    }

    public void setRangeColor(int color){
        this.range_color = color;
        invalidate();
        postInvalidate();
    }

    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
        postInvalidate();
    }

    public void setProgress(int progress){
        if(progress>180){
            this.progress = 180;
        }else if(progress<-180){
            this.progress = -180;
        }else {
            this.progress = progress;
        }
        invalidate();
        postInvalidate();
    }
}
