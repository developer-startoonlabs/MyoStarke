package com.start.apps.pheezee.views;

import static android.graphics.Paint.Join.MITER;
import static android.graphics.Paint.Join.ROUND;
import static android.graphics.Paint.Style.STROKE;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import start.apps.pheezee.R;

/**
 * Custom circle arc with max and min angle which is used in monitor and session summary.
 */
public class ArcViewInside extends View {
    Context context;
    boolean enableMinMaxLines = false;
    int min = 0, max = 0;
    int min_angle=-10, max_angle=-95;
    Path path;
    int range_color = Color.BLUE;
    int mX=50, mY=50;
    int radius = 100;
    Paint mPaint ;
    RectF oval, circle;
    Bitmap bitmap;
    public ArcViewInside(Context context) {
        super(context);
        init(null);
    }



    public ArcViewInside(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public ArcViewInside(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcViewInside(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        mPaint = new Paint();
        path = new Path();
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.active_dot);
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
        radius-=45;
//        mY = radius+20;
        float left = mX-radius, top = mY-radius, right = mX+radius, bottom = mY+radius;
        oval = new RectF(left, top, right, bottom);
        circle = new RectF(left,top,right,bottom);

//        for new circle
//        circle2 = new RectF(left,top,right,bottom);
        float scaleMarkSize = getResources().getDisplayMetrics().density * 16; // 16dp
        mPaint.setStyle(STROKE);
        mPaint.setStrokeWidth(radius/10);
        //Setting the color of the circle
        mPaint.setColor(Color.GRAY);
        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStyle(STROKE);       // set to STOKE
        mPaint.setStrokeJoin(ROUND);    // set the join to round you want
        mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
        mPaint.setAntiAlias(true);
        canvas.drawArc(oval, 0, 360, false, mPaint);
        mPaint.setColor(range_color);
        canvas.drawArc(oval, -min_angle,-(max_angle-min_angle) , false, mPaint);
        Paint paint = new Paint();
        paint.setTextSize(radius/6);
        paint.setColor(range_color);
        canvas.drawCircle(oval.centerX(),oval.centerY(),radius/2,paint);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(radius/2);
        Paint paint_angle = new Paint();
        paint_angle.setTextSize(radius/4);
        paint_angle.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(max_angle).concat("°"),oval.centerX()-(radius/7),oval.centerY()+(radius/10),paint_angle);
        Point p = calculatePointOnArc(oval.centerX(),oval.centerY(),radius,-min_angle-(max_angle-min_angle));
        drawCircleOnArc(p,canvas,paint);
        Paint range_paint = new Paint();
        range_paint.setTextSize(radius/6);
        range_paint.setColor(Color.RED);
        if(enableMinMaxLines)
            drawMinMaxLines(canvas,oval.centerX(),oval.centerY(),radius,-min,-max);

        canvas.save();
        for (int i = 0; i < 360; i += 45) {
            float angle = (float) Math.toRadians(i); // Need to convert to radians first

            float startX = (float) (mX + radius * Math.sin(angle));
            float startY = (float) (mY - radius * Math.cos(angle));

            float stopX = (float) (mX + (radius - scaleMarkSize) * Math.sin(angle));
            float stopY = (float) (mY - (radius - scaleMarkSize) * Math.cos(angle));

            canvas.drawLine(startX, startY, stopX, stopY, paint);
//            if(i==90)
//                canvas.drawText(String.valueOf(0), startX+(radius/12), startY+(radius/12), paint);
//            else if(i==0)
//                canvas.drawText(String.valueOf(90), startX-(radius/10), startY-(radius/12), paint);
//            else if(i==45)
//                canvas.drawText(String.valueOf(45), startX, startY - (radius/12), paint);
//            else if(i==315)
//                canvas.drawText(String.valueOf(135), startX-(radius/6), startY - (radius/12), paint);
//            else if(i==135)
//                canvas.drawText(String.valueOf(-45), startX, startY + (radius/12), paint);
//            else if(i==180)
//                canvas.drawText(String.valueOf(-90), startX-(radius/6), startY + (radius/6), paint);
//            else if(i==225)
//                canvas.drawText(String.valueOf(-145), startX-(radius/3), startY + (radius/6), paint);
//            else if(i==270)
//                canvas.drawText("\u00B1"+String.valueOf(180), startX-(radius/3), startY+(radius/12) , paint);

            if(i==90)
                canvas.drawText(String.valueOf(0), stopX-(radius/10), stopY+(radius/14), paint);
            else if(i==0)
                canvas.drawText(String.valueOf(90), stopX-(radius/8), stopY+(radius/6), paint);
            else if(i==45)
                canvas.drawText(String.valueOf(45), stopX-(radius/6), stopY + (radius/8), paint);
            else if(i==315)
                canvas.drawText(String.valueOf(135), stopX-(radius/8), stopY + (radius/6), paint);
            else if(i==135)
                canvas.drawText(String.valueOf(-45), stopX-(radius/6), stopY - (radius/14), paint);
            else if(i==180)
                canvas.drawText(String.valueOf(-90), stopX-(radius/6), stopY - (radius/12), paint);
            else if(i==225)
                canvas.drawText(String.valueOf(-135), stopX-(radius/8), stopY - (radius/16), paint);
            else if(i==270)
                canvas.drawText(String.valueOf(180), stopX-(radius/12), stopY+(radius/12) , paint);
        }
        canvas.restore();
        invalidate();
    }


