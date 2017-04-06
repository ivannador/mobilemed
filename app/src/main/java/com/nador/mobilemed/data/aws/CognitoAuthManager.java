package com.nador.mobilemed.data.aws;

import android.content.SharedPreferences;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.nador.mobilemed.data.APIConstants;
import com.nador.mobilemed.data.AppPreferencesHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by nador on 26/05/16.
 */
public class CognitoAuthManager {

    private CognitoUserPool mUserPool;
    private CognitoSyncManager mSyncManager;
    private CognitoCachingCredentialsProvider mCredentialsProvider;
    private APIConstants mAPIConstants;
    private SharedPreferences mPrivatePreferences;

    // Store new password continuation while the user types in new pw
    private NewPasswordContinuation mNewPasswordContinuation;

    public CognitoAuthManager(
            CognitoUserPool userPool,
            CognitoSyncManager syncManager,
            CognitoCachingCredentialsProvider credentialsProvider,
            APIConstants apiConstants,
            SharedPreferences privatePreferences) {
        mUserPool = userPool;
        mSyncManager = syncManager;
        mCredentialsProvider = credentialsProvider;
        mAPIConstants = apiConstants;
        mPrivatePreferences = privatePreferences;
    }

    public String getIdentityId() {
        String identityId = mCredentialsProvider.getIdentityId();
        if (identityId != null) {
            return mCredentialsProvider.getIdentityId();
        }
        return "";
    }

