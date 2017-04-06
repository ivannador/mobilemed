package com.nador.mobilemed.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nador.mobilemed.R;

import java.util.ArrayList;

/**
 * Created by nador on 29/06/16.
 */
public class StepIndicator extends ViewGroup {

    public enum State {
        INACTIVE, ACTIVE, FINISHED
    }

    private int mStepCount;
    private String mFirstStepLabel;
    private String mSecondStepLabel;
    private String mThirdStepLabel;
    private String mFourthStepLabel;

    private Paint mNodeFillPaint;
    private Paint mNodeStrokePaint;
    private Paint mNodeTextPaint;
    private Paint mNodeTextSmallPaint;
    private Paint mNodeLabelPaint;
    private Paint mProgressLinePaint;

    private int mInactiveColor;
    private int mActiveColor;
    private int mFinishedColor;
    private int mNodeTextColor;

    private float mNodeSize;
    private float mLabelTextHeight;
    private float mNodeNumberTextHeight;
    private float mNodeStepTextHeight;

    private float mNodeStrokeWidth = 4.0f;

    private float mNodeLabelVerticalPadding;

    private ArrayList<NodeView> mNodeViewList = new ArrayList<>();
    private ArrayList<ProgressLineView> mProgressLineViewList = new ArrayList<>();
    private ArrayList<Float> mLabelCenterList = new ArrayList<>();

    public StepIndicator(Context context) {
        super(context);
        init();
    }

