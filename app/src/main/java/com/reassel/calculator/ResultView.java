package com.reassel.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import androidx.appcompat.widget.AppCompatTextView;

public class ResultView extends AppCompatTextView {

    public ResultView(Context context) {
        super(context);
        init();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void decreaseTextSize(float size) {
        float currentSize = getTextSize();
        setTextSize(currentSize - size);
    }

    public void move(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, long duration) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(duration);
        startAnimation(animation);
    }
}
