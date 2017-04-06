package com.nador.mobilemed.data.dagger.user;

import android.content.Context;

import com.nador.mobilemed.data.manager.ContactManager;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.APIConstants;
import com.nador.mobilemed.data.dagger.scopes.UserScope;
import com.nador.mobilemed.data.manager.AppointmentManager;
import com.nador.mobilemed.data.manager.FunctionAvailabilityManager;
import com.nador.mobilemed.data.manager.HistoryManager;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.data.manager.MedicationManager;
import com.nador.mobilemed.data.manager.VideoManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 27/07/16.
 */
@Module
public class UserModule {

    Context mContext;
    String mUserId;
    String mUsername;

    public UserModule(Context context, String userId, String username) {
        mContext = context;
        mUserId = userId;
        mUsername = username;
    }

    @Provides
    @UserScope
    Context provideContext() {
        return mContext;
    }

    @Provides
    @UserScope
    String provideUserId() {
        return mUserId;
    }

    @Provides
    @UserScope
    @Named("username")
    String provideUsername() {
        return mUsername;
    }

    @Provides
    @UserScope
    VideoManager provideVideoManager(Context context) {
        return new VideoManager(context, MobilemedApp.getCognitoComponent(mContext).s3Manager());
    }

    @Provides
    @UserScope
    ContactManager provideContactManager(Context context, String userId) {
        return new ContactManager(context, userId);
    }

    @Provides
    @UserScope
    AppointmentManager provideAppointmentManager(Context context, String userId) {
        return new AppointmentManager(context, userId);
    }

    @Provides
    @UserScope
    MedicationManager provideMedicationManager(Context context, String userId) {
        return new MedicationManager(context, userId);
    }

    @Provides
    @UserScope
    @Named("ECG")
    HistoryManager provideECGHistoryManager(Context context, String userId) {
        return new HistoryManager.ECGHistory(context, userId, MobilemedApp.getCognitoComponent(context).s3Manager());
    }

    @Provides
    @UserScope
    @Named("Temperature")
    HistoryManager provideTemperatureHistoryManager(Context context, String userId) {
        return new HistoryManager.TemperatureHistory(context, userId);
    }

    @Provides
    @UserScope
    @Named("BP")
    HistoryManager provideBloodPressureHistoryManager(Context context, String userId) {
        return new HistoryManager.BloodPressureHistory(context, userId);
    }

    @Provides
    @UserScope
    @Named("Weight")
    HistoryManager provideWeightHistoryManager(Context context, String userId) {
        return new HistoryManager.WeightHistory(context, userId);
    }

    @Provides
    @UserScope
    @Named("Glucometer")
    HistoryManager provideGlucometerHistoryManager(Context context, String userId) {
        return new HistoryManager.GlucometerHistory(context, userId);
    }

    @Provides
    @UserScope
    MeasurementManager provideMeasurementManager(Context context, String userId) {
        return new MeasurementManager(context, userId, MobilemedApp.getCognitoComponent(context).cognitoManager());
    }

    @Provides
    @UserScope
    FunctionAvailabilityManager provideFunctionAvailabilityManager(Context context, APIConstants apiConstants) {
        return new FunctionAvailabilityManager(context, apiConstants, MobilemedApp.getCognitoComponent(context).cognitoManager());
    }
}