    private void drawMaxMinCircleOnArc(Point p1, Point p2, Canvas canvas, Paint paint1, Paint paint) {
        canvas.drawCircle(p1.x , p1.y, radius / 10, paint1);
        canvas.drawCircle(p2.x , p2.y, radius / 10, paint);


    }

    private void drawMinMaxLines(Canvas canvas,float circleCeX, float circleCeY, float circleRadius, float startAngle, float endAngle){
        float scaleMarkSize = getResources().getDisplayMetrics().density * 16;
        float minAngle = (float) Math.toRadians(120); // Need to convert to radians first
        float maxAngle = (float) Math.toRadians(-30);
        double endAngleRadian = Math.toRadians(startAngle);
        int startX = (int) Math.round((circleCeX + (circleRadius+25) * Math.cos(endAngleRadian)));
        int startY = (int) Math.round((circleCeY + (circleRadius+25) * Math.sin(endAngleRadian)));

        int stopX = (int) Math.round((circleCeX + (circleRadius-25) * Math.cos(endAngleRadian)));
        int stopY = (int) Math.round((circleCeY + (circleRadius-25) * Math.sin(endAngleRadian)));

        endAngleRadian = Math.toRadians(endAngle);
        int startX1 = (int) Math.round((circleCeX + (circleRadius+25) * Math.cos(endAngleRadian)));
        int startY1 = (int) Math.round((circleCeY + (circleRadius+25) * Math.sin(endAngleRadian)));

        int stopX1 = (int) Math.round((circleCeX + (circleRadius-25) * Math.cos(endAngleRadian)));
        int stopY1 = (int) Math.round((circleCeY + (circleRadius-25) * Math.sin(endAngleRadian)));

        Paint range_paint = new Paint();
        range_paint.setTextSize(radius/6);
        range_paint.setColor(ContextCompat.getColor(context,R.color.home_orange));
        range_paint.setStrokeWidth(20);
        canvas.drawLine(startX, startY, stopX, stopY, range_paint);
        range_paint.setColor(ContextCompat.getColor(context,R.color.home_orange));
        canvas.drawLine(startX1, startY1, stopX1, stopY1, range_paint);

    }
    private void drawCircleOnArc(Point p, Canvas canvas,Paint paint) {
        canvas.drawCircle(p.x , p.y, radius / 10, paint);
    }

    public void setEnableMinMaxLines(boolean enableMinMaxLines){
        this.enableMinMaxLines = enableMinMaxLines;
        invalidate();
        postInvalidate();
    }

    public void setMinMaxLinesValues(int min, int max){
        this.min = min;
        this.max = max;
        invalidate();
        postInvalidate();
    }

    public void setEnableAndMinMax(int min,int max, boolean enableMinMaxLines){
        this.enableMinMaxLines = enableMinMaxLines;
        this.min = min;
        this.max = max;
        invalidate();
        postInvalidate();
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
        this.range_color = color;
        invalidate();
        postInvalidate();
    }

    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
        postInvalidate();
    }


    private Point calculatePointOnArc(float circleCeX, float circleCeY, float circleRadius, float endAngle)
    {
        Point point = new Point();
        double endAngleRadian = Math.toRadians(endAngle);

        int pointX = (int) Math.round((circleCeX + circleRadius * Math.cos(endAngleRadian)));
        int pointY = (int) Math.round((circleCeY + circleRadius * Math.sin(endAngleRadian)));
        point.x = pointX;
        point.y = pointY;

        return point;
    }



}
