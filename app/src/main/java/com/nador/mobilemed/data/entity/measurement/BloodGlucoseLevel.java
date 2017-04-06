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
public class BloodGlucoseLevel extends Result {

    @JsonField(name = "value")
    private float mGlucoseLevel;

    public BloodGlucoseLevel() {}
    public BloodGlucoseLevel(final float glucoseLevel, final GlucoseUnit unit) {
        mGlucoseLevel = glucoseLevel;
        setUnit(unit);
    }

    public void setGlucoseLevel(float glucoseLevel) {
        this.mGlucoseLevel = glucoseLevel;
    }

    public float getGlucoseLevel() {
        return mGlucoseLevel;
    }

    public String getStringValue() {
        return String.valueOf(mGlucoseLevel);
    }

    public String getFormattedString() {
        return String.format("%.1f", mGlucoseLevel) + " " + getUnit().getString();
    }

    @Override
    public void convertTo(Unit unit) {
        setGlucoseLevel(getUnit().convertTo(getGlucoseLevel(), unit));
        setUnit(unit);
    }

    @Override
    public Unit getUnitEnum() {
        return GlucoseUnit.valueOf(getUnitDescription());
    }

    @Override
    public SerializedResult serialize() {
        String serializedValue = "";
        try {
            serializedValue = LoganSquare.serialize(this);
        } catch (IOException e) {
            Timber.e(e, "Could not serialize class");
        }
        return new SerializedResult(SerializedResult.Type.GLUCOSE, serializedValue);
    }

    public enum GlucoseUnit implements Unit {
        MMOL("mmol/L") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((GlucoseUnit) toUnit) {
                    case MMOL:
                        val = value;
                        break;
                    case MG:
                        val = GlucoseUnit.MMOL_MG_RATIO * value;
                        break;
                }
                return val;
            }
        }, MG("mg/dL") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((GlucoseUnit) toUnit) {
                    case MMOL:
                        val = (1f / GlucoseUnit.MMOL_MG_RATIO) * value;
                        break;
                    case MG:
                        val = value;
                        break;
                }
                return val;
            }
        };

        private final String mName;

        GlucoseUnit(final String name) {
            mName = name;
        }

        @Override
        public String getString() {
            return mName;
        }

        static final float MMOL_MG_RATIO = 18f;
    }
}
