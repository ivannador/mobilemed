package com.nador.mobilemed.presentation.presenter.base;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.nador.mobilemed.data.aws.CognitoManager;
import com.nador.mobilemed.data.dagger.authentication.AuthenticationComponent;
import com.nador.mobilemed.data.dagger.authentication.AuthenticationModule;
import com.nador.mobilemed.data.dagger.authentication.DaggerAuthenticationComponent;

import javax.inject.Inject;

/**
 * Created by nador on 06/07/16.
 */
public abstract class AuthenticatingPresenter<V extends MvpView> extends SubscribingPresenter<V> {

    protected Context mContext;

    protected AuthenticationComponent mAuthenticationComponent;
    @Inject protected CognitoManager mCognitoManager;

    public AuthenticatingPresenter(Context context) {
        mContext = context;
        mAuthenticationComponent = DaggerAuthenticationComponent.builder()
                .authenticationModule(new AuthenticationModule(context))
                .build();
    }
}
