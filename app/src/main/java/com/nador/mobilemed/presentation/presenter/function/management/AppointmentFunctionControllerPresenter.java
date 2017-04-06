package com.nador.mobilemed.presentation.presenter.function.management;

import android.content.Context;

import com.nador.mobilemed.MobilemedApp;
import com.nador.mobilemed.data.entity.Appointment;
import com.nador.mobilemed.data.manager.AppointmentManager;
import com.nador.mobilemed.data.utils.ReactiveExtensions;
import com.nador.mobilemed.presentation.function.management.IAppointmentFunctionController;
import com.nador.mobilemed.presentation.presenter.base.SubscribingPresenter;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by nador on 18/07/16.
 */
public class AppointmentFunctionControllerPresenter extends SubscribingPresenter<IAppointmentFunctionController> {

    @Inject
    AppointmentManager mAppointmentManager;

    public AppointmentFunctionControllerPresenter(Context context) {
        MobilemedApp.getUserComponent(context).inject(this);
    }

    public void getAppointmentList(boolean forceRefresh) {
        mAppointmentManager.getAppointmentList()
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(appointments -> {
                    if (isViewAttached()) {
                        getView().setAppointmentList(appointments);
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        Timber.e(throwable, "Get appointment list failed");
                        getView().getAppointmentListFailed();
                    }
                });
    }

    public void editAppointment(final Appointment appointment) {
        mAppointmentManager.editAppointment(appointment)
                .compose(ReactiveExtensions.applyIOSchedulers())
                .subscribe(aBoolean -> {
                    if (isViewAttached()) {
                        if (aBoolean) {
                            getView().editAppointmentSuccessful();
                        } else {
                            getView().editAppointmentFailed();
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable, "Edit appointment failed");
                    if (isViewAttached()) {
                        getView().editAppointmentFailed();
                    }
                });
    }
}
