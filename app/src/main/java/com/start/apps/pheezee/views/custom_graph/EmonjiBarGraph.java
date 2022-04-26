package com.start.apps.pheezee.views.custom_graph;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import start.apps.pheezee.R;

import java.util.HashMap;

abstract class GetDay {
    private static final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final String[] days2 = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static HashMap<String, Integer> map = new HashMap<String, Integer>() {
        {
            for(int i = 0; i < 7; i++)
            {
                this.put(days[i],i);
                this.put(days2[i],i);
                this.put(String.valueOf(i), i);
            }
        }
    };

    public static int getDay(int d){
        //Range of should be  0 <= d < 7
        return d;
    }

    public static int getDay(String s) {
        Integer k = map.get(s);
        if(k == null)
            return -1;
        return k;
    }
}

interface EmData{
    int getDay();
    int getUpperBound();
    int getLowerBound();
}

public class EmonjiBarGraph extends View {
    private  String[] bar_names = null;
    private boolean not_initialized;

    private float minX, minY, maxX, maxY, bar_height, bar_width, bar_gap, emonji_dimension, x1, x2, x_width;
    private int[] emonji = null, bar_color = null;
    private Bitmap[] emonji_bitmaps = null;
    private int num_bar = 0;

    private ChangeInterface change_functions = new ChangeInterface() {
        @Override
        public int colorChangeFunction(int l, int u) {
            float k = bar_color.length*((u - l)/180f);
            return Math.max(0, Math.min((int)k, bar_color.length - 1));
        }

        @Override
        public int emonjiChangeFunction(int l, int u) {
            float k = emonji.length*((u - l)/180f);
            return Math.max(0, Math.min((int)k, emonji.length - 1));
        }
    };
    public void setBarHeight(float bar_height) {
        this.bar_height = bar_height;
    }
    public void setBarWidth(float bar_width) {
        this.bar_width = bar_width;
    }
    public void setBarGap(float bar_gap) {
        this.bar_gap = bar_gap;
    }
    public void setEmonji_dimension(float emonji_dimension) {
        this.emonji_dimension = emonji_dimension;
    }
    public void setChangeInterface(ChangeInterface change_functions) {
        this.change_functions = change_functions;
    }
    public void setUpperLine(short u) {
        if(u < 180 && u > 0)
            this.u = u;
        invalidate();
    }
    public void setLowerLine(short l) {
        if(l < 180 && l > 0)
            this.l = l;
        invalidate();
    }
    public short getUpperLine(){
        return u;
    }
    public short getLowerLine(){
        return l;
    }

    public void notifyDataSetChanged() {
        for(int j = 0; j < num_bar; j++) {
            isSet[j] = true;
        }
        int d;
        if(data != null)
            for(EmData ed : data) {
                if(ed != null) {
                    d = ed.getDay();
                    if (d < num_bar) {
                        int k = 2 * d, l = k + 1;
                        p[k].setIntValues(anim_b[d][0], ed.getLowerBound());
                        p[l].setIntValues(anim_b[d][1], ed.getUpperBound());
                        isSet[d] = false;
                    }
                }
            }
        for(int j = 0; j < num_bar; j++){
            if(isSet[j]) {
                int k = 2 * j;
                p[k].setIntValues(0);
                p[k + 1].setIntValues(0);
            }
        }
        animator.setValues(p);
        animator.start();
    }
    public void setEmonjiResource(int[] emonji) {
        this.emonji = emonji;
        if(emonji != null) {
            emonji_bitmaps = new Bitmap[emonji.length];
            for (int i = 0; i < emonji.length; i++) {
                emonji_bitmaps[i] = BitmapFactory.decodeResource(getResources(), emonji[i]);
            }
            src.set(0, 0, emonji_bitmaps[0].getWidth(), emonji_bitmaps[0].getHeight());
        }
        invalidate();
    }
    public void setBitmapResource(Bitmap[] bitmaps){
        this.emonji_bitmaps = bitmaps;
    }
    public void setBarColors(int[] bar_color) {
        this.bar_color = bar_color;
    }
    public void setUpperLineColor(int color){
        this.uCol = color;
    }
    public void setLowerLineColor(int color) {
        this.lCol = color;
    }
    public void setAnimationDuration(long millisec) {
        animator.setDuration(millisec);
    }
    public void setBarNames(String[] bar_names) {
        this.bar_names = bar_names;
    }
    public void setBarData(EmData[] data){
        if(data.length > num_bar)
            return;
        this.data = data;
        notifyDataSetChanged();
    }

    private ValueAnimator animator;
    private PropertyValuesHolder p[];
    private String PROPERTY_L = "l", PROPERTY_U = "u";
    private boolean[] isSet;
    private int[][] anim_b;
    private EmData[] data;
    private int uCol = 0, lCol = 0;
    private short i, u = 180, l = 0;

