package com.nador.mobilemed.data.dagger.authentication;

import android.content.Context;

import com.nador.mobilemed.data.dagger.scopes.AuthenticationScope;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.aws.CognitoManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 06/07/16.
 */
@Module
public class AuthenticationModule {
    Context mContext;

    public AuthenticationModule(Context context) {
        mContext = context;
    }

    @Provides
    @AuthenticationScope
    Context provideContext() {
        return mContext;
    }

    @Provides
    @AuthenticationScope
    CognitoManager provideCognitoManager(Context context) {
        return MobilemedApp.getCognitoComponent(context.getApplicationContext()).cognitoManager();
    }
}
