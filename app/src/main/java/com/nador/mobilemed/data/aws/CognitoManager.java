package com.nador.mobilemed.data.aws;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.nador.mobilemed.data.dagger.cognito.CognitoModule;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by nador on 24/05/16.
 */
@Singleton
public class CognitoManager {

    @Inject CognitoAuthManager mAuthManager;

    @Inject @Named(CognitoModule.MAIN_DATASET) CognitoDataset mMainDataset;
    @Inject @Named(CognitoModule.SETTINGS_DATASET) CognitoDataset mSettingsDataset;
    // FIXME: will not be stored on Cognito (sensitive data)
    @Inject @Named(CognitoModule.MEASUREMENTS_DATASET) CognitoDataset mMeasurementsDataset;

    @Inject
    public CognitoManager() {}

    public String getIdentityId() {
        return mAuthManager.getIdentityId();
    }

    public Observable<String> getCurrentUsername() {
        return mAuthManager.getCurrentUserName();
    }

    public Observable<Boolean> registerUser() {
        return mAuthManager.registerUser();
    }

    public Observable<ChallengeContinuation> loginUser(final String username, final String password, final boolean rememberUser) {
        return mAuthManager.loginUser(username, password, rememberUser);
    }

    public Observable<Boolean> autologinCurrentUser() {
        return mAuthManager.autologinCurrentUser();
    }

    public Observable<Boolean> reauthCurrentUser() {
        return mAuthManager.reauthCurrentUser();
    }

    public Observable<Boolean> logoutUser() {
        return mAuthManager.logoutUser()
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        mMainDataset.close();
                        mSettingsDataset.close();
                        mMeasurementsDataset.close();
                    }
                    return Observable.just(aBoolean);
                });
    }

    public Observable<ForgotPasswordContinuation> forgotPassword(final String username) {
        return mAuthManager.forgotPassword(username);
    }

    public CognitoDataset getMainDataset() {
        return mMainDataset;
    }

    public CognitoDataset getSettingsDataset() {
        return mSettingsDataset;
    }

    public CognitoDataset getMeasurementsDataset() {
        return mMeasurementsDataset;
    }
}
