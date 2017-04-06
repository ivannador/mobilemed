package com.nador.mobilemed.data.measurement.utils;

import java.util.List;

/**
 * Created by nador on 03/06/16.
 */
// Low-pass filter for three dimensional discrete-time data
public class LowPassFilter {

    // Initialize with the frequency at which the low-pass filter should cut
    public LowPassFilter(float cutFrequency) {
        mTimeConstant = 1 / cutFrequency;
        flush();
    }

    private float mTimeConstant;
    private float mAlpha;
    private float mTimestamp;
    private float mTimestampBase;
    private float mDt;
    private float[] mOutput;

    private int mCount;

    public float[] filter(float[] input) {
        mTimestamp = System.nanoTime();

        // Calculate delta with averaging the current and original timestamp
        // as it produces a more accurate, near-constant delta
        mDt = ((mTimestamp - mTimestampBase) / (mCount * 1000000000.0f));
        mCount++;

        // Calculate alpha for filtering
        mAlpha = mDt / (mTimeConstant + mDt);

        // Discrete-time low pass filtering
        // y[i] = y[i] + alpha * (x[i] - y[i])
        mOutput[0] = mOutput[0] + mAlpha * (input[0] - mOutput[0]);
        mOutput[1] = mOutput[1] + mAlpha * (input[1] - mOutput[1]);
        mOutput[2] = mOutput[2] + mAlpha * (input[2] - mOutput[2]);

        return mOutput;
    }

    // Set the filter to default values
    public void flush() {
        mAlpha = 0.0f;
        mTimestamp = System.nanoTime();
        mTimestampBase = System.nanoTime();
        mDt = 0.0f;
        mOutput = new float[] {0.0f, 0.0f, 0.0f};
        mCount = 1;
    }

    /**
     *  A simple approach for smoothing (low-pass filtering) a one-dimensional array of float values
     */
    public static List<Float> smoothFloat(List<Float> values, float smoothingConstant) {
        float value = values.get(0);
        for (int i = 0; i < values.size(); i++) {
            float currentValue = values.get(i);
            value += (currentValue - value) / smoothingConstant;
            values.set(i, value);
        }
        return values;
    }

    /**
     *  A simple approach for smoothing (low-pass filtering) a one-dimensional array of short values
     */
    public static List<Short> smoothShort(List<Short> values, float smoothingConstant) {
        short value = values.get(0);
        for (int i = 0; i < values.size(); i++) {
            short currentValue = values.get(i);
            value += (currentValue - value) / smoothingConstant;
            values.set(i, value);
        }
        return values;
    }
}
