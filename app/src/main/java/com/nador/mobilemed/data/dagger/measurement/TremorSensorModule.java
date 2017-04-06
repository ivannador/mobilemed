package com.nador.mobilemed.data.dagger.measurement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.nador.mobilemed.data.dagger.scopes.MeasurementScope;
import com.nador.mobilemed.data.measurement.utils.HighPassFilter;
import com.nador.mobilemed.data.measurement.utils.TremorSensorListener;
import com.nador.mobilemed.data.measurement.TremorSensorManager;
import com.nador.mobilemed.data.measurement.utils.LowPassFilter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 03/06/16.
 */
@Module
public class TremorSensorModule {

    private static final int TREMOR_MAX_FREQ = 7;
    private static final int TREMOR_MIN_FREQ = 4;

    TremorSensorListener mListener;

    // Non-application-wide context
    private Context mContext;

    public TremorSensorModule(Context context, TremorSensorListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Provides
    @MeasurementScope
    Context provideContext() {
        return mContext;
    }

    @Provides
    @MeasurementScope
    TremorSensorListener provideListener() {
        return mListener;
    }

    @Provides
    @MeasurementScope
    SensorManager provideSensorManager(Context context) {
        return (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides
    @MeasurementScope
    Sensor provideAccelerationSensor(SensorManager sensorManager) {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Provides
    @MeasurementScope
    LowPassFilter provideNoiseFilter() {
        return new LowPassFilter(TREMOR_MAX_FREQ);
    }

    @Provides
    @MeasurementScope
    HighPassFilter provideGravityFilter() {
        return new HighPassFilter(TREMOR_MIN_FREQ);
    }

    @Provides
    @MeasurementScope
    TremorSensorManager provideTremorSensorManager(
            SensorManager sensorManager,
            Sensor accelerationSensor,
            LowPassFilter lowPassFilter,
            HighPassFilter highPassFilter,
            TremorSensorListener listener) {
        return new TremorSensorManager(sensorManager, accelerationSensor, lowPassFilter, highPassFilter, listener);
    }
}
