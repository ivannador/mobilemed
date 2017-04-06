package com.nador.mobilemed.presentation.presenter.function.measurement;

import android.content.Context;

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.nador.mobilemed.data.dagger.measurement.MeasurementComponent;
import com.nador.mobilemed.data.dagger.measurement.MeasurementModule;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.data.measurement.Glucometer;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.function.measurement.IGlucometerMeasurementController;
import com.nador.mobilemed.data.dagger.measurement.BluetoothHealthModule;
import com.nador.mobilemed.data.dagger.measurement.DaggerBluetoothHealthComponent;
import com.nador.mobilemed.data.dagger.measurement.DaggerMeasurementComponent;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nador on 14/07/16.
 */
public class GlucometerMeasurementControllerPresenter extends SubscribingPresenter<IGlucometerMeasurementController> {

    MeasurementComponent mMeasurementComponent;
    @Inject
    Glucometer mGlucometer;
    @Inject
    MeasurementManager mMeasurementManager;

    public GlucometerMeasurementControllerPresenter(Context context) {
        mMeasurementComponent = DaggerMeasurementComponent.builder()
                .bluetoothHealthComponent(DaggerBluetoothHealthComponent.builder()
                        .bluetoothHealthModule(new BluetoothHealthModule(context))
                        .build())
                .measurementModule(new MeasurementModule(context))
                .build();
        mMeasurementComponent.inject(this);
    }

    public void measureGlucoseLevel() {
        Subscription subscription = mGlucometer
                .connectDevice()
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return mGlucometer.measureGlucoseLevel();
                    } else {
                        return Observable.error(new Throwable("Couldn't connect to glucometer!"));
                    }
                })
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(glucoseLevel -> {
                    if (isViewAttached()) {
                        if (glucoseLevel != null) {
                            getView().processMeasurementResult(glucoseLevel);
                        } else {
                            getView().processMeasurementFailed();
                        }
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().processMeasurementFailed();
                    }
                });
        mSubscriptions.add(subscription);
    }

    public void storeMeasurement(final float level, final Result.Method method) {
        BloodGlucoseLevel glucoseLevel = new BloodGlucoseLevel(level, BloodGlucoseLevel.GlucoseUnit.MMOL);
        glucoseLevel.setMethod(method);
        mMeasurementManager.storeResult(glucoseLevel)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        if (aBoolean) {
                            getView().storeResultSuccessful();
                        } else {
                            getView().storeResultFailed();
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable, "Store error");
                    if (throwable instanceof NotAuthorizedException) {
                        getView().userReauthNeeded();
                    } else {
                        getView().storeResultFailed();
                    }
                });
    }
}