    private void init() {
        not_initialized = true;
        gap = convertDpToPixel(10);
        paint.setStrokeWidth(4);
        paint.setTextSize(convertDpToPixel(12));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        anim_b = new int[num_bar][2];
        for(int j = 0; j < num_bar; j++) {
            anim_b[j][0] = 0;
            anim_b[j][1] = 180;
        }
        isSet = new boolean[num_bar];
        p = new PropertyValuesHolder[2 * num_bar];
        animator = new ValueAnimator();
        for(int j = 0; j < num_bar; j++) {
            String str = String.valueOf(j);
            p[2 * j] = PropertyValuesHolder.ofInt(PROPERTY_L + str, 0, 0);
            p[2 * j + 1] = PropertyValuesHolder.ofInt(PROPERTY_U + str, 180, 0);
        }
        animator.setValues(p);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for(short j = 0; j < num_bar; j++) {
                    String str = String.valueOf(j);
                    anim_b[j][0] = (int)animation.getAnimatedValue(PROPERTY_L + str);
                    anim_b[j][1] = (int)animation.getAnimatedValue(PROPERTY_U + str);
                }
                invalidate();
            }
        });
        animator.setDuration((long)(1000));
        color = Color.parseColor("#5B777575");
        x1 = convertDpToPixel(30);
        x2 = x1 + num_bar * x_width + (num_bar > 0 ? num_bar - 1 : 0) * bar_gap;
        setLayoutParams(new ViewGroup.LayoutParams((int)x2, (int)(emonji_dimension + bar_height)));
        if(data != null)
            notifyDataSetChanged();
    }
    public EmonjiBarGraph(Context context) {
        super(context);
        dm = (float)context.getResources().getDisplayMetrics().densityDpi;
        init();
    }

    public EmonjiBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        dm = (float)context.getResources().getDisplayMetrics().densityDpi;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EmonjiBarGraph, 0, 0);
        bar_width = a.getDimensionPixelSize(R.styleable.EmonjiBarGraph_barWidth, 0);
        bar_height  = a.getDimensionPixelSize(R.styleable.EmonjiBarGraph_barHeight, 0);
        emonji_dimension = a.getDimensionPixelSize(R.styleable.EmonjiBarGraph_emonjiDimension, 0);
        bar_gap = a.getDimensionPixelSize(R.styleable.EmonjiBarGraph_barGap, 0);
        lCol = a.getColor(R.styleable.EmonjiBarGraph_lowerLineColor, 0);
        uCol = a.getColor(R.styleable.EmonjiBarGraph_upperLineColor, 0);
        num_bar = a.getColor(R.styleable.EmonjiBarGraph_barNum, 0);
        final int id = a.getResourceId(R.styleable.EmonjiBarGraph_emonjiArray, 0);
        if(id != 0) {
            final TypedArray resourceArray = getResources().obtainTypedArray(id);
            emonji = new int[resourceArray.length()];
            for(int i = 0; i < resourceArray.length(); i++)
                emonji[i] = resourceArray.getResourceId(i, 0);
            resourceArray.recycle();
            setEmonjiResource(emonji);
        }
        final int id1 = a.getResourceId(R.styleable.EmonjiBarGraph_colorArray, 0);
        if(id != 0) {
            final TypedArray resourceArray = getResources().obtainTypedArray(id1);
            bar_color = new int[resourceArray.length()];
            for(int i = 0; i < resourceArray.length(); i++)
                bar_color[i] = resourceArray.getColor(i, 0);
            resourceArray.recycle();
        }
        x_width = Math.max(bar_width, emonji_dimension);
        a.recycle();
        init();
    }

    private int color;
    private Paint paint = new Paint();

    private Rect src = new Rect();
    private RectF dest = new RectF(), outer_rect = new RectF(), round_rect = new RectF(), shadow = new RectF();
    private Bar bar;

    private void beforeDraw() {
        minX = getPaddingLeft();
        minY = getPaddingTop() + 10;
        //maxX = getWidth() - getPaddingRight();
        maxY = getHeight() - getPaddingBottom() ;
        dest.set(minX, minY + gap,  emonji_dimension, emonji_dimension + gap);
        float barY1 = minY + emonji_dimension + 3 * gap;
        float barY2 = minY + emonji_dimension + bar_height;
        round_rect.set(minX, barY1, minX + bar_width, barY2 - 3f * gap);
        outer_rect.set(Math.min(minX, minX), minY, Math.max(minX + bar_width, emonji_dimension), barY2);
        shadow.set(outer_rect);
        bar = new Bar(src, dest, round_rect, outer_rect, shadow, barY1);
        not_initialized = false;
        animator.start();
        bar.translateX(x1);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(not_initialized) {
            beforeDraw();
        }
        i = 0;
//        if((anim_b[i][0] != anim_b[i][1]))
        if(num_bar != 0)
        {
            bar.setBounds(anim_b[i][0], anim_b[i][1]);
            bar.draw(canvas, paint);
        }
        float offset = 0;
        for(i = 1; i < num_bar; i++) {
            offset += x_width + bar_gap;
            bar.translateX(x_width + bar_gap);
//            if((anim_b[i][0] != anim_b[i][1]))
            {
                bar.setBounds(anim_b[i][0], anim_b[i][1]);
                bar.draw(canvas, paint);
            }
        }
        paint.setColor(Color.GRAY);
        int val = 180;
        for(int j = 0; j < 7; j++) {
            canvas.drawText(String.valueOf(val), 30, bar.uY + bar.getVal(val) + 10, paint);
            val -= 30;
        }
        bar.translateX(-offset);
        if(uCol != 0)
            paint.setColor(uCol);
        canvas.drawLine(x1, bar.uY + bar.getVal(u), x2, bar.uY + bar.getVal(u), paint);
        if(lCol != 0)
            paint.setColor(lCol);
        canvas.drawLine(x1, bar.uY + bar.getVal(l), x2, bar.uY + bar.getVal(l), paint);
    }
    private float gap;
    private float dm;
    private float convertDpToPixel(float dp){
        return dp * (dm / DisplayMetrics.DENSITY_DEFAULT);
    }
    private class Bar{
        private RectF emonji_dest, bar, outerRect, shadow;
        private Rect emonji_src;
        private float bar_height;
        private float width;
        private int lower_bound = 0, upper_bound = 180;
        private float mid;
        private float uY;
        private Bar(Rect emonji_src, RectF emonji_dest, RectF bar, RectF outerRect, RectF shadow, float uY) {
            this.emonji_dest = emonji_dest;
            this.bar = bar;
            this.outerRect = outerRect;
            this.emonji_src = emonji_src;
            this.bar_height = bar.bottom - bar.top;
            this.mid = (outerRect.right + outerRect.left)/2;
            this.shadow = shadow;
            this.shadow.set(
                    shadow.left - 5,
                    shadow.top - 5,
                    shadow.right + 5,
                    shadow.bottom + 5);
            this.uY = uY;
            locationX(emonji_dest, mid - emonji_dimension/2);
            locationX(bar, mid - bar_width/2);
        }
        private void translateX(float x) {
            translateRectX(emonji_dest, x);
            translateRectX(bar, x);
            translateRectX(outerRect, x);
            translateRectX(shadow, x);
            this.mid =  (outer_rect.right + outer_rect.left)/2;
        }
        private void translateY(float y) {
            translateRectY(emonji_dest, y);
            translateRectY(bar, y);
            translateRectY(outerRect, y);
            translateRectY(shadow, y);
        }
        private void translateRectX(RectF rect, float x) {
            if(rect != null) {
                rect.left += x;
                rect.right += x;
            }
        }
        private void translateRectY(RectF rect, float y) {
            if(rect != null) {
                rect.top += y;
                rect.bottom += y;
            }
        }
        private void locationX(RectF rect, float x) {
            if(rect != null) {
                rect.right = rect.right - rect.left + x;
                rect.left = x;
            }
        }
        private void draw(Canvas canvas, Paint paint) {
//            canvas.drawText();

            paint.setColor(color);
            canvas.drawRoundRect(shadow, 10, 10, paint);

            paint.setColor(Color.WHITE);
            canvas.drawRect(outer_rect, paint);

            paint.setColor(bar_color[change_functions.colorChangeFunction(anim_b[i][0], anim_b[i][1])]);
            if(lower_bound != 0 && upper_bound != 0) {
                if (emonji_bitmaps != null)
                    canvas.drawBitmap(emonji_bitmaps[change_functions.emonjiChangeFunction(anim_b[i][0], anim_b[i][1])], emonji_src, emonji_dest, paint);
                String lb = String.valueOf(lower_bound), ub = String.valueOf(upper_bound);
                canvas.drawText(ub, mid, round_rect.top - 10, paint);
                canvas.drawText(lb, mid, round_rect.bottom + gap + 4, paint);
                canvas.drawRoundRect(round_rect, 10, 10, paint);
            }
            if(bar_names != null)
                canvas.drawText(bar_names[i], mid, outer_rect.bottom - 10, paint);
        }
        public void setBounds(int lower_bound, int upper_bound) {
            this.lower_bound = lower_bound;
            this.upper_bound = upper_bound;
            round_rect.bottom = uY + getVal(lower_bound);
            round_rect.top = uY + getVal(upper_bound);
        }
        private float getVal(int bound) {
            return bar_height*((180f - bound)/180f);
        }
    }
}