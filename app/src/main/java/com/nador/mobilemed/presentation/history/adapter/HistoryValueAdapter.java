package com.nador.mobilemed.presentation.history.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;
import com.nador.mobilemed.data.entity.measurement.ECGGraph;
import com.nador.mobilemed.data.entity.measurement.ECGMeasurements;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.entity.measurement.Temperature;
import com.nador.mobilemed.data.entity.measurement.Weight;
import com.nador.mobilemed.presentation.utils.DateUtil;

import java.util.List;

/**
 * Created by nador on 07/08/16.
 */
public abstract class HistoryValueAdapter<VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    public interface ResultSelectedListener {
        void onResultSelected(final Result result);
    }

    protected Context mContext;
    protected int mCurrentSelectedPos = -1;

    protected List<Result> mResultList;

    protected ResultSelectedListener mListener;

    public int getCurrentSelectedPos() {
        return mCurrentSelectedPos;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        final Result result = mResultList.get(position);

        holder.mContainer.setOnClickListener(v -> {
            if (mCurrentSelectedPos != position) {
                mCurrentSelectedPos = position;
                if (mListener != null) {
                    mListener.onResultSelected(result);
                }

                notifyDataSetChanged();
            }
        });

        holder.mDateTextView.setText(DateUtil.getDateString(result.getDate()));
        holder.mMethodTextView.setText(
                mContext.getString(result.getMethod() == Result.Method.AUTOMATIC
                        ? R.string.HISTORY_TEXT_LIST_AUTOMATIC
                        : R.string.HISTORY_TEXT_LIST_MANUAL));

        if (mCurrentSelectedPos == position) {
            holder.mContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBlueLight));
        } else {
            holder.mContainer.setBackgroundColor(ContextCompat.getColor(mContext, (position % 2 == 0) ? android.R.color.white : R.color.colorWhiteOff));
        }

    }

    @Override
    public int getItemCount() {
        return (mResultList == null) ? 0 : mResultList.size();
    }

    public void setDataList(final List<Result> resultList) {
        mResultList = resultList;
        notifyDataSetChanged();
    }

    public void setResultSelectedListener(final ResultSelectedListener listener) {
        mListener = listener;
    }

    public static class BloodpressureHistoryAdapter extends HistoryValueAdapter<BaseViewHolder.BloodPressureViewHolder> {

        public BloodpressureHistoryAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public BaseViewHolder.BloodPressureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_history_bloodpressure, parent, false);
            return new BaseViewHolder.BloodPressureViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder.BloodPressureViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            final BloodPressure bloodPressure = (BloodPressure) mResultList.get(position);

            holder.mSystolicTextView.setText(String.valueOf(bloodPressure.getSystolicValue()) + " " + bloodPressure.getUnit().getString());
            holder.mDiastolicTextView.setText(String.valueOf(bloodPressure.getDiastolicValue()) + " " + bloodPressure.getUnit().getString());
            holder.mPulseTextView.setText(String.valueOf(bloodPressure.getPulseValue()) + " " + bloodPressure.getPulseUnit());
        }
    }

    public static class WeightHistoryAdapter extends HistoryValueAdapter<BaseViewHolder.SingleValueViewHolder> {

        public WeightHistoryAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public BaseViewHolder.SingleValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_history_weight, parent, false);
            return new BaseViewHolder.SingleValueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder.SingleValueViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            final Weight weight = (Weight) mResultList.get(position);
            holder.mValueTextView.setText(weight.getFormattedString());
        }
    }

    public static class TemperatureHistoryAdapter extends HistoryValueAdapter<BaseViewHolder.SingleValueViewHolder> {

        public TemperatureHistoryAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public BaseViewHolder.SingleValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_history_temperature, parent, false);
            return new BaseViewHolder.SingleValueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder.SingleValueViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            final Temperature temperature = (Temperature) mResultList.get(position);
            holder.mValueTextView.setText(temperature.getFormattedString());
        }
    }

    public static class GlucometerHistoryAdapter extends HistoryValueAdapter<BaseViewHolder.SingleValueViewHolder> {

        public GlucometerHistoryAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public BaseViewHolder.SingleValueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_history_glucometer, parent, false);
            return new BaseViewHolder.SingleValueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder.SingleValueViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            final BloodGlucoseLevel level = (BloodGlucoseLevel) mResultList.get(position);
            holder.mValueTextView.setText(level.getFormattedString());
        }
    }

    public static class ECGHistoryAdapter extends HistoryValueAdapter<BaseViewHolder.ECGViewHolder> {

        public ECGHistoryAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public void setDataList(List<Result> resultList) {
            if (resultList.size() > 0) {
                mCurrentSelectedPos = 0;
            }

            super.setDataList(resultList);
        }

        @Override
        public BaseViewHolder.ECGViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_history_ecg, parent, false);
            return new BaseViewHolder.ECGViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder.ECGViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);

            holder.mPulseTextView.setVisibility(View.GONE);
            holder.mPWaveTextView.setVisibility(View.GONE);
            holder.mPRSegmentTextView.setVisibility(View.GONE);
            holder.mQRSComplexTextView.setVisibility(View.GONE);
            holder.mPRIntervalTextView.setVisibility(View.GONE);
            holder.mQTIntervalTextView.setVisibility(View.GONE);
            holder.mSTSegmentTextView.setVisibility(View.GONE);
            holder.mTWaveTextView.setVisibility(View.GONE);
            holder.mUWaveTextView.setVisibility(View.GONE);

            final ECGGraph graph = (ECGGraph) mResultList.get(position);
            if (!payloads.isEmpty()) {
                graph.setECGMeasurements((ECGMeasurements) payloads.get(0));
            }
            ECGMeasurements measurements = graph.getECGMeasurements();

            if (measurements != null) {
                if (measurements.hasPulseValue()) {
                    holder.mPulseTextView.setVisibility(View.VISIBLE);
                    holder.mPulseTextView.setText(String.format("Pulse: %.0f bpm", measurements.getPulseValue()));
                    holder.mPulseTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.Pulse.isNormalValue(measurements.getPulseValue()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasPWave()) {
                    holder.mPWaveTextView.setVisibility(View.VISIBLE);
                    holder.mPWaveTextView.setText(String.format("P wave: %.2f s", measurements.getPWave()));
                    holder.mPWaveTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.PWave.isNormalValue(measurements.getPWave()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasPRSegment()) {
                    holder.mPRSegmentTextView.setVisibility(View.VISIBLE);
                    holder.mPRSegmentTextView.setText(String.format("PR segment: %.2f s", measurements.getPRSegment()));
                    holder.mPRSegmentTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.PRSegment.isNormalValue(measurements.getPRSegment()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasPRInterval()) {
                    holder.mPRIntervalTextView.setVisibility(View.VISIBLE);
                    holder.mPRIntervalTextView.setText(String.format("PR interval: %.2f s", measurements.getPRInterval()));
                    holder.mPRIntervalTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.PRInterval.isNormalValue(measurements.getPRInterval()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasQRSComplex()) {
                    holder.mQRSComplexTextView.setVisibility(View.VISIBLE);
                    holder.mQRSComplexTextView.setText(String.format("QRS complex: %.2f s", measurements.getQRSComplex()));
                    holder.mQRSComplexTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.QRSComplex.isNormalValue(measurements.getQRSComplex()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasSTSegment()) {
                    holder.mSTSegmentTextView.setVisibility(View.VISIBLE);
                    holder.mSTSegmentTextView.setText(String.format("ST segment: %.2f s", measurements.getSTSegment()));
                    holder.mSTSegmentTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.STSegment.isNormalValue(measurements.getSTSegment()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasTWave()) {
                    holder.mTWaveTextView.setVisibility(View.VISIBLE);
                    holder.mTWaveTextView.setText(String.format("T wave: %.2f s", measurements.getTWave()));
                    holder.mTWaveTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.TWave.isNormalValue(measurements.getTWave()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasQTInterval()) {
                    holder.mQTIntervalTextView.setVisibility(View.VISIBLE);
                    holder.mQTIntervalTextView.setText(String.format("QT interval: %.2f s", measurements.getQTInterval()));
                    holder.mQTIntervalTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.QTInterval.isNormalValue(measurements.getQTInterval()) ? android.R.color.black : R.color.colorRedMedium));
                }
                if (measurements.hasUWave()) {
                    holder.mUWaveTextView.setVisibility(View.VISIBLE);
                    holder.mUWaveTextView.setText(String.format("U wave: %.2f s", measurements.getUWave()));
                    holder.mUWaveTextView.setTextColor(
                            ContextCompat.getColor(
                                    mContext,
                                    ECGMeasurements.MeasurementValues.UWave.isNormalValue(measurements.getUWave()) ? android.R.color.black : R.color.colorRedMedium));
                }
            }
        }
    }
}
