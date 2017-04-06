package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by nador on 25/08/16.
 */
@JsonObject
public class TremorLevel extends Result {

    @JsonField
    private float mLevel;

    public TremorLevel() {}
    public TremorLevel(final float level) {
        mLevel = level;
        setMethod(Method.AUTOMATIC);
    }

    public float getLevel() {
        return mLevel;
    }

    public void setLevel(float level) {
        this.mLevel = level;
    }

    public String getStringValue() {
        return String.valueOf(mLevel);
    }

    public String getFormattedString() {
        return String.format("%s", mLevel);
    }

    @Override
    public Unit getUnitEnum() {
        return null;
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
        return new SerializedResult(SerializedResult.Type.TREMOR, serializedValue);
    }
}
