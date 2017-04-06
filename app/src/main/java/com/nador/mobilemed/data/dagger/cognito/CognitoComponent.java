package com.nador.mobilemed.data.dagger.cognito;

import com.nador.mobilemed.data.dagger.AppModule;
import com.nador.mobilemed.data.aws.CognitoManager;
import com.nador.mobilemed.data.aws.S3Manager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by nador on 24/05/16.
 */
@Singleton
@Component(modules = {AppModule.class, CognitoModule.class})
public interface CognitoComponent {
    CognitoManager cognitoManager();
    S3Manager s3Manager();
}
