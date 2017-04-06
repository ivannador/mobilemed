package com.nador.mobilemed.data.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.nador.mobilemed.data.APIConstants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 23/05/16.
 */
@Module
public class AppModule {

    private static final String APP_PREFERENCES = "PrivateAppPreferences";

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    // Provide application where context is needed
    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    // Provide application API constants
    @Provides
    @Singleton
    APIConstants provideAPIConstants(Application application) {
        return new APIConstants(application);
    }

    // Provice application shared preferences
    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }
}
