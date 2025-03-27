package com.example.slasher.modular;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NonScrollableScrollView extends ScrollView {

    public NonScrollableScrollView(Context context) {
        super(context);
    }

    public NonScrollableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Do not allow touch events to be intercepted
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Do not handle touch events
        return false;
    }
}

