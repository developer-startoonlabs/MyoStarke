package com.example.sai.pheezeeapp.views;

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
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.sai.pheezeeapp.R;

public class ArcViewInside extends View {
    Context context;
    int min_angle=-10, max_angle=-95;
    Path path;
    int range_color = Color.BLUE;
    int mX=50, mY=50;
    int radius = 100;
    Paint mPaint ;
    boolean showPoints = false;
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
        radius-=60;
//        float left = mX+30-radius, top = mY+30-radius, right = mX-30+radius, bottom = mY-30+radius;
        float left = mX-radius, top = mY-radius, right = mX+radius, bottom = mY+radius;
//        oval = new RectF(mX - radius, mY - radius, mX + radius, mY + radius);
        oval = new RectF(left, top, right, bottom);
//        circle = new RectF(left+40,top+40,right-40,bottom-40);
        circle = new RectF(left,top,right,bottom);
        float scaleMarkSize = getResources().getDisplayMetrics().density * 16; // 16dp


        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(radius/10);
        //Setting the color of the circle
        mPaint.setColor(Color.GRAY);
        mPaint.setDither(true);                    // set the dither to true
        mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
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
        paint_angle.setTextSize(radius/5);
        paint_angle.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(max_angle).concat("°"),oval.centerX()-(radius/7),oval.centerY()+(radius/10),paint_angle);
        Point p = calculatePointOnArc(oval.centerX(),oval.centerY(),radius,-min_angle-(max_angle-min_angle));
        drawCircleOnArc(p,canvas,paint);

//        canvas.drawText("90",getWidth()/2,100,paint);
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
                canvas.drawText(String.valueOf(-145), stopX-(radius/8), stopY - (radius/16), paint);
            else if(i==270)
                canvas.drawText("\u00B1"+String.valueOf(180), stopX-(radius/12), stopY+(radius/12) , paint);
        }
        canvas.restore();
        invalidate();
    }

    private void drawCircleOnArc(Point p, Canvas canvas,Paint paint) {
//        if(max_angle<0)
//            canvas.drawCircle(p.x,p.y-30,radius/10,paint);
//        else
//            canvas.drawCircle(p.x,p.y+30,radius/10,paint);

//        if(max_angle<90 && max_angle>-90) {
//            if(max_angle>0) {
//                canvas.drawCircle(p.x - 30, p.y + 30, radius / 10, paint);
//            }
//            else {
//                canvas.drawCircle(p.x - 30, p.y - 30, radius / 10, paint);
//            }
//        }
//        else {
//            if(max_angle>0)
//                canvas.drawCircle(p.x + 30, p.y+30, radius / 10, paint);
//            else
//                canvas.drawCircle(p.x - 30, p.y-30, radius / 10, paint);
//        }

        canvas.drawCircle(p.x , p.y, radius / 10, paint);
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