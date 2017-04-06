package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by nador on 16/08/16.
 */
@JsonObject
public class Temperature extends Result {

    @JsonField(name = "value")
    private float mValue;

    public Temperature() {}
    public Temperature(final float value, final TemperatureUnit unit) {
        mValue = value;
        setUnit(unit);
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        this.mValue = value;
    }

    public String getStringValue() {
        return String.valueOf(mValue);
    }

    public String getFormattedString() {
        return String.format("%.1f", mValue) + " " + getUnit().getString();
    }

    @Override
    public Unit getUnitEnum() {
        return TemperatureUnit.valueOf(getUnitDescription());
    }

    @Override
    public void convertTo(Unit unit) {
        setValue(getUnit().convertTo(getValue(), unit));
        setUnit(unit);
    }

    @Override
    public SerializedResult serialize() {
        String serializedValue = "";
        try {
            serializedValue = LoganSquare.serialize(this);
        } catch (IOException e) {
            Timber.e(e, "Could not serialize class");
        }
        return new SerializedResult(SerializedResult.Type.TEMPERATURE, serializedValue);
    }

    public enum TemperatureUnit implements Unit {
        CELSIUS("˚C") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((TemperatureUnit) toUnit) {
                    case CELSIUS:
                        val = value;
                        break;
                    case FAHRENHEIT:
                        val = TemperatureUnit.C_F_RATIO * value;
                        break;
                }
                return val;
            }
        }, FAHRENHEIT("˚F") {
            @Override
            public float convertTo(float value, Unit toUnit) {
                float val = 0f;
                switch ((TemperatureUnit) toUnit) {
                    case CELSIUS:
                        val = (1f / TemperatureUnit.C_F_RATIO) * value;
                        break;
                    case FAHRENHEIT:
                        val = value;
                        break;
                }
                return val;
            }
        };

        private final String mName;

        TemperatureUnit(final String name) {
            mName = name;
        }

        @Override
        public String getString() {
            return mName;
        }

        static final float C_F_RATIO = 0.45359237f;
    }
}