    public StepIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StepIndicator,
                0, 0);

        try {
            mStepCount = a.getInt(R.styleable.StepIndicator_stepCount, 1);
            mFirstStepLabel = a.getString(R.styleable.StepIndicator_firstStepLabel);
            mSecondStepLabel = a.getString(R.styleable.StepIndicator_secondStepLabel);
            mThirdStepLabel = a.getString(R.styleable.StepIndicator_thirdStepLabel);
            mFourthStepLabel = a.getString(R.styleable.StepIndicator_fourthStepLabel);
            mInactiveColor = a.getColor(R.styleable.StepIndicator_inactiveColor, Color.GRAY);
            mActiveColor = a.getColor(R.styleable.StepIndicator_activeColor, Color.BLUE);
            mFinishedColor = a.getColor(R.styleable.StepIndicator_finishedColor, Color.GREEN);
            mNodeTextColor = a.getColor(R.styleable.StepIndicator_nodeTextColor, Color.WHITE);
            mLabelTextHeight = a.getDimension(R.styleable.StepIndicator_labelTextHeight, 10.0f);
            mNodeSize = a.getDimension(R.styleable.StepIndicator_nodeSize, 30.0f);
            mNodeLabelVerticalPadding = a.getDimension(R.styleable.StepIndicator_nodeLabelVerticalPadding, 14.0f);

            mNodeNumberTextHeight = mNodeSize / 3;
            mNodeStepTextHeight = mNodeSize / 5;
        } finally {
            a.recycle();
        }

        init();
    }

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(int stepCount) {
        this.mStepCount = stepCount;
        invalidate();
    }

    public String getFirstStepLabel() {
        return mFirstStepLabel;
    }

    public void setFirstStepLabel(String mFirstStepLabel) {
        this.mFirstStepLabel = mFirstStepLabel;
        invalidate();
    }

    public String getSecondStepLabel() {
        return mSecondStepLabel;
    }

    public void setSecondStepLabel(String mSecondStepLabel) {
        this.mSecondStepLabel = mSecondStepLabel;
        invalidate();
    }

    public String getThirdStepLabel() {
        return mThirdStepLabel;
    }

    public void setThirdStepLabel(String mThirdStepLabel) {
        this.mThirdStepLabel = mThirdStepLabel;
        invalidate();
    }

    public String getFourthStepLabel() {
        return mFourthStepLabel;
    }

    public void setFourthStepLabel(String mFourthStepLabel) {
        this.mFourthStepLabel = mFourthStepLabel;
        invalidate();
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(int mActiveColor) {
        this.mActiveColor = mActiveColor;
        invalidate();
    }

    public int getInactiveColor() {
        return mInactiveColor;
    }

    public void setInactiveColor(int mInactiveColor) {
        this.mInactiveColor = mInactiveColor;
        invalidate();
    }

    public int getFinishedColor() {
        return mFinishedColor;
    }

    public void setFinishedColor(int mFinishedColor) {
        this.mFinishedColor = mFinishedColor;
        invalidate();
    }

    public int getNodeTextColor() {
        return mNodeTextColor;
    }

    public void setNodeTextColor(int mNodeTextColor) {
        this.mNodeTextColor = mNodeTextColor;
        invalidate();
    }

    public float getLabelTextHeight() {
        return mLabelTextHeight;
    }

    public void setLabelTextHeight(float mLabelTextHeight) {
        this.mLabelTextHeight = mLabelTextHeight;
        invalidate();
    }

    public void setStep1State(State state) {
        if (mNodeViewList.get(0) != null) {
            mNodeViewList.get(0).setState(state);
            if (mProgressLineViewList.size() >= 1) {
                mProgressLineViewList.get(0).setState(state);
            }
            invalidate();
        }
    }

    public void setStep2State(State state) {
        if (mNodeViewList.get(1) != null) {
            mNodeViewList.get(1).setState(state);
            if (mProgressLineViewList.size() >= 2) {
                mProgressLineViewList.get(1).setState(state);
            }
            invalidate();
        }
    }

    public void setStep3State(State state) {
        if (mNodeViewList.get(2) != null) {
            mNodeViewList.get(2).setState(state);
            if (mProgressLineViewList.size() >= 3) {
                mProgressLineViewList.get(2).setState(state);
            }
            invalidate();
        }
    }

    public void setStep4State(State state) {
        if (mNodeViewList.get(3) != null) {
            mNodeViewList.get(3).setState(state);
            invalidate();
        }
    }

    private void init() {
        setLayerToSW(this);

        mNodeFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNodeFillPaint.setColor(mInactiveColor);
        mNodeFillPaint.setStyle(Paint.Style.FILL);

        mNodeStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNodeStrokePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWhiteOff));
        mNodeStrokePaint.setStrokeWidth(mNodeStrokeWidth);
        mNodeStrokePaint.setStyle(Paint.Style.STROKE);

        mProgressLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressLinePaint.setColor(mInactiveColor);
        mProgressLinePaint.setStyle(Paint.Style.FILL);

        mNodeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNodeTextPaint.setColor(mNodeTextColor);
        mNodeTextPaint.setTextSize(mNodeNumberTextHeight);
        mNodeTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mNodeTextSmallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNodeTextSmallPaint.setColor(mNodeTextColor);
        mNodeTextSmallPaint.setTextSize(mNodeStepTextHeight);

        mNodeLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNodeLabelPaint.setColor(mInactiveColor);
        mNodeLabelPaint.setTextSize(mLabelTextHeight);
        mNodeTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        for (int index = 0; index < mStepCount - 1; index++) {
            ProgressLineView view = new ProgressLineView(getContext());
            addView(view);
            mProgressLineViewList.add(view);
        }

        for (int index = 0; index < mStepCount; index++) {
            NodeView view = new NodeView(getContext(), String.valueOf(index + 1));
            addView(view);
            mNodeViewList.add(view);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (mLabelTextHeight + mNodeLabelVerticalPadding + getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        float horizontalOffset = (ww / mStepCount - mNodeSize) / 2;
        float verticalOffset = (hh - mNodeSize) / 2;
        for (NodeView view : mNodeViewList) {
            view.layout(
                    (int) horizontalOffset,
                    (int) verticalOffset,
                    (int) (horizontalOffset + mNodeSize),
                    (int) (verticalOffset + mNodeSize));

            mLabelCenterList.add(horizontalOffset + mNodeSize / 2);
            horizontalOffset += ww / mStepCount;
        }

        float lineHeight = mNodeSize / 3;
        float lineY = (hh - lineHeight) / 2;
        for (int index = 0; index < mStepCount - 1; index++) {
            if (mProgressLineViewList.get(index) != null
                    && mLabelCenterList.get(index) != null
                    && mLabelCenterList.get(index + 1) != null) {
                mProgressLineViewList.get(index)
                        .layout(
                                mLabelCenterList.get(index).intValue(),
                                (int) lineY,
                                mLabelCenterList.get(index + 1).intValue(),
                                (int) (lineY + lineHeight));
            }
        }

        invalidate();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) (mStepCount * mNodeSize * 1.5f);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) (mNodeSize + mNodeLabelVerticalPadding + mLabelTextHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        int minh = getSuggestedMinimumHeight() + getPaddingBottom() + getPaddingTop();
        int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFirstStepLabel != null && !mFirstStepLabel.isEmpty() && mLabelCenterList.get(0) != null) {
            Rect bounds = new Rect();
            mNodeLabelPaint.getTextBounds(mFirstStepLabel, 0, mFirstStepLabel.length(), bounds);

            switch (mNodeViewList.get(0).getState()) {
                case INACTIVE:
                    mNodeLabelPaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mNodeLabelPaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mNodeLabelPaint.setColor(mFinishedColor);
                    break;
            }

            canvas.drawText(mFirstStepLabel, mLabelCenterList.get(0) - bounds.centerX(), getHeight() + bounds.centerY(), mNodeLabelPaint);
        }
        if (mSecondStepLabel != null && !mSecondStepLabel.isEmpty() && mLabelCenterList.get(1) != null) {
            Rect bounds = new Rect();
            mNodeLabelPaint.getTextBounds(mSecondStepLabel, 0, mSecondStepLabel.length(), bounds);

            switch (mNodeViewList.get(1).getState()) {
                case INACTIVE:
                    mNodeLabelPaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mNodeLabelPaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mNodeLabelPaint.setColor(mFinishedColor);
                    break;
            }

            canvas.drawText(mSecondStepLabel, mLabelCenterList.get(1) - bounds.centerX(), getHeight() + bounds.centerY(), mNodeLabelPaint);
        }
        if (mThirdStepLabel != null && !mThirdStepLabel.isEmpty() && mLabelCenterList.get(2) != null) {
            Rect bounds = new Rect();
            mNodeLabelPaint.getTextBounds(mThirdStepLabel, 0, mThirdStepLabel.length(), bounds);

            switch (mNodeViewList.get(2).getState()) {
                case INACTIVE:
                    mNodeLabelPaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mNodeLabelPaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mNodeLabelPaint.setColor(mFinishedColor);
                    break;
            }

            canvas.drawText(mThirdStepLabel, mLabelCenterList.get(2) - bounds.centerX(), getHeight() + bounds.centerY(), mNodeLabelPaint);
        }
        if (mFourthStepLabel != null && !mFourthStepLabel.isEmpty() && mLabelCenterList.get(3) != null) {
            Rect bounds = new Rect();
            mNodeLabelPaint.getTextBounds(mFourthStepLabel, 0, mFourthStepLabel.length(), bounds);

            switch (mNodeViewList.get(3).getState()) {
                case INACTIVE:
                    mNodeLabelPaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mNodeLabelPaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mNodeLabelPaint.setColor(mFinishedColor);
                    break;
            }

            canvas.drawText(mFourthStepLabel, mLabelCenterList.get(3) - bounds.centerX(), getHeight() + bounds.centerY(), mNodeLabelPaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private class NodeView extends View {
        private State mState = State.INACTIVE;

        private RectF mBounds;

        private Rect mNumberBounds = new Rect();
        private Rect mStepBounds = new Rect();

        private String mNodeNumber;
        private final String mNodeText = "STEP";

        public NodeView(Context context, String nodeNumber) {
            super(context);
            mNodeNumber = nodeNumber;
        }

        public String getNodeNumber() {
            return mNodeNumber;
        }

        public void setNodeNumber(String mNodeNumber) {
            this.mNodeNumber = mNodeNumber;
        }

        public State getState() {
            return mState;
        }

        public void setState(State mState) {
            this.mState = mState;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            switch (mState) {
                case INACTIVE:
                    mNodeFillPaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mNodeFillPaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mNodeFillPaint.setColor(mFinishedColor);
                    break;
            }

            canvas.drawCircle(
                    mBounds.centerX(),
                    mBounds.centerY(),
                    mNodeSize / 2 - mNodeStrokeWidth,
                    mNodeFillPaint);
            canvas.drawCircle(
                    mBounds.centerX(),
                    mBounds.centerY(),
                    mNodeSize / 2 - mNodeStrokeWidth,
                    mNodeStrokePaint);

            canvas.drawText(mNodeNumber, mBounds.centerX() - mNumberBounds.centerX(), mBounds.centerY(), mNodeTextPaint);
            canvas.drawText(mNodeText, mBounds.centerX() - mStepBounds.centerX(), mBounds.centerY() + mStepBounds.height() * 1.2f, mNodeTextSmallPaint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF(0, 0, w, h);
            mNodeTextPaint.getTextBounds(mNodeNumber, 0, mNodeNumber.length(), mNumberBounds);
            mNodeTextSmallPaint.getTextBounds(mNodeText, 0, mNodeText.length(), mStepBounds);
        }
    }

    private class ProgressLineView extends View {
        private State mState = State.INACTIVE;

        private RectF mBounds;

        public ProgressLineView(Context context) {
            super(context);
        }

        public State getState() {
            return mState;
        }

        public void setState(State mState) {
            this.mState = mState;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            switch (mState) {
                case INACTIVE:
                    mProgressLinePaint.setColor(mInactiveColor);
                    break;
                case ACTIVE:
                    mProgressLinePaint.setColor(mActiveColor);
                    break;
                case FINISHED:
                    mProgressLinePaint.setColor(mFinishedColor);
                    break;
            }
            canvas.drawRect(mBounds, mProgressLinePaint);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF(0, 0, w, h);
        }
    }
}
