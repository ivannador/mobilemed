package com.nador.mobilemed.presentation.widget.ecg;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.nador.mobilemed.data.entity.measurement.ECGMeasurements;

/**
 * Created by nador on 06/09/16.
 */
public class ECGLineChart extends LineChart {

    public interface MeasurementsListener {
        void onMeasurementsAvailable(ECGMeasurements measurements);
    }

    private ECGMeasurements.SegmentType mCurrentHighlightSelection;

    private MeasurementsListener mListener;

    public ECGLineChart(Context context) {
        super(context);
        setHighlightPerDragEnabled(true);
    }

    public ECGLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHighlightPerDragEnabled(true);
    }

    public ECGLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHighlightPerDragEnabled(true);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new ECGLineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public void setMeasurementsListener(MeasurementsListener listener) {
        mListener = listener;
    }

    public void setCurrentHighlightSelection(ECGMeasurements.SegmentType currentHighlightSelection) {
        if (currentHighlightSelection == null) {
            setDragEnabled(true);
        } else {
            setDragEnabled(false);
        }
        this.mCurrentHighlightSelection = currentHighlightSelection;

        if (currentHighlightSelection != null) {
            float increment = (getHighestVisibleX() - getLowestVisibleX()) / 3;

            ECGHighlight firstHighlight = new ECGHighlight(getLowestVisibleX() + increment, 0, mCurrentHighlightSelection.provideTypes().first);
            ECGHighlight secondHighlight = new ECGHighlight(getHighestVisibleX() - increment, 0, mCurrentHighlightSelection.provideTypes().second);
            Highlight[] highlights = new Highlight[2];
            highlights[0] = firstHighlight;
            highlights[1] = secondHighlight;
            highlightValues(highlights);
            notifyMeasurementsListener();
        } else {
            highlightValues(null);
        }
    }

    public ECGMeasurements.SegmentType getCurrentHighlightSelection() {
        return mCurrentHighlightSelection;
    }

    /**
     * Modified to enable multiple values to be highlighted by touch gesture
     *
     * @param high
     * @param callListener
     */
    @Override
    public void highlightValue(Highlight high, boolean callListener) {
        Entry e = null;

        if (mCurrentHighlightSelection == null) {
            mIndicesToHighlight = null;
        } else {
            if (high != null) {

                if (mLogEnabled)
                    Log.i(LOG_TAG, "Highlighted: " + high.toString());

                e = mData.getEntryForHighlight(high);
                if (e != null) {

                    ECGHighlight firstHighlight = (ECGHighlight) mIndicesToHighlight[0];
                    ECGHighlight secondHighlight = (ECGHighlight) mIndicesToHighlight[1];

                    // Check which existing highlight is closer to the new highlight
                    // The closer one will be replaced by the new highlight
                    float highX = high.getX();
                    float firstHighXDiff = Math.abs(firstHighlight.getX() - highX);
                    float secondHighXDiff = Math.abs(secondHighlight.getX() - highX);

                    boolean isFirstHighlight = firstHighXDiff < secondHighXDiff;

                    ECGHighlight newHighlight = new ECGHighlight(
                            highX,
                            high.getDataSetIndex(),
                            isFirstHighlight ? firstHighlight.getType() : secondHighlight.getType());

                    if (isFirstHighlight) {
                        mIndicesToHighlight[0] = newHighlight;
                    } else {
                        mIndicesToHighlight[1] = newHighlight;
                    }
                }
            }
        }

        setLastHighlighted(mIndicesToHighlight);

        if (callListener && mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {
                // notify the listener
                mSelectionListener.onValueSelected(e, high);
            }
        }

        notifyMeasurementsListener();

        // redraw the chart
        invalidate();
    }

    private void notifyMeasurementsListener() {
        if (mListener != null && mIndicesToHighlight != null) {
            ECGMeasurements measurements = new ECGMeasurements();
            ECGHighlight first = (ECGHighlight) mIndicesToHighlight[0];
            ECGHighlight second = (ECGHighlight) mIndicesToHighlight[1];
            float interval = second.getX() - first.getX();
            switch (getCurrentHighlightSelection()) {
                case PWave:
                    measurements.setPWave(interval);
                    break;
                case PRSegment:
                    measurements.setPRSegment(interval);
                    break;
                case QRSComplex:
                    measurements.setQRSComplex(interval);
                    break;
                case STSegment:
                    measurements.setSTSegment(interval);
                    break;
                case TWave:
                    measurements.setTWave(interval);
                    break;
                case UWave:
                    measurements.setUWave(interval);
                    break;
            }

            mListener.onMeasurementsAvailable(measurements);
        }
    }
}
