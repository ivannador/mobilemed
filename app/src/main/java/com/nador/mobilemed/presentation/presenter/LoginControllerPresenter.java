package com.nador.mobilemed.presentation.presenter;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.presentation.login.ILoginController;
import com.nador.mobilemed.presentation.presenter.base.AuthenticatingPresenter;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nador on 20/06/16.
 */
public class LoginControllerPresenter extends AuthenticatingPresenter<ILoginController> {

    private ForgotPasswordContinuation mContinuation;
    private NewPasswordContinuation mNewPWContinuation;

    public LoginControllerPresenter(Context context) {
        super(context);
        mAuthenticationComponent.inject(this);
    }

    public void login(final String username, final String password, final boolean rememberUser) {
        Timber.d("Attempt to login user...");
        Subscription s = mCognitoManager.loginUser(username, password, rememberUser)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(challengeContinuation -> {
                    if ("NEW_PASSWORD_REQUIRED".equals(challengeContinuation.getChallengeName())) {
                        mNewPWContinuation = (NewPasswordContinuation) challengeContinuation;
                        if (isViewAttached()) {
                            getView().newPasswordRequireData();
                        }
                    } else {
                        challengeContinuation.continueTask();
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().loginFailed(throwable.getMessage());
                    }
                }, () -> createUserComponent());

        mSubscriptions.add(s);
    }

    public void createUserComponent() {
        Subscription s = mCognitoManager.getCurrentUsername()
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(username -> {
                    if (isViewAttached()) {
                        MobilemedApp.createUserComponent(mContext, username);
                        syncDatasets();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Could not get current username");
                    MobilemedApp.createUserComponent(mContext, "");
                    syncDatasets();
                });
    }

    public void syncDatasets() {
        Observable.zip(
                mCognitoManager.getMeasurementsDataset().synchronize(),
                mCognitoManager.getSettingsDataset().synchronize(),
                (measurementSyncResult, settingsSyncResult) -> measurementSyncResult && settingsSyncResult
        )
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean1 -> {
                    getView().loginSucceeded();
                }, throwable -> {
                    Timber.e(throwable, "Could not sync");
                    getView().loginSucceeded();
                });
    }

    public void setNewPasswordRequiredData(final String password, final String repeatPassword) {
        if (mNewPWContinuation != null) {
            if (!password.equals(repeatPassword)) {
                if (isViewAttached()) {
                    getView().newPasswordNoMatch();
                }
                return;
            }

            Observable.fromCallable(() -> {
                mNewPWContinuation.setPassword(password);
                mNewPWContinuation.continueTask();

                mNewPWContinuation = null;
                return true;
            }).compose(ReactiveExtensions.applyIOSchedulers())
                    .subscribe(aBoolean -> {}, throwable -> {
                        if (isViewAttached()) {
                            getView().loginFailed(throwable.getMessage());
                        }
                    });
        }
    }

    public void cancelNewPasswordFlow() {
        if (mNewPWContinuation != null) {
            Observable.fromCallable(() -> {
                mNewPWContinuation.continueTask();
                mNewPWContinuation = null;
                return true;
            }).compose(ReactiveExtensions.applyIOSchedulers())
                    .subscribe(aBoolean -> {}, throwable -> {
                        if (isViewAttached()) {
                            getView().loginFailed(throwable.getMessage());
                        }
                    });
        }
    }

    public void forgotPassword(final String username) {
        Subscription s = mCognitoManager.forgotPassword(username)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(new Subscriber<ForgotPasswordContinuation>() {
                    @Override
                    public void onCompleted() {
                        if (isViewAttached()) {
                            getView().forgotPasswordSucceeded();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().forgotPasswordFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(ForgotPasswordContinuation continuation) {
                        mContinuation = continuation;
                        if (isViewAttached()) {
                            getView().forgotPasswordRequireData(mContinuation.getParameters().getDestination(), mContinuation.getParameters().getDeliveryMedium());
                        }
                    }
                });

        mSubscriptions.add(s);
    }

    public void setForgotPasswordRequiredData(final String verificationCode, final String password, final String repeatPassword) {
        if (mContinuation != null) {

            if (!password.equals(repeatPassword)) {
                if (isViewAttached()) {
                    getView().forgotPasswordNoMatch(mContinuation.getParameters().getDestination(), mContinuation.getParameters().getDeliveryMedium());
                }
                return;
            }

            Observable.fromCallable(() -> {
                mContinuation.setVerificationCode(verificationCode);
                mContinuation.setPassword(password);
                mContinuation.continueTask();

                mContinuation = null;
                return true;
            }).compose(ReactiveExtensions.applyIOSchedulers())
            .subscribe();
        }
    }
}
