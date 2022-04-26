package com.start.apps.smileyprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import start.apps.smileyprogressbar.R;

/**
 * Custom circle arc with max and min angle which is used in monitor and session summary.
 */
public class VerticalStarProgressView extends View {
    Context context;
    int num_of_starts = 5;
    int progress = 0;
    public VerticalStarProgressView(Context context) {
        super(context);
        init(null);
    }

    public VerticalStarProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public VerticalStarProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VerticalStarProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
    }

    /**
     *
     * @param canvas
     */

    @Override
    protected void onDraw(Canvas canvas) {
        int per_20 = (getHeight()*23)/100;
        int height_to_be_taken = getHeight()-2*per_20;
        int each_height = height_to_be_taken/num_of_starts;
        each_height = Math.min(each_height,getWidth());
        int right_to_be_taken = getWidth()/2+each_height/2;
        int left_to_be_taken = getWidth()/2 - each_height/2;
        int top = 0, bottom = 0;
        int temp_progress = progress;
        for (int i=num_of_starts-1;i>=0;i--){
            top = (i*each_height)+per_20+10;
            bottom = ((i+1)*each_height)+per_20+10;
            Drawable d = getResources().getDrawable(R.drawable.ic_star_black_24dp, null);
            d.setBounds(left_to_be_taken, top, right_to_be_taken, bottom);
            if(temp_progress!=0){
                d.mutate();
                d.setTint(getResources().getColor(R.color.home_orange));
                temp_progress--;
            }

            d.draw(canvas);
        }
    }

    public void setProgress(int progress){
        if(progress>num_of_starts){
            this.progress = num_of_starts;
        }else {
            this.progress = progress;
        }
        invalidate();
        postInvalidate();
    }


}
