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
public class Weight extends Result {

    @JsonField(name = "value")
    private float mValue;

    public Weight() {}
    public Weight(final float value, final WeightUnit unit) {
        mValue = value;
        setUnit(unit);
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        this.mValue = value;
    }

    @Override
    public Unit getUnitEnum() {
        return WeightUnit.valueOf(getUnitDescription());
    }

    @Override
    public void convertTo(Unit unit) {
        setValue(getUnit().convertTo(getValue(), unit));
        setUnit(unit);
    }

    public String getStringValue() {
        return String.valueOf(mValue);
    }

    public String getFormattedString() {
        return String.format("%.1f", mValue) + " " + getUnit().getString();
    }

    @Override
    public SerializedResult serialize() {
        String serializedValue = "";
        try {
            serializedValue = LoganSquare.serialize(this);
        } catch (IOException e) {
            Timber.e(e, "Could not serialize class");
        }
        return new SerializedResult(SerializedResult.Type.WEIGHT, serializedValue);
    }

    public enum WeightUnit implements Unit {
        LB("lb") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((WeightUnit) toUnit) {
                    case LB:
                        val = value;
                        break;
                    case KG:
                        val = WeightUnit.LB_KG_RATIO * value;
                        break;
                }
                return val;
            }
        }, KG("kg") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((WeightUnit) toUnit) {
                    case LB:
                        val = (1f / WeightUnit.LB_KG_RATIO) * value;
                        break;
                    case KG:
                        val = value;
                        break;
                }
                return val;
            }
        };

        private final String mName;

        WeightUnit(final String name) {
            mName = name;
        }

        @Override
        public String getString() {
            return mName;
        }

        static final float LB_KG_RATIO = 0.45359237f;
    }
}
