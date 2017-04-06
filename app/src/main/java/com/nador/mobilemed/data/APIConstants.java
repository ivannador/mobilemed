package com.nador.mobilemed.data;

import android.app.Application;
import android.content.Context;

import com.nador.mobilemed.R;

/**
 * Created by nador on 26/05/16.
 */
public final class APIConstants {

    Context mContext;

    public APIConstants(Application application) {
        mContext = application;
    }

    public String getAWSIdentityPoolId() {
        return mContext.getString(R.string.AWS_IDENTITY_POOL_ID);
    }

    public String getAWSCognitoRegion() {
        return mContext.getString(R.string.AWS_COGNITO_REGION);
    }

    public String getAWSUserPoolId() {
        return mContext.getString(R.string.AWS_USER_POOL_ID);
    }

    public String getAWSAppClientId() {
        return mContext.getString(R.string.AWS_APP_CLIENT_ID);
    }

    public String getAWSAppClientSecret() {
        return mContext.getString(R.string.AWS_APP_CLIENT_SECRET);
    }

    public String getAWSCognitoIdentityProvider() {
        return mContext.getString(R.string.AWS_COGNITO_IDENTITY_PROVIDER);
    }

    public String getAWSS3MainBucket() {
        return mContext.getString(R.string.AWS_S3_BUCKET_MAIN);
    }
    public String getAWSS3ECGBucket() {
        return mContext.getString(R.string.AWS_S3_BUCKET_ECG);
    }

    public String getAWSS3Region() {
        return mContext.getString(R.string.AWS_S3_REGION);
    }

    public String getConfigurationRealmName() {
        return mContext.getString(R.string.CONFIGURATION_REALM);
    }
}
