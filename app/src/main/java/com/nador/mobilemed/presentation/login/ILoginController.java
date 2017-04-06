package com.nador.mobilemed.presentation.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by nador on 20/06/16.
 */
public interface ILoginController extends MvpView {
    void loginSucceeded();
    void loginFailed(final String message);

    void newPasswordRequireData();
    void newPasswordNoMatch();

    void forgotPasswordRequireData(final String destination, final String deliveryMethod);
    void forgotPasswordSucceeded();
    void forgotPasswordFailed(final String message);
    void forgotPasswordNoMatch(final String destination, final String deliveryMethod);
}
