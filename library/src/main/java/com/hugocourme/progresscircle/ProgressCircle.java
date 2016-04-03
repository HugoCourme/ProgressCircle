package com.hugocourme.progresscircle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by HugoCOURME on 02/02/2016.
 */

public class ProgressCircle extends View {
    private static final String TAG = "ProgressCircle";

    /*  Size    */
    private float mRimWidth = 50.0f;
    private float mProgressWidth = 50.0f;
    private float mBorderWidth = 0.0f;
    private float mProgressLength = 0.0f;

    /*  Color   */
    private int mRimColor = Color.LTGRAY;
    private int mBorderColor = Color.TRANSPARENT;
    private int mProgressColor = Color.DKGRAY;

    /*  Paint   */
    private Paint mContourPaint = new Paint();
    private Paint mRimPaint = new Paint();
    private Paint mProgressPaint = new Paint();

    /*  Rect    */
    private RectF mCircleOuterBorder;
    private RectF mCircleInnerBorder;
    private RectF mCircleBounds;

    /*  Animation  */
    private int mProgressSpeed = 500;

    /*  Optional    */
    private boolean mWithBorder = false;

    public ProgressCircle(Context context) {
        super(context);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs);
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressCircle);

        mRimColor = ta.getColor(R.styleable.ProgressCircle_progress_unreached_color, mRimColor);
        mRimWidth = ta.getDimension(R.styleable.ProgressCircle_progress_unreached_bar_height, mRimWidth);

        mProgressColor = ta.getColor(R.styleable.ProgressCircle_progress_reached_color, mProgressColor);
        mProgressWidth = ta.getDimension(R.styleable.ProgressCircle_progress_reached_bar_height, mProgressWidth);

        mWithBorder = ta.getBoolean(R.styleable.ProgressCircle_progress_with_border, mWithBorder);

        if (mWithBorder) {
            mBorderColor = ta.getColor(R.styleable.ProgressCircle_progress_border_color, mBorderColor);
            mBorderWidth = ta.getDimension(R.styleable.ProgressCircle_progress_border_height, mBorderWidth);
        }

        mProgressSpeed = ta.getInteger(R.styleable.ProgressCircle_progress_animation_speed, mProgressSpeed);
        mProgressLength = ta.getInt(R.styleable.ProgressCircle_progress_value, 0) * 3.6f;

        ta.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mCircleBounds, 0, 360, false, mRimPaint);
        if (mWithBorder) {
            canvas.drawArc(mCircleInnerBorder, 0, 360, false, mContourPaint);
            canvas.drawArc(mCircleOuterBorder, 0, 360, false, mContourPaint);
        }

        canvas.drawArc(mCircleBounds, -90, mProgressLength, false, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(heightSize, widthSize);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((widthMode | heightMode) == MeasureSpec.AT_MOST) {
            size = (int) (Math.max(mRimWidth, mProgressWidth) * 4 + mBorderWidth * 2);
        }

        setMeasuredDimension(size, size);
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        Log.d(TAG, "onSizeChanged() called with : nW[" + newWidth + "]->oW[" + oldWidth + "] nH[" + newHeight + "]->oH[" + oldHeight + "]");
        setupBounds();
        setupPaint();
        invalidate();
    }

    private void setupBounds() {
        float extra = Math.max(mRimWidth, mProgressWidth);
        int paddingLT = Math.max(getPaddingLeft(), getPaddingTop());
        int paddingRB = Math.max(getPaddingRight(), getPaddingBottom());

        mBorderWidth = mWithBorder ? mBorderWidth : 0.0f;

        mCircleBounds = new RectF(
                extra / 2.0f + mBorderWidth + paddingLT,
                extra / 2.0f + mBorderWidth + paddingLT,
                getWidth() - extra / 2.0f - mBorderWidth - paddingRB,
                getHeight() - extra / 2.0f - mBorderWidth - paddingRB);

        if (!mWithBorder) {
            return;
        }

        mCircleInnerBorder = new RectF(
                mCircleBounds.left + extra / 2.0f + mBorderWidth / 2.0f + paddingLT,
                mCircleBounds.top + extra / 2.0f + mBorderWidth / 2.0f + paddingLT,
                mCircleBounds.right - extra / 2.0f - mBorderWidth / 2.0f - paddingRB,
                mCircleBounds.bottom - extra / 2.0f - mBorderWidth / 2.0f - paddingRB);
        mCircleOuterBorder = new RectF(
                mCircleBounds.left - extra / 2.0f - mBorderWidth / 2.0f - paddingLT,
                mCircleBounds.top - extra / 2.0f - mBorderWidth / 2.0f - paddingLT,
                mCircleBounds.right + extra / 2.0f + mBorderWidth / 2.0f + paddingRB,
                mCircleBounds.bottom + extra / 2.0f + mBorderWidth / 2.0f + paddingRB);
    }

    private void setupPaint() {
        mContourPaint.setColor(mBorderColor);
        mContourPaint.setStyle(Paint.Style.STROKE);
        mContourPaint.setAntiAlias(true);
        mContourPaint.setStrokeWidth(mBorderWidth);

        mRimPaint.setColor(mRimColor);
        mRimPaint.setStyle(Paint.Style.STROKE);
        mRimPaint.setAntiAlias(true);
        mRimPaint.setStrokeWidth(mRimWidth);

        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public void incrementProgress(float percentage) {
        this.startAnimation(mProgressLength + percentage * 3.6f);
    }

    public void setProgress(float progress) {
        progress %= 100;
        this.startAnimation(progress * 3.6f);
    }

    public void startAnimation(float endProgress) {
        ValueAnimator anim = ValueAnimator.ofFloat(mProgressLength, endProgress);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProgressCircle.this.mProgressLength = (Float) valueAnimator.getAnimatedValue();
                ProgressCircle.this.invalidate();
            }
        });
        anim.setDuration(mProgressSpeed);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    /************************************/
            /*  getter/setter   */

    /************************************/

    public void setBarWidth(float barWidth) {
        mProgressWidth = barWidth;
        postInvalidate();
    }

    public void setRimWidth(float rimWidth) {
        mRimWidth = rimWidth;
        postInvalidate();
    }

    public void setBorderWidth(float contourWidth) {
        mBorderWidth = contourWidth;
        postInvalidate();
    }

    public void setRimColor(int rimColor) {
        mRimColor = rimColor;
        postInvalidate();
    }

    public void setBarColor(int barColor) {
        mProgressColor = barColor;
        postInvalidate();
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        postInvalidate();
    }

    public void setProgressSpeed(int progressSpeed) {
        this.mProgressSpeed = progressSpeed;
    }

   /* private class ProgressAnimation extends Animation {

        private ProgressCircle progressCircle;
        private float increment = 0;
        private float currentAngle = 0;

        public ProgressAnimation(ProgressCircle progressCircle) {
            this.progressCircle = progressCircle;
        }

        public void setIncrement(float increment) {
            this.increment = increment;
            this.currentAngle = this.progressCircle.mProgressLength;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            progressCircle.mProgressLength = currentAngle + (increment * interpolatedTime);
            progressCircle.invalidate();
        }
    }*/
}
