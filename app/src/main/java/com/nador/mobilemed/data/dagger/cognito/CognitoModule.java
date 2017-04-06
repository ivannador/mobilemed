package com.nador.mobilemed.data.dagger.cognito;

import android.app.Application;
import android.content.SharedPreferences;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import com.nador.mobilemed.data.APIConstants;
import com.nador.mobilemed.data.aws.CognitoAuthManager;
import com.nador.mobilemed.data.aws.CognitoDataset;
import com.nador.mobilemed.data.aws.S3Manager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 24/05/16.
 */
@Module
public class CognitoModule {

    public static final String MAIN_DATASET = "main_dataset";
    public static final String SETTINGS_DATASET = "settings_dataset";
    // FIXME: will not be stored on Cognito (sensitive data)
    public static final String MEASUREMENTS_DATASET = "measurements_dataset";

    // Provide Cognito User Pool for Cognito-based authentication
    @Provides
    @Singleton
    CognitoUserPool provideUserPool(Application application, APIConstants apiConstants) {
        return new CognitoUserPool(
                application,
                apiConstants.getAWSUserPoolId(),
                apiConstants.getAWSAppClientId(),
                apiConstants.getAWSAppClientSecret());

    }

    // Provide Cognito credential provider for authentication
    @Provides
    @Singleton
    CognitoCachingCredentialsProvider provideCredentialsProvider(Application application, APIConstants apiConstants) {
        return new CognitoCachingCredentialsProvider(
                application,
                apiConstants.getAWSIdentityPoolId(),
                Regions.fromName(apiConstants.getAWSCognitoRegion()));
    }

    // Provide Cognito synchronization manager for dataset manipulation
    @Provides
    @Singleton
    CognitoSyncManager provideSyncManager(Application application, CognitoCachingCredentialsProvider credentialsProvider, APIConstants apiConstants) {
        return new CognitoSyncManager(
                application,
                Regions.fromName(apiConstants.getAWSCognitoRegion()),
                credentialsProvider);
    }

    // Provide Cognito authentication manager
    @Provides
    @Singleton
    CognitoAuthManager provideAuthManager(
            CognitoUserPool userPool,
            CognitoSyncManager syncManager,
            CognitoCachingCredentialsProvider credentialsProvider,
            APIConstants apiConstants,
            SharedPreferences privatePreferences) {
        return new CognitoAuthManager(
                userPool,
                syncManager,
                credentialsProvider,
                apiConstants,
                privatePreferences);
    }

    @Provides
    @Singleton
    S3Manager provideS3Manager(
            Application application,
            CognitoCachingCredentialsProvider credentialsProvider,
            APIConstants apiConstants
    ) {
        return new S3Manager(application, credentialsProvider, apiConstants);
    }

    // Provide CognitoDataset by dataset name
    @Provides
    @Singleton
    @Named(MAIN_DATASET)
    CognitoDataset provideMainDataset(CognitoSyncManager syncManager) {
        return new CognitoDataset(
                MAIN_DATASET,
                syncManager);
    }

    // Provide CognitoDataset by dataset name
    @Provides
    @Singleton
    @Named(MEASUREMENTS_DATASET)
    CognitoDataset provideMeasurementsDataset(CognitoSyncManager syncManager) {
        return new CognitoDataset(
                MEASUREMENTS_DATASET,
                syncManager);
    }

    // Provide CognitoDataset by dataset name
    @Provides
    @Singleton
    @Named(SETTINGS_DATASET)
    CognitoDataset provideSettingsDataset(CognitoSyncManager syncManager) {
        return new CognitoDataset(
                SETTINGS_DATASET,
                syncManager);
    }
}
