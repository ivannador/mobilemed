package com.nador.mobilemed.data.entity.measurement;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by nador on 07/08/16.
 */
@JsonObject
public abstract class Result {

    public enum Method {
        AUTOMATIC(1),
        MANUAL(2);

        private int mTypeCode;

        Method(int typeCode) {
            mTypeCode = typeCode;
        }

        public int getTypeCode() {
            return mTypeCode;
        }
    }

    public interface Unit {
        float convertTo(float value, Unit toUnit);
        String getString();
    }

    @JsonField(name = "type")
    private String mTypeDescription;
    @JsonField(name = "date")
    protected long mDate;
    @JsonField(name = "unit")
    private String mUnitDescription;

    public Result() {
        saveMethodEnum(Method.AUTOMATIC);
        mDate = System.currentTimeMillis();
    }

    public String getTypeDescription() {
        return mTypeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.mTypeDescription = typeDescription;
    }

    public Method getMethod() {
        return getMethodEnum();
    }

    public void setMethod(Method method) {
        saveMethodEnum(method);
    }

    public void saveMethodEnum(Method val) {
        mTypeDescription = val.toString();
    }

    public Method getMethodEnum() {
        return Method.valueOf(mTypeDescription);
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public String getUnitDescription() {
        return mUnitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.mUnitDescription = unitDescription;
    }

    public Unit getUnit() {
        return getUnitEnum();
    }

    public void setUnit(Unit unit) {
        saveUnitEnum(unit);
    }

    public void saveUnitEnum(Unit val) {
        mUnitDescription = val.toString();
    }

    public abstract Unit getUnitEnum();

    public abstract void convertTo(Unit unit);

    public abstract SerializedResult serialize();
}
