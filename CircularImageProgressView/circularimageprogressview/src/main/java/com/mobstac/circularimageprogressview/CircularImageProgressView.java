package com.mobstac.circularimageprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Copyright (C) 2017 Mobstac, Inc.
 * All rights reserved
 *
 * @author Kislay
 * @since 24/03/17
 */


public class CircularImageProgressView extends View {

    private Context mContext;

    private Paint mArcPaintBackground, mArcPaintPrimary, mImagePaint;
    private final int MIN_SIZE = dpToPixel(120);

    private RectF mArcRect = new RectF();
    private Rect mImgRect = new Rect();
    private int mProgressColor = Color.parseColor("#FF4081");
    private int mProgressBackgroundColor = Color.parseColor("#757575");

    private final int STROKE_WIDTH_MIN = 5;
    private final int STROKE_WIDTH_MAX = 75;
    private int mStrokeWidth = 15;

    private Bitmap mBitmap;
    private boolean mProgressHidden = false, mImageHidden = false;


    private final int PROGRESS_MIN = 0;
    private int mProgressMax = 100;
    private int mProgress = 0;
    private int mCurrentProgress = 0;

    private int mImageResource = -1;
    private int mImageTintColor = Color.WHITE;
    private int mImagePadding = 120;
    private final float IMAGE_PADDING_SCALE = 4.66F;
    private ColorFilter mImageFilter;

    public CircularImageProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public CircularImageProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mContext = context;

        mProgressColor = getAccentColor(mContext);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularImageProgressView, 0, 0);
            mStrokeWidth = a.getInteger(R.styleable.CircularImageProgressView_progress_width, mStrokeWidth);
            mProgress = a.getInteger(R.styleable.CircularImageProgressView_progress, mProgress);
            mImageResource = a.getResourceId(R.styleable.CircularImageProgressView_image, mImageResource);
            mProgressColor = a.getColor(R.styleable.CircularImageProgressView_progress_color, mProgressColor);
            mProgressBackgroundColor = a.getColor(R.styleable.CircularImageProgressView_progress_background_color, mProgressBackgroundColor);
            mProgressMax = a.getInt(R.styleable.CircularImageProgressView_max, mProgressMax);
            mImageTintColor = a.getColor(R.styleable.CircularImageProgressView_image_tint, mImageTintColor);
            a.recycle();
        }

        initImagePaint();
        initArcPaint();

        if (isValidImageResource(mContext, mImageResource))
            mBitmap = getBitmapFromVectorDrawable(context, mImageResource);
        setProgress(mProgress);
    }

    public void setProgress(final int progress) {
        if (mProgressHidden || progress > mProgressMax || progress < PROGRESS_MIN)
            return;
        mCurrentProgress = progress;
        mProgress = mProgressMax == 100 ? progress : (int) (((float) progress / mProgressMax) * 100F);
        invalidate();
    }

    public void clearImageTint() {
        mImageFilter = null;
        initImagePaint();
        invalidate();
    }

    public void setImageTint(int color) {
        mImageFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        initImagePaint();
        invalidate();
    }

    public void setImageResource(int resource) {
        mImageResource = resource;
        invalidate();
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mProgressHidden) {
            // background full circle arc
            canvas.drawArc(mArcRect, 270, 360, false, mArcPaintBackground);
            // draw starting at top of circle in clockwise direction
            canvas.drawArc(mArcRect, 270, (360 * (mProgress / 100f)), false, mArcPaintPrimary);
        }

        //draw image in the center
        if (!mImageHidden)
            if (isValidImageResource(mContext, mImageResource) && mBitmap != null)
                canvas.drawBitmap(mBitmap, null, mImgRect, mImagePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = getDefaultSize(MIN_SIZE, widthMeasureSpec);
        final int height = getDefaultSize(MIN_SIZE, heightMeasureSpec);
        int dimensionMin = MIN_SIZE;
        int dimensionMax = MIN_SIZE;

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode == View.MeasureSpec.EXACTLY || widthMode == View.MeasureSpec.EXACTLY) {
            dimensionMin = Math.min(width, height);
            dimensionMax = Math.max(width, height);
        } else {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(MIN_SIZE, View.MeasureSpec.EXACTLY);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(MIN_SIZE, View.MeasureSpec.EXACTLY);
        }

        int paddingMax = Math.max(
                Math.max(getPaddingLeft(), getPaddingRight()),
                Math.max(getPaddingTop(), getPaddingBottom())
        );

        int arcDiameter = dimensionMin - paddingMax - mStrokeWidth;
        float top = height < width ? (dimensionMin / 2 - (arcDiameter / 2)) : (dimensionMax / 2 - (arcDiameter / 2));
        float left = height > width ? (dimensionMin / 2 - (arcDiameter / 2)) : (dimensionMax / 2 - (arcDiameter / 2));

        //Set the bound for the rectangle containing the progress arc
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mImagePadding = (int) ((arcDiameter / IMAGE_PADDING_SCALE));

        //Set the bound for the image, with some padding from the progress arc
        mImgRect.set(
                (int) left + mImagePadding,
                (int) top + mImagePadding,
                (int) left + arcDiameter - mImagePadding,
                (int) top + arcDiameter - mImagePadding
        );

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCircleWidth(int width) {
        if (width < STROKE_WIDTH_MIN)
            mStrokeWidth = STROKE_WIDTH_MIN;
        else if (width > STROKE_WIDTH_MAX)
            mStrokeWidth = STROKE_WIDTH_MAX;
        else
            mStrokeWidth = width;
        initArcPaint();
        requestLayout();
    }

    public int getCircleWidth() {
        return mStrokeWidth;
    }

    public int getMax() {
        return mProgressMax;
    }


    /**
     * @param drawableId Resource ID of drawable, can also be a vector
     * @return Bitmap object
     */
    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {

        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void hideProgress() {
        mProgressHidden = true;
        invalidate();
    }

    public void showProgress() {
        mProgressHidden = false;
        invalidate();
    }

    public void hideImage() {
        mImageHidden = true;
        invalidate();
    }

    public void showImage() {
        mImageHidden = false;
        invalidate();
    }

    private int getAccentColor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    private boolean isValidImageResource(Context context, int resourceId) {
        String resourceName = String.valueOf(resourceId);
        return getResources().getIdentifier(resourceName, "drawable", context.getPackageName()) > 0;
    }

    /**
     * Convert DP to device specific pixels
     *
     * @param dp Dimension in dp
     * @return Dimension in pixels
     */
    private int dpToPixel(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void initArcPaint() {
        mArcPaintBackground = new Paint() {
            {
                setDither(true);
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Cap.BUTT);
                setStrokeJoin(Join.BEVEL);
                setColor(mProgressBackgroundColor);
                setStrokeWidth(mStrokeWidth);
                setAntiAlias(true);
            }
        };

        mArcPaintPrimary = new Paint() {
            {
                setDither(true);
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Cap.BUTT);
                setStrokeJoin(Join.BEVEL);
                setColor(mProgressColor);
                setStrokeWidth(mStrokeWidth);
                setAntiAlias(true);
            }
        };
    }

    private void initImagePaint() {
        mImagePaint = new Paint() {
            {
                if (mImageFilter != null)
                    setColorFilter(mImageFilter);
            }
        };
    }

}