package com.nador.mobilemed.data.dagger;

import android.app.Application;

import com.nador.mobilemed.data.APIConstants;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by nador on 07/06/16.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Application application();
    APIConstants apiConstants();
}
