package com.nador.mobilemed.presentation.presenter.function.measurement;

import android.content.Context;

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.nador.mobilemed.data.dagger.measurement.MeasurementComponent;
import com.nador.mobilemed.data.dagger.measurement.MeasurementModule;
import com.nador.mobilemed.data.measurement.BloodPressureMonitor;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.data.dagger.measurement.BluetoothHealthModule;
import com.nador.mobilemed.data.dagger.measurement.DaggerBluetoothHealthComponent;
import com.nador.mobilemed.data.dagger.measurement.DaggerMeasurementComponent;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.presentation.function.measurement.IBloodpressureMeasurementController;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nador on 27/06/16.
 */
public class BloodpressureMeasurementControllerPresenter extends SubscribingPresenter<IBloodpressureMeasurementController> {

    MeasurementComponent mMeasurementComponent;
    @Inject
    BloodPressureMonitor mBloodPressureMonitor;
    @Inject MeasurementManager mMeasurementManager;

    public BloodpressureMeasurementControllerPresenter(Context context) {
        mMeasurementComponent = DaggerMeasurementComponent.builder()
                .bluetoothHealthComponent(DaggerBluetoothHealthComponent.builder()
                        .bluetoothHealthModule(new BluetoothHealthModule(context))
                        .build())
                .measurementModule(new MeasurementModule(context))
                .build();
        mMeasurementComponent.inject(this);
    }

    public void measureBloodpressure() {
        Subscription subscription = mBloodPressureMonitor
                .connectDevice()
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return mBloodPressureMonitor.measureBloodPressure();
                    } else {
                        return Observable.error(new Throwable("Couldn't connect to blood pressure monitor!"));
                    }
                })
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(bloodPressure -> {
                    if (isViewAttached()) {
                        if (bloodPressure != null) {
                            getView().processMeasurementResult(bloodPressure);
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

    public void storeMeasurement(final long systolicValue, final long diastolicValue, final long pulseValue, final Result.Method method) {
        BloodPressure bp = new BloodPressure(systolicValue, diastolicValue, pulseValue);
        bp.setMethod(method);
        mMeasurementManager.storeResult(bp)
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
