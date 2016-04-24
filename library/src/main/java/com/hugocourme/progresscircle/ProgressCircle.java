package com.hugocourme.progresscircle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
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
    private TextFormat mTextFormat = TextFormat.NONE;
    private float mTextSize = 0.0f;
    private float mPercentageTextSize = 0.0f;

    /*  Color   */
    private int mRimColor = Color.LTGRAY;
    private int mBorderColor = Color.TRANSPARENT;
    private int mProgressColor = Color.DKGRAY;
    private int mTextColor = Color.TRANSPARENT;

    /*  Paint   */
    private Paint mContourPaint = new Paint();
    private Paint mRimPaint = new Paint();
    private Paint mProgressPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mPercentageTextPaint = new Paint();

    /*  Rect    */
    private RectF mCircleOuterBorder;
    private RectF mCircleInnerBorder;
    private RectF mCircleBounds;
    private Rect mTextBounds = new Rect();
    private Rect mPercentageTextBounds = new Rect();

    /*  Animation  */
    private int mProgressSpeed = 500;

    /*  Boolean    */
    private boolean mWithBorder = false;
    private boolean mCustomText = false;

    /*  Padding     */
    private int paddingLT = 0;
    private int paddingRB = 0;

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

        mTextFormat = TextFormat.fromId(ta.getInt(R.styleable.ProgressCircle_progress_text, mTextFormat.id));
        mTextColor = ta.getColor(R.styleable.ProgressCircle_progress_text_color, mTextColor);

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
        drawText(canvas);
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
        setupBounds();
        if (!mCustomText) {
            setupTextSize();
        }
        setupPaint();
        invalidate();
    }

    /**
     * Setup the bounds of every part of the component
     */
    private void setupBounds() {
        float extra = Math.max(mRimWidth, mProgressWidth);
        paddingLT = Math.max(getPaddingLeft(), getPaddingTop());
        paddingRB = Math.max(getPaddingRight(), getPaddingBottom());

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
                mCircleBounds.left + extra / 2.0f + mBorderWidth / 2.0f,
                mCircleBounds.top + extra / 2.0f + mBorderWidth / 2.0f,
                mCircleBounds.right - extra / 2.0f - mBorderWidth / 2.0f,
                mCircleBounds.bottom - extra / 2.0f - mBorderWidth / 2.0f);
        mCircleOuterBorder = new RectF(
                mCircleBounds.left - extra / 2.0f - mBorderWidth / 2.0f,
                mCircleBounds.top - extra / 2.0f - mBorderWidth / 2.0f,
                mCircleBounds.right + extra / 2.0f + mBorderWidth / 2.0f,
                mCircleBounds.bottom + extra / 2.0f + mBorderWidth / 2.0f);
    }

    private void setupTextSize() {
        if (mTextFormat == TextFormat.NONE) {
            mTextSize = 0.0f;
            mPercentageTextSize = 0.0f;
            return;
        }

        if (mWithBorder) {
            mTextSize = (mCircleInnerBorder.bottom - mCircleInnerBorder.top - mBorderWidth) / 2.5f;
        } else {
            mTextSize = (mCircleBounds.bottom - mCircleBounds.top - Math.max(mProgressWidth, mRimWidth)) / 2.5f;
        }
        mPercentageTextSize = mTextSize / 2f;

        if (mTextFormat == TextFormat.MEDIUM) {
            mTextSize /= 1.25f;
        }

        if (mTextFormat == TextFormat.SMALL) {
            mTextSize = mPercentageTextSize;
        }
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

        if (mTextFormat == TextFormat.NONE) {
            return;
        }
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        if (mTextFormat != TextFormat.SMALL) {
            mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }

        mPercentageTextPaint.setColor(mTextColor);
        mPercentageTextPaint.setAntiAlias(true);
        mPercentageTextPaint.setTextSize(mPercentageTextSize);
        mPercentageTextPaint.getTextBounds("%", 0, 1, mPercentageTextBounds);
    }

    /**
     * Draws the text displayed in the component
     *
     * @param canvas The canvas to draw in
     */
    protected void drawText(Canvas canvas) {
        if (mTextFormat == TextFormat.NONE) {
            return;
        }
        String text = String.valueOf(Math.round(mProgressLength / 3.6));
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        int x = paddingLT / 2 - paddingRB / 2 + getWidth() / 2 - mTextBounds.width() / 2 - mPercentageTextBounds.width() / 2;
        int y = paddingLT / 2 - paddingRB / 2 + getHeight() / 2 + mTextBounds.height() / 3;
        canvas.drawText(text, x, y, mTextPaint);

        x += mTextBounds.width() + mPercentageTextBounds.width() / 8;
        y += mPercentageTextBounds.height() / 2 - mTextBounds.height() / 2;
        canvas.drawText("%", x, y, mPercentageTextPaint);
    }

    /**
     * Increment the current progress of the component
     *
     * @param percentage
     */
    public void incrementProgress(float percentage) {
        this.startAnimation(mProgressLength + percentage * 3.6f);
    }

    protected void startAnimation(float endProgress) {
        ValueAnimator anim = ValueAnimator.ofFloat(mProgressLength, endProgress);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProgressCircle.this.mProgressLength = (Float) valueAnimator.getAnimatedValue();
                ProgressCircle.this.invalidate();
            }
        });
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(mProgressSpeed);
        anim.start();
    }

    /************************************/
            /*  getter/setter   */

    /************************************/

    public float getProgress() {
        return mProgressLength == 0 ? mProgressLength : mProgressLength / 3.6f;
    }

    public void setProgress(float progress) {
        progress %= 100;
        this.startAnimation(progress * 3.6f);
    }

    public TextFormat getTextFormat() {
        return mTextFormat;
    }

    public void setTextFormat(TextFormat format) {
        mTextFormat = format;
        postInvalidate();
    }

    public void setBarWidth(float barWidth) {
        mProgressWidth = barWidth;
        postInvalidate();
    }

    public float getBarWigth() {
        return mProgressWidth;
    }

    public float getRimWidth() {
        return mRimWidth;
    }

    public void setRimWidth(float rimWidth) {
        mRimWidth = rimWidth;
        postInvalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float contourWidth) {
        mBorderWidth = contourWidth;
        postInvalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mCustomText = true;
        postInvalidate();
    }

    public int getRimColor() {
        return mRimColor;
    }

    public void setRimColor(int rimColor) {
        mRimColor = rimColor;
        postInvalidate();
    }

    public int getBarColor() {
        return mProgressColor;
    }

    public void setBarColor(int barColor) {
        mProgressColor = barColor;
        postInvalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        postInvalidate();
    }

    public int getProgressSpeed() {
        return mProgressSpeed;
    }

    public void setProgressSpeed(int progressSpeed) {
        this.mProgressSpeed = progressSpeed;
    }

    private enum TextFormat {
        NONE(0), SMALL(1), MEDIUM(2), BIG(3);
        int id;

        TextFormat(int id) {
            this.id = id;
        }

        static TextFormat fromId(int id) {
            for (TextFormat s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return NONE;
        }
    }
}
