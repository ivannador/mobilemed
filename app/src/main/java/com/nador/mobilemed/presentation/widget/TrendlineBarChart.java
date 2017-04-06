package com.nador.mobilemed.presentation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;

/**
 * Created by nador on 15/09/16.
 */
public class TrendlineBarChart extends CombinedChart {

    private boolean mFitBars = false;

    public TrendlineBarChart(Context context) {
        super(context);
    }

    public TrendlineBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendlineBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFitBars(boolean fitBars) {
        this.mFitBars = fitBars;
    }

    public boolean isFitBars() {
        return mFitBars;
    }

    @Override
    protected void calcMinMax() {

        if (mAutoScaleMinMaxEnabled)
            mData.calcMinMax();

        if (mFitBars && mData.getBarData() != null) {
            mXAxis.calculate(mData.getXMin() - mData.getBarData().getBarWidth() / 2f, mData.getXMax() + mData.getBarData().getBarWidth() / 2f);
        } else {
            mXAxis.calculate(mData.getXMin(), mData.getXMax());
        }

        // calculate axis range (min / max) according to provided data
        mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
        mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT), mData.getYMax(YAxis.AxisDependency
                .RIGHT));
    }
}
