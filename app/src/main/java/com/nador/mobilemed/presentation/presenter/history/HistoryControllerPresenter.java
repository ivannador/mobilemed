package com.nador.mobilemed.presentation.presenter.history;

import android.content.Context;

import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.nador.mobilemed.data.entity.measurement.ECGGraph;
import com.nador.mobilemed.data.entity.measurement.ECGMeasurements;
import com.nador.mobilemed.data.manager.HistoryManager;
import com.nador.mobilemed.data.manager.MeasurementManager;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.history.IHistoryController;
import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.entity.measurement.Result;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 29/07/16.
 */
public abstract class HistoryControllerPresenter<V extends IHistoryController> extends SubscribingPresenter<V> {

    protected ArrayList<Result> mResultList;

    public abstract void getResultList(boolean forceRefresh);

    public void getResultList(boolean forceRefresh, final HistoryManager manager) {
        if (mResultList == null || mResultList.isEmpty() || forceRefresh) {
            manager.getHistory()
                    .compose(ReactiveExtensions.applyIOSchedulers())
                    .subscribe(results -> {
                        mResultList = results;
                        if (isViewAttached()) {
                            getView().setResultList(mResultList);
                        }
                    }, throwable -> {
                        Timber.e(throwable, "Get history error");
                        if (throwable instanceof NotAuthorizedException && isViewAttached()) {
                            getView().userReauthNeeded();
                        }
                    });
        } else {
            if (isViewAttached()) {
                getView().setResultList(mResultList);
            }
        }
    }

    // It does not fetch result list, may return null
    public ArrayList<Result> getResultList() {
        return mResultList;
    }

    public static class BloodpressureHistoryControllerPresenter extends HistoryControllerPresenter<IHistoryController> {

        @Inject
        @Named("BP")
        HistoryManager mHistoryManager;

        public BloodpressureHistoryControllerPresenter(Context context) {
            MobilemedApp.getUserComponent(context).inject(this);
        }

        @Override
        public void getResultList(boolean forceRefresh) {
            getResultList(forceRefresh, mHistoryManager);
        }
    }

    public static class WeightHistoryControllerPresenter extends HistoryControllerPresenter<IHistoryController> {

        @Inject
        @Named("Weight")
        HistoryManager mHistoryManager;

        public WeightHistoryControllerPresenter(Context context) {
            MobilemedApp.getUserComponent(context).inject(this);
        }

        @Override
        public void getResultList(boolean forceRefresh) {
            getResultList(forceRefresh, mHistoryManager);
        }
    }

    public static class TemperatureHistoryControllerPresenter extends HistoryControllerPresenter<IHistoryController> {

        @Inject
        @Named("Temperature")
        HistoryManager mHistoryManager;

        public TemperatureHistoryControllerPresenter(Context context) {
            MobilemedApp.getUserComponent(context).inject(this);
        }

        @Override
        public void getResultList(boolean forceRefresh) {
            getResultList(forceRefresh, mHistoryManager);
        }
    }

    public static class GlucometerHistoryControllerPresenter extends HistoryControllerPresenter<IHistoryController> {

        @Inject
        @Named("Glucometer")
        HistoryManager mHistoryManager;

        public GlucometerHistoryControllerPresenter(Context context) {
            MobilemedApp.getUserComponent(context).inject(this);
        }

        @Override
        public void getResultList(boolean forceRefresh) {
            getResultList(forceRefresh, mHistoryManager);
        }
    }

    public static class ECGHistoryControllerPresenter extends HistoryControllerPresenter<IHistoryController.IECGHistoryController> {

        @Inject
        @Named("ECG")
        HistoryManager mHistoryManager;
        @Inject
        MeasurementManager mMeasurementManager;

        public ECGHistoryControllerPresenter(Context context) {
            MobilemedApp.getUserComponent(context).inject(this);
        }

        @Override
        public void getResultList(boolean forceRefresh) {
            getResultList(forceRefresh, mHistoryManager);
        }

        @Override
        public void getResultList(boolean forceRefresh, HistoryManager manager) {
            if (mResultList == null || mResultList.isEmpty() || forceRefresh) {
                manager.getHistory()
                        .compose(ReactiveExtensions.applyIOSchedulers())
                        .subscribe(results -> {
                            mResultList = results;
                            if (isViewAttached()) {
                                getView().setResultList(mResultList);
                            }
                            checkPulse();
                        }, throwable -> {
                            Timber.e(throwable, "Get history error");
                            if (throwable instanceof NotAuthorizedException && isViewAttached()) {
                                getView().userReauthNeeded();
                            }
                        });
            } else {
                if (isViewAttached()) {
                    getView().setResultList(mResultList);
                }
            }
        }

        public void storeECGMeasurement(String chartName, ECGMeasurements measurements) {
            measurements.setECGGraphName(chartName);
            for (Result result : mResultList) {
                ECGGraph graph = (ECGGraph) result;
                if (graph.getName().equals(measurements.getECGGraphName())) {
                    graph.setECGMeasurements(measurements);
                    break;
                }
            }
        }

        public void recordECGMeasurement(String chartName) {
            for (Result result : mResultList) {
                ECGGraph graph = (ECGGraph) result;
                if (graph.getName().equals(chartName) && graph.getECGMeasurements() != null) {
                    Observable.fromCallable(() -> {
                        mMeasurementManager.storeObjectPrimaryKey(graph.getECGMeasurements());
                        return true;
                    }).compose(ReactiveExtensions.applyIOSchedulers())
                            .subscribe(aBoolean -> {
                                if (isViewAttached()) {
                                    if (aBoolean) {
                                        getView().recordECGMeasurementsDone();
                                    } else {
                                        getView().recordECGMeasurementsError();
                                    }
                                }
                            }, throwable -> {
                                Timber.e(throwable, "Record ECG Measurements error");
                                if (isViewAttached()) {
                                    getView().recordECGMeasurementsError();
                                }
                            });
                } else {
                    if (isViewAttached()) {
                        getView().recordECGMeasurementsError();
                    }
                }
            }
        }

        public void checkPulse() {
            for (Result result : mResultList) {
                ECGGraph.SingleChannel graph = (ECGGraph.SingleChannel) result;
                if (graph.getECGMeasurements() == null || !graph.getECGMeasurements().hasPulseValue()) {
                    Observable.fromCallable(() -> graph.calculatePulseValue())
                            .compose(ReactiveExtensions.applyIOSchedulers())
                            .subscribe(aBoolean -> {
                                if (isViewAttached()) {
                                    getView().pulseCalculated();
                                }
                                recordECGMeasurement(graph.getName());
                            }, throwable -> {
                                Timber.e(throwable, "Pulse calculation error");
                            });
                }
            }
        }
    }
}
