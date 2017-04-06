package com.nador.mobilemed.presentation.presenter;

import android.content.Context;

import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.presenter.base.AuthenticatingPresenter;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.presentation.splash.ISplashController;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nador on 23/05/16.
 */
public class SplashControllerPresenter extends AuthenticatingPresenter<ISplashController> {

    private static final int MAX_PROGRESS = 100;
    private static final int MAX_STEPS = 3;
    private int mRemainingSteps = 3;

    public SplashControllerPresenter(Context context) {
        super(context);
        mAuthenticationComponent.inject(this);
    }

    public void initialize() {
        if (mSubscriptions.hasSubscriptions()) {
            return;
        }

        Subscription s;
        if (MobilemedApp.hasUserLoggedIn(mContext)) {
            s = mCognitoManager.reauthCurrentUser()
                    .compose(ReactiveExtensions.applyIOSchedulers())
                    .subscribe(aBoolean -> {
                        if (isViewAttached()) {
                            getView().setProgress(MAX_PROGRESS);
                            if (aBoolean) {
                                getView().userReauthenticated();
                            } else {
                                getView().userReauthLoginNeeded();
                            }
                        }
                    }, throwable -> {
                        if (isViewAttached()) {
                            getView().setProgress(MAX_PROGRESS);
                            getView().userReauthLoginNeeded();
                        }
                    });

        } else {
            Timber.d("Attempt to autologin current user...");
            s = mCognitoManager.autologinCurrentUser()
                    .compose(ReactiveExtensions.applyIOSchedulers())
                    .subscribe(aBoolean -> {
                        mRemainingSteps--;
                        if (isViewAttached()) {
                            getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                            if (aBoolean) {
                                createUserComponent();
                            } else {
                                getView().dataInitializedLoginNeeded();
                            }
                        }
                    }, throwable -> {
                        mRemainingSteps--;
                        if (isViewAttached()) {
                            getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                            getView().dataInitializedLoginNeeded();
                        }
                    });
        }

        mSubscriptions.add(s);
    }

    public void createUserComponent() {
        Subscription s = mCognitoManager.getCurrentUsername()
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(username -> {
                    mRemainingSteps--;
                    if (isViewAttached()) {
                        getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                        MobilemedApp.createUserComponent(mContext, username);
                        syncDatasets();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Could not get current username");
                    mRemainingSteps--;
                    if (isViewAttached()) {
                        getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                        MobilemedApp.createUserComponent(mContext, "");
                        syncDatasets();
                    }
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
                    mRemainingSteps--;
                    if (isViewAttached()) {
                        getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                        getView().dataInitialized();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Could not sync");
                    mRemainingSteps--;
                    if (isViewAttached()) {
                        getView().setProgress(MAX_PROGRESS - mRemainingSteps * (MAX_PROGRESS / MAX_STEPS));
                        getView().dataInitialized();
                    }
                });
    }
}
