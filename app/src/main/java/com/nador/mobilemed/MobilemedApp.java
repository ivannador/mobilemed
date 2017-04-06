package com.nador.mobilemed;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.nador.mobilemed.data.dagger.AppComponent;
import com.nador.mobilemed.data.dagger.AppModule;
import com.nador.mobilemed.data.dagger.cognito.CognitoComponent;
import com.nador.mobilemed.data.dagger.cognito.CognitoModule;
import com.nador.mobilemed.data.dagger.measurement.TremorSensorComponent;
import com.nador.mobilemed.data.dagger.user.UserComponent;
import com.nador.mobilemed.data.dagger.user.UserModule;
import com.nador.mobilemed.BuildConfig;
import com.nador.mobilemed.data.dagger.DaggerAppComponent;
import com.nador.mobilemed.data.dagger.cognito.DaggerCognitoComponent;
import com.nador.mobilemed.data.dagger.measurement.DaggerTremorSensorComponent;
import com.nador.mobilemed.data.dagger.user.DaggerUserComponent;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by nador on 23/05/16.
 */
public class MobilemedApp extends Application {

    private AppComponent mAppComponent;

    private CognitoComponent mCognitoComponent;

    private UserComponent mUserComponent;

    private TremorSensorComponent mTremorComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
        }

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        // Create Cognito backend managers
        mCognitoComponent = DaggerCognitoComponent.builder()
                .appModule(new AppModule(this))
                .cognitoModule(new CognitoModule())
                .build();
    }

    /**
     * Create user component when user is logged in
     */
    public void createUserComponent(String username) {
        mUserComponent = DaggerUserComponent.builder()
                .appComponent(mAppComponent)
                .userModule(new UserModule(this, mCognitoComponent.cognitoManager().getIdentityId(), username))
                .build();
    }

    public void removeUserComponent() {
        mUserComponent = null;
    }

    public static void createUserComponent(Context context, String username) {
        ((MobilemedApp) context.getApplicationContext()).createUserComponent(username);
    }

    public static void removeUserComponent(Context context) {
        ((MobilemedApp) context.getApplicationContext()).removeUserComponent();
    }

    public static AppComponent getAppComponent(Context context) {
        return ((MobilemedApp) context.getApplicationContext()).mAppComponent;
    }

    public static CognitoComponent getCognitoComponent(Context context) {
        return ((MobilemedApp) context.getApplicationContext()).mCognitoComponent;
    }

    public static UserComponent getUserComponent(Context context) {
        return ((MobilemedApp) context.getApplicationContext()).mUserComponent;
    }

    public static boolean hasUserLoggedIn(Context context) {
        return MobilemedApp.getUserComponent(context) != null;
    }
}
