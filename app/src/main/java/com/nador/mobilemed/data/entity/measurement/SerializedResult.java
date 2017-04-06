package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import io.realm.RealmObject;

/**
 * Wrapper class for Result objects
 * until Realm supports polymorphism
 *
 * Created by nador on 22/08/16.
 */
public class SerializedResult extends RealmObject {

    public enum Type {
        GLUCOSE("blood_glucose"),
        BLOODPRESSURE("blood_pressure"),
        WEIGHT("weight"),
        TEMPERATURE("temperature"),
        PAIN("pain"),
        STRESS("stress"),
        MOOD("mood"),
        TREMOR("tremor");

        private final String mName;

        Type(final String name) {
            mName = name;
        }

        public String getString() {
            return mName;
        }
    }

    private String mEnumDescription;
    private String mSerializedValue;

    public SerializedResult() {}

    public SerializedResult(final Type type, final String serializedValue) {
        setType(type);
        mSerializedValue = serializedValue;
    }

    public Type getType() {
        return getTypeEnum();
    }

    public void setType(Type type) {
        saveTypeEnum(type);
    }

    public void saveTypeEnum(Type val) {
        mEnumDescription = val.toString();
    }

    public Type getTypeEnum() {
        return Type.valueOf(mEnumDescription);
    }

    public void setSerializedValue(String serializedValue) {
        this.mSerializedValue = serializedValue;
    }

    public String getSerializedValue() {
        return mSerializedValue;
    }

    public Result deserialize() throws IOException {
        switch (getType()) {
            case GLUCOSE:
                return LoganSquare.parse(mSerializedValue, BloodGlucoseLevel.class);
            case BLOODPRESSURE:
                return LoganSquare.parse(mSerializedValue, BloodPressure.class);
            case WEIGHT:
                return LoganSquare.parse(mSerializedValue, Weight.class);
            case TEMPERATURE:
                return LoganSquare.parse(mSerializedValue, Temperature.class);
            default:
                throw new IOException();
        }
    }
}
