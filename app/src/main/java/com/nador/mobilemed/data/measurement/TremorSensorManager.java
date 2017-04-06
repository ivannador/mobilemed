package com.nador.mobilemed.data.measurement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.nador.mobilemed.data.measurement.utils.HighPassFilter;
import com.nador.mobilemed.data.measurement.utils.LowPassFilter;
import com.nador.mobilemed.data.measurement.utils.TremorSensorListener;

import timber.log.Timber;

/**
 * Created by nador on 03/06/16.
 */
public class TremorSensorManager implements SensorEventListener {

    // Acceleration measurement sampling rate in microseconds
    // Nyquist rate: sampling rate >= 2 * max. examined freq
    // In this case, max. tremor freq from medical sources: 7 Hz
    // So sampling rate we chose: 20 Hz -> 50000 us
    private static final int TREMOR_SAMPLING_RATE = 50000;

    private SensorManager mSensorManager;
    private Sensor mAccelerationSensor;

    private LowPassFilter mNoiseFilter;
    private HighPassFilter mGravityFilter;

    private TremorSensorListener mListener;

    public TremorSensorManager(
            final SensorManager sensorManager,
            final Sensor accelerationSensor,
            final LowPassFilter lowPassFilter,
            final HighPassFilter highPassFilter,
            final TremorSensorListener listener) {
        mSensorManager = sensorManager;
        mAccelerationSensor = accelerationSensor;
        mNoiseFilter = lowPassFilter;
        mGravityFilter = highPassFilter;
        mListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Timber.d("Accelerometer accuracy changed to %s", accuracy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] linearAcc = mGravityFilter.filter(event.values);
        float[] tremor = mNoiseFilter.filter(linearAcc);

        float tremorMagnitude = (float) Math.sqrt(
                (double) (tremor[0] * tremor[0] + tremor[1] * tremor[1] + tremor[2] * tremor[2]));

        mListener.processSensorValue(tremorMagnitude);
    }

    public void resume() {
        mSensorManager.registerListener(this, mAccelerationSensor, TREMOR_SAMPLING_RATE);
    }

    public void pause() {
        mSensorManager.unregisterListener(this);
    }
}
