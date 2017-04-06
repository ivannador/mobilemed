package com.nador.mobilemed.data.measurement.utils;

/**
 * Created by nador on 03/06/16.
 */
// High-pass filter for three dimensional discrete-time data
public class HighPassFilter extends LowPassFilter {

    private float[] mOutput;

    public HighPassFilter(float cutFrequency) {
        super(cutFrequency);
        flush();
    }

    @Override
    public float[] filter(float[] input) {
        float[] lowPass = super.filter(input);

        // high-pass filtered data = original data - low-pass filtered data
        mOutput[0] = input[0] - lowPass[0];
        mOutput[1] = input[1] - lowPass[1];
        mOutput[2] = input[2] - lowPass[2];

        return mOutput;
    }

    // Set the filter to default values
    @Override
    public void flush() {
        super.flush();

        mOutput = new float[] {0.0f, 0.0f, 0.0f};
    }
}