    public Observable<String> getCurrentUserName() {
        return Observable.create(subscriber -> {
            // Last cached user
            final CognitoUser user = mUserPool.getCurrentUser();
            user.getDetails(new GetDetailsHandler() {
                @Override
                public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                    Map<String, String> attributes = cognitoUserDetails.getAttributes().getAttributes();
                    String name = "";
                    if (attributes.get("custom:user_role").equals("2")) {
                        name += "Dr. ";
                    }
                    name += attributes.get("given_name") + " " + attributes.get("family_name");
                    subscriber.onNext(name);
                    subscriber.onCompleted();
                }

                @Override
                public void onFailure(Exception exception) {
                    subscriber.onError(exception);
                }
            });

        });
    }

    // TESTING
    public Observable<Boolean> registerTestUser(final String username) {
        CognitoUserAttributes attributes = new CognitoUserAttributes();
        attributes.addAttribute("given_name", username);
        attributes.addAttribute("family_name", "Testuser");
        attributes.addAttribute("middle_name", "");
        attributes.addAttribute("picture", "qwerty");
        attributes.addAttribute("email", username + "@testuser.hu");
        attributes.addAttribute("gender", "male");
        attributes.addAttribute("locale", "hu");
        attributes.addAttribute("phone_number", "+3612345555");
        attributes.addAttribute("address", "1234 Foo, Bar 5.");
        return Observable.create(subscriber -> {
            mUserPool.signUp(username, "xyz", attributes, null, new SignUpHandler() {
                @Override
                public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }

                @Override
                public void onFailure(Exception exception) {
                    subscriber.onError(exception);
                }
            });
        });
    }

    // TODO
    public Observable<Boolean> registerUser() {
        return Observable.just(true);
    }

    public Observable<ChallengeContinuation> loginUser(final String username, final String password, final boolean rememberUser) {
        return Observable.create(subscriber -> {
            Timber.d("Logging in user...");
            final CognitoUser user = mUserPool.getUser(username);
            user.getSession(new AuthenticationHandler() {
                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                    Timber.d("Authentication of user successful.");
                    String idToken = userSession.getIdToken().getJWTToken();

                    Map<String, String> logins = new HashMap<>();
                    logins.put(mAPIConstants.getAWSCognitoIdentityProvider(), idToken);
                    mCredentialsProvider.setLogins(logins);
                    mCredentialsProvider.refresh();

                    CognitoUserAttributes attrs = new CognitoUserAttributes();
                    attrs.addAttribute("custom:identity_id", getIdentityId());
                    Observable.fromCallable(() -> {
                        user.updateAttributes(attrs, new UpdateAttributesHandler() {
                            @Override
                            public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                                Timber.d("Update attribute identity_id succeeeded");
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Timber.e(exception, "Update attribute identity_id failed");
                            }
                        });
                        return true;
                    }).subscribe(aBoolean -> {}, throwable -> Timber.e(throwable, "Update attribute identity_id failed"));

                    AppPreferencesHelper.putRememberUser(mPrivatePreferences, rememberUser);
                    subscriber.onCompleted();
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
                    Timber.d("Requiring user authentication...");
                    AuthenticationDetails authenticationDetails = new AuthenticationDetails(UserId, password, null);
                    authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                    authenticationContinuation.continueTask();
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                    continuation.continueTask();
                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {
                    subscriber.onNext(continuation);
                }

                @Override
                public void onFailure(Exception exception) {
                    Timber.e(exception, "Authentication of user failed.");
                    subscriber.onError(exception);
                }
            });
        });
    }

    public Observable<Boolean> autologinCurrentUser() {
        return Observable.create(subscriber -> {
            if (!AppPreferencesHelper.getRememberUser(mPrivatePreferences)) {
                Timber.d("Autologin not enabled for user, aborting...");
                subscriber.onError(new Throwable("Autologin not enabled for user, aborting..."));
                return;
            }

            Timber.d("Trying to autologin current user...");
            refreshCurrentUserSession(subscriber);
        });
    }

    public Observable<Boolean> reauthCurrentUser() {
        return Observable.create(subscriber -> {
            Timber.d("Trying to reauthenticate current user...");
            refreshCurrentUserSession(subscriber);
        });
    }

    private void refreshCurrentUserSession(Subscriber subscriber) {
        final CognitoUser currentUser = mUserPool.getCurrentUser();
        currentUser.getSession(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Timber.d("Refresh session of current user successful.");
                String idToken = userSession.getIdToken().getJWTToken();

                Map<String, String> logins = new HashMap<>();
                logins.put(mAPIConstants.getAWSCognitoIdentityProvider(), idToken);
                mCredentialsProvider.setLogins(logins);
                mCredentialsProvider.refresh();

                CognitoUserAttributes attrs = new CognitoUserAttributes();
                attrs.addAttribute("custom:identity_id", getIdentityId());
                Observable.fromCallable(() -> {
                    currentUser.updateAttributes(attrs, new UpdateAttributesHandler() {
                        @Override
                        public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                            Timber.d("Update attribute identity_id succeeeded");
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Timber.e(exception, "Update attribute identity_id failed");
                        }
                    });
                    return true;
                }).subscribe(aBoolean -> {}, throwable -> Timber.e(throwable, "Update attribute identity_id failed"));

                subscriber.onNext(true);
                subscriber.onCompleted();
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
                Timber.d("User could not be authenticated, log in by providing username and password.");
                subscriber.onNext(false);
                subscriber.onCompleted();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                continuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                continuation.continueTask();
            }

            @Override
            public void onFailure(Exception exception) {
                Timber.e(exception, "Refresh session of user failed.");
                subscriber.onError(exception);
            }
        });
    }

    public Observable<Boolean> logoutUser() {
        return Observable.fromCallable(() -> {
            CognitoUser user = mUserPool.getCurrentUser();
            user.signOut();

            mSyncManager.wipeData();

            return true;
        });
    }

    public Observable<ForgotPasswordContinuation> forgotPassword(final String username) {
        return Observable.create(subscriber -> {
            Timber.d("User %s forgot password...", username);
            final CognitoUser user = mUserPool.getUser(username);
            user.forgotPassword(new ForgotPasswordHandler() {
                @Override
                public void onSuccess() {
                    Timber.d("Forgot password request successful.");
                    subscriber.onCompleted();
                }

                @Override
                public void getResetCode(ForgotPasswordContinuation continuation) {
                    Timber.d("Reset code sent out.");
                    subscriber.onNext(continuation);
                }

                @Override
                public void onFailure(Exception exception) {
                    Timber.e(exception, "Forgot password request for user %s failed.", username);
                    subscriber.onError(exception);
                }
            });
        });
    }
}
