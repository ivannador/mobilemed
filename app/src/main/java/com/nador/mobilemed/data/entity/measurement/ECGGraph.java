package com.nador.mobilemed.data.entity.measurement;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.nador.mobilemed.data.measurement.utils.cwt.CWT;
import com.nador.mobilemed.data.measurement.utils.cwt.ComplexNumber;
import com.nador.mobilemed.R;
import com.nador.mobilemed.presentation.widget.ecg.ECGLineChart;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by nador on 11/08/16.
 */
public abstract class ECGGraph extends Result {

    private String mName;

    protected float mSamplingRate;

    private ECGMeasurements mECGMeasurements;

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setSamplingRate(float samplingRate) {
        this.mSamplingRate = samplingRate;
    }

    public float getSamplingRate() {
        return mSamplingRate;
    }

    public void setECGMeasurements(ECGMeasurements ecgMeasurements) {
        if (this.mECGMeasurements != null) {
            if (ecgMeasurements.hasPulseValue()) {
                mECGMeasurements.setPulseValue(ecgMeasurements.getPulseValue());
            }
            if (ecgMeasurements.hasPWave()) {
                mECGMeasurements.setPWave(ecgMeasurements.getPWave());
            }
            if (ecgMeasurements.hasPRSegment()) {
                mECGMeasurements.setPRSegment(ecgMeasurements.getPRSegment());
            }
            if (ecgMeasurements.hasPRInterval()) {
                mECGMeasurements.setPRInterval(ecgMeasurements.getPRInterval());
            }
            if (ecgMeasurements.hasQRSComplex()) {
                mECGMeasurements.setQRSComplex(ecgMeasurements.getQRSComplex());
            }
            if (ecgMeasurements.hasSTSegment()) {
                mECGMeasurements.setSTSegment(ecgMeasurements.getSTSegment());
            }
            if (ecgMeasurements.hasTWave()) {
                mECGMeasurements.setTWave(ecgMeasurements.getTWave());
            }
            if (ecgMeasurements.hasQTInterval()) {
                mECGMeasurements.setQTInterval(ecgMeasurements.getQTInterval());
            }
            if (ecgMeasurements.hasUWave()) {
                mECGMeasurements.setUWave(ecgMeasurements.getUWave());
            }
        } else {
            this.mECGMeasurements = ecgMeasurements;
        }
    }

    public ECGMeasurements getECGMeasurements() {
        return mECGMeasurements;
    }

    public abstract List<LineChart> plotData(final Context context);

    @Override
    public Unit getUnitEnum() {
        return null;
    }

    @Override
    public void convertTo(Unit unit) {
    }

    @Override
    public SerializedResult serialize() {
        return null;
    }

    public static class SingleChannel extends ECGGraph {

        private List<Integer> mFirstValueList;

        @Override
        public List<LineChart> plotData(final Context context) {
            List<Entry> ecgList = new ArrayList<>();

            for (int i = 0; i < mFirstValueList.size(); i++) {
                ecgList.add(new Entry(i / mSamplingRate, mFirstValueList.get(i)));
            }

            LineDataSet ecgSet = new LineDataSet(ecgList, "");
            ecgSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            ecgSet.setDrawCircles(false);
            ecgSet.setColor(Color.BLACK);
            ecgSet.setDrawHorizontalHighlightIndicator(false);
            ecgSet.setHighlightLineWidth(3f);
            ecgSet.setHighLightColor(ContextCompat.getColor(context, R.color.colorRedMedium));

            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(ecgSet);
            LineData data = new LineData(dataSets);
            data.setDrawValues(false);

            LineChart chart = new ECGLineChart(context);
            chart.setData(data);

            List<LineChart> chartList = new ArrayList<>();
            chartList.add(chart);
            return chartList;
        }

        public void setFirstValueList(List<Integer> firstValueList) {
            this.mFirstValueList = firstValueList;
        }

        // TODO: multiple channel
        public boolean calculatePulseValue() {

            List<Double> values = new ArrayList<>();
            for (Integer value : mFirstValueList) {
                values.add(Double.valueOf(value));
            }

            int sampleCount = 4096;
            double[] doubles = new double[sampleCount];
            for (int i = 0; i < sampleCount; i++) {
                doubles[i] = values.get(i);
            }

            double dt = 1f;
            // Minimum scale (only)
            double s0 = 1f;
            // Irrelevant (scaling increment)
            double dj = 0.25;
            // Number of scales (only 1 right now)
            int jtot = 1;

            ArrayList<Object> res = CWT.cWT(doubles, dt, CWT.Wavelet.DOG, 1, s0, dj, jtot);
            ComplexNumber[][] numbers = (ComplexNumber[][]) res.get(0);

            List<Integer> ints = new ArrayList<>();
            int mean = 0;
            int meanCount = 0;
            for (int i = 0; i < numbers.length; i++) {
                double real = numbers[i][0].Real;// * numbers[i][0].Real;
                if (real < 0) {
                    ints.add(0);
                    continue;
                }
                mean += real * real;
                meanCount++;

                if (real < Integer.MIN_VALUE) {
                    ints.add(Integer.MIN_VALUE);
                }
                if (real > Integer.MAX_VALUE) {
                    ints.add(Integer.MAX_VALUE);
                }
                ints.add((int) Math.round(real * real));
            }
            mean = mean / meanCount;

            int threshold = 0;
            int thresholdCount = 0;
            for (int i = 0; i < ints.size(); i++) {
                ints.set(i, ints.get(i) - mean);
                if (ints.get(i) > 0) {
                    threshold += ints.get(i);
                    thresholdCount++;
                }
            }
            threshold = threshold / thresholdCount;
            int peaks = 0;
            for (int i = 0; i < ints.size(); i++) {
                if (i == 0 || i == ints.size() - 1) {
                    continue;
                }
                int val = ints.get(i);
                if (val >= threshold && ints.get(i - 1) < val && ints.get(i + 1) < val) {
                    peaks++;
                }
            }

            float beats = (float) peaks * ((float) values.size() / (float) sampleCount);
            Timber.d("Peaks: %d, beats/min: %.0f", peaks, beats);

            if (getECGMeasurements() != null) {
                getECGMeasurements().setPulseValue(beats);
            } else {
                ECGMeasurements measurements = new ECGMeasurements(getName());
                measurements.setPulseValue(beats);
                setECGMeasurements(measurements);
            }

            return true;
        }
    }

    public static class DualChannel extends ECGGraph {

        private List<Integer> mFirstValueList;
        private List<Integer> mSecondValueList;

        @Override
        public List<LineChart> plotData(final Context context) {
            List<LineChart> chartList = new ArrayList<>();
            return chartList;
        }

        public void setFirstValueList(List<Integer> firstValueList) {
            this.mFirstValueList = firstValueList;
        }

        public void setSecondValueList(List<Integer> secondValueList) {
            this.mSecondValueList = secondValueList;
        }
    }

    public static class TriChannel extends ECGGraph {

        private List<Integer> mFirstValueList;
        private List<Integer> mSecondValueList;
        private List<Integer> mThirdValueList;

        @Override
        public List<LineChart> plotData(final Context context) {
            List<LineChart> chartList = new ArrayList<>();
            return chartList;
        }

        public void setFirstValueList(List<Integer> firstValueList) {
            this.mFirstValueList = firstValueList;
        }

        public void setSecondValueList(List<Integer> secondValueList) {
            this.mSecondValueList = secondValueList;
        }

        public void setThirdValueList(List<Integer> thirdValueList) {
            this.mThirdValueList = thirdValueList;
        }
    }
}
