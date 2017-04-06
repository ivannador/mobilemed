package com.nador.mobilemed.presentation.history.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nador.mobilemed.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nador on 17/08/16.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.listItemContainer)
    ViewGroup mContainer;
    @Bind(R.id.dateTextView)
    TextView mDateTextView;
    @Bind(R.id.methodTextView)
    TextView mMethodTextView;

    BaseViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public static class BloodPressureViewHolder extends BaseViewHolder {
        @Bind(R.id.systolicTextView)
        TextView mSystolicTextView;
        @Bind(R.id.diastolicTextView)
        TextView mDiastolicTextView;
        @Bind(R.id.pulseTextView)
        TextView mPulseTextView;

        BloodPressureViewHolder(View view) {
            super(view);
        }
    }

    public static class ECGViewHolder extends BaseViewHolder {

        @Bind(R.id.pulseTextView)
        TextView mPulseTextView;
        @Bind(R.id.pWaveTextView)
        TextView mPWaveTextView;
        @Bind(R.id.prSegmentTextView)
        TextView mPRSegmentTextView;
        @Bind(R.id.qrsTextView)
        TextView mQRSComplexTextView;
        @Bind(R.id.prIntervalTextView)
        TextView mPRIntervalTextView;
        @Bind(R.id.qtIntervalTextView)
        TextView mQTIntervalTextView;
        @Bind(R.id.stSegmentTextView)
        TextView mSTSegmentTextView;
        @Bind(R.id.tWaveTextView)
        TextView mTWaveTextView;
        @Bind(R.id.uWaveTextView)
        TextView mUWaveTextView;

        ECGViewHolder(View view) {
            super(view);
        }
    }

    public static class SingleValueViewHolder extends BaseViewHolder {

        @Bind(R.id.valueTextView)
        TextView mValueTextView;

        SingleValueViewHolder(View view) {
            super(view);
        }
    }
}
