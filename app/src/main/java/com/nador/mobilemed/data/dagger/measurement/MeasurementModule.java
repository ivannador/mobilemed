package com.nador.mobilemed.data.dagger.measurement;

import android.content.Context;

import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.dagger.scopes.MeasurementScope;
import com.nador.mobilemed.data.manager.MeasurementManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 28/06/16.
 */
@Module
public class MeasurementModule {

    Context mContext;

    public MeasurementModule(Context context) {
        mContext = context;
    }

    @Provides
    @MeasurementScope
    Context provideContext() {
        return mContext;
    }

    // Measurement can only used when user is authenticated -> UserComponent exists
    @Provides
    @MeasurementScope
    MeasurementManager provideMeasurementManager(Context context) {
        return MobilemedApp.getUserComponent(context).measurementManager();
    }
}
