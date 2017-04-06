package com.nador.mobilemed.data.dagger.authentication;

import com.nador.mobilemed.data.dagger.scopes.AuthenticationScope;
import com.nador.mobilemed.presentation.presenter.DashboardControllerPresenter;
import com.nador.mobilemed.presentation.presenter.LoginControllerPresenter;
import com.nador.mobilemed.presentation.presenter.SplashControllerPresenter;

import dagger.Component;

/**
 * Created by nador on 06/07/16.
 */
@AuthenticationScope
@Component(dependencies = {}, modules = AuthenticationModule.class)
public interface AuthenticationComponent {
    void inject(SplashControllerPresenter presenter);
    void inject(LoginControllerPresenter presenter);
    void inject(DashboardControllerPresenter presenter);
}
