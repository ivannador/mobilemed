package com.nador.mobilemed.data.entity.measurement;

import android.support.v4.util.Pair;

import com.nador.mobilemed.presentation.widget.ecg.ECGHighlight;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nador on 09/09/16.
 */
public class ECGMeasurements extends RealmObject {

    public interface HighlightTypeProvider {
        Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes();
    }

    public interface NormalValueProvider {
        boolean isNormalValue(float value);
    }

    public enum SegmentType implements HighlightTypeProvider {
        PWave {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.P, ECGHighlight.Type.PEnd);
            }
        }, PRSegment {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.PEnd, ECGHighlight.Type.R);
            }
        }, QRSComplex {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.Q, ECGHighlight.Type.S);
            }
        }, STSegment {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.S, ECGHighlight.Type.T);
            }
        }, TWave {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.T, ECGHighlight.Type.TEnd);
            }
        }, UWave {
            @Override
            public Pair<ECGHighlight.Type, ECGHighlight.Type> provideTypes() {
                return new Pair<>(ECGHighlight.Type.U, ECGHighlight.Type.UEnd);
            }
        }
    }

    public enum MeasurementValues implements NormalValueProvider {
        PWave {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 0.08f);
            }
        }, PRSegment {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 10f);
            }
        }, QRSComplex {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0.08f && value < 0.1f);
            }
        }, STSegment {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 10f);
            }
        }, TWave {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 0.16f);
            }
        }, UWave {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 10f);
            }
        }, PRInterval {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0.12f && value < 0.2f);
            }
        }, QTInterval {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 0f && value < 0.44f);
            }
        }, Pulse {
            @Override
            public boolean isNormalValue(float value) {
                return (value > 60f && value < 100f);
            }
        }
    }

    @PrimaryKey
    private String mECGGraphName;

    private boolean mHasPulseValue = false;
    private float mPulseValue;

    private boolean mHasPWave = false;
    private float mPWave;

    private boolean mHasPRSegment = false;
    private float mPRSegment;

    private boolean mHasPRInterval = false;
    private float mPRInterval;

    private boolean mHasQRSComplex = false;
    private float mQRSComplex;

    private boolean mHasSTSegment = false;
    private float mSTSegment;

    private boolean mHasTWave = false;
    private float mTWave;

    private boolean mHasQTInterval = false;
    private float mQTInterval;

    private boolean mHasUWave = false;
    private float mUWave;

    public ECGMeasurements() {}

    public ECGMeasurements(String mECGGraphName) {
        this.mECGGraphName = mECGGraphName;
    }

    // Copy constructor for RealmObject to Object mapping
    public ECGMeasurements(final ECGMeasurements otherMeasurements) {
        this.mECGGraphName = otherMeasurements.getECGGraphName();
        this.mHasPulseValue = otherMeasurements.hasPulseValue();
        this.mPulseValue = otherMeasurements.getPulseValue();
        this.mHasPWave = otherMeasurements.hasPWave();
        this.mPWave = otherMeasurements.getPWave();
        this.mHasPRSegment = otherMeasurements.hasPRSegment();
        this.mPRSegment = otherMeasurements.getPRSegment();
        this.mHasPRInterval = otherMeasurements.hasPRInterval();
        this.mPRInterval = otherMeasurements.getPRInterval();
        this.mHasQRSComplex = otherMeasurements.hasQRSComplex();
        this.mQRSComplex = otherMeasurements.getQRSComplex();
        this.mHasSTSegment = otherMeasurements.hasSTSegment();
        this.mSTSegment = otherMeasurements.getSTSegment();
        this.mHasTWave = otherMeasurements.hasTWave();
        this.mTWave = otherMeasurements.getTWave();
        this.mHasQTInterval = otherMeasurements.hasQTInterval();
        this.mQTInterval = otherMeasurements.getQTInterval();
        this.mHasUWave = otherMeasurements.hasUWave();
        this.mUWave = otherMeasurements.getUWave();
    }

    public String getECGGraphName() {
        return mECGGraphName;
    }

    public void setECGGraphName(String mECGGraphName) {
        this.mECGGraphName = mECGGraphName;
    }

    public boolean hasPulseValue() {
        return mHasPulseValue;
    }

    public void setHasPulseValue(boolean mHasPulseValue) {
        this.mHasPulseValue = mHasPulseValue;
    }

    public float getPulseValue() {
        return mPulseValue;
    }

    public void setPulseValue(float mPulseValue) {
        setHasPulseValue(true);
        this.mPulseValue = mPulseValue;
    }

    public boolean hasPWave() {
        return mHasPWave;
    }

    public void setHasPWave(boolean mHasPWave) {
        this.mHasPWave = mHasPWave;
    }

    public float getPWave() {
        return mPWave;
    }

    public void setPWave(float mPWave) {
        setHasPWave(true);
        this.mPWave = mPWave;

        if (hasPRSegment()) {
            setPRInterval(getPWave() + getPRSegment());
        }
    }

    public boolean hasPRSegment() {
        return mHasPRSegment;
    }

    public void setHasPRSegment(boolean mHasPRSegment) {
        this.mHasPRSegment = mHasPRSegment;
    }

    public float getPRSegment() {
        return mPRSegment;
    }

    public void setPRSegment(float mPRSegment) {
        setHasPRSegment(true);
        this.mPRSegment = mPRSegment;

        if (hasPWave()) {
            setPRInterval(getPWave() + getPRSegment());
        }
    }

    public boolean hasPRInterval() {
        return mHasPRInterval;
    }

    public void setHasPRInterval(boolean mHasPRInterval) {
        this.mHasPRInterval = mHasPRInterval;
    }

    public float getPRInterval() {
        return mPRInterval;
    }

    public void setPRInterval(float mPRInterval) {
        setHasPRInterval(true);
        this.mPRInterval = mPRInterval;
    }

    public boolean hasQRSComplex() {
        return mHasQRSComplex;
    }

    public void setHasQRSComplex(boolean mHasQRSComplex) {
        this.mHasQRSComplex = mHasQRSComplex;
    }

    public float getQRSComplex() {
        return mQRSComplex;
    }

    public void setQRSComplex(float mQRSComplex) {
        setHasQRSComplex(true);
        this.mQRSComplex = mQRSComplex;

        if (hasSTSegment() && hasTWave()) {
            setQTInterval(getQRSComplex() + getSTSegment() + getTWave());
        }
    }

    public boolean hasSTSegment() {
        return mHasSTSegment;
    }

    public void setHasSTSegment(boolean mHasSTSegment) {
        this.mHasSTSegment = mHasSTSegment;
    }

    public float getSTSegment() {
        return mSTSegment;
    }

    public void setSTSegment(float mSTSegment) {
        setHasSTSegment(true);
        this.mSTSegment = mSTSegment;

        if (hasQRSComplex() && hasTWave()) {
            setQTInterval(getQRSComplex() + getSTSegment() + getTWave());
        }
    }

    public boolean hasTWave() {
        return mHasTWave;
    }

    public void setHasTWave(boolean mHasTWave) {
        this.mHasTWave = mHasTWave;
    }

    public float getTWave() {
        return mTWave;
    }

    public void setTWave(float mTWave) {
        setHasTWave(true);
        this.mTWave = mTWave;

        if (hasSTSegment() && hasQRSComplex()) {
            setQTInterval(getQRSComplex() + getSTSegment() + getTWave());
        }
    }

    public boolean hasQTInterval() {
        return mHasQTInterval;
    }

    public void setHasQTInterval(boolean mHasQTInterval) {
        this.mHasQTInterval = mHasQTInterval;
    }

    public float getQTInterval() {
        return mQTInterval;
    }

    public void setQTInterval(float mQTInterval) {
        setHasQTInterval(true);
        this.mQTInterval = mQTInterval;
    }

    public boolean hasUWave() {
        return mHasUWave;
    }

    public void setHasUWave(boolean mHasUWave) {
        this.mHasUWave = mHasUWave;
    }

    public float getUWave() {
        return mUWave;
    }

    public void setUWave(float mUWave) {
        setHasUWave(true);
        this.mUWave = mUWave;
    }
}
