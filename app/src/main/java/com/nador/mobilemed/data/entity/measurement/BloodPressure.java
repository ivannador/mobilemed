package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by nador on 07/08/16.
 */
@JsonObject
public class BloodPressure extends Result {
    @JsonField(name = "systolic")
    private long mSystolicValue;
    @JsonField(name = "diastolic")
    private long mDiastolicValue;
    @JsonField(name = "pulse")
    private long mPulseValue;

    public BloodPressure() {}
    public BloodPressure(final long systolicValue, final long diastolicValue, final long pulseValue) {
        mSystolicValue = systolicValue;
        mDiastolicValue = diastolicValue;
        mPulseValue = pulseValue;
        setUnit(BloodPressureUnit.MMHG);
    }

    public void setDiastolicValue(long diastolicValue) {
        this.mDiastolicValue = diastolicValue;
    }

    public void setSystolicValue(long systolicValue) {
        this.mSystolicValue = systolicValue;
    }

    public void setPulseValue(long pulseValue) {
        this.mPulseValue = pulseValue;
    }

    public long getSystolicValue() {
        return mSystolicValue;
    }

    public long getDiastolicValue() {
        return mDiastolicValue;
    }

    public long getPulseValue() {
        return mPulseValue;
    }

    public String getPulseUnit() {
        return "Beats/Min";
    }

    @Override
    public Unit getUnitEnum() {
        return BloodPressureUnit.valueOf(getUnitDescription());
    }

    @Override
    public void convertTo(Unit unit) {}

    @Override
    public SerializedResult serialize() {
        String serializedValue = "";
        try {
            serializedValue = LoganSquare.serialize(this);
        } catch (IOException e) {
            Timber.e(e, "Could not serialize class");
        }
        return new SerializedResult(SerializedResult.Type.BLOODPRESSURE, serializedValue);
    }

    public enum BloodPressureUnit implements Unit {
        MMHG("mmHg") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                return value;
            }
        };

        private final String mName;

        BloodPressureUnit(final String name) {
            mName = name;
        }

        @Override
        public String getString() {
            return mName;
        }
    }
}
