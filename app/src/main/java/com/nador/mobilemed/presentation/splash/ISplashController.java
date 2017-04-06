package com.nador.mobilemed.presentation.splash;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by nador on 23/05/16.
 */
public interface ISplashController extends MvpView {
    void setProgress(final int progress);

    void dataInitialized();
    void dataInitializedLoginNeeded();

    void userReauthenticated();
    void userReauthLoginNeeded();
}
