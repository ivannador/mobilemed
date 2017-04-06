package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by nador on 26/07/16.
 */
@JsonObject
public class PainPoint extends Result {

    @JsonField
    private int mLevel;
    @JsonField
    private float mXCoord;
    @JsonField
    private float mYCoord;
    @JsonField
    private float mXMax;
    @JsonField
    private float mYMax;

    public PainPoint() {}
    public PainPoint(final int level, final float xCoord, final float yCoord, final float xMax, final float yMax) {
        mLevel = level;
        mXCoord = xCoord;
        mYCoord = yCoord;
        mXMax = xMax;
        mYMax = yMax;
        setMethod(Method.MANUAL);
    }

    public float getYCoord() {
        return mYCoord;
    }

    public void setYCoord(final float yCoord) {
        this.mYCoord = yCoord;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(final int level) {
        this.mLevel = level;
    }

    public float getXCoord() {
        return mXCoord;
    }

    public void setXCoord(final float xCoord) {
        this.mXCoord = xCoord;
    }

    public float getXMax() {
        return mXMax;
    }

    public void setXMax(final float xMax) {
        this.mXMax = xMax;
    }

    public float getYMax() {
        return mYMax;
    }

    public void setYMax(final float yMax) {
        this.mYMax = yMax;
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
        return new SerializedResult(SerializedResult.Type.PAIN, serializedValue);
    }
}
