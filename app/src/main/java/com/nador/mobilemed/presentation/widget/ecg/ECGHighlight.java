package com.nador.mobilemed.presentation.widget.ecg;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.highlight.Highlight;

/**
 * Created by nador on 07/09/16.
 */
public class ECGHighlight extends Highlight {

    public interface ColorProvider {
        int getColor(Context context);
    }

    public enum Type implements ColorProvider {
        P {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, PEnd {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, Q {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, R {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, S {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, T {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, TEnd {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, U {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }, UEnd {
            @Override
            public int getColor(Context context) {
                return ContextCompat.getColor(context, com.nador.mobilemed.R.color.colorPainLevel9);
            }
        }
    }

    private Type mType;

    public ECGHighlight(float x, int dataSetIndex, Type type) {
        super(x, dataSetIndex, 0);
        mType = type;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public Type getType() {
        return mType;
    }
}
